package com.yi.lrcview;

import android.support.annotation.NonNull;

import com.dxm.lrc.LyricItem;

/**
 * Created by ants on 21/12/2016.
 */

public class LyricItems {
    public static abstract class AbsVisitor1<P, R> implements LyricItem.Visitor1<P, R> {
        @Override public R visitTitle(LyricItem.Title title, P p) {
            return visitDefault(title, p);
        }

        @Override public R visitArtist(LyricItem.Artist artist, P p) {
            return visitDefault(artist, p);
        }

        @Override public R visitAlbum(LyricItem.Album album, P p) {
            return visitDefault(album, p);
        }

        @Override public R visitDuration(LyricItem.Duration duration, P p) {
            return visitDefault(duration, p);
        }

        @Override public R visitAuthor(LyricItem.Author author, P p) {
            return visitDefault(author, p);
        }

        @Override public R visitLine(LyricItem.Line line, P p) {
            return visitDefault(line, p);
        }

        @Override public R visitOffset(LyricItem.Offset offset, P p) {
            return visitDefault(offset, p);
        }

        abstract R visitDefault(@NonNull LyricItem item, P p);
    }

}
