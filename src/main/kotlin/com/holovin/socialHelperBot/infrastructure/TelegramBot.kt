package com.holovin.socialHelperBot.infrastructure

import com.holovin.socialHelperBot.app.DownloadService
import com.holovin.socialHelperBot.model.MediaType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.net.URL

@Service
class TelegramBot(
    private val properties: TelegramBotProperties,
    private val downloadService: DownloadService
) : TelegramLongPollingBot(properties.token) {


    override fun getBotUsername(): String = properties.username!!


    override fun onUpdateReceived(update: Update) {
        logger.info("Received message {}", update)
        if (update.hasMessage() && update.message.hasText()) {
            val chatId = update.message.chatId

            when (val text = update.message.text) {
                "/start" -> sendWelcomeMessage(chatId)
                "/about" -> sendAboutMessage(chatId)
                else -> sendMediaMessage(text, chatId)
            }
        }
    }

    private fun sendWelcomeMessage(chatId: Long) {
        val message = "Welcome to SocialHelperBot! Here are the available commands:\n\n" +
                "/help - Displays the available commands\n" +
                "/about - Displays information about the bot"
        val sendMessage = SendMessage(chatId.toString(), message)
        executeCommand(sendMessage)
    }

    private fun sendAboutMessage(chatId: Long) {
        val message = "SocialHelperBot is a bot that helps manage a channel with media files."
        val sendMessage = SendMessage(chatId.toString(), message)
        executeCommand(sendMessage)
    }

    private fun sendMediaMessage(text: String, chatId: Long) {
        val media = downloadService.getStreamLinks(text)
        if (media.urls.size == 1) {
            val sendVideo = SendVideo()
            sendVideo.chatId = chatId.toString()
            sendVideo.video = InputFile(URL(media.urls[0]).openStream(), "file")
            execute(sendVideo)
        } else {
            val sendMediaGroup = SendMediaGroup()
            sendMediaGroup.chatId = chatId.toString()
            sendMediaGroup.medias = media.urls.map {
                val inputMedia = if (media.type == MediaType.PHOTO) InputMediaPhoto() else InputMediaVideo()
                inputMedia.setMedia(URL(it).openStream(), "file")
                inputMedia
            }
            execute(sendMediaGroup)
        }
    }

    private fun executeCommand(message: BotApiMethodMessage) {
        try {
            execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}
