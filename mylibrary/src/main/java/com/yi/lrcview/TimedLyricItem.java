package com.yi.lrcview;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dxm.lrc.LyricGroup;
import com.dxm.lrc.LyricItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ants on 21/12/2016.
 */

public class TimedLyricItem {
    @NonNull private final LyricItem item;
    private final long startTimeMS;
    private final long endTimeMS;


    public TimedLyricItem(@NonNull LyricItem item, long startTimeMS, long endTimeMS) {
        this.item = item;
        this.startTimeMS = startTimeMS;
        this.endTimeMS = endTimeMS;
    }

    @NonNull public LyricItem getItem() {
        return item;
    }

    public long getStartTimeMS() {
        return startTimeMS;
    }

    public long getEndTimeMS() {
        return endTimeMS;
    }

    @NonNull public static List<TimedLyricItem> expand(@NonNull LyricGroup group) {
        List<LyricItem> beforeLines = new LinkedList<>();
        LyricItem title = group.getTitle();
        if (null != title) beforeLines.add(title);
        LyricItem album = group.getAlbum();
        if (null != album) beforeLines.add(album);
        LyricItem artist = group.getArtist();
        if (null != artist) beforeLines.add(artist);
        LyricItem author = group.getAuthor();
        if (null != author) beforeLines.add(author);
        List<LyricItem.Line> lines = group.getLines();
        long firstLineStart = lines.isEmpty() ? 0L : lines.get(0).getTimeMS();
        long beforeLinesEach = beforeLines.isEmpty() ? firstLineStart : firstLineStart / beforeLines.size();
        List<TimedLyricItem> expanded = new ArrayList<>(beforeLines.size() + lines.size());
        for (int i = 0; i < beforeLines.size(); i++) expanded.add(new TimedLyricItem(beforeLines.get(i), beforeLinesEach * i, beforeLinesEach * (i + 1)));
        for (int i = 0; i < lines.size() - 1; i++) {
            LyricItem.Line line = lines.get(i);
            expanded.add(new TimedLyricItem(line, line.getTimeMS(), lines.get(i + 1).getTimeMS()));
        }
        if (!lines.isEmpty()) {
            LyricItem.Line lastLine = lines.get(lines.size() - 1);
            LyricItem.Duration duration = group.getDuration();
            expanded.add(new TimedLyricItem(lastLine, lastLine.getTimeMS(), null == duration ? lastLine.getTimeMS() + 3000 : Math.max(duration.getDuration(), lastLine.getTimeMS() + 3000)));
        }
        return expanded;
    }

}
