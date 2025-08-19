package com.vadhara7.mentorship_tree.presentation.qrScanner.vm

import com.vadhara7.mentorship_tree.core.mvi.*
import com.vadhara7.mentorship_tree.domain.repository.PermissionRepository
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
    private val repository: QrScannerRepository,
    private val permissionRepository: PermissionRepository
) : Processor<QrScannerIntent, QrScannerEffect, QrScannerState> {
    override fun process(intent: QrScannerIntent, state: QrScannerState): Flow<QrScannerEffect> {
        return when (intent) {
            QrScannerIntent.StartScanning -> flow {
                if (permissionRepository.hasCameraPermission()) {
                    val text = repository.scan()
                    emit(QrScannerEffect.OnResult(text))
                } else {
                    emit(QrScannerEffect.PermissionDenied)
                }
            }
        }
    }
}

class QrScannerReducer : Reducer<QrScannerEffect, QrScannerState> {
    override fun reduce(effect: QrScannerEffect, state: QrScannerState): QrScannerState {
        return when (effect) {
            is QrScannerEffect.OnResult -> state.copy(result = effect.text)
            QrScannerEffect.PermissionDenied -> state.copy(hasPermission = false)
        }
    }
}

class QrScannerPublisher : Publisher<QrScannerEffect, QrScannerEvent> {
    override fun publish(effect: QrScannerEffect): QrScannerEvent? {
        return when (effect) {
            is QrScannerEffect.OnResult -> effect.text?.let { QrScannerEvent.OnScanned(it) }
            QrScannerEffect.PermissionDenied -> QrScannerEvent.OnPermissionDenied
        }
    }
}
