package com.vadhara7.mentorship_tree.presentation.qrScanner.vm

import com.vadhara7.mentorship_tree.core.mvi.*
import com.vadhara7.mentorship_tree.domain.repository.QrScannerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class QrScannerViewModel(
    processor: QrScannerProcessor,
    reducer: QrScannerReducer,
    publisher: QrScannerPublisher
) : MviViewModel<QrScannerIntent, QrScannerEffect, QrScannerEvent, QrScannerState>(
    defaultState = QrScannerState(),
    processor = processor,
    reducer = reducer,
    publisher = publisher
)

class QrScannerProcessor(
    private val repository: QrScannerRepository
) : Processor<QrScannerIntent, QrScannerEffect, QrScannerState> {
    override fun process(intent: QrScannerIntent, state: QrScannerState): Flow<QrScannerEffect> {
        return when (intent) {
            QrScannerIntent.StartScanning -> flow {
                val text = repository.scan()
                emit(QrScannerEffect.OnResult(text))
            }
        }
    }
}

class QrScannerReducer : Reducer<QrScannerEffect, QrScannerState> {
    override fun reduce(effect: QrScannerEffect, state: QrScannerState): QrScannerState {
        return when (effect) {
            is QrScannerEffect.OnResult -> state.copy(result = effect.text)
        }
    }
}

class QrScannerPublisher : Publisher<QrScannerEffect, QrScannerEvent> {
    override fun publish(effect: QrScannerEffect): QrScannerEvent? {
        return when (effect) {
            is QrScannerEffect.OnResult -> effect.text?.let { QrScannerEvent.OnScanned(it) }
        }
    }
}
