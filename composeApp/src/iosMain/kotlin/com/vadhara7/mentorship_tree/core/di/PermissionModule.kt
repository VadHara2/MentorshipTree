package com.vadhara7.mentorship_tree.core.di

import com.vadhara7.mentorship_tree.data.repository.IosPermissionRepository
import com.vadhara7.mentorship_tree.domain.repository.PermissionRepository
import org.koin.dsl.module

val permissionModule = module {
    single<PermissionRepository> { IosPermissionRepository() }
}
