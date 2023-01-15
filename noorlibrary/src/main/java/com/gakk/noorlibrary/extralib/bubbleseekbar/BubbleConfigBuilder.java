package com.gakk.noorlibrary.extralib.bubbleseekbar;


import androidx.annotation.ColorInt;


/**
 * config BubbleSeekBar's attributes
 * <p/>
 * Created by woxingxiao on 2017-03-14.
 */
public class BubbleConfigBuilder {

    float min;
    float max;
    float progress;
    boolean floatType;
    int trackSize;
    int secondTrackSize;
    int thumbRadius;
    int thumbRadiusOnDragging;
    int trackColor;
    int secondTrackColor;
    int thumbColor;
    int sectionCount;
    boolean showSectionMark;
    boolean autoAdjustSectionMark;
    boolean showSectionText;
    int sectionTextSize;
    int sectionTextColor;
    int sectionTextPosition;
    int sectionTextInterval;
    boolean showThumbText;
    int thumbTextSize;
    int thumbTextColor;
    boolean showProgressInFloat;
    long animDuration;
    boolean touchToSeek;
    boolean seekStepSection;
    boolean seekBySection;
    int bubbleColor;
    int bubbleTextSize;
    int bubbleTextColor;
    boolean alwaysShowBubble;
    long alwaysShowBubbleDelay;
    boolean hideBubble;
    boolean rtl;

    private BubbleSeekBar mBubbleSeekBar;

    BubbleConfigBuilder(BubbleSeekBar bubbleSeekBar) {
        mBubbleSeekBar = bubbleSeekBar;
    }

    public void build() {
        mBubbleSeekBar.config(this);
    }

    public BubbleConfigBuilder min(float min) {
        this.min = min;
        this.progress = min;
        return this;
    }

    public BubbleConfigBuilder progress(float progress) {
        this.progress = progress;
        return this;
    }



    public BubbleConfigBuilder thumbTextColor(@ColorInt int color) {
        thumbTextColor = color;
        return this;
    }

    public float getMin() {
        return min;
    }

    public float getProgress() {
        return progress;
    }


}
