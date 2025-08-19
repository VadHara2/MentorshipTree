package com.vadhara7.mentorship_tree.core.di

import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorProcessor
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorPublisher
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorReducer
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorViewModel
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthProcessor
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthReducer
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthPublisher
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthViewModel
import com.vadhara7.mentorship_tree.presentation.tree.vm.*
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationProcessor
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationReducer
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationViewModel
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationPublisher
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
    singleOf(::TreeProcessor)
    singleOf(::TreeReducer)
    singleOf(::TreePublisher)
    viewModelOf(::TreeViewModel)

    // Notification
    singleOf(::NotificationProcessor)
    singleOf(::NotificationReducer)
    singleOf(::NotificationPublisher)
    viewModelOf(::NotificationViewModel)

    // AddMentor
    singleOf(::AddMentorProcessor)
    singleOf(::AddMentorReducer)
    singleOf(::AddMentorPublisher)
    viewModelOf(::AddMentorViewModel)
}