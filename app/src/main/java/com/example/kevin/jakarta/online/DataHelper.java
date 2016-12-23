package com.example.kevin.jakarta.online;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.dxm.rxbinder.annotations.Partial;
import com.dxm.rxbinder.annotations.RxBind;
import com.example.kevin.jakarta.Utils.CacheUtil;
import com.example.kevin.jakarta.common.Constance;
import com.example.kevin.jakarta.common.EmptySubscriber;
import com.example.kevin.jakarta.dummy.DummyData;
import com.example.kevin.jakarta.dummy.MusicContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by kevin on 16-12-14.
 */

public class DataHelper {
    private static final String TAG = DataHelper.class.getName();

    public PublishSubject<List<MusicContent.MusicItem>> listSubject = PublishSubject.create();

    public DataHelper(Context context) {
        CacheUtil.getInstance().init(context);
    }

    public void requestList(@NonNull final Context ctx, @NonNull final String id) {
        listSubject.onNext(DummyData.list(id));

//        String dir = FileUtil.getMusicDir(ctx).getAbsolutePath();
//        if (IsUtils.isNullOrEmpty(listCache(id))) {
//            requestOnlineList(id, dir);
//        } else {
//            requestCacheList(id, dir);
//        }
    }

    //3.2 请求在线 分类音乐列表
    private void requestOnlineList(final String classifyId) {
        Observable.just(MusicHttpClient.getInstance().requestList(classifyId))
                .doOnNext(cacheList(classifyId))
                .map(parserItemsData())
                .subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<List<MusicContent.MusicItem>>() {
                    @Override public void onError(Throwable e) {
                        listSubject.onError(e);
                    }

                    @Override public void onNext(List<MusicContent.MusicItem> o) {
                        listSubject.onNext(o);
                    }
                });
    }

    private Action1<String> cacheList(final String categoryID) {
        return new Action1<String>() {
            @Override public void call(String s) {
                updateListCache(categoryID, s);
            }
        };
    }

    private void requestCacheList(final String id) {
        Observable.just(listCache(id)).map(parserItemsData()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<List<MusicContent.MusicItem>>() {
                    @Override public void onError(Throwable e) {
                        listSubject.onError(e);
                    }

                    @Override public void onNext(List<MusicContent.MusicItem> o) {
                        listSubject.onNext(o);
                        requestOnlineList(id);
                    }
                });
    }

    private Func1<String, List<MusicContent.MusicItem>> parserItemsData() {
        return new Func1<String, List<MusicContent.MusicItem>>() {
            @Override public List<MusicContent.MusicItem> call(String s) {
                try {
                    List<MusicContent.MusicItem> data = new ArrayList<>();
                    JSONObject object = new JSONObject(s);
                    JSONArray result = object.optJSONArray("result");
                    if (result != null) {
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject item = result.optJSONObject(i);
                            if (item != null) {
                            }
                        }
                    }
                    return data;
                } catch (JSONException e) {
                    throw new RuntimeException("parser music item data error,maybe the data format error callback to server");
                }
            }
        };
    }

    //8.2.2 获取 items缓存数据
    private String listCache(String categoryID) {
        return CacheUtil.getInstance().getString(Constance.MUSIC_LIST + "_" + categoryID);
    }

    //8.2.1 同步 items缓存数据
    private void updateListCache(String categoryID, String data) {
        CacheUtil.getInstance().putString(Constance.MUSIC_LIST + "_" + categoryID, data);
    }

    public PublishSubject<List<CategoryInfo>> categorySubject = PublishSubject.create();

    public void requestCategory() {
        categorySubject.onNext(DummyData.categoryInfoList());
//
//        if (IsUtils.isNullOrEmpty(categoryCache())) {
//            requestOnlineCategory();
//        } else {
//            requestCacheCategory();
//        }
    }

    //3.1 请求在线 音乐分类数据
    private void requestOnlineCategory() {
        Observable.just(MusicHttpClient.getInstance().requestCategory())
                .doOnNext(cacheCategory())
                .map(DataHelperBindings.parserClassifyData(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<List<CategoryInfo>>() {
                    @Override public void onError(Throwable e) {
                        categorySubject.onError(e);
                    }

                    @Override public void onNext(List<CategoryInfo> o) {
                        categorySubject.onNext(o);
                    }
                });
    }

    private Action1<String> cacheCategory() {
        return new Action1<String>() {
            @Override public void call(String s) {
                updateRequestCategoryCache(s);
            }
        };
    }

    private void requestCacheCategory() {
        Observable.just(categoryCache()).map(DataHelperBindings.parserClassifyData(this))
                .observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<List<CategoryInfo>>() {
                    @Override public void onError(Throwable e) {
                        categorySubject.onError(e);
                    }

                    @Override public void onNext(List<CategoryInfo> o) {
                        categorySubject.onNext(o);
                        requestOnlineCategory();
                    }
                });
    }

    //6.1 解析分类数据
    @RxBind List<CategoryInfo> parserClassifyData(String s) throws JSONException {
        JSONObject object = new JSONObject(s);
        List<CategoryInfo> data = new ArrayList<>();
        JSONArray result = object.optJSONArray("result");
        if (result != null) {
            for (int i = 0; i < result.length(); i++) {
                JSONObject item = result.optJSONObject(i);
                if (item != null) {
                    CategoryInfo music = CategoryInfo.builder().id(Integer.parseInt(item.optString("id")))
                            .title(item.optString("name"))
                            .build();
                    data.add(music);
                }
            }
        }
        return data;
    }

    private String categoryCache() {
        return CacheUtil.getInstance().getString(Constance.MUSIC_CATEGORY);
    }

    //8.1.1 同步 分类缓存数据
    private void updateRequestCategoryCache(String data) {
        CacheUtil.getInstance().putString(Constance.MUSIC_CATEGORY, data);
    }

    public PublishSubject<MusicContent.MusicItem> downloadSubject = PublishSubject.create();

    public void downloadMusic(@NonNull MusicContent.MusicItem musicItem) {
        downloadSubject.onNext(musicItem);
        final MusicContent.MusicItem item = musicItem;
        /*if (new File(CacheUtil.getMusicCachePath(musicItem.url())).exists()) {
            downloadSubject.onNext(item);
            return;
        }
        Observable.just(musicItem).map(DataHelperBindings.download(this)).map(DataHelperBindings.cacheData(this, musicItem))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<MusicContent.MusicItem>() {
                    @Override public void onError(Throwable e) {
                        downloadSubject.onError(e);
                    }

                    @Override public void onNext(MusicContent.MusicItem o) {
                        downloadSubject.onNext(o);
                    }
                });*/
    }

    @RxBind Pair<byte[], byte[]> download(MusicContent.MusicItem item) {
        byte[] musicData = MusicHttpClient.getInstance().download(item.url());
        byte[] lyricData = MusicHttpClient.getInstance().download(item.lyricUrl());
        return new Pair<>(musicData, lyricData);
    }

    @RxBind MusicContent.MusicItem cacheData(Pair<byte[], byte[]> pair, @Partial MusicContent.MusicItem item) {
        try {
            OutputStream outMusicStream = new FileOutputStream(CacheUtil.getMusicCachePath(item.url()));
            OutputStream outLyricStream = new FileOutputStream(CacheUtil.getMusicLyricPath(item.lyricUrl()));

            outMusicStream.write(pair.first);
            outLyricStream.write(pair.second);
            outMusicStream.close();
            outLyricStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cacheList = CacheUtil.getInstance().getString(Constance.DOWNLOAD_MUSIC_LIST);
        com.alibaba.fastjson.JSONArray list = com.alibaba.fastjson.JSONArray.parseArray(cacheList);
        list.add(item);
        CacheUtil.getInstance().putString(Constance.DOWNLOAD_MUSIC_LIST, list.toJSONString());
        return item;
    }
}
