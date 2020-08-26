package com.laotang.quickext

fun String.encryptDES(iv: String, key: String): String {
    return CryptoUtil.encryptDES(this, iv, key)
}

fun String.decryptDES(iv: String, key: String): String {
    return CryptoUtil.decryptDES(this, iv, key)
}