package com.vadhara7.mentorship_tree.di

import com.vadhara7.mentorship_tree.secrets.SecretsRepository
import org.koin.dsl.module

val appModule = module {
    single { SecretsRepository() }
}