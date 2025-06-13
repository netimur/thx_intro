package com.netimur.businesscard

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GifWebView(modifier: Modifier = Modifier, anim: String) {
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
                    setBackgroundColor(0x00000000)
                }
            },
            modifier = modifier
        )
    }
}