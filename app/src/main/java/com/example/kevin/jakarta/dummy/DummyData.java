package com.example.kevin.jakarta.dummy;

import com.example.kevin.jakarta.online.CategoryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 16-12-15.
 */

public class DummyData {
    public static List<CategoryInfo> categoryInfoList() {
        List<CategoryInfo> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            list.add(CategoryInfo.builder().id(i).title("类别" + i).build());
        }
        return list;
    }


    public static List<MusicContent.MusicItem> list(String categoryID) {
        List<MusicContent.MusicItem> list = new ArrayList<>();
        MusicContent.MusicItem none = MusicContent.MusicItem.builder().id(0)
                .name("无音乐").author("none").duration(0).type(MusicContent.MusicType.NONE)
                .build();
        list.add(none);
        for (int i = 1; i < 10; i++) {
            MusicContent.MusicItem item = MusicContent.MusicItem.builder().id(i)
                    .name(String.format("分类%s ： 歌曲%s", categoryID, i)).author("china voice")
                    .duration(i * 5000).type(MusicContent.MusicType.ONLINE)
                    .url("http://ac-oi06foci.clouddn.com/e1a02f525a09e9858f2f.m4a")
                    .build();
            list.add(item);
        }
        return list;
    }
}