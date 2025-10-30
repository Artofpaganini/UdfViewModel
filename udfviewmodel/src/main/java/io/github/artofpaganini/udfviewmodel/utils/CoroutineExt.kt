package io.github.artofpaganini.udfviewmodel.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

fun <T> Flow<T>.launchIn(
    scope: CoroutineScope,
    catchBlock: suspend (t: Throwable) -> Unit = Throwable::printStackTrace,
): Job = this.catch { throwable -> catchBlock.invoke(throwable) }
    .launchIn(scope)

fun CoroutineScope.launch(
    context: CoroutineContext,
    catchBlock: (t: Throwable) -> Unit = Throwable::printStackTrace,
    finallyBlock: (() -> Unit)? = null,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    tryBlock: suspend CoroutineScope.() -> Unit
): Job = launch(context + BaseCoroutineExceptionHandler(catchBlock), start = start) {
    try {
        tryBlock()
    } finally {
        finallyBlock?.invoke()
    }
}

private class BaseCoroutineExceptionHandler(
    private val errorCallback: ((Throwable) -> Unit)
) : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        errorCallback(exception)
    }
}

inline fun <reified T> Flow<T>.observe(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit
): Job = lifecycleOwner.lifecycleScope.launch {
    flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect { value ->
        action(value)
    }
}

inline fun <reified T> Flow<T>.observe(
    fragment: Fragment,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit
): Job {
    val lifecycleOwner = fragment.getCurrentLifecycleOwner()
    return lifecycleOwner.lifecycleScope.launch {
        flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect { value ->
            action(value)
        }
    }
}

fun Fragment.getCurrentLifecycleOwner(): LifecycleOwner = try {
    this.viewLifecycleOwner
} catch (exception: Exception) {
    if (exception is IllegalStateException) this else throw exception
}