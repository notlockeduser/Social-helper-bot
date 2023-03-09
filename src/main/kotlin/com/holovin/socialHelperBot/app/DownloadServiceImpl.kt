package com.holovin.socialHelperBot.app

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.holovin.socialHelperBot.model.Media
import com.holovin.socialHelperBot.model.MediaType
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class DownloadServiceImpl : DownloadService {

    private val client = OkHttpClient()
    private val gson = Gson()

    override fun getStreamLinks(link: String): Media {
        val jsonString = getTikVmJson(link)
        val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
        val dataJsonObject = jsonObject.getAsJsonObject("data")
        return if (dataJsonObject.has("images"))
            Media(dataJsonObject.getAsJsonArray("images").asList().map { it.asString }, MediaType.PHOTO)
        else Media(listOf(dataJsonObject.get("play").asString), MediaType.VIDEO)
    }

    private fun getTikVmJson(link: String): String {
        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("www.tikwm.com")
            .addPathSegment("api")
            .addQueryParameter("url", link)
            .build()

        val request = Request.Builder()
            .url(httpUrl)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val responseBody = response.body().string()
        logger.info("Response = {}", responseBody)
        return responseBody
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}