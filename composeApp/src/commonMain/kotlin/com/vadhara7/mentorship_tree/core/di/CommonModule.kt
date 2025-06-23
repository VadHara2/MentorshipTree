package com.vadhara7.mentorship_tree.core.di

import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule: Module = module {
    includes(dataModule)
    includes(domainModule)
    includes(networkModule)
    includes(presentationModule)
}