package com.gakk.noorlibrary.model

import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

@Keep
class UpCommingPrayer : BaseObservable() {
    @get: Bindable
    var currentWaqtStartingTime: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentWaqtStartingTime)
        }

    @get: Bindable
    var nextWaqtName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nextWaqtName)
        }
    @get: Bindable
    var currentWaqtName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentWaqtName)
        }
    @get: Bindable
    var nextWaqtNameTracker: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nextWaqtNameTracker)
        }

    @get: Bindable
    var nextWaqtTime: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.nextWaqtTime)
        }

    @get: Bindable
    var timeLeft: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.timeLeft)
        }
}