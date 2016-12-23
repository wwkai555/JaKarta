package com.yi.lrcview;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dxm.lrc.LyricGroup;
import com.dxm.variable.Var;
import com.dxm.variable.Variables;

import org.dxm.recyclerviews.ItemDecorations;

import java.util.Collections;

/**
 * Created by ants on 21/12/2016.
 */

public class LyricController extends RecyclerView.OnScrollListener {
    @NonNull private final RecyclerView recyclerView;
    @NonNull private final LinearLayoutManager manager;
    private final int padding;

    public final Var<Long> scrollTime = new Variables.Observable<Long>(0L) {
        @Override public void onChange(Long aLong, final Long newTime) {
            if (adapter.get().getItemCount() == 0) return;
            int position = adapter.get().positionForTime(newTime);
            int minPosition = manager.findFirstVisibleItemPosition();
            int maxPosition = manager.findLastVisibleItemPosition();
            if (position < minPosition || position > maxPosition) {
                recyclerView.stopScroll();
                manager.scrollToPositionWithOffset(position, recyclerView.getHeight() / 2);
                return;
            }
            RecyclerView.ViewHolder hitView = recyclerView.findViewHolderForLayoutPosition(position);
            if (null != hitView) {
                recyclerView.smoothScrollBy(0, (hitView.itemView.getTop() + hitView.itemView.getHeight() / 2) - (recyclerView.getHeight() / 2));
            } else {
                manager.scrollToPositionWithOffset(position, recyclerView.getHeight() / 2);
            }
            recyclerView.post(new Runnable() {
                @Override public void run() {
                    adapter.get().highlighting.set(newTime);
                }
            });
        }
    };
    public final Var<LyricGroup> group = new Variables.Observable<LyricGroup>(LyricGroup.builder().build()) {
        @Override public void onChange(LyricGroup group, LyricGroup newGroup) {
            adapter.set(new LyricAdapter(TimedLyricItem.expand(newGroup)));
        }
    };
    private final Var<LyricAdapter> adapter = new Variables.Observable<LyricAdapter>(new LyricAdapter(Collections.<TimedLyricItem>emptyList())) {
        @Override public void onChange(LyricAdapter lyricAdapter, LyricAdapter adapter) {
            recyclerView.setAdapter(adapter);
        }
    };

    public LyricController(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(this);
        padding = recyclerView.getContext().getResources().getDisplayMetrics().heightPixels / 2;
        recyclerView.addItemDecoration(ItemDecorations.linearSpacingBuilder().top(padding).bottom(padding).build());
        recyclerView.setLayoutManager(manager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }
}