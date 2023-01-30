package com.udacity

import android.animation.*
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingWidth = 0f
    private val textSize = resources.getDimension(R.dimen.default_text_size)
    private val buttonName = resources.getString(R.string.button_name)
    private val buttonLoading = resources.getString(R.string.button_loading)
    private var button = buttonName
    private val view = this
    private var endAngle = 0f

    private var background = 0
    private var loadingColor = 0
    private var textColor = 0
    private var circleColor = 0

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
    }

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
        style = Paint.Style.FILL
    }


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            background = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(background)

        paint.color = loadingColor
        canvas.drawRect(0F, 0F, loadingWidth, heightSize.toFloat(), paint)

        paint.color = textColor
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            button,
            widthSize.toFloat() / 2, heightSize.toFloat() / 8 * 5, paint
        )

        paint.color = circleColor
        canvas.drawArc((widthSize / 10.0).toFloat(),
            (heightSize / 4.0).toFloat(),
            (widthSize / 10.0 + heightSize / 2.0).toFloat(),
            (heightSize * 3.0 / 4).toFloat(),
            0f,
            endAngle,
            true,
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {

        super.performClick()
        valueAnimator.setEvaluator(FloatEvaluator())
        valueAnimator.setFloatValues(0F, widthSize.toFloat())
        valueAnimator.addUpdateListener {
            loadingWidth = it.animatedValue as Float
            button = buttonLoading
            endAngle = 360f * it.animatedValue as Float / widthSize
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator) {
                loadingWidth = 0F
                button = buttonName
                endAngle = 0F
                invalidate()
                view.isEnabled = true
            }
        })
        valueAnimator.duration = 2000
        valueAnimator.start()

        return true
    }

}