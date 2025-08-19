package com.vadhara7.mentorship_tree.core.di

import com.vadhara7.mentorship_tree.data.repository.IosQrScannerRepository
import com.vadhara7.mentorship_tree.domain.repository.QrScannerRepository
import org.koin.dsl.module

val qrScannerModule = module {
    single<QrScannerRepository> { IosQrScannerRepository() }
}

