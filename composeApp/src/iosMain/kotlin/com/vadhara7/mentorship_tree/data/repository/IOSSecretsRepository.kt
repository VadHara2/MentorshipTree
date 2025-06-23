package com.vadhara7.mentorship_tree.data.repository

import platform.Foundation.NSBundle

actual class SecretsRepository actual constructor() {
    actual fun getGoogleAuthServerId(): String {

        val info = NSBundle
            .mainBundle
            .infoDictionary
            ?: error("Info.plist not found in bundle")

        // Повертаємо значення під ключем "API_KEY"
        return (info["GIDServerClientID"] as? String)
            ?: error("GIDServerClientID not set in Info.plist")
    }
}