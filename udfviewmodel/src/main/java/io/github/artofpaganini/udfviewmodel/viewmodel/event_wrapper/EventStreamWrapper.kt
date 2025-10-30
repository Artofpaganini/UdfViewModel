package io.github.artofpaganini.udfviewmodel.viewmodel.event_wrapper

import androidx.annotation.AnyThread
import io.github.artofpaganini.udfviewmodel.viewmodel.logStage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import java.util.UUID
import java.util.concurrent.LinkedBlockingDeque

@AnyThread
internal class EventStreamWrapper<Event>(private val vmName: String) {

    private val pendingEvent by lazy { LinkedBlockingDeque<Unique<Event>>() }

    private val stream = MutableStateFlow<Unique<Event>?>(null)

    private var onReturnPredicate: (() -> Boolean)? = null

    fun getStream(): Flow<Event> = stream.filterNotNull()
        .map { unique -> unique.event }
        .onStart {
            if (pendingEvent.isEmpty()) return@onStart
            val condition = onReturnPredicate?.invoke()
            vmName.logStage(
                methodName = "getEventStream",
                message = "При подписке, доступен список необработанных ивентов ${getPendingEventNames()}!"
            )
            if (condition == null) postPending() else postOnReturnPending(condition)
        }

    infix fun post(newEvent: Event) = synchronized(this) {
        val new = Unique(newEvent)
        if (stream.value == null) {
            vmName.logStage(methodName = "postEvent", message = "Ивент ${new.event.getName()} отправлен!")
            stream.update { new }
        } else {
            vmName.logStage(
                methodName = "postEvent",
                message = "Ивент ${newEvent.getName()} добавлен, в очередь, отложенных ивентов ${getPendingEventNames()}!"
            )
            pendingEvent.offerLast(new)
        }
    }

    fun postOnReturnEvent(newEvent: Event, predicate: () -> Boolean) = synchronized(this) {
        val new = Unique(newEvent)
        if (pendingEvent.contains(new)) return@synchronized
        vmName.logStage(
            methodName = "postOnReturnEvent",
            message = "Ивент ${newEvent.getName()} добавлен," +
                " в очередь, отложенных ивентов ${getPendingEventNames()}" +
                " и попробует выполниться, при возврате на экран!"
        )
        if (onReturnPredicate != predicate) onReturnPredicate = predicate
        pendingEvent.offerLast(new)
    }

    fun handle() = synchronized(this) {
        vmName.logStage(
            methodName = "handleEvent",
            message = "Ивент ${stream.value?.event?.getName()} обработан!"
        )
        stream.update { null }
        postPending()
    }

    private fun postPending() {
        if (pendingEvent.isNotEmpty() && stream.value == null) {
            val unique = pendingEvent.pollFirst()
            vmName.logStage(
                methodName = "postPending",
                message = "Отложенный эффект ${unique?.event.getName()} отправлен и удален из очереди!"
            )
            onReturnPredicate = null
            stream.update { unique }
        }
    }

    private fun postOnReturnPending(isSuccess: Boolean) {
        if (stream.value != null) return
        val unique = pendingEvent.pollLast()
        if (isSuccess) {
            vmName.logStage(
                methodName = "postOnReturnPending",
                message = "Отложенный эффект ${unique?.event.getName()} отправлен!"
            )
            stream.update { unique }
        }
        vmName.logStage(
            methodName = "postOnReturnPending",
            message = "Отложенный эффект ${unique?.event.getName()} удален из очереди!"
        )
        onReturnPredicate = null
    }

    private data class Unique<Event>(val event: Event, val uuid: UUID = UUID.randomUUID())

    private fun getPendingEventNames(): List<String> =
        pendingEvent.mapNotNull { unique -> unique.event?.let { event -> event::class.simpleName } }

    private fun Event?.getName(): String =
        this?.let { event -> event::class.java.simpleName }.ifNullOrEmpty("Unknown event")
}

internal fun String?.ifNullOrEmpty(value: String) = if (this.isNullOrEmpty()) value else this
