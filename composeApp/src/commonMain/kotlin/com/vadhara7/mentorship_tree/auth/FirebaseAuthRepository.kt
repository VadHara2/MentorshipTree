package com.vadhara7.mentorship_tree.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.OAuthProvider

/**
 * Firebase implementation of [AuthRepository].
 */
class FirebaseAuthRepository(
    private val auth: FirebaseAuth = Firebase.auth
) : AuthRepository {

    override fun currentUserId(): String? = auth.currentUser?.uid

    override suspend fun signInWithGoogle(idToken: String): String {
        val credential: AuthCredential = GoogleAuthProvider.credential(idToken, null)
        val result = auth.signInWithCredential(credential)
        return result.user?.uid ?: ""
    }

    override suspend fun signInWithApple(idToken: String): String {
        val credential = OAuthProvider.credentialWithProvider("apple.com") {
            setIdToken(idToken)
        }
        val result = auth.signInWithCredential(credential)
        return result.user?.uid ?: ""
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}
