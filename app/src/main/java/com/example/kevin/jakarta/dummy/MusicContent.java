package com.example.kevin.jakarta.dummy;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import com.dxm.rxbinder.annotations.RxBind;
import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by kevin on 16-12-13.
 */

public class MusicContent {

    public enum MusicType {NONE, OFFLINE, ONLINE}

    public static Observable<List<MusicItem>> createMusicData(Context context) {
        List<MusicItem> musicItems = new ArrayList<>();
        return Observable.just(musicItems).map(MusicContentBindings.attachNoneMusicTag())
                .map(MusicContentBindings.attachOnlineMusicTag())
                .map(MusicContentBindings.attachOfflineMusicTag());

    }

    @RxBind static List<MusicItem> attachNoneMusicTag(List<MusicItem> items) {
        items.add(MusicItem.builder().type(MusicType.NONE).build());
        return items;
    }

//    private static MusicItem NoneMusicItem() {
//        MusicItem none = new MusicItem();
//        none.type = MusicType.NONE;
//        return none;
//    }

    @RxBind static List<MusicItem> attachOnlineMusicTag(List<MusicItem> items) {
        items.add(MusicItem.builder().type(MusicType.ONLINE).id(1).build());
        return items;
    }

//    private static MusicItem OnlineMusicItem() {
//        MusicItem onlineTag = new MusicItem();
//        onlineTag.type = MusicType.ONLINE;
//        onlineTag.thumbUrl = "";
//        return onlineTag;
//    }

    @RxBind static List<MusicItem> attachOfflineMusicTag(List<MusicItem> items) {
        List<MusicItem> offlineItems = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            items.add(MusicItem.builder().type(MusicType.OFFLINE).id(2).path("http://ac-oi06foci.clouddn.com/e1a02f525a09e9858f2f.m4a").build());
        // load cache music
//        items.addAll(offlineItems);
        return items;
    }

//    private static List<MusicItem> offlineMusicList() {
//        List<MusicItem> offlineItems = new ArrayList<>();
//        // load cache music
//        return offlineItems;
//    }

    @AutoValue public static abstract class MusicItem implements Parcelable {
        public abstract int id();

        public abstract String name();

        public abstract String author();

        public abstract String path();

        @NonNull public abstract MusicType type();

        public abstract String url();

        public abstract String thumbUrl();

        public abstract long duration();

        public static Builder builder() {
            return new AutoValue_MusicContent_MusicItem.Builder().id(0).name("default").author("default")
                    .path("").type(MusicType.NONE).duration(0).url("").thumbUrl("");
        }

        @AutoValue.Builder public static abstract class Builder {
            public abstract Builder id(int id);

            public abstract Builder name(String name);

            public abstract Builder author(String author);

            public abstract Builder type(MusicType type);

            public abstract Builder path(String path);

            public abstract Builder url(String url);

            public abstract Builder duration(long duration);

            public abstract Builder thumbUrl(String thumbUrl);

            public abstract MusicItem build();
        }
    }
}
