package com.holovin.socialHelperBot.app

import com.holovin.socialHelperBot.model.Media

interface DownloadService {

    fun getStreamLinks(link: String): Media
}
