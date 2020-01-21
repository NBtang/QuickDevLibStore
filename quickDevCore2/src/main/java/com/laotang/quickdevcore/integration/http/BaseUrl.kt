package com.laotang.quickdevcore.integration.http

import okhttp3.HttpUrl

interface BaseUrl {
    fun url(): HttpUrl?
}