package com.netimur.businesscard

import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.netimur.businesscard.ui.theme.BusinessCardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.ui.graphics.Color as ComposeColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .build()

        val soundId = soundPool.load(this, R.raw.thx, 1)

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0 && sampleId == soundId) {
                soundPool.play(
                    soundId,
                    1f, 1f,
                    0, 0,
                    1f
                )
            }
        }
        enableEdgeToEdge()
        setContent {
            var started = remember {
                mutableStateOf(false)
            }
            LaunchedEffect(Unit) {
                started.value = true
            }

            var showButton = remember {
                mutableStateOf(false)
            }

            LaunchedEffect(Unit) {
                delay(10000)
                showButton.value = true
            }

            BusinessCardTheme {
                val screenWidthPx = screenWidthPx()
                val screenHeightPx = screenHeightPx()
                val boxColor = animateColorAsState(
                    targetValue = if (started.value) {
                        Color.Black
                    } else {
                        Color.Transparent
                    },
                    animationSpec = tween(durationMillis = 7000, easing = FastOutLinearInEasing)
                )
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = ComposeColor.Black)
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                color = boxColor.value,
                                topLeft = Offset.Zero,
                                size = Size(width = screenWidthPx, height = screenHeightPx)
                            )

                        },
                ) { innerPadding ->
                    val screenWidthPx = screenWidthPx()
                    val metalGradient = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2A2A2A),  // тёмный
                            Color(0xFFAAAAAA),  // светлый
                            Color(0xFF2A2A2A),   // тёмный
                            Color(0xFFE0E0E0),  // бликовый
                            Color(0xFFAAAAAA),  // светлый
                            Color(0xFF2A2A2A)   // тёмный
                        ),
                        startX = 0F,
                        endX = screenWidthPx
                    )
                    val paddingHorizontal = 130F

                    val light1X = remember {
                        Animatable(0F)
                    }

                    val screenHeightPx = screenHeightPx()

                    val lights = List(4) {
                        LightMotion(
                            direction = LightMotionDirection.entries.random(),
                            startPadding = Random.nextInt(from = 0, until = screenWidthPx.toInt())
                                .toFloat(),
                            y = Random.nextInt(from = 0, until = screenHeightPx.toInt()).toFloat(),
                            speed = Random.nextInt(from = 4000, until = 8000)
                        )
                    }

                    val lightsXMutableList: MutableList<Animatable<Float, AnimationVector1D>> =
                        mutableListOf()

                    for (light in lights) {
                        val x = remember { Animatable(light.startPadding) }
                        lightsXMutableList.add(x)
                    }

                    val lightsX = lightsXMutableList.toList()


                    val lightRadius = remember {
                        Animatable(1F)
                    }

                    val light2X = remember {
                        Animatable(screenWidthPx)
                    }


                    LaunchedEffect(Unit) {
                        launch {
                            light1X.snapTo(targetValue = paddingHorizontal)
                            light1X.animateTo(
                                targetValue = screenWidthPx - paddingHorizontal,
                                animationSpec = tween(
                                    durationMillis = 6000,
                                    easing = FastOutLinearInEasing
                                )
                            )
                            light1X.snapTo(screenWidthPx * 2)
                        }
                        launch {
                            light2X.snapTo(targetValue = screenWidthPx)
                            light2X.animateTo(
                                targetValue = 0F,
                                animationSpec = tween(
                                    durationMillis = 6000,
                                    easing = FastOutLinearInEasing
                                )
                            )
                            light2X.snapTo(targetValue = -200F)
                        }
                        launch {
                            lightRadius.snapTo(50F)
                            lightRadius.animateTo(
                                targetValue = 80F,
                                animationSpec = tween(durationMillis = 2000)
                            )
                            lightRadius.animateTo(
                                targetValue = 30F,
                                animationSpec = tween(durationMillis = 5000, delayMillis = 2000)
                            )
                        }

                        lightsX.forEachIndexed { index, lightX ->
                            launch {
                                lights.getOrNull(index)?.let { light ->
                                    lightX.snapTo(light.startPadding)
                                    val target = when (light.direction) {
                                        LightMotionDirection.LEFT_TO_RIGHT -> screenWidthPx + 100F
                                        LightMotionDirection.RIGHT_TO_LEFT -> -100F
                                    }
                                    lightX.animateTo(
                                        targetValue = target,
                                        animationSpec = tween(durationMillis = light.speed)
                                    )
                                    lightX.snapTo(-100F)
                                }
                            }
                        }
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(color = Color.Black)
                    ) {
                        val tracking = 50.dp.toPx()

                        val hBarHeight = 5.dp.toPx()
                        val hStemWidth = 10.dp.toPx()

                        val tBarHeight = 7.dp.toPx()
                        val hBarLength = 50.dp.toPx()
                        val hBarStartX = center.x - hBarLength / 2
                        val hBarEndX = center.x + hBarLength / 2

                        val hBarTopY = center.y - hBarHeight / 2
                        val hBarBottomY = center.y + hBarHeight / 2

                        val hStemHeight = 50.dp.toPx()
                        val hLeftStemStartX = hBarStartX - hStemWidth
                        val hLeftStemEndX = hBarStartX
                        val hLeftStemTopY = center.y - hStemHeight / 2
                        val hLeftStemBottomY = center.y + hStemHeight / 2
                        val tStemBottomY = hLeftStemBottomY
                        val hRightStemStartX = hBarEndX
                        val hRightStemEndX = hRightStemStartX + hStemWidth
                        val hRightStemTopY = hLeftStemTopY
                        val hRightStemBottomY = hLeftStemBottomY
                        val baselineTopLeft = Offset(
                            x = paddingHorizontal,
                            y = center.y + hStemHeight
                        )
                        val baselineBottomRight = Offset(
                            x = size.width - paddingHorizontal,
                            y = center.y + hStemHeight + tBarHeight
                        )

                        runDraw(iAmDrawing = "H letter") {
                            val hBar = Path().apply {
                                addRect(
                                    rect = Rect(
                                        topLeft = Offset(
                                            x = hBarStartX,
                                            y = hBarTopY
                                        ),
                                        bottomRight = Offset(
                                            x = hBarEndX,
                                            y = hBarBottomY
                                        )
                                    )
                                )
                            }
                            val hStems = Path().apply {
                                addRect(
                                    rect = Rect(
                                        topLeft = Offset(
                                            x = hLeftStemStartX,
                                            y = hLeftStemTopY
                                        ),
                                        bottomRight = Offset(
                                            x = hLeftStemEndX,
                                            y = hLeftStemBottomY
                                        )
                                    )
                                )
                                addRect(
                                    rect = Rect(
                                        topLeft = Offset(
                                            x = hRightStemStartX,
                                            y = hRightStemTopY
                                        ),
                                        bottomRight = Offset(
                                            x = hRightStemEndX,
                                            y = hRightStemBottomY
                                        )
                                    )
                                )
                            }
                            val hLetter = Path().apply {
                                addPath(hBar)
                                addPath(hStems)
                            }
                            drawPath(
                                path = hLetter,
                                brush = metalGradient
                            )
                        }
                        runDraw(iAmDrawing = "T letter") {
                            val padding = paddingHorizontal
                            val tBarStartX = padding
                            val tBarEndX = screenWidthPx - padding
                            val tBarBottomY = center.y - hStemHeight / 2 - 10.dp.toPx()
                            val tBarTopY = tBarBottomY - tBarHeight
                            val tBar = Path().apply {
                                addRect(
                                    rect = Rect(
                                        topLeft = Offset(
                                            x = tBarStartX,
                                            y = tBarTopY
                                        ),
                                        bottomRight = Offset(
                                            x = tBarEndX,
                                            y = tBarBottomY
                                        )
                                    )
                                )
                            }

                            val tStemWidth = 10.dp.toPx()
                            val tStemStartX = center.x - hBarLength / 2 - hStemWidth -
                                    tracking - tStemWidth

                            val tStemEndX = tStemStartX + tStemWidth

                            val tStem = Path().apply {
                                addRect(
                                    rect = Rect(
                                        topLeft = Offset(
                                            x = tStemStartX,
                                            y = tBarBottomY
                                        ),
                                        bottomRight = Offset(
                                            x = tStemEndX,
                                            y = tStemBottomY
                                        )
                                    )
                                )
                            }
                            val tLetter = Path().apply {
                                addPath(tBar)
                                addPath(tStem)
                            }
                            drawPath(
                                path = tLetter,
                                brush = metalGradient
                            )
                        }
                        runDraw(iAmDrawing = "X letter") {
                            val xLetterWidth = 50.dp.toPx()
                            val leftBarTopStartX = center.x + hBarLength / 2 + tracking
                            val topY = hLeftStemTopY
                            val barWidth = 10.dp.toPx()
                            val rightBarBottomEndX = leftBarTopStartX + barWidth * 2 + xLetterWidth
                            val bottomY = hLeftStemBottomY

                            val path1 = Path().apply {
                                moveTo(x = leftBarTopStartX, y = topY)
                                lineTo(x = leftBarTopStartX + barWidth, y = topY)
                                lineTo(x = rightBarBottomEndX, y = bottomY)
                                lineTo(x = rightBarBottomEndX - barWidth, y = bottomY)
                                close()
                            }

                            val rightXEnd = leftBarTopStartX + barWidth * 2 + xLetterWidth

                            val path2 = Path().apply {
                                moveTo(x = rightXEnd, y = topY)
                                lineTo(x = rightXEnd - barWidth, y = topY)
                                lineTo(x = leftBarTopStartX, y = bottomY)
                                lineTo(x = leftBarTopStartX + barWidth, y = bottomY)
                                close()
                            }

                            drawPath(path = path1, brush = metalGradient)
                            drawPath(path = path2, brush = metalGradient)
                        }
                        runDraw(iAmDrawing = "Baseline") {
                            val baseline = Path().apply {
                                addRect(
                                    rect = Rect(
                                        topLeft = baselineTopLeft,
                                        bottomRight = baselineBottomRight
                                    )
                                )
                            }
                            drawPath(
                                path = baseline,
                                brush = metalGradient
                            )
                        }
                        runDraw(iAmDrawing = "Light1") {
                            val center = Offset(
                                x = light1X.value,
                                y = baselineTopLeft.y
                            )

                            val path = Path().apply {
                                addRect(
                                    rect = Rect(
                                        center = center,
                                        radius = lightRadius.value
                                    )
                                )
                            }
                            drawPath(
                                path = path,
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = lightRadius.value
                                )
                            )
                        }
                        runDraw(iAmDrawing = "Light2") {
                            val center = Offset(
                                x = light2X.value,
                                y = hLeftStemTopY - 100F
                            )

                            val radius = 100F

                            val path = Path().apply {
                                addRect(
                                    rect = Rect(
                                        center = center,
                                        radius = radius
                                    )
                                )
                            }
                            drawPath(
                                path = path,
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = radius
                                )
                            )
                        }
                        runDraw(iAmDrawing = "Lights") {
                            lightsX.forEachIndexed { index, lightX ->
                                lights.getOrNull(index)?.let { light ->
                                    val center = Offset(
                                        x = lightX.value,
                                        y = light.y
                                    )

                                    val path = Path().apply {
                                        addRect(
                                            rect = Rect(
                                                center = center,
                                                radius = 50F
                                            )
                                        )
                                    }
                                    drawPath(
                                        path = path,
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.3f),
                                                Color.Transparent
                                            ),
                                            center = center,
                                            radius = 50F
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class LightMotionDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT
}

data class LightMotion(
    val direction: LightMotionDirection,
    val startPadding: Float,
    val speed: Int,
    val y: Float
)

fun DrawScope.runDraw(
    iAmDrawing: String,
    drawBlock: () -> Unit
) {
    drawBlock()
}

@Composable
fun screenWidth(): Dp = with(LocalConfiguration.current) { this.screenWidthDp.dp }

@Composable
fun screenWidthPx(): Float {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthDp = configuration.screenWidthDp
    return with(density) { screenWidthDp.dp.toPx() }
}

@Composable
fun screenHeightPx(): Float {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightDp = configuration.screenHeightDp
    return with(density) { screenHeightDp.dp.toPx() }
}