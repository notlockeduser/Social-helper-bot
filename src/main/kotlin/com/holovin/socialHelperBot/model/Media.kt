package com.holovin.socialHelperBot.model

data class Media(
    val urls: List<String>,
    val type: MediaType
)

enum class MediaType {
    PHOTO, VIDEO
}