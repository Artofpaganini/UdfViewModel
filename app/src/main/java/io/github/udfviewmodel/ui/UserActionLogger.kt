package io.github.udfviewmodel.ui

object UserActionLogger {
    private val actions = ArrayDeque<String>()
    private const val MAX_SIZE = 30

    fun log(action: String) {
        synchronized(actions) {
            if (actions.size >= MAX_SIZE) actions.removeFirst()
            actions.addLast("${System.currentTimeMillis()}: $action")
        }
    }

    fun dump(): String = synchronized(actions) {
        actions.joinToString("\n")
    }
}