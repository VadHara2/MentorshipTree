package com.vadhara7.mentorship_tree.core.di

import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthProcessor
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthReducer
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthPublisher
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthViewModel
import com.vadhara7.mentorship_tree.presentation.home.vm.*
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    // Auth
    singleOf(::AuthProcessor)
    singleOf(::AuthReducer)
    singleOf(::AuthPublisher)
    viewModelOf(::AuthViewModel)


    // Home
    singleOf(::HomeProcessor)
    singleOf(::HomeReducer)
    viewModelOf(::HomeViewModel)
}