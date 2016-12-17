package com.example.kevin.jakarta.online;

import com.google.auto.value.AutoValue;

/**
 * Created by kevin on 16-12-14.
 */

@AutoValue
public abstract class CategoryInfo {
    public abstract int id();

    public abstract String title();

    public static Builder builder() {
        return new AutoValue_CategoryInfo.Builder();
    }

    @AutoValue.Builder public static abstract class Builder {
        public abstract Builder id(int id);

        public abstract Builder title(String title);

        public abstract CategoryInfo build();
    }

}
