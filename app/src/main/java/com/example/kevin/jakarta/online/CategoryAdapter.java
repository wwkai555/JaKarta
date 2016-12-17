package com.example.kevin.jakarta.online;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.kevin.jakarta.databinding.ItemCategoryOnlineMusicBinding;

import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;

/**
 * Created by kevin on 16-12-14.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private int keyPosition;
    private List<CategoryInfo> musicItems = new ArrayList<>();
    public PublishSubject<String> categoryRequest = PublishSubject.create();

    public void setMusicItems(List<CategoryInfo> list) {
        musicItems.clear();
        musicItems.addAll(list);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCategoryOnlineMusicBinding musicBinding = ItemCategoryOnlineMusicBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(musicBinding);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setAdapter(this);
        holder.binding.setCategoryInfo(musicItems.get(position));
        holder.binding.setViewHolder(holder);
        holder.binding.btnCategory.setSelected(position == keyPosition);
    }

    @Override public int getItemCount() {
        return musicItems.size();
    }

    public void requestList(int categoryID, int layoutPosition) {
        keyPosition = layoutPosition;
        categoryRequest.onNext(String.valueOf(categoryID));
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCategoryOnlineMusicBinding binding;

        public ViewHolder(ItemCategoryOnlineMusicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setViewHolder(this);
        }
    }
}
