package com.mrlaughing.moyuan.ui.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.cardview.widget.CardView

class EinkCardView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
  init {
    setCardBackgroundColor(Color.WHITE)
    cardElevation = 0f
    radius = 0f
    setContentPadding(16.dpToPx(context), 12.dpToPx(context), 16.dpToPx(context), 12.dpToPx(context))
  }
  private fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()
}
