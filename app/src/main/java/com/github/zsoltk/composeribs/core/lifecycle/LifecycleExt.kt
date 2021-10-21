package com.github.zsoltk.composeribs.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Lifecycle.changesAsFlow(): Flow<Lifecycle.State> =
    callbackFlow {
        val observer = LifecycleEventObserver { source, _ ->
            trySend(currentState)
        }
        trySend(currentState)
        addObserver(observer)
        awaitClose { removeObserver(observer) }
    }
