package com.example.kevin.jakarta;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;

import com.dxm.rxbinder.annotations.RxBind;
import com.example.kevin.jakarta.clip.MusicClipFragment;
import com.example.kevin.jakarta.dummy.MusicContent;
import com.example.kevin.jakarta.dummy.VideoInfo;
import com.example.kevin.jakarta.online.OnlineMusicListActivity;
import com.example.kevin.jakarta.type.MusicTypeFragment;
import com.google.auto.value.AutoValue;

import rx.subjects.PublishSubject;

/**
 * Created by kevin on 16-12-14.
 */

public class MusicControl {
    final PublishSubject<ClipMusicInfo> musicSubject = PublishSubject.create();
    private ClipMusicInfo clipInfo;
    private final VideoInfo videoInfo;
    private final int parentId;

    public MusicControl(VideoInfo videoInfo, int parentID) {
        this.videoInfo = videoInfo;
        this.parentId = parentID;
    }

    public void showMusicTypeFragment(FragmentActivity activity) {
        MusicContent.MusicItem item = MusicContent.MusicItem.builder().build();
        showMusicTypeFragment(activity, ClipMusicInfo.builder().item(item).clipDuration(videoInfo.duration).build());
    }

    public void showMusicTypeFragment(FragmentActivity activity, @NonNull ClipMusicInfo clipInfo) {
        this.clipInfo = clipInfo;
        MusicTypeFragment fragment = MusicTypeFragment.newInstance(clipInfo.item());
        activity.getSupportFragmentManager().beginTransaction().replace(parentId, fragment, "music").commit();
        fragment.musicTypeSubject.subscribe(MusicControlBindings.switchMusicType(this));
    }

    @RxBind void switchMusicType(Pair<MusicContent.MusicItem, FragmentActivity> pair) {
        if (pair.first.type() == MusicContent.MusicType.NONE) {
            callbackResult(pair.first);
        } else if (pair.first.type() == MusicContent.MusicType.OFFLINE) {
            doOfflineType(pair);
        } else {
            doOnlineType(pair);
        }
    }

    private void doOfflineType(Pair<MusicContent.MusicItem, FragmentActivity> pair) {
        if (pair.first.duration() <= videoInfo.duration) {
            callbackResult(pair.first);
        } else {
            showMusicClipFragment(pair.first, pair.second);
        }
    }

    private void doOnlineType(Pair<MusicContent.MusicItem, FragmentActivity> pair) {
        if (MusicContent.MusicType.NONE == clipInfo.item().type()) {
            goOnlineMusicList(pair.second);
        } else {
            doOfflineType(new Pair<>(clipInfo.item(), pair.second));
        }
    }

    private void callbackResult(MusicContent.MusicItem item) {
        ClipMusicInfo clipMusicInfo = ClipMusicInfo.builder().item(item).clipDuration(videoInfo.duration).build();
        callbackResult(clipMusicInfo);
    }

    private void callbackResult(ClipMusicInfo clipMusicInfo) {
        musicSubject.onNext(clipMusicInfo);
    }

    private void goOnlineMusicList(FragmentActivity activity) {
        Intent data = new Intent(activity, OnlineMusicListActivity.class);
        activity.startActivityForResult(data, MusicTypeFragment.REQUEST_CODE_ONLINE);
    }

    private void showMusicClipFragment(MusicContent.MusicItem musicItem, FragmentActivity activity) {
        ClipMusicInfo clipMusicInfo = ClipMusicInfo.builder().item(musicItem)
                .startTime(clipInfo.startTime()).clipDuration(clipInfo.clipDuration())
                .build();
        MusicClipFragment clipFragment = MusicClipFragment.newInstance(clipMusicInfo);
        activity.getSupportFragmentManager().beginTransaction().replace(parentId, clipFragment, "music_clip").commit();
        clipFragment.clipSubject.subscribe(MusicControlBindings.clipResult(this));
    }

    @RxBind void clipResult(Pair<FragmentActivity, MusicControl.ClipMusicInfo> clipPair) {
        callbackResult(clipPair.second);
        showMusicTypeFragment(clipPair.first, clipPair.second);
    }

    public void filterMusicAction(MusicContent.MusicItem item, FragmentActivity fragmentActivity) {
        if (item.type() == MusicContent.MusicType.NONE || item.duration() <= videoInfo.duration) {
            callbackResult(item);
            return;
        }
        showMusicClipFragment(item, fragmentActivity);
    }

    @AutoValue public static abstract class ClipMusicInfo implements Parcelable {
        @Nullable public abstract MusicContent.MusicItem item();

        public abstract long startTime();

        public abstract long clipDuration();

        public static Builder builder() {
            return new AutoValue_MusicControl_ClipMusicInfo.Builder().startTime(0).clipDuration(0);
        }

        @AutoValue.Builder public static abstract class Builder {
            public abstract Builder item(MusicContent.MusicItem item);

            public abstract Builder startTime(long startTime);

            public abstract Builder clipDuration(long clipDuration);

            public abstract ClipMusicInfo build();
        }

        @Override public String toString() {
            return "[type : " + item().type() + " name : " + item().name() + " author : " + item().author()
                    + " startTime : " + startTime() + " clipDuration : " + clipDuration() + "]";
        }
    }

}

