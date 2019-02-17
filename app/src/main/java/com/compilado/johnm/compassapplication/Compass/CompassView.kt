package com.compilado.johnm.compassapplication.Compass

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.DecimalFormat
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.compilado.johnm.compassapplication.Compass.CompassConstans.DATA_PADDING
import com.compilado.johnm.compassapplication.Compass.CompassConstans.DEGREE
import com.compilado.johnm.compassapplication.Compass.CompassConstans.NEEDLE_PADDING
import com.compilado.johnm.compassapplication.Compass.CompassConstans.TEXT_SIZE_FACTOR
import com.compilado.johnm.compassapplication.R
import kotlinx.android.synthetic.main.layout_compass_view.view.*

class CompassView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), SensorEventListener, ViewTreeObserver.OnGlobalLayoutListener {

    private var mCurrentDegree = 0f

    private var mDegreesStep = 0

    init {
        inflate(context, R.layout.layout_compass_view, this)
        val mSensorManager = getContext().getSystemService(SENSOR_SERVICE) as SensorManager
        mSensorManager.registerListener(
            this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )

        updateLayout()
    }

    private fun updateLayout() {
        compass_view.setDegreesStep(mDegreesStep)
        compass_view.getViewTreeObserver().addOnGlobalLayoutListener {
            compass_view.getViewTreeObserver().removeOnGlobalLayoutListener(this)
            val width = compass_view.getMeasuredWidth()
            val needlePadding = (width * NEEDLE_PADDING).toInt()
            compass_view.setPadding(needlePadding, needlePadding, needlePadding, needlePadding)

            val dataPaddingTop = (width * DATA_PADDING).toInt()
            data_layout.setPadding(0, dataPaddingTop, 0, 0)

            val degreeTextSize = width * TEXT_SIZE_FACTOR
            tv_degree.setTextSize(degreeTextSize)
        }
    }


    private fun updateTextDirection(degree: Float) {
        val deg = 360 + degree
        val decimalFormat = DecimalFormat("###.#")
        var value: String
        if (deg > 0 && deg <= 90) {
            value = String.format("%s%s NE", (decimalFormat.format(-degree)), DEGREE)
        } else if (deg > 90 && deg <= 180) {
            value = String.format("%s%s ES", (decimalFormat.format(-degree)), DEGREE)
        } else if (deg > 180 && deg <= 270) {
            value = String.format("%s%s SW", (decimalFormat.format(-degree)), DEGREE)
        } else {
            value = String.format("%s%s WN", (decimalFormat.format(-degree)), DEGREE)
        }
        tv_degree.setText(value)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (widthMeasureSpec < heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        } else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


    override fun onSensorChanged(p0: SensorEvent?) {

        val degree = Math.round(p0!!.values[0])

        val rotateAnimation =
            RotateAnimation(
                mCurrentDegree,
                (-degree).toFloat(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            )
        rotateAnimation.setDuration(210)
        rotateAnimation.setFillAfter(true)
        iv_needle.startAnimation(rotateAnimation)

        updateTextDirection(mCurrentDegree)

        mCurrentDegree = (-degree).toFloat()
    }

    override fun onGlobalLayout() {}

}



