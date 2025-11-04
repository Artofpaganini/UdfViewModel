package io.github.udfviewmodel

import android.content.Context
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Choreographer
import io.github.artofpaganini.udfviewmodel.ActionLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class AnrMonitor(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val lastFrameTime = AtomicLong(SystemClock.uptimeMillis())
    private val lastMessageTime = AtomicLong(SystemClock.uptimeMillis())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val choreographer = Choreographer.getInstance()
    private val choreographerCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            lastFrameTime.store(TimeUnit.NANOSECONDS.toMillis(frameTimeNanos))
            choreographer.postFrameCallback(this)
        }
    }

    private var looper: Looper? = Looper.getMainLooper()

    fun start() {
        looper?.setMessageLogging { _ -> lastMessageTime.exchange(SystemClock.uptimeMillis()) }
        choreographer.postFrameCallback(choreographerCallback)
        startAnrMonitorCycle()
    }

    fun stop() {
        choreographer.removeFrameCallback(choreographerCallback)
        looper?.setMessageLogging(null)
        looper = null
        scope.cancel()
    }

    private fun startAnrMonitorCycle() {
        scope.launch {
            while (isActive) {
                delay(TIME_OUT_MS)
                Log.w("EWQ", "startAnrMonitorCycle: ${isLooperFreeze()} and ${isChoreographFreeze()}", )
                if (isLooperFreeze() || isChoreographFreeze()) writeAnrTrace()
            }
        }
    }

    private fun writeAnrTrace() {
        val file = File(context.filesDir, "anr_log.txt")
        if (file.exists()) return
        looper?.let { main ->
            val stack = main.thread.stackTrace

            val crashFrame = stack.find { frame -> frame.className.startsWith("io.github.udfviewmodel") }
            val formatted = stack.joinToString("\n") { trace ->
                val className = trace.className
                val mark = when {
                    className.contains("ExternalSyntheticLambda") -> "[synthetic]"
                    className.contains("r8\$lambda") -> "[r8]"
                    className.contains("ComposableSingletons") -> "[compose]"
                    className.contains("AndroidComposeView") -> "[compose]"
                    className.startsWith("androidx.compose.") -> "[compose]"
                    className.startsWith("io.github.udfviewmodel") -> "[project]"
                    className.startsWith("android.view.") -> "[android]"
                    className.startsWith("android.") || className.startsWith("java.") -> "[system]"
                    else -> "[external]"
                }
                "at ${className}.${trace.methodName} (${trace.fileName}:${trace.lineNumber}) $mark"
            }

            val log = """
                      === ANR DETECTED ===
                      Время: ${dateFormat.format(Date())}
                      Место падения: ${crashFrame?.fileName}:${crashFrame?.lineNumber}
                      Действия пользователя до ANR: 
                      ${ActionLogger.dump()}
                      
                      "Full stack:"
                      $formatted
                      ====================
                  """.trimIndent()

            file.appendText(log + "\n\n")
        }
    }

    private fun isLooperFreeze(): Boolean = SystemClock.uptimeMillis() - lastMessageTime.load() > TIME_OUT_MS

    private fun isChoreographFreeze(): Boolean = SystemClock.uptimeMillis() - lastFrameTime.load() > TIME_OUT_MS

    private companion object {
        const val TIME_OUT_MS = 4000L
    }
}