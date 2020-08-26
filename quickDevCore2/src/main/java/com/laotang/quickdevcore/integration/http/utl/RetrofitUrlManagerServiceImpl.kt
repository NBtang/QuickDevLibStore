package com.laotang.quickdevcore.integration.http.utl

import me.jessyan.retrofiturlmanager.RetrofitUrlManager

class RetrofitUrlManagerServiceImpl :
    IRetrofitUrlManagerService {
    private var mOnUrlParserListeners: MutableList<OnUrlParserListener>? = null

    override fun putDomain(domainName: String, domainUrl: String) {
        RetrofitUrlManager.getInstance().putDomain(domainName, domainUrl)
    }

    @Synchronized
    override fun addUrlParserListener(listener: OnUrlParserListener) {
        getUrlParserListeners().add(listener)
    }

    override fun removeDomain(domainName: String) {
        removeUrlParserListener(domainName)
        RetrofitUrlManager.getInstance().removeDomain(domainName)
    }

    @Synchronized
    override fun removeUrlParserListener(domainName: String) {
        RetrofitUrlManager.getInstance().fetchDomain(domainName)?.apply {
            var index = 0
            getUrlParserListeners().forEachIndexed { i, onUrlParserListener ->
                val httpUrl = onUrlParserListener.getDomainUrl()
                if (httpUrl != null && this.toString() == httpUrl.toString()) {
                    index = i
                }
            }
            getUrlParserListeners().removeAt(index)
        }
    }

    @Synchronized
    override fun removeUrlParserListener(listener: OnUrlParserListener) {
        getUrlParserListeners().remove(listener)
    }

    internal fun getUrlParserListeners(): MutableList<OnUrlParserListener> {
        if (mOnUrlParserListeners == null) {
            mOnUrlParserListeners = mutableListOf()
        }
        return mOnUrlParserListeners!!
    }
}
