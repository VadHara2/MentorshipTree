package com.vadhara7.mentorship_tree.core.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule: Module = module {
    single { Firebase.auth }
    single { Firebase.firestore }

    includes(dataModule)
    includes(domainModule)
    includes(networkModule)
    includes(presentationModule)
}