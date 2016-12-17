package com.example.kevin.jakarta.dummy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kevin on 16-12-14.
 */

public class VideoInfo implements Parcelable{

    public VideoInfo(long duration) {
        this.duration = duration;
    }

    public long duration;

    protected VideoInfo(Parcel in) {
        duration = in.readLong();
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
    }
}
