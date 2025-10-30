package io.github.artofpaganini.udfviewmodel.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LifecycleResumePauseActions(
    launchAction: (@DisallowComposableCalls () -> Unit)? = null,
    stopAction: (@DisallowComposableCalls () -> Unit)? = null,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    LifecycleResumeEffect(launchAction, stopAction, lifecycleOwner) {
        launchAction?.invoke()
        onPauseOrDispose {
            stopAction?.invoke()
        }
    }
}

@Composable
fun LifecycleStartStopActions(
    launchAction: (@DisallowComposableCalls () -> Unit)? = null,
    cancelAction: (@DisallowComposableCalls () -> Unit)? = null,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    LifecycleStartEffect(launchAction, cancelAction, lifecycleOwner) {
        launchAction?.invoke()
        onStopOrDispose {
            cancelAction?.invoke()
        }
    }
}