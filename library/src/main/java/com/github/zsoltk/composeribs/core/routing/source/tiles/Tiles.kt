package com.github.zsoltk.composeribs.core.routing.source.tiles

import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.plugin.Destroyable
import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import com.github.zsoltk.composeribs.core.routing.RoutingSourceAdapter
import com.github.zsoltk.composeribs.core.routing.adapter
import com.github.zsoltk.composeribs.core.routing.source.tiles.operation.deselectAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.coroutines.EmptyCoroutineContext

class Tiles<T : Any>(
    initialElements: List<T>,
    onScreenResolver: OnScreenResolver<TransitionState> = TilesOnScreenResolver
) : RoutingSource<T, Tiles.TransitionState>, Destroyable {

    enum class TransitionState {
        CREATED, STANDARD, SELECTED, DESTROYED
    }

    private val scope = CoroutineScope(EmptyCoroutineContext + Dispatchers.Unconfined)

    private val state = MutableStateFlow(
        initialElements.map {
            TilesElement(
                key = RoutingKey(it),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STANDARD,
                operation = Operation.Noop()
            )
        }
    )

    override val adapter: RoutingSourceAdapter<T, TransitionState> by lazy(LazyThreadSafetyMode.NONE) {
        adapter(scope, onScreenResolver)
    }

    override val elements: StateFlow<TilesElements<T>>
        get() = state

    override val canHandleBackPress: StateFlow<Boolean> =
        state.map { list -> list.any { it.targetState == TransitionState.SELECTED } }
            .stateIn(scope, SharingStarted.Eagerly, false)

    override fun onTransitionFinished(key: RoutingKey<T>) {
        state.update { list ->
            list.mapNotNull {
                if (it.key == key) {
                    if (it.targetState == TransitionState.DESTROYED) {
                        null
                    } else {
                        it.onTransitionFinished()
                    }
                } else {
                    it
                }
            }
        }
    }

    fun perform(operation: TilesOperation<T>) {
        if (operation.isApplicable(state.value)) {
            state.update { operation(it) }
        }
    }

    override fun onBackPressed() {
        deselectAll()
    }

    override fun destroy() {
        scope.cancel()
    }

}
