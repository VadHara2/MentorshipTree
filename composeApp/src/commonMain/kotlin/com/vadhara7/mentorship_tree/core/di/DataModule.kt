package com.vadhara7.mentorship_tree.core.di

import com.vadhara7.mentorship_tree.data.repository.SecretsRepositoryImpl
import com.vadhara7.mentorship_tree.domain.repository.SecretsRepository
import com.vadhara7.mentorship_tree.data.repository.UserRepositoryImpl
import com.vadhara7.mentorship_tree.domain.repository.UserRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::SecretsRepositoryImpl) {
        bind<SecretsRepository>()
    }

    singleOf(::UserRepositoryImpl) {
        bind<UserRepository>()
    }
}