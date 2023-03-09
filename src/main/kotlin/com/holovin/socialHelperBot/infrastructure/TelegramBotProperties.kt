package com.holovin.socialHelperBot.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "telegram.bot")
data class TelegramBotProperties(
    var username: String? = null,
    var token: String? = null
)