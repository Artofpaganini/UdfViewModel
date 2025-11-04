package io.github.udfviewmodel

import android.content.Context
import android.os.Looper
import android.util.Log
import android.view.Choreographer
import io.github.udfviewmodel.ui.UserActionLogger
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

class AnrMonitor(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var lastFrameTime = System.currentTimeMillis()
    private var lastMessageTime = System.currentTimeMillis()

    private val timeoutMs = 5000L

    fun start() {
        startLooperLogging()
        startCoroutineChecker()
        startChoreographerChecker()
    }

    fun stop() {
        scope.cancel()
    }

    // === Вариант 1: Coroutines + Dispatchers.Main ===
    private fun startCoroutineChecker() {
        scope.launch {
            while (isActive) {
                delay(timeoutMs)
                if (System.currentTimeMillis() - lastMessageTime > timeoutMs) {
                    logAnr("Looper/Coroutine Watchdog")
                }
            }
        }
    }

    // === Вариант 2: Choreographer ===
    private fun startChoreographerChecker() {
        val callback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                lastFrameTime = System.currentTimeMillis()
                Choreographer.getInstance().postFrameCallback(this)
            }
        }
        Choreographer.getInstance().postFrameCallback(callback)

        scope.launch {
            while (isActive) {
                delay(timeoutMs)
                if (System.currentTimeMillis() - lastFrameTime > timeoutMs) {
                    logAnr("Choreographer")
                }
            }
        }
    }

    // === Вариант 3: Looper.setMessageLogging ===
    private fun startLooperLogging() {
        Looper.getMainLooper().setMessageLogging { msg ->
            if (msg.startsWith(">>>>>") || msg.startsWith("<<<<<")) {
                lastMessageTime = System.currentTimeMillis()
            }
        }

        scope.launch {
            while (isActive) {
                delay(timeoutMs)
                if (System.currentTimeMillis() - lastMessageTime > timeoutMs) {
                    logAnr("Looper.setMessageLogging")
                }
            }
        }
    }

    // === Запись в файл с ограничением размера ===
    private fun logAnr(source: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val stack = Looper.getMainLooper().thread.stackTrace

        val crashFrame = stack.find { frame -> frame.className.startsWith("io.github.udfviewmodel") }

        val formatted = stack.joinToString("\n") { e ->
            val className = e.className
            val mark = when {
                className.contains("ExternalSyntheticLambda") -> "[synthetic]"
                className.contains("r8\$lambda") -> "[r8]"
                className.contains("ComposableSingletons") -> "[compose]"
                className.contains("AndroidComposeView") -> "[compose]"
                className.startsWith("androidx.compose.") -> "[compose]"
                className.startsWith("android.view.") -> "[android]"
                className.startsWith("android.") || className.startsWith("java.") -> "[system]"
                else -> "unknown"
            }
            "at ${className}.${e.methodName} (${e.fileName}:${e.lineNumber}) $mark"
        }

        val log = """
        === ANR DETECTED ===
        Место падения: $source
        Время: $timestamp
        Действия пользователя до ANR:
        ${UserActionLogger.dump()}
        Обратить внимание на :
        $crashFrame
          
        "Full stack:"
        $formatted
        ====================
    """.trimIndent()

        try {
            val file = File(context.filesDir, "anr_log.txt")
            if (file.exists()) {
                file.delete()
            }
            file.appendText(log + "\n\n")
        } catch (e: Exception) {
            Log.e("AnrMonitor", "Failed to write log", e)
        }
    }
}