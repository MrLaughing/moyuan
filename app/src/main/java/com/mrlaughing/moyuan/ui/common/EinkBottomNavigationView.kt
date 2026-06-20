package com.mrlaughing.moyuan.ui.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView

class EinkBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {
    init {
        itemTextColor = android.content.res.ColorStateList.valueOf(Color.BLACK)
        itemIconTintList = android.content.res.ColorStateList.valueOf(Color.BLACK)
        setBackgroundColor(Color.WHITE)
    }
}
