package com.vadhara7.mentorship_tree.auth

import platform.Foundation.NSObject

/**
 * Placeholder implementation of [AuthTokenProvider] for iOS.
 * Integrating Sign in with Apple or Google should be done here using
 * the platform APIs. Currently returns `null`.
 */
class IosAuthTokenProvider : NSObject(), AuthTokenProvider {
    override suspend fun getGoogleIdToken(): String? = null
    override suspend fun getAppleIdToken(): String? = null
}
