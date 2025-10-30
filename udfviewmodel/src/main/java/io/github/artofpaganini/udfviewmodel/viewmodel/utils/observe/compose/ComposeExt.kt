package io.github.artofpaganini.udfviewmodel.viewmodel.utils.observe.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.artofpaganini.udfviewmodel.utils.observe
import io.github.artofpaganini.udfviewmodel.viewmodel.provider.UdfEventProvider

@Composable
inline fun <reified Event : Any> UdfEventProvider<Event>.collectEvent(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline handleEvent: (effect: Event) -> Unit
) {
    val callback by rememberUpdatedState(newValue = handleEvent)
    LaunchedEffect(key1 = lifecycleOwner) {
        getEvent().observe(lifecycleOwner, lifecycleState, callback)
    }
}

@Composable
inline fun <reified Event : Any> UdfEventProvider<Event>.collectEvent(
    fragment: Fragment,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline handleEvent: (effect: Event) -> Unit
) {
    val callback by rememberUpdatedState(newValue = handleEvent)
    LaunchedEffect(key1 = fragment) {
        getEvent().observe(fragment, lifecycleState, callback)
    }
}