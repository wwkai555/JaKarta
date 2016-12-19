package com.example.kevin.jakarta.online;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kevin.jakarta.databinding.ItemListOnlineMusicBinding;
import com.example.kevin.jakarta.dummy.MusicContent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;

/**
 * Created by kevin on 16-12-15.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    public PublishSubject<MusicContent.MusicItem> downloadSubject = PublishSubject.create();
    private List<MusicContent.MusicItem> itemList = new ArrayList<>();

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemListOnlineMusicBinding musicBinding = ItemListOnlineMusicBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(musicBinding);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setAdapter(this);
        holder.binding.setItem(itemList.get(position));
    }

    @Override public int getItemCount() {
        return itemList.size();
    }

    public void list(List<MusicContent.MusicItem> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
    }

    @BindingAdapter("src")
    public static void loadImage(View view, String url) {
        Picasso.with(view.getContext()).load(url).into((ImageView) view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemListOnlineMusicBinding binding;

        public ViewHolder(ItemListOnlineMusicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
