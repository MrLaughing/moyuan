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

    val padding = 20f
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

      paint.pathEffect = null
      if (isRuined) {
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        paint.alpha = 50
      } else {
        paint.alpha = if (isWithered) 80 else 255
      }

      canvas.drawCircle(cx, cy, radius, paint)

      paint.pathEffect = null
      paint.textSize = 24f
      paint.textAlign = Paint.Align.CENTER

      val label = if (isRuined) {
        WitherStage.RUIN.displayName
      } else {
        plant.level
      }
      canvas.drawText(label, cx, cy + radius + 30, paint)

      paint.textSize = 14f
      paint.alpha = if (isRuined) 50 else 150
      canvas.drawText(stage.displayName, cx, cy + radius + 50, paint)

      paint.alpha = 255
    }
  }
}
