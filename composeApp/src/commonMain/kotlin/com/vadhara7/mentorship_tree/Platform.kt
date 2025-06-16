package com.vadhara7.mentorship_tree

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform