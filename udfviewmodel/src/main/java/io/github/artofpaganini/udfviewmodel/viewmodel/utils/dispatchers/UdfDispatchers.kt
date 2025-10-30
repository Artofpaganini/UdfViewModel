package io.github.artofpaganini.udfviewmodel.viewmodel.utils.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface UdfDispatchers {
    val map: CoroutineDispatcher
    val work: CoroutineDispatcher
}

internal class UdfDispatchersImpl(
    override val map: CoroutineDispatcher,
    override val work: CoroutineDispatcher,
) : UdfDispatchers

fun udfDispatchers(
    map: CoroutineDispatcher,
    work: CoroutineDispatcher,
): UdfDispatchers = UdfDispatchersImpl(map, work)