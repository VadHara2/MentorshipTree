package com.vadhara7.mentorship_tree.domain.repository

actual interface QrScannerRepository {
    actual suspend fun scan(): String?
}

