package com.vadhara7.mentorship_tree.core.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import com.vadhara7.mentorship_tree.domain.usecase.*

val domainModule = module {
    factoryOf(::GetOrCreateUserUseCase)
    factoryOf(::GetMyTreeUseCase)
    factoryOf(::GetRequestsUseCase)
}