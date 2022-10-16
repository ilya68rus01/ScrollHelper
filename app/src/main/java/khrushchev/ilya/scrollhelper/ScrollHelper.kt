package khrushchev.ilya.scrollhelper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class ScrollHelper @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyle: Int = 0
) : View(context, attrs) {

    var scrollIcon: Drawable? = null

    private var _scrollIconHeight: Int? = null
    private var _scrollIconWidth: Int? = null

    private val scrollIconHeight
        get() = _scrollIconHeight ?: throw IllegalStateException("ScrollIconHeight not calculated")
    private val scrollIconWidth
        get() = _scrollIconWidth ?: throw IllegalStateException("ScrollIconWidth not calculated")

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 35f
        isAntiAlias = true
        strokeWidth = 2f
        style = Paint.Style.FILL
    }

    private var dY = 0F
    private var currPos = 0
    private var actionBarHeight by Delegates.notNull<Int>()

    private var scrollHelperListener: ScrollHelperListener? = null

    init {
        init(attrs, defStyle)
        val tv = TypedValue()
        if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
    }

    fun setOnScrollHelperListener(helperListener: ScrollHelperListener) {
        this.scrollHelperListener = helperListener
    }

    fun removeOnScrollHelperListener(){
        this.scrollHelperListener = null
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ScrollHelper, defStyle, 0
        )

        if (a.hasValue(R.styleable.ScrollHelper_scrollIcon)) {
            scrollIcon = a.getDrawable(
                R.styleable.ScrollHelper_scrollIcon
            )
            scrollIcon?.callback = this
        }
        scrollIcon?.let {
            if (it is BitmapDrawable) {
                it.bitmap.let {
                    _scrollIconHeight = it.height
                    _scrollIconWidth = it.width
                }
            } else {
                it.toBitmap().let {
                    _scrollIconHeight = it.height
                    _scrollIconWidth = it.width
                }
            }
        }
        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val scrollIconTopPos = 0 + dY.toInt()
        val scrollIconBottomPos = scrollIconHeight + dY.toInt()

        scrollIcon?.let {
            it.setBounds(
                contentWidth - scrollIconWidth,
                scrollIconTopPos,
                contentWidth,
                scrollIconBottomPos
            )
            it.draw(canvas)
        }

        if (isPressed) {
            val textX = contentWidth - scrollIconWidth - textPaint.textSize / 2
            val textY = scrollIconTopPos + scrollIconHeight / 2 + textPaint.textSize / 2
            canvas.drawText(scrollHelperListener?.getCharAtPosition(currPos).toString(), textX, textY, textPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("MyLogs", event.toString())
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                true
            }
            MotionEvent.ACTION_UP -> {
                isPressed = false
                false
            }
            MotionEvent.ACTION_MOVE -> {
                computeScrollProgress(event)
            }
            else -> {
                isPressed = false
                false
            }
        }
    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        invalidate()
    }

    private fun computeScrollProgress(event: MotionEvent): Boolean {
        val screenHeight = height - paddingBottom - actionBarHeight
        if (event.y < 0 || event.y > screenHeight) return false
        dY = event.y
        scrollHelperListener?.let {
            currPos = (dY * it.getItemCount() / screenHeight).toInt()
            it.scrollToPosition(currPos)
        }
        invalidate()
        return true
    }
}