package com.laotang.quickdevcore.integration.http

class ServerException(msg: String,val code:Int=0) : Exception(msg)