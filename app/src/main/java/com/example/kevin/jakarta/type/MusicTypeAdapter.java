package com.example.kevin.jakarta.type;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.kevin.jakarta.Utils.AnimationUtil;
import com.example.kevin.jakarta.databinding.FragmentItemBinding;

import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;

import static com.example.kevin.jakarta.dummy.MusicContent.MusicItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MusicItem} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class MusicTypeAdapter extends RecyclerView.Adapter<MusicTypeAdapter.ViewHolder> {
    private int key = 0;
    private List<MusicItem> mValues = new ArrayList<>();
    PublishSubject<Pair<Integer, MusicItem>> publishSubject = PublishSubject.create();

    MusicTypeAdapter() {
    }

    void items(List<MusicItem> items) {
        mValues.clear();
        mValues.addAll(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FragmentItemBinding itemBinding = FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.itemBinding.setAdapter(this);
        holder.itemBinding.setItem(mValues.get(position));
        holder.itemBinding.setHolder(holder);
        AnimationUtil.scaleAnimation(key == position ? 1.2f : 1.0f, key == position ? 200 : 0, 1f, holder.itemView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void switchTypeControl(MusicItem item, ViewHolder holder) {
        Log.d("MusicRecyclerView", "getLayoutPosition : " + holder.getLayoutPosition());
        key = holder.getLayoutPosition();
        publishSubject.onNext(new Pair<>(holder.getLayoutPosition(), item));
        notifyDataSetChanged();
    }

    public void updateKeyPosition(int key) {
        this.key = key;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        FragmentItemBinding itemBinding;

        ViewHolder(FragmentItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
            itemBinding.setHolder(this);
        }
    }
}
