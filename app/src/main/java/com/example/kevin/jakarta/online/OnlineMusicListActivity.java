package com.example.kevin.jakarta.online;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dxm.rxbinder.annotations.RxBind;
import com.example.kevin.jakarta.R;
import com.example.kevin.jakarta.common.Constance;
import com.example.kevin.jakarta.dummy.MusicContent;

import org.dxm.recyclerviews.ItemDecorations;

import java.util.List;

public class OnlineMusicListActivity extends AppCompatActivity {
    private DataHelper dataHelper;
    private ListAdapter listAdapter = new ListAdapter();
    private CategoryAdapter categoryAdapter = new CategoryAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataHelper = new DataHelper(this);
        setContentView(R.layout.activity_online_music_list);
        RecyclerView rcyCategory = (RecyclerView) findViewById(R.id.rcyCategory);
        RecyclerView rcyList = (RecyclerView) findViewById(R.id.rcyList);

        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcyCategory.setLayoutManager(categoryLayoutManager);
        rcyCategory.setAdapter(categoryAdapter);
        categoryAdapter.categoryRequest.subscribe(OnlineMusicListActivityBindings.requestList(this));

        LinearLayoutManager listLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcyList.setLayoutManager(listLayoutManager);
        rcyList.setAdapter(listAdapter);
        listAdapter.downloadSubject.subscribe(OnlineMusicListActivityBindings.downloadMusic(this));
        rcyList.addItemDecoration(ItemDecorations.linearSpacingBuilder().itemSpacing(1).build());

        dataHelper.categorySubject.subscribe(OnlineMusicListActivityBindings.updateCategory(this));
        dataHelper.listSubject.subscribe(OnlineMusicListActivityBindings.updateList(this));
        dataHelper.downloadSubject.subscribe(OnlineMusicListActivityBindings.updateDownloadList(this));

        dataHelper.requestCategory();
    }

    @RxBind void updateCategory(List<CategoryInfo> categoryList) {
        categoryAdapter.setMusicItems(categoryList);
        categoryAdapter.notifyDataSetChanged();
        dataHelper.requestList(this, String.valueOf(categoryList.get(0).id()));
    }

    @RxBind void requestList(String id) {
        dataHelper.requestList(this, id);
    }

    @RxBind void updateList(List<MusicContent.MusicItem> itemList) {
        listAdapter.list(itemList);
        listAdapter.notifyDataSetChanged();
    }

    @RxBind void downloadMusic(MusicContent.MusicItem item) {
        dataHelper.downloadMusic(item);
    }

    @RxBind void updateDownloadList(MusicContent.MusicItem item) {
        Intent intent = new Intent();
        intent.putExtra(Constance.CLICK_MUSIC, item);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void close(View view) {
        finish();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }

}
