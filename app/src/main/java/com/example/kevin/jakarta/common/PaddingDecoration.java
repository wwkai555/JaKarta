package com.example.kevin.jakarta.common;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by kevin on 16-12-17.
 */

public class PaddingDecoration extends RecyclerView.ItemDecoration {
    private int left;
    private int right;
    private int firstPadding;
    private int endPadding;

    public PaddingDecoration(int left, int right, int firstPadding, int endPadding) {
        this.left = left;
        this.right = right;
        this.firstPadding = firstPadding;
        this.endPadding = endPadding;
        Log.d("PaddingDecoration", "defaultPadding : " + firstPadding + "endPadding : " + endPadding);
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int adapterPos = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        int count = parent.getAdapter().getItemCount();
        outRect.left = adapterPos == 0 ? this.firstPadding : this.left;
        outRect.top = 0;
        outRect.right = adapterPos == count - 1 ? this.endPadding : this.right;
        outRect.bottom = 0;
    }
}
