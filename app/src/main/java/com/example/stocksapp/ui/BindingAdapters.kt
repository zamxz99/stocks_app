package com.example.stocksapp.ui

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("android:text")
fun TextView.setText(@StringRes resId: Int?) {
    resId ?: return
    if (resId == ResourcesCompat.ID_NULL) {
        text = ""
    } else {
        setText(resId)
    }
}