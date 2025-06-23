package com.vadhara7.mentorship_tree.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base interface for Model-View-Intent (MVI) architecture.
 *
 * @param INTENT Represents the user intents/actions.
 * @param EVENT Represents one-time events to be sent to the UI.
 * @param STATE Represents the current state of the UI.
 */
interface Mvi<INTENT, EVENT, STATE> {
    val state: Flow<STATE>
    val event: Flow<EVENT>

    fun process(intent: INTENT)
}

/** Marker interface for user intents. */
interface Intent

/** Marker interface for effects produced by processing an intent. */
interface Effect

/** Marker interface for one-time events sent to the UI. */
interface Event

/** Marker interface for representing the UI state. */
interface State

/**
 * Processes a given [INTENT] based on the current [STATE] and produces a stream of [EFFECT].
 *
 * @param INTENT The user intent.
 * @param EFFECT The effect produced by processing the intent.
 * @param STATE The current state.
 */
interface Processor<INTENT : Intent, EFFECT : Effect, STATE : State> {
    fun process(intent: INTENT, state: STATE): Flow<EFFECT>
}

/**
 * Reduces an [EFFECT] to a new [STATE].
 *
 * @param EFFECT The effect to reduce.
 * @param STATE The current state.
 * @return New state if changed, or null if no change is necessary.
 */
interface Reducer<EFFECT : Effect, STATE : State> {
    fun reduce(effect: EFFECT, state: STATE): STATE?
}

/**
 * Publishes an [EFFECT] as a one-time [EVENT] for the UI.
 *
 * @param EFFECT The effect to publish.
 * @param EVENT The event to be emitted.
 */
interface Publisher<EFFECT : Effect, EVENT : Event> {
    fun publish(effect: EFFECT): EVENT?
}

/**
 * Repeats an [EFFECT] by generating a new [INTENT] based on the current [STATE].
 *
 * @param EFFECT The effect that may trigger a repeated intent.
 * @param INTENT The repeated intent.
 * @param STATE The current state.
 */
interface Repeater<EFFECT : Effect, INTENT : Intent, STATE : State> {
    fun repeat(effect: EFFECT, state: STATE): INTENT?
}

/**
 * Base ViewModel implementation for MVI architecture.
 *
 * @param INTENT The type of user intent.
 * @param EFFECT The type of effect produced.
 * @param EVENT The type of event emitted to the UI.
 * @param STATE The type of state managed by the ViewModel.
 *
 * @property state The current state as a Flow.
 * @property event The stream of one-time events.
 */
open class MviViewModel<INTENT : Intent, EFFECT : Effect, EVENT : Event, STATE : State>(
    defaultState: STATE,
    private val processor: Processor<INTENT, EFFECT, STATE>,
    private val reducer: Reducer<EFFECT, STATE>,
    private val publisher: Publisher<EFFECT, EVENT>? = null,
    private val repeater: Repeater<EFFECT, INTENT, STATE>? = null,
) : ViewModel(), Mvi<INTENT, EVENT, STATE> {

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(defaultState)
    private val _eventChannel: Channel<EVENT> = Channel(Channel.BUFFERED)

    private val intentJobMap = mutableMapOf<INTENT, Job>()

    override val state = _state.asStateFlow()
    override val event: Flow<EVENT> = _eventChannel.receiveAsFlow()

    /**
     * Processes the given [intent] by:
     * 1. Cancelling any existing job associated with the intent.
     * 2. Invoking the [processor] to handle the intent and produce a stream of effects.
     * 3. For each emitted effect, applying the [reducer] to update the state,
     *    publishing an event via [publisher] if available, and
     *    repeating the intent via [repeater] if necessary.
     *
     * @param intent The user intent to be processed.
     */
    override fun process(intent: INTENT) {
        intentJobMap[intent]?.cancel()
        val job = processor.process(intent, _state.value)
            .onEach { effect: EFFECT ->
                reducer.reduce(effect, _state.value)?.let { _state.value = it }
                publisher?.publish(effect)?.let { event ->
                    viewModelScope.launch { _eventChannel.send(event) }
                }
                repeater?.repeat(effect, _state.value)?.let { process(it) }
            }
            .launchIn(viewModelScope)
        intentJobMap[intent] = job
    }
}