package com.example.kevin.jakarta.clip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dxm.lrc.FileLineReader;
import com.dxm.lrc.LyricGroup;
import com.dxm.lrc.LyricItem;
import com.dxm.rxbinder.annotations.RxBind;
import com.example.kevin.jakarta.MusicControl;
import com.example.kevin.jakarta.Utils.CacheUtil;
import com.example.kevin.jakarta.Utils.DensityUtil;
import com.example.kevin.jakarta.common.PaddingDecoration;
import com.example.kevin.jakarta.databinding.FragmentMusicClipBinding;
import com.example.kevin.jakarta.dummy.MusicContent;
import com.example.kevin.jakarta.online.OnlineMusicListActivity;
import com.example.kevin.jakarta.type.MusicTypeFragment;
import com.yi.lrcview.LyricController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class MusicClipFragment extends Fragment {
    private static final String ARG_ITEM = "ARG_ITEM";
    private static final long FULL_MUSIC_LENGTH = 20 * 1000;//MS

    private LyricController controller;
    public PublishSubject<Long> timeSubject = PublishSubject.create();
    public PublishSubject<Pair<FragmentActivity, MusicControl.ClipMusicInfo>> clipSubject = PublishSubject.create();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    @Deprecated
    public MusicClipFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MusicClipFragment newInstance(MusicControl.ClipMusicInfo clipMusicInfo) {
        MusicClipFragment fragment = new MusicClipFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, clipMusicInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeSubject.observeOn(AndroidSchedulers.mainThread()).subscribe(MusicClipFragmentBindings.scrollTime(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MusicControl.ClipMusicInfo clipMusicInfo = getArguments().getParcelable(ARG_ITEM);
        FragmentMusicClipBinding clipBinding = FragmentMusicClipBinding.inflate(LayoutInflater.from(container.getContext()), container, false);
        clipBinding.setClipInfo(clipMusicInfo);
        controller = new LyricController(clipBinding.rcyLyric);
        Observable.just(clipMusicInfo.item()).map(MusicClipFragmentBindings.lyricGroup(this)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MusicClipFragmentBindings.update(this));
        VolumeAdapter adapter = new VolumeAdapter();
        int videoPanelWidth = (int) ((DensityUtil.getScreenWidth(this.getActivity()) * clipMusicInfo.clipDuration()) / FULL_MUSIC_LENGTH);
        long durationMSPer = (2 + DensityUtil.dip2px(getActivity(), 2)) * FULL_MUSIC_LENGTH / DensityUtil.getScreenWidth(this.getActivity());
        int defaultPadding = (DensityUtil.getScreenWidth(getActivity()) - videoPanelWidth) / 2;
        adapter.setDurationMSPerBar(durationMSPer);
        String path = clipMusicInfo.item().type() == MusicContent.MusicType.ONLINE ? clipMusicInfo.item().url() : clipMusicInfo.item().path();
        long sc = System.currentTimeMillis();
        adapter.test(path);
        Log.d("MusicClipFragment", "clip volume time : " + (System.currentTimeMillis() - sc));
//        adapter.setInput(path);
        clipBinding.rcyVolume.setAdapter(adapter);
        clipBinding.rcyVolume.addOnScrollListener(new ScrollListener(clipBinding));
        clipBinding.rcyVolume.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        clipBinding.rcyVolume.addItemDecoration(new PaddingDecoration(2, 0, defaultPadding, defaultPadding));

        Log.d("MusicClipFragment", "videoPanelWidth : " + videoPanelWidth + " video duration : " + clipMusicInfo.clipDuration());
        clipBinding.viewVideoPanel.getLayoutParams().width = videoPanelWidth;
        clipBinding.setFragment(this);
        return clipBinding.getRoot();
    }

    @RxBind LyricGroup lyricGroup(MusicContent.MusicItem item) {
        String cache = CacheUtil.getMusicLyricPath(item.lyricUrl());
        List<LyricItem> lyricItems = new ArrayList<>();
        if (new File(cache).exists()) {
            try {
                lyricItems.addAll(FileLineReader.fromStream(new FileInputStream(cache)).parse(LyricItem.LineParser.Instance));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //test
        lyricItems.clear();
        try {
            lyricItems.addAll(FileLineReader.fromStream(getActivity().getAssets().open("lyric.lrc")).parse(LyricItem.LineParser.Instance));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return LyricGroup.builder().addItems(lyricItems, LyricGroup.Policy.PickFirst).build();
    }

    @RxBind void update(LyricGroup lyricGroup) {
        controller.group.set(lyricGroup);
        //for test
        Observable.interval(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override public void call(Long aLong) {
                long time = aLong * 2000;
                controller.scrollTime.set(time);
            }
        });
    }

    @RxBind void scrollTime(long timeMS) {
        if (controller != null)
            controller.scrollTime.set(timeMS);
    }

    class ScrollListener extends RecyclerView.OnScrollListener {
        private int scrollX;
        private FragmentMusicClipBinding binding;
        private MusicControl.ClipMusicInfo clipMusicInfo;

        public ScrollListener(FragmentMusicClipBinding binding) {
            this.binding = binding;
            clipMusicInfo = binding.getClipInfo();
        }

        @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int startTime = (int) ((scrollX * FULL_MUSIC_LENGTH) / DensityUtil.getScreenWidth(recyclerView.getContext()));
                MusicControl.ClipMusicInfo info = MusicControl.ClipMusicInfo.builder().startTime(startTime).clipDuration(clipMusicInfo.clipDuration())
                        .item(clipMusicInfo.item()).build();
                binding.setClipInfo(info);
            }
        }

        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollX += dx;
        }
    }

    public void switchMusic() {
        Intent data = new Intent(this.getActivity(), OnlineMusicListActivity.class);
        this.getActivity().startActivityForResult(data, MusicTypeFragment.REQUEST_CODE_ONLINE);
    }

    public void save(MusicControl.ClipMusicInfo clipMusicInfo) {
        clipSubject.onNext(new Pair<>(this.getActivity(), clipMusicInfo));
    }
}

