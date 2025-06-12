package com.netimur.businesscard

import android.util.Log
import android.webkit.WebView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

typealias Position = Animatable<Float, AnimationVector1D>

@Composable
fun BlackWhiteVaporwaveScreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val outermostRadius = infiniteTransition.animateFloat(
        initialValue = 500F,
        targetValue = 650F,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val GradientBlack = listOf(
        Color(0xFF0A0A0A), // глубокий чёрный
        Color(0xFF141414), // угольный
        Color(0xFF1E1E1E), // графитовый
        Color(0xFF121212)  // стандартный Android dark
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = GradientBlack
                )
            )
    ) {
        val rotationX = remember {
            Animatable(0F)
        }

        val rotationY = remember {
            Animatable(0F)
        }

        val alpha = remember { Animatable(1f) }

        val coroutineScope = rememberCoroutineScope()
        val anim = remember {
            mutableStateOf("diamond")
        }
        var rotationJob = remember {
            mutableStateOf<Job?>(null)
        }
        GifWebView(
            anim = anim.value,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5F)
                .graphicsLayer {
                    this.rotationY = rotationY.value
                    this.rotationX = rotationX.value
                    this.alpha = alpha.value
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                        },
                        onDragEnd = { },
                        onDragCancel = {},
                        onDrag = onDrag@{ pointerInputChange, offset ->
                            val direction = when {
                                offset.x > 0 -> SwipeDirection.RIGHT
                                offset.x < 0 -> SwipeDirection.LEFT
                                offset.y > 0 -> SwipeDirection.DOWN
                                offset.y < 0 -> SwipeDirection.UP
                                else -> null
                            }

                            if (rotationJob.value?.isActive == true) {
                                return@onDrag
                            } else {
                                rotationJob.value = coroutineScope.launch {
                                    launch {
                                        delay(500L)
                                        val animation = anims.random()
                                        if (animation == "flashbacks") {
                                            anims.remove("flashbacks")
                                        }
                                        anim.value = animation
                                    }
                                    launch {
                                        alpha.animateTo(
                                            0.0f,
                                            animationSpec = tween(500)
                                        )
                                        alpha.animateTo(
                                            1f,
                                            animationSpec = tween(500)
                                        )
                                    }
                                    launch {
                                        val anim: AnimationSpec<Float> =
                                            tween(1000, easing = LinearOutSlowInEasing)
                                        when (direction) {
                                            SwipeDirection.UP -> {
                                                rotationX.animateTo(720F, animationSpec = anim)
                                            }

                                            SwipeDirection.DOWN -> {
                                                rotationX.animateTo(-720F, anim)
                                            }

                                            SwipeDirection.LEFT -> {
                                                rotationY.animateTo(-720F, anim)
                                            }

                                            SwipeDirection.RIGHT -> {
                                                rotationY.animateTo(720F, anim)
                                            }

                                            null -> {}
                                        }

                                        rotationY.snapTo(0f)
                                        rotationX.snapTo(0f)
                                    }
                                }
                            }

                        }
                    )
                }
        )
        val starCount = 200
        val stars = remember {
            List(starCount) {
                StaticStar(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    twinkleSeed = Random.nextFloat()
                )
            }
        }

        val infiniteTransition = rememberInfiniteTransition()
        val twinkleAnim = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {

            horizontalLines.forEach { line ->
                val path = Path().apply {
                    moveTo(
                        x = -100F,
                        y = center.y + line.yPadding
                    )
                    quadraticTo(
                        x1 = center.x,
                        y1 = center.y + line.quadraticYPadding,
                        x2 = size.width + 100F,
                        y2 = center.y + line.yPadding
                    )
                }

                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = line.width.toFloat())
                )
            }

            verticalLines.forEach { line ->
                val (xPaddingTop, xPaddingBottom) = when (line.direction) {
                    Direction.LEFT_TO_RIGHT -> line.xPaddingTop to line.xPaddingBottom
                    Direction.RIGHT_TO_LEFT -> -line.xPaddingTop to -line.xPaddingBottom
                }

                val path = Path().apply {
                    moveTo(
                        x = center.x + xPaddingTop,
                        y = center.y
                    )
                    lineTo(
                        x = center.x + xPaddingBottom,
                        y = size.height
                    )
                }
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            val yPadding = 100F

            runDraw(iAmDrawing = "Center lines") {
                val lineLength = 350.dp.toPx()
                val strokeWidthMain = 2.dp.toPx()

                val xStart = (size.width - lineLength) / 2
                val yLineY = center.y - yPadding
                val yStart = (size.height - lineLength) / 2 - yPadding


                drawLine(
                    color = Color.White,
                    start = Offset(x = xStart, y = yLineY),
                    end = Offset(x = xStart + lineLength - 200F, y = yLineY),
                    strokeWidth = strokeWidthMain,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = Color.White,
                    start = Offset(x = xStart + lineLength - 100F, y = yLineY),
                    end = Offset(x = xStart + lineLength, y = yLineY),
                    strokeWidth = strokeWidthMain,
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = Color.White,
                    start = Offset(x = center.x, y = yStart),
                    end = Offset(x = center.x, y = yStart + lineLength),
                    strokeWidth = strokeWidthMain,
                    cap = StrokeCap.Round
                )
            }
            val maskPath = Path().apply {
                addRect(
                    rect = Rect(
                        topLeft = Offset(x = 0F, y = center.y),
                        bottomRight = Offset(x = size.width, y = size.height)
                    )
                )
            }

            runDraw(iAmDrawing = "Sun") {
                val center = center.copy(y = center.y - yPadding)
                val radius1 = 200F
                val path = Path().apply {
                    addOval(
                        Rect(
                            center = center,
                            radius = radius1
                        )
                    )
                }

                val result = Path().apply {
                    op(path1 = path, path2 = maskPath, operation = PathOperation.Difference)
                }
                drawPath(
                    path = result,
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.5f to Color(0x88FFFFFF),
                            1.0f to Color(0x00FFFFFF),      // прозрачный край
                        ),
                        center = center,
                        radius = radius1
                    ),
                    blendMode = BlendMode.Plus
                )

                val path2 = Path().apply {
                    addOval(
                        Rect(
                            center = center,
                            radius = outermostRadius.value
                        )
                    )
                }

                val result2 = Path().apply {
                    op(path1 = path2, path2 = maskPath, operation = PathOperation.Difference)
                }

                drawPath(
                    path = result2,
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to Color.White,           // центр — ярко белый
                            0.3f to Color(0x88FFFFFF),     // почти белый
                            1.0f to Color(0x00FFFFFF),      // прозрачный край
                        ),
                        center = center,
                        radius = outermostRadius.value
                    ),
                    blendMode = BlendMode.Plus
                )

                val path3 = Path().apply {
                    val radius = 400F
                    addOval(
                        Rect(
                            center = center,
                            radius = radius
                        )
                    )
                }

                val tempResult3 = Path().apply {
                    op(path1 = path3, path2 = maskPath, operation = PathOperation.Difference)
                }

                val mask2 = Path().apply {
                    addRect(
                        rect = Rect(
                            topLeft = Offset(x = center.x - 100F, y = 400f),
                            bottomRight = Offset(x = center.x + 100f, y = center.y - 500F)
                        )
                    )
                }

                val result3 = Path().apply {
                    op(path1 = tempResult3, path2 = mask2, operation = PathOperation.Difference)
                }

                drawPath(
                    path = result3,
                    color = Color.White,
                    style = Stroke(
                        width = 2.dp.toPx(),
                    )
                )
            }
            runDraw(iAmDrawing = "Sun pies") {
                val radius = 900f
                val size = Size(radius * 2, radius * 2)

                drawArc(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.3f to Color(0x16FFFFFF),     // почти белый ,           // центр — ярко белый
                            0.6f to Color(0x10FFFFFF),     // почти белый
                            1.0f to Color(0x00FFFFFF),      // прозрачный край
                        )
                    ),
                    startAngle = 330F,
                    sweepAngle = 30F,
                    useCenter = true,
                    topLeft = center.copy(
                        x = center.x - radius,
                        y = center.y - yPadding - radius
                    ),
                    size = size,
                    style = Fill,
                    blendMode = BlendMode.Plus
                )

                drawArc(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.3f to Color(0x16FFFFFF),
                            0.6f to Color(0x10FFFFFF),
                            1.0f to Color(0x00FFFFFF),
                        )
                    ),
                    startAngle = 180F,
                    sweepAngle = 30F,
                    useCenter = true,
                    topLeft = center.copy(
                        x = center.x - radius,
                        y = center.y - yPadding - radius
                    ),
                    size = size,
                    style = Fill,
                    blendMode = BlendMode.Plus
                )

                drawArc(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.3f to Color(0x16FFFFFF),
                            0.6f to Color(0x10FFFFFF),
                            1.0f to Color(0x00FFFFFF),
                        )
                    ),
                    startAngle = 245F,
                    sweepAngle = 50F,
                    useCenter = true,
                    topLeft = center.copy(
                        x = center.x - radius,
                        y = center.y - yPadding - radius
                    ),
                    size = size,
                    style = Fill,
                    blendMode = BlendMode.Plus
                )
            }
            runDraw(iAmDrawing = "Stars") {
                stars.forEach { star ->
                    val alpha =
                        0.3f + abs(sin((twinkleAnim.value + star.twinkleSeed) * 2 * Math.PI)).toFloat() * 0.7f

                    val path = Path().apply {
                        addOval(
                            oval = Rect(
                                radius = 1.5f,
                                center = Offset(
                                    x = star.x * size.width,
                                    y = star.y * size.height
                                )
                            )
                        )
                    }
                    val result = Path().apply {
                        op(path, maskPath, operation = PathOperation.Difference)
                    }
                    drawPath(
                        path = result,
                        color = Color.White.copy(alpha = alpha)
                    )
                }
            }
        }
    }
}

enum class SwipeDirection {
    UP, DOWN, LEFT, RIGHT
}

data class StaticStar(
    val x: Float,
    val y: Float,
    val twinkleSeed: Float
)

@Composable
fun GifWebView(modifier: Modifier, anim: String) {
    key(anim) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    loadDataWithBaseURL(
                        null,
                        """
                    <html>
                        <body style="margin:0; padding:0; background:transparent;">
                            <img src="file:///android_res/raw/${anim}.gif" style="width:100%; height:auto;" />
                        </body>
                    </html>
                    """.trimIndent(),
                        "text/html",
                        "utf-8",
                        null
                    )
                    setBackgroundColor(0x00000000) // Прозрачный фон
                }
            },
            modifier = modifier // Укажи нужный размер
        )
    }
}

val anims = mutableSetOf(
    "smile",
    "balance",
    "flashbacks"
)

data class HorizontalLine(
    val yPadding: Float,
    val quadraticYPadding: Float,
    val width: Int
)

val horizontalLines = List(100) { index ->
    val yPadding = 12f * index.toFloat().pow(1.5f)
    val quadraticYPadding = 10f * index.toFloat().pow(1.54f)

    HorizontalLine(
        yPadding = yPadding,
        quadraticYPadding = quadraticYPadding,
        width = 2
    )
}

const val totalLines = 100
const val centerIndex = totalLines / 2

val verticalLines = List(totalLines) { rawIndex ->

    val index = rawIndex - centerIndex
    if (index == 0) return@List null
    val xPaddingTop = 40f * abs(index)
    val xPaddingBottom = 1.2f * xPaddingTop.pow(1.4f)

    VerticalLine(
        xPaddingTop = xPaddingTop,
        xPaddingBottom = xPaddingBottom,
        direction = if (index < 0) Direction.RIGHT_TO_LEFT else Direction.LEFT_TO_RIGHT
    )
}.filterNotNull()

data class VerticalLine(
    val xPaddingTop: Float,
    val xPaddingBottom: Float,
    val direction: Direction
)


enum class Direction {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT
}