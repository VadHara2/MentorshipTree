package com.vadhara7.mentorship_tree.data.repository

import com.vadhara7.mentorship_tree.BuildConfig
import com.vadhara7.mentorship_tree.domain.repository.SecretsRepository

actual class SecretsRepositoryImpl : SecretsRepository {
    override fun getGoogleAuthServerId(): String = BuildConfig.GOOGLE_SERVER_ID
}