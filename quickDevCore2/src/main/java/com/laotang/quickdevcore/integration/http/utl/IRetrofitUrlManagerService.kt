package com.laotang.quickdevcore.integration.http.utl


interface IRetrofitUrlManagerService {
    fun putDomain(domainName: String, domainUrl: String)
    fun addUrlParserListener(listener: OnUrlParserListener)
    fun removeDomain(domainName: String)
    fun removeUrlParserListener(domainName: String)
    fun removeUrlParserListener(listener: OnUrlParserListener)
}