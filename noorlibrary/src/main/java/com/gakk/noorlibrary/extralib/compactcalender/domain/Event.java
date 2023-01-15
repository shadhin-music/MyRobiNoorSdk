package com.gakk.noorlibrary.extralib.compactcalender.domain;

import android.graphics.Color;

import androidx.annotation.Nullable;

public class Event {

    private int color;
    private long timeInMillis;
    private Object data;
    private Boolean isChecked = false;

    static final int eventGreen = Color.argb(255, 0, 153, 102);
    static final int eventGray = Color.argb(31, 0, 0, 0);

    public Event(long timeInMillis) {
        this.color = eventGray;
        this.timeInMillis = timeInMillis;
    }

    public Event(long timeInMillis, Object data) {
        this.color = eventGray;
        this.timeInMillis = timeInMillis;
        this.data = data;
    }

    public int getColor() {
        return color;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        if(checked){
            color = eventGreen;
        }else{
            color = eventGray;
        }
        isChecked = checked;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Nullable
    public Object getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (color != event.color) return false;
        if (timeInMillis != event.timeInMillis) return false;
        if (data != null ? !data.equals(event.data) : event.data != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + (int) (timeInMillis ^ (timeInMillis >>> 32));
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "color=" + color +
                ", timeInMillis=" + timeInMillis +
                ", data=" + data +
                '}';
    }
}
