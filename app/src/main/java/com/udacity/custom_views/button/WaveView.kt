package com.udacity.custom_views.button

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import com.udacity.R
import kotlin.properties.Delegates


class WaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {
    companion object {
        const val DEFAULT_ANIMATION_DURATION = 1200L
        const val IMMEDIATE_ANIMATION_DURATION = 300L
    }

    private var state: ButtonState by Delegates.observable(ButtonState.Idle) { _, _, newValue ->
        when (newValue) {
            ButtonState.Idle -> buttonIdle()
            ButtonState.Clicked -> buttonClicked()
            ButtonState.Loading -> buttonLoading()
            ButtonState.Completed -> buttonCompleted()
        }
    }

    private var mWidth = 0
    private var mHeight = 0
    private var path: Path? = null
    private var cycle = 500
    private var waveHeight = 15
    private var startPoint = Point()
    private var progress = 0
    private var translateX = 40
    private var shouldAnimate = true

    private lateinit var idleText: String
    private lateinit var loadingText: String
    private lateinit var loadedText: String

    private var idleTextColor = Color.parseColor("#000000")
    private var loadingTextColor = Color.parseColor("#FFFFFF")
    private var loadedTextColor = Color.parseColor("#FFFFFF")

    private var idleBackgroundColor = Color.parseColor("#FFFFFF")
    private var loadingBackgroundColor = Color.parseColor("#1a7aff")
    private var loadedBackgroundColor = Color.parseColor("#1a7aff")

    private var idleBorderColor = Color.parseColor("#1a7aff")
    private var loadingBorderColor = Color.parseColor("#1a7aff")
    private var loadedBorderColor = Color.parseColor("#1a7aff")

    private lateinit var valueAnimator: ValueAnimator

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 0f
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = dip2px(context, 5f).toFloat()
        strokeWidth = 0f
        textSize = 60f
    }

    init {
        path = Path()

        context.withStyledAttributes(attrs, R.styleable.WaveButton) {
            idleText = getString(R.styleable.WaveButton_idleText) ?: "Download"
            loadingText = getString(R.styleable.WaveButton_loadingText) ?: "Downloading"
            loadedText = getString(R.styleable.WaveButton_loadedText) ?: "Downloaded"

            idleTextColor =
                getColor(R.styleable.WaveButton_idleTextColor, Color.parseColor("#000000"))
            loadingTextColor =
                getColor(R.styleable.WaveButton_loadingTextColor, Color.parseColor("#FFFFFF"))
            loadedTextColor =
                getColor(R.styleable.WaveButton_loadedTextColor, Color.parseColor("#FFFFFF"))

            idleBackgroundColor = getColor(
                R.styleable.WaveButton_idleBackgroundColor,
                Color.parseColor("#FFFFFF")
            )
            loadingBackgroundColor = getColor(
                R.styleable.WaveButton_loadingBackgroundColor,
                Color.parseColor("#1a7aff")
            )
            loadedBackgroundColor = getColor(
                R.styleable.WaveButton_loadedBackgroundColor,
                Color.parseColor("#1a7aff")
            )

            idleBorderColor =
                getColor(R.styleable.WaveButton_idleBorderColor, Color.parseColor("#1a7aff"))
            loadingBorderColor =
                getColor(R.styleable.WaveButton_loadingBorderColor, Color.parseColor("#1a7aff"))
            loadedBorderColor =
                getColor(R.styleable.WaveButton_loadedBorderColor, Color.parseColor("#1a7aff"))
        }
    }

    private fun buttonClicked() {
        // Reset button
        reset()

        // Disable the button
        isEnabled = false

        invalidate()

        state = ButtonState.Loading
    }

    private fun buttonIdle() {
        reset()

        invalidate()
    }

    private fun buttonLoading() {
        // Start the animation
        valueAnimator.start()
    }

    private fun buttonCompleted() {
        reset()

        progress = 100

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (state == ButtonState.Loading) {
            backgroundPaint.color = loadingBackgroundColor

            startPoint.y = (mHeight - progress / 100.0 * mHeight).toInt()

            path!!.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
            var j = 1

            for (i in 1..8) {
                if (i % 2 == 0) {
                    path!!.quadTo(
                        (startPoint.x + cycle * j).toFloat(),
                        (startPoint.y + waveHeight).toFloat(),
                        (
                                startPoint.x + cycle * 2 * i).toFloat(),
                        startPoint.y.toFloat()
                    )
                } else {
                    path!!.quadTo(
                        (startPoint.x + cycle * j).toFloat(),
                        (startPoint.y - waveHeight).toFloat(),
                        (
                                startPoint.x + cycle * 2 * i).toFloat(),
                        startPoint.y.toFloat()
                    )
                }
                j += 2
            }

            path!!.lineTo(mWidth.toFloat(), mHeight.toFloat())
            path!!.lineTo(startPoint.x.toFloat(), mHeight.toFloat())
            path!!.lineTo(startPoint.x.toFloat(), startPoint.y.toFloat())
            path!!.close()
            canvas.drawPath(path!!, backgroundPaint)

            if (startPoint.x + translateX >= 0) {
                startPoint.x = -cycle * 4
            }

            startPoint.x += translateX

            path!!.reset()
            if (shouldAnimate) {
                postInvalidateDelayed(0)
            }
        } else if (state == ButtonState.Completed) {
            backgroundPaint.style = Paint.Style.FILL
            backgroundPaint.color = loadedBackgroundColor
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        }

        //Border
        borderPaint.color = when (state) {
            ButtonState.Idle -> idleBorderColor
            ButtonState.Clicked, ButtonState.Loading -> loadingBorderColor
            ButtonState.Completed -> loadedBorderColor
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), borderPaint)

        //Text
        val text = when (state) {
            ButtonState.Idle -> {
                textPaint.color = idleTextColor
                idleText
            }
            ButtonState.Clicked, ButtonState.Loading -> {
                textPaint.color = loadingTextColor
                loadingText
            }
            ButtonState.Completed -> {
                textPaint.color = loadedTextColor
                loadedText
            }
        }

        drawText(canvas, textPaint, text)

        createAnimator()
    }

    fun startLoading() {
        state = ButtonState.Clicked
    }

    private fun drawText(canvas: Canvas, paint: Paint?, text: String) {

        val targetRect = Rect(0, 0, mWidth, mHeight)
        val fontMetrics = paint!!.fontMetricsInt
        val baseline =
            (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = height / 3f
        canvas.drawText(text, targetRect.centerX().toFloat(), baseline.toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mWidth = getViewSize(400, widthMeasureSpec)
        mHeight = getViewSize(400, heightMeasureSpec)
        startPoint.set(-cycle * 3, mHeight / 2)
    }

    private fun getViewSize(defaultSize: Int, measureSpec: Int): Int {
        var viewSize = defaultSize

        val mode = MeasureSpec.getMode(measureSpec)

        val size = MeasureSpec.getSize(measureSpec)
        when (mode) {
            MeasureSpec.UNSPECIFIED -> viewSize = defaultSize
            MeasureSpec.AT_MOST ->
                viewSize = size
            MeasureSpec.EXACTLY -> viewSize = size
        }
        return viewSize
    }

    private fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun setCompleted() {
        state = ButtonState.Completed
    }

    fun resetButton() {
        state = ButtonState.Idle
    }

    private fun reset() {
        progress = 0
        path!!.reset()
        if (this::valueAnimator.isInitialized)
            valueAnimator.cancel()
        isEnabled = true
    }

    private fun createAnimator() {
        if (!this::valueAnimator.isInitialized) {
            ValueAnimator.ofFloat(0f, mHeight.toFloat()).apply {
                duration = DEFAULT_ANIMATION_DURATION
                interpolator = LinearInterpolator()
                addUpdateListener {
                    if (state == ButtonState.Completed) {
                        duration = IMMEDIATE_ANIMATION_DURATION
                    } else {
                        progress = (it.animatedValue as Float).toInt()
                    }

                    invalidate()
                }
            }.also {
                valueAnimator = it
            }
        }
    }
}