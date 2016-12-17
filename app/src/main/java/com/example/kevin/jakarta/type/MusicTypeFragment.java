package com.example.kevin.jakarta.type;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dxm.rxbinder.annotations.RxBind;
import com.example.kevin.jakarta.type.MusicTypeFragmentBindings;
import com.example.kevin.jakarta.common.PaddingDecoration;
import com.example.kevin.jakarta.databinding.FragmentMusicListBinding;
import com.example.kevin.jakarta.dummy.MusicContent;
import com.example.kevin.jakarta.Utils.DensityUtil;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * A fragment representing a list of Items.
 */
public class MusicTypeFragment extends Fragment {
    public static int REQUEST_CODE_ONLINE = 101;
    public static final String ITEM = "ITEM";
    private MusicTypeAdapter adapter = new MusicTypeAdapter();
    public PublishSubject<Pair<MusicContent.MusicItem, FragmentActivity>> musicTypeSubject = PublishSubject.create();

    public MusicTypeFragment() {
    }

    public static MusicTypeFragment newInstance(MusicContent.MusicItem item) {
        MusicTypeFragment fragment = new MusicTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMusicListBinding fragmentBindings = FragmentMusicListBinding.inflate(LayoutInflater.from(getContext()), container, false);
        fragmentBindings.setMusicTypeFragment(this);
        int defaultPadding = (DensityUtil.getScreenWidth(getContext()) / 2) - DensityUtil.dip2px(getContext(), 30);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fragmentBindings.rcyTypeList.setLayoutManager(manager);
        fragmentBindings.rcyTypeList.addItemDecoration(new PaddingDecoration(15, 15, defaultPadding, 15));
        fragmentBindings.rcyTypeList.setAdapter(adapter);

        MusicContent.createMusicData(this.getActivity()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MusicTypeFragmentBindings.updateItemList(this));
        adapter.publishSubject.subscribe(MusicTypeFragmentBindings.callBack(this));
        return fragmentBindings.getRoot();
    }

    @RxBind void updateItemList(List<MusicContent.MusicItem> itemList) {
        MusicContent.MusicItem item = getArguments().getParcelable(ITEM);
        int key = 0;
        if (item != null) {
            if (item.type() == MusicContent.MusicType.NONE) {
                key = 0;
            } else if (item.type() == MusicContent.MusicType.ONLINE) {
                key = 1;
            } else {
                for (MusicContent.MusicItem item1 : itemList) {
                    if (item1.id() == item.id()) key = itemList.indexOf(item1);
                }
            }
        }
        adapter.items(itemList);
        adapter.updateKeyPosition(key);
        adapter.notifyDataSetChanged();
    }

    @RxBind void callBack(Pair<Integer, MusicContent.MusicItem> integerPair) {
        musicTypeSubject.onNext(new Pair<>(integerPair.second, MusicTypeFragment.this.getActivity()));
    }

    public void clear() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }
}
