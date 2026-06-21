package com.mrlaughing.moyuan.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.mrlaughing.moyuan.R
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.data.model.WitherStage
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sqrt

class GardenRendererView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var plants: List<PlantStateEntity> = emptyList()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }
    private val bounds = mutableListOf<RectF>()
    private var onPlantClickListener: ((PlantStateEntity) -> Unit)? = null

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val idx = hitTest(e.x, e.y)
            if (idx >= 0 && idx < plants.size) {
                onPlantClickListener?.invoke(plants[idx])
                return true
            }
            return false
        }
    })

    fun setPlants(newPlants: List<PlantStateEntity>) {
        plants = newPlants
        invalidate()
    }

    fun setOnPlantClickListener(listener: (PlantStateEntity) -> Unit) {
        onPlantClickListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun hitTest(x: Float, y: Float): Int {
        for (i in bounds.indices) {
            if (bounds[i].contains(x, y)) return i
        }
        return -1
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (plants.isEmpty()) return
        val cols = ceil(sqrt(plants.size.toDouble())).toInt().coerceAtLeast(2)
        val rows = ceil(plants.size.toDouble() / cols).toInt()
        if (width == 0 || height == 0) return

        val padding = resources.getDimension(R.dimen.garden_padding)
        val cellW = (width - padding * 2) / cols
        val cellH = (height - padding * 2) / rows
        val radius = min(cellW, cellH) / 3f

        bounds.clear()

        plants.forEachIndexed { i, plant ->
            val r = i / cols
            val c = i % cols
            val cx = padding + c * cellW + cellW / 2f
            val cy = padding + r * cellH + cellH / 2f

            bounds.add(RectF(cx - radius, cy - radius, cx + radius, cy + radius))

            val stage = WitherStage.fromString(plant.witherStage)
            val isRuined = stage == WitherStage.RUIN
            val isWithered = stage != WitherStage.NONE && !isRuined

            // 绘制圆圈
            paint.pathEffect = if (isRuined) DashPathEffect(floatArrayOf(10f, 10f), 0f) else null
            paint.alpha = when {
                isRuined -> 50
                isWithered -> 80
                else -> 255
            }
            canvas.drawCircle(cx, cy, radius, paint)
            paint.alpha = 255
            paint.pathEffect = null

            // 绘制等级标签
            val label = if (isRuined) {
                WitherStage.RUIN.displayName
            } else {
                plant.level
            }
            textPaint.textSize = resources.getDimension(R.dimen.garden_level_text_size)
            textPaint.alpha = 255
            canvas.drawText(label, cx, cy + radius + resources.getDimension(R.dimen.garden_label_offset), textPaint)

            // 绘制状态标签
            textPaint.textSize = resources.getDimension(R.dimen.garden_label_text_size)
            textPaint.alpha = if (isRuined) 50 else 150
            canvas.drawText(stage.displayName, cx, cy + radius + resources.getDimension(R.dimen.garden_stage_offset), textPaint)
            textPaint.alpha = 255
        }
    }
}
