package com.mrlaughing.moyuan.ui.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ProgressBar

class EinkProgressBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = android.R.attr.progressBarStyleHorizontal
) : ProgressBar(context, attrs, defStyleAttr) {
  init {
    isIndeterminate = false
    progressTintList = android.content.res.ColorStateList.valueOf(Color.BLACK)
    progressBackgroundTintList = android.content.res.ColorStateList.valueOf(Color.LTGRAY)
  }
}
