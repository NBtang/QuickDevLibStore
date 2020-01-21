package com.laotang.quickext

inline fun <reified T> quickConfig(init:T.()->Unit):T{
    val instance = T::class.java.newInstance()
    instance.init()
    return instance
}