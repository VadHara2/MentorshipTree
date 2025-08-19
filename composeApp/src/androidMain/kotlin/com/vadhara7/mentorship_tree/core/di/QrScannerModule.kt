package com.vadhara7.mentorship_tree.core.di

import com.vadhara7.mentorship_tree.data.repository.AndroidQrScannerRepository
import com.vadhara7.mentorship_tree.domain.repository.QrScannerRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val qrScannerModule = module {
    single<QrScannerRepository> { AndroidQrScannerRepository(androidContext()) }
}

