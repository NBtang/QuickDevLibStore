package com.laotang.quickdevcore.integration.http.utl

import okhttp3.HttpUrl

interface OnUrlParserListener {
    fun parseUrl(domainUrl: HttpUrl?, url: HttpUrl): HttpUrl?
}