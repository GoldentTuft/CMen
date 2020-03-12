package jp.ni.example.cmen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import kotlin.math.round
import kotlin.math.sqrt


class Scale(val origin: Float, val to: Float) {
    fun adjust(x: Float): Float {
        return x * (origin /to)
    }
}


class CMenView : View {
    private var paint: Paint = Paint()
    private var metalPaint: Paint = Paint()
    private var toolStartPaint: Paint = Paint()
    private var toolEndPaint: Paint = Paint()
    private var originPointPaint: Paint = Paint()


    var chamferSize = 10f
    var toolDiameter = 20f
    var clearance = 5f
    var originalPoint = 0 // マジックナンバー良くないと思うけど。0:右上, 1:右下

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        paint.color = Color.argb(255, 255, 0, 255)
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE

        metalPaint.color = Color.argb(255, 0, 0, 0)
        metalPaint.strokeWidth = 4f
        metalPaint.style = Paint.Style.STROKE

        toolStartPaint.color = Color.argb(255, 0, 190, 190)
        toolStartPaint.strokeWidth = 4f
        toolStartPaint.style = Paint.Style.STROKE

        toolEndPaint.color = Color.argb(255, 190,  0, 190)
        toolEndPaint.strokeWidth = 4f
        toolEndPaint.style = Paint.Style.STROKE

        originPointPaint.color = Color.argb(255, 0,  0, 190)
        originPointPaint.strokeWidth = 4f
        originPointPaint.style = Paint.Style.STROKE


    }

    private fun toLinesFromPoints(vararg points: Float) : FloatArray? {
        var res = floatArrayOf()

        if (points.size < 4) return null

        for (i in 3..(points.size - 1) step 2) {
                res += points[i-3]
                res += points[i-2]
                res += points[i-1]
                res += points[i]
        }
        return res
    }
    
    private fun drawOriginalPoint(x: Float, y: Float, scale: Scale, paint: Paint, canvas: Canvas) {
        val originPointLines = toLinesFromPoints(
            x-scale.adjust(1f), y,
            x+scale.adjust(1f), y,
            x, y,
            x, y-scale.adjust(1f),
            x, y+scale.adjust(1f))
        if (originPointLines != null)
            canvas.drawLines(originPointLines, paint)
        canvas.drawCircle(x, y, scale.adjust(1f), paint)
    }

    private fun drawTool(ox: Float, oy: Float, x: Float, y: Float, scale: Scale, paint: Paint, canvas: Canvas) {
        canvas.drawCircle(
            ox + scale.adjust(x),
            oy + scale.adjust(y),
            scale.adjust(toolDiameter/2f),
            paint)
    }

    private fun drawPoint(dx: Float, dy: Float,line: Float, text: String, paint: Paint, canvas: Canvas) {
        val tempTextSize = paint.textSize
        val tempStyle = paint.style
        val ts = 80f

        paint.textSize = ts
        paint.style = Paint.Style.FILL
        canvas.drawText(text, dx, dy+((ts+ts*0.1f)*line), paint)
        paint.style = tempStyle
        paint.textSize = tempTextSize
    }

    private fun drawSummary(canvas: Canvas) {
        var paint = Paint()

        paint.color = Color.argb(255, 0, 0, 0)
        paint.style = Paint.Style.FILL
        paint.textSize = 40f

        canvas.drawText("C${chamferSize}, 工具経${toolDiameter}, 逃げ(clearance)${clearance}",
            10f, 40f+10f, paint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        var width = canvas.getWidth().toFloat()
        var height = canvas.getHeight().toFloat()

        val maxWidth = width / 2f
        val maxHeight = height / 3f

        val scale = Scale(maxWidth, 50F)
        val scaledChamferSize = scale.adjust(chamferSize)

        fun drawRightTop() {
           val ox = maxWidth
           val oy = maxHeight
           val left = 0f
           val bottom = maxHeight * 2f

           val metalLines = toLinesFromPoints(
               left, oy,
               ox - scaledChamferSize, oy,
               ox, oy + scaledChamferSize,
               ox, bottom
           )
           if (metalLines != null)
               canvas.drawLines(metalLines, metalPaint)

           // 原点描画
           drawOriginalPoint(ox, oy, scale, originPointPaint, canvas)

           // ツール描画
           val xOffSet = (toolDiameter/2f + clearance + chamferSize) - (toolDiameter/2f)*(sqrt(2f))
           val yOffSet = toolDiameter/2f + clearance
           drawTool(ox, oy, -xOffSet, -yOffSet, scale, toolStartPaint, canvas)
           drawTool(ox, oy, yOffSet, xOffSet, scale, toolEndPaint, canvas)

            // 始点座標描画
            drawPoint(0f,  bottom+20f, 1f,
                "P1: X-${round(xOffSet*1000f)/1000f}Y${round(yOffSet*1000)/1000f}",
                toolStartPaint, canvas)
            // 終点座標描画
            drawPoint(0f,  bottom+20f, 2f,
                "P2: X${round(yOffSet*1000f)/1000f}Y-${round(xOffSet*1000)/1000f}",
                toolEndPaint, canvas)
        }

        fun drawRightBottom() {
            val ox = maxWidth
            val oy = maxHeight * 2f
            val left = 0f
            val top = maxHeight

            val metalLines = toLinesFromPoints(
                left, oy,
                ox - scaledChamferSize, oy,
                ox, oy - scaledChamferSize,
                ox, top
            )
            if (metalLines != null)
                canvas.drawLines(metalLines, metalPaint)

            // 原点描画
            drawOriginalPoint(ox, oy, scale, originPointPaint, canvas)

            // ツール描画
            val xOffSet = toolDiameter/2f + clearance
            val yOffSet = (toolDiameter/2f + clearance + chamferSize) - (toolDiameter/2f)*(sqrt(2f))
            drawTool(ox, oy, xOffSet, -yOffSet, scale, toolStartPaint, canvas)
            drawTool(ox, oy, -yOffSet, xOffSet, scale, toolEndPaint, canvas)

            // 始点座標描画
            drawPoint(0f,  top-20f, -1f,
                "P1: X${round(xOffSet*1000f)/1000f}Y${round(yOffSet*1000)/1000f}",
                toolStartPaint, canvas)
            // 終点座標描画
            drawPoint(0f,  top-20f, 0f,
                "P2: X-${round(yOffSet*1000f)/1000f}-Y${round(xOffSet*1000)/1000f}",
                toolEndPaint, canvas)
        }


        // アウトライン
//        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
//        canvas.drawLine(width, maxHeight, width-200f, maxHeight, paint)
//        canvas.drawLine(width, maxHeight*2, width-200f, maxHeight*2, paint)

        drawSummary(canvas)
        if (originalPoint == MyModel.ORIGINAL.POINT.RIGHT_TOP.id) {
            drawRightTop()
        } else if (originalPoint == MyModel.ORIGINAL.POINT.RIGHT_BOTTOM.id) {
            drawRightBottom()
        }
    }


}