package com.netimur.businesscard

import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private val soundPool: SoundPool? = try {
        SoundPool.Builder()
            .setMaxStreams(5)
            .build()
    } catch (_: Exception) {
        null
    }

    var isFlashbacksLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MainViewModel()
        val flashbacksSoundId = soundPool?.load(this, R.raw.flashbacks_mp3, 1)
        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0 && sampleId == flashbacksSoundId) {
                isFlashbacksLoaded = true
            }
        }
        enableEdgeToEdge()

        setContent {
            val mediaPlayer = remember {
                MediaPlayer.create(this, R.raw.flashbacks_mp3)
            }

            BlackWhiteVaporwaveScreen(
                playFlashbacks = {
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer.start()
                    }
                },
                stopFlashbacks = {
                    mediaPlayer?.release()
                }
            )
            /*val started = viewModel.started.collectAsStateWithLifecycle(
                minActiveState = Lifecycle.State.RESUMED,
                context = Dispatchers.Main.immediate + SupervisorJob(),
                initialValue = false
            )
            ThxIntroScreen(started.value, onRepeat = viewModel::repeat)*/
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
fun screenHeight(): Dp = with(LocalConfiguration.current) { this.screenHeightDp.dp }

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