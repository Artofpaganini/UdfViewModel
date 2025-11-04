package io.github.artofpaganini.udfviewmodel

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ActionLogger {
    private val actions = ArrayDeque<String>()
    private const val MAX_SIZE = 30

    @SuppressLint("ConstantLocale")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun log(action: String) = synchronized(actions) {
        if (actions.size >= MAX_SIZE) actions.removeFirst()
        actions.addLast("${dateFormat.format(Date())}: $action")
    }

    fun dump(): String = synchronized(actions) {
        actions.joinToString(", \n")
    }
}