package com.vadhara7.mentorship_tree.data.repository

import com.vadhara7.mentorship_tree.domain.repository.SecretsRepository
import platform.Foundation.NSBundle

actual class SecretsRepositoryImpl : SecretsRepository {
    override fun getGoogleAuthServerId(): String {

        val info = NSBundle
            .mainBundle
            .infoDictionary
            ?: error("Info.plist not found in bundle")

        return (info["GIDServerClientID"] as? String)
            ?: error("GIDServerClientID not set in Info.plist")
    }
}