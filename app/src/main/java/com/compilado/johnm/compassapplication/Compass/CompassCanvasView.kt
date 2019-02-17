package com.compilado.johnm.compassapplication.Compass

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.support.constraint.ConstraintLayout
import android.text.TextPaint
import android.util.AttributeSet
import com.compilado.johnm.compassapplication.Compass.CompassConstans.DEFAULT_MINIMIZED_ALPHA
import com.compilado.johnm.compassapplication.Compass.CompassConstans.EAST_INDEX
import com.compilado.johnm.compassapplication.Compass.CompassConstans.NORTH_INDEX
import com.compilado.johnm.compassapplication.Compass.CompassConstans.SOUTH_INDEX
import com.compilado.johnm.compassapplication.Compass.CompassConstans.WEST_INDEX


class CompassCanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mWidth = 0
    private var mCenterX = 0
    private var mCenterY = 0

    private var mDegreesStep = CompassConstans.DEFAULT_DEGREES_STEP

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (widthMeasureSpec < heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        } else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mWidth = if (measuredHeight > measuredWidth) measuredWidth else measuredHeight

        mCenterX = mWidth / 2
        mCenterY = mWidth / 2

        drawCompassSkeleton(canvas)
        drawOuterCircle(canvas)

    }

    private fun drawOuterCircle(canvas: Canvas?) {
        val mStrokeWidth = (mWidth * 0.01f)
        val paintOutCircle = Paint()
        paintOutCircle.style = Paint.Style.STROKE
        paintOutCircle.strokeWidth = mStrokeWidth
        paintOutCircle.color = CompassConstans.DEFAULT_BORDER_COLOR

        val radius = ((mWidth / 2) - (mStrokeWidth / 2))

        val rectF = RectF()
        rectF.set(mCenterX - radius, mCenterY - radius, mCenterX + radius, mCenterY + radius)

        if (CompassConstans.DEFAULT_SHOW_BORDER)
            canvas?.drawArc(rectF, 0f, 360f, false, paintOutCircle)

    }

    private fun drawCompassSkeleton(canvas: Canvas?) {
        val paintCompassSkeleto = Paint()
        paintCompassSkeleto.style = Paint.Style.FILL_AND_STROKE
        paintCompassSkeleto.strokeCap = Paint.Cap.ROUND
        paintCompassSkeleto.color = CompassConstans.DEFAULT_BORDER_COLOR

        val textPaint = TextPaint()
        textPaint.textSize = (mWidth * 0.06f)
        textPaint.color = CompassConstans.DEFAULT_ORIENTATION_LABELS_COLOR

        val rect = Rect()
        val rPadded = mCenterX - (mWidth * 0.01)

        var i = 0
        while (i <= 360) {
            var rEnd: Int
            var rText: Int
            if ((i % 90) == 0) {
                rEnd = (mCenterX - (mWidth * 0.08f)).toInt()
                rText = (mCenterX - (mWidth * 0.15f)).toInt()
                paintCompassSkeleto.color = CompassConstans.DEGREES_COLOR
                paintCompassSkeleto.strokeWidth = (mWidth * 0.02f)

                showOrientationLabel(canvas, textPaint, rect, i, rText)
            } else if ((i % 45) == 0) {
                rEnd = (mCenterX - (mWidth * 0.06f)).toInt()
                paintCompassSkeleto.color = CompassConstans.DEGREES_COLOR
                paintCompassSkeleto.strokeWidth = (mWidth * 0.02f)

            } else {
                rEnd = (mCenterX - (mWidth * 0.04f)).toInt()
                paintCompassSkeleto.color = CompassConstans.DEGREES_COLOR
                paintCompassSkeleto.strokeWidth = (mWidth * 0.015f)
                paintCompassSkeleto.alpha = DEFAULT_MINIMIZED_ALPHA
            }

            val dregrees = i.toDouble()
            val startX = (mCenterX + rPadded * Math.cos(Math.toRadians(dregrees))).toFloat()
            val startY = (mCenterX - rPadded * Math.sin(Math.toRadians(dregrees))).toFloat()
            val stopX = (mCenterX + rEnd * Math.cos(Math.toRadians(dregrees))).toFloat()
            val stopY = (mCenterX - rEnd * Math.sin(Math.toRadians(dregrees))).toFloat()


            canvas?.drawLine(startX, startY, stopX, stopY, paintCompassSkeleto)

            i += CompassConstans.DEFAULT_DEGREES_STEP
        }

    }

    private fun showOrientationLabel(canvas: Canvas?, textPaint: TextPaint, rect: Rect, i: Int, rText: Int) {
        if ( CompassConstans.SHOW_ORIENTATION_LABEL) {
            val dregrees = i.toDouble()
            val textX = (mCenterX + rText * Math.cos(Math.toRadians(dregrees)))
            val textY = (mCenterX - rText * Math.sin(Math.toRadians(dregrees)))

            var direction = EAST_INDEX
            if (i == 0) {
                direction = EAST_INDEX
            } else if (i == 90) {
                direction = NORTH_INDEX
            } else if (i == 180) {
                direction = WEST_INDEX
            } else if (i == 270) {
                direction = SOUTH_INDEX
            }

            textPaint.getTextBounds(direction, 0, 1, rect)
            canvas?.drawText(
                direction, (textX - rect.width() / 2).toFloat(),
                (textY + rect.height() / 2).toFloat(), textPaint
            )
        }
    }

    fun setDegreesStep(degreesStep: Int) {
        if (degreesStep < 360 || degreesStep > 0 || 360 % degreesStep == 0) {
        }
        mDegreesStep = degreesStep
        invalidate()
    }

}

