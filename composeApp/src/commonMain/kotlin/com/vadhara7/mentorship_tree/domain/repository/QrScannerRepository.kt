package com.vadhara7.mentorship_tree.domain.repository

expect interface QrScannerRepository {
    suspend fun scan(): String?
}

