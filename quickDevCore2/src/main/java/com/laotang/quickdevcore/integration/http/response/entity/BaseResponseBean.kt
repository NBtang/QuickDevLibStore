package com.laotang.quickdevcore.integration.http.response.entity

class BaseResponseBean<T>:ResponseBean<T>() {
    var code: Int=0
    var message: String=""
    var success: Boolean=false
}