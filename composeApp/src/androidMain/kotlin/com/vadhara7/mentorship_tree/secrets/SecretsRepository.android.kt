package com.vadhara7.mentorship_tree.secrets

import com.vadhara7.mentorship_tree.BuildConfig


actual class SecretsRepository actual constructor() {
    actual fun getGoogleAuthServerId(): String = BuildConfig.GOOGLE_SERVER_ID
}