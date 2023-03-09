package com.holovin.socialHelperBot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SocialHelperBotApplication

fun main(args: Array<String>) {
    runApplication<SocialHelperBotApplication>(*args)
}

