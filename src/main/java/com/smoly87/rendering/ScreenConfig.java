package com.smoly87.rendering;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ScreenConfig {
    public abstract double xMin();
    public abstract double xMax();
    public abstract double yMin();
    public abstract double yMax();
    public abstract int screenWidth();
    public abstract int screenHeight();
    public static Builder builder() {
        return new AutoValue_ScreenConfig.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setxMin(double value);
        public abstract Builder setxMax(double value);
        public abstract Builder setyMax(double value);
        public abstract Builder setyMin(double value);
        public abstract Builder setScreenWidth(int value);
        public abstract Builder setScreenHeight(int value);
        public abstract ScreenConfig build();
    }
}
