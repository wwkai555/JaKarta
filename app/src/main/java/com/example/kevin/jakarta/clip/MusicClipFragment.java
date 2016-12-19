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

import com.example.kevin.jakarta.MusicControl;
import com.example.kevin.jakarta.Utils.DensityUtil;
import com.example.kevin.jakarta.common.PaddingDecoration;
import com.example.kevin.jakarta.databinding.FragmentMusicClipBinding;
import com.example.kevin.jakarta.dummy.MusicContent;
import com.example.kevin.jakarta.online.OnlineMusicListActivity;
import com.example.kevin.jakarta.type.MusicTypeFragment;

import rx.subjects.PublishSubject;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class MusicClipFragment extends Fragment {
    private static final String ARG_ITEM = "ARG_ITEM";
    private static final long FULL_MUSIC_LENGTH = 20 * 1000;//MS
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MusicControl.ClipMusicInfo clipMusicInfo = getArguments().getParcelable(ARG_ITEM);
        FragmentMusicClipBinding clipBinding = FragmentMusicClipBinding.inflate(LayoutInflater.from(container.getContext()), container, false);
        clipBinding.setClipInfo(clipMusicInfo);
        VolumeAdapter adapter = new VolumeAdapter();
        int videoPanelWidth = (int) ((DensityUtil.getScreenWidth(this.getActivity()) * clipMusicInfo.clipDuration()) / FULL_MUSIC_LENGTH);
        long durationMSPer = (2 + DensityUtil.dip2px(getActivity(), 2)) * FULL_MUSIC_LENGTH / DensityUtil.getScreenWidth(this.getActivity());
        int defaultPadding = (DensityUtil.getScreenWidth(getActivity()) - videoPanelWidth) / 2;
        adapter.setDurationMSPerBar(durationMSPer);
        String path = clipMusicInfo.item().type() == MusicContent.MusicType.ONLINE ? clipMusicInfo.item().url() : clipMusicInfo.item().path();
        adapter.test(path);
//        adapter.setInput(path);
        clipBinding.rcyVolume.setAdapter(adapter);
        clipBinding.rcyVolume.addOnScrollListener(new ScrollListener(clipBinding));
        clipBinding.rcyVolume.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        clipBinding.rcyVolume.addItemDecoration(new PaddingDecoration(2, 0, defaultPadding, defaultPadding)/*ItemDecorations.linearSpacingBuilder().itemSpacing(2).build()*/);

        Log.d("MusicClipFragment", "videoPanelWidth : " + videoPanelWidth + " video duration : " + clipMusicInfo.item().duration());
        clipBinding.viewVideoPanel.getLayoutParams().width = videoPanelWidth;
        clipBinding.setFragment(this);
        return clipBinding.getRoot();
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

