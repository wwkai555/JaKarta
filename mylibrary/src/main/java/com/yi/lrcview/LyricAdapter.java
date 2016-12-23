package com.yi.lrcview;

import android.databinding.ObservableFloat;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dxm.lrc.LyricItem;
import com.dxm.variable.Var;
import com.dxm.variable.Variables;
import com.yi.mylibrary.BR;
import com.yi.mylibrary.databinding.ListLyricAlbumBinding;
import com.yi.mylibrary.databinding.ListLyricArtistBinding;
import com.yi.mylibrary.databinding.ListLyricAuthorBinding;
import com.yi.mylibrary.databinding.ListLyricLineBinding;
import com.yi.mylibrary.databinding.ListLyricTitleBinding;

import java.util.Collections;
import java.util.List;

/**
 * Created by ants on 21/12/2016.
 */

public class LyricAdapter extends RecyclerView.Adapter<LyricAdapter.ViewHolder> {
    interface ViewType {
        int title = 0;
        int album = 1;
        int duration = 2;
        int artist = 3;
        int author = 4;
        int line = 5;
    }

    @NonNull private final List<TimedLyricItem> items;
    @NonNull public Var<Long> highlighting = new Variables.Observable<Long>(0L) {
        @Override public void onChange(Long aLong, Long newTime) {
            notifyDataSetChanged();
        }
    };

    public LyricAdapter(@NonNull List<TimedLyricItem> items) {
        this.items = Collections.unmodifiableList(items);
        setHasStableIds(true);
    }

    @Override public long getItemId(int position) {
        return items.get(position).getStartTimeMS();
    }

    public int positionForTime(long time) {
        final int maxIndex = getItemCount() - 1;
        for (int i = 0; i < maxIndex; i++) {
            TimedLyricItem item = items.get(i);
            if (item.getStartTimeMS() <= time && item.getEndTimeMS() >= time) return i;
        }
        return maxIndex;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ViewType.title:
                return new ViewHolder(ListLyricTitleBinding.inflate(inflater, parent, false));
            case ViewType.album:
                return new ViewHolder(ListLyricAlbumBinding.inflate(inflater, parent, false));
            case ViewType.artist:
                return new ViewHolder(ListLyricArtistBinding.inflate(inflater, parent, false));
            case ViewType.author:
                return new ViewHolder(ListLyricAuthorBinding.inflate(inflater, parent, false));
            case ViewType.line:
                return new ViewHolder(ListLyricLineBinding.inflate(inflater, parent, false));
        }
        throw new IllegalArgumentException("Unknown viewType " + viewType);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        TimedLyricItem item = items.get(position);
        long highlighting = this.highlighting.get();
        holder.setItem(item.getItem());
        float alpha = 1f;
        if (item.getStartTimeMS() < highlighting && item.getEndTimeMS() <= highlighting) {
            alpha = (item.getStartTimeMS() / (float) highlighting) / 3f;
        } else if (item.getStartTimeMS() >= highlighting && item.getEndTimeMS() > highlighting) {
            alpha = (highlighting / (float) item.getStartTimeMS()) / 3f;
        }
        holder.setHightlighted(alpha);
    }

    @Override public int getItemCount() {
        return items.size();
    }


    @Override public int getItemViewType(int position) {
        return items.get(position).getItem().accept(null, viewTypeVisitor);
    }

    private final LyricItem.Visitor1<Void, Integer> viewTypeVisitor = new LyricItems.AbsVisitor1<Void, Integer>() {
        @Override public Integer visitTitle(LyricItem.Title title, Void aVoid) {
            return ViewType.title;
        }

        @Override public Integer visitArtist(LyricItem.Artist artist, Void aVoid) {
            return ViewType.artist;
        }

        @Override public Integer visitAlbum(LyricItem.Album album, Void aVoid) {
            return ViewType.album;
        }

        @Override public Integer visitDuration(LyricItem.Duration duration, Void aVoid) {
            return ViewType.duration;
        }

        @Override public Integer visitAuthor(LyricItem.Author author, Void aVoid) {
            return ViewType.author;
        }

        @Override public Integer visitLine(LyricItem.Line line, Void aVoid) {
            return ViewType.line;
        }

        @Override Integer visitDefault(@NonNull LyricItem item, Void aVoid) {
            throw new IllegalArgumentException("Unsupported item " + item);
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull final ViewDataBinding binding;
        private final ObservableFloat highlighted = new ObservableFloat();

        private ViewHolder(@NonNull ViewDataBinding binding) {
            super(binding.getRoot());
            binding.setVariable(BR.highlighted, highlighted);
            this.binding = binding;
        }

        void setHightlighted(float highlighted) {
            this.highlighted.set(highlighted);
        }

        void setItem(@NonNull LyricItem item) {
            binding.setVariable(BR.item, item);
        }
    }

}
