package com.example.kevin.jakarta.online;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dxm.rxbinder.annotations.RxBind;
import com.example.kevin.jakarta.common.Constance;
import com.example.kevin.jakarta.common.EmptySubscriber;
import com.example.kevin.jakarta.Utils.CacheUtil;
import com.example.kevin.jakarta.dummy.DummyData;
import com.example.kevin.jakarta.dummy.MusicContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private void requestOnlineList(final String classifyId, final String dir) {
        Observable.just(new MusicHttpClient().requestList(classifyId))
                .doOnNext(cacheList(classifyId))
                .map(parserItemsData(dir))
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

    private void requestCacheList(final String id, final String dir) {
        Observable.just(listCache(id)).map(parserItemsData(dir)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<List<MusicContent.MusicItem>>() {
                    @Override public void onError(Throwable e) {
                        listSubject.onError(e);
                    }

                    @Override public void onNext(List<MusicContent.MusicItem> o) {
                        listSubject.onNext(o);
                        requestOnlineList(id, dir);
                    }
                });
    }

    private Func1<String, List<MusicContent.MusicItem>> parserItemsData(final String dir) {
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
        Observable.just(new MusicHttpClient().requestCategory())
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
  /*      final MusicContent.MusicItem item = musicItem;
        if (new File(item.path()).exists()) {
            downloadSubject.onNext(item);
            return;
        }
        Observable.just(new MusicHttpClient().download(item.url())).map(cacheData(item)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<MusicContent.MusicItem>() {
                    @Override public void onError(Throwable e) {
                        downloadSubject.onError(e);
                    }

                    @Override public void onNext(MusicContent.MusicItem o) {
                        downloadSubject.onNext(o);
                    }
                });*/
    }

    private Func1<byte[], MusicContent.MusicItem> cacheData(final MusicContent.MusicItem item) {
        return new Func1<byte[], MusicContent.MusicItem>() {
            @Override public MusicContent.MusicItem call(byte[] bytes) {
                try {
                    OutputStream outputStream = new FileOutputStream(item.path());
                    outputStream.write(bytes);
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cacheMusicItem(item);
                return item;
            }
        };
    }

    /**
     * 缓存单个音乐到 音乐列表，用于其它地方导出已下载音乐文本数据
     */
    private void cacheMusicItem(MusicContent.MusicItem item) {
        String cacheList = CacheUtil.getInstance().getString(Constance.DOWNLOAD_MUSIC_LIST);
        com.alibaba.fastjson.JSONArray list = com.alibaba.fastjson.JSONArray.parseArray(cacheList);
        list.add(item);
        CacheUtil.getInstance().putString(Constance.DOWNLOAD_MUSIC_LIST, list.toJSONString());
    }


}
