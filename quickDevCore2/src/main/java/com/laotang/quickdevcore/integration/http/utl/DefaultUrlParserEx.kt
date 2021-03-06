package com.laotang.quickdevcore.integration.http.utl

import com.laotang.quickdevcore.utils.rootKodein
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import me.jessyan.retrofiturlmanager.parser.AdvancedUrlParser
import me.jessyan.retrofiturlmanager.parser.DomainUrlParser
import me.jessyan.retrofiturlmanager.parser.SuperUrlParser
import me.jessyan.retrofiturlmanager.parser.UrlParser
import okhttp3.HttpUrl

import me.jessyan.retrofiturlmanager.RetrofitUrlManager.IDENTIFICATION_PATH_SIZE
import org.kodein.di.generic.instance

class DefaultUrlParserEx : UrlParser {
    private lateinit var mDomainUrlParser: UrlParser

    @Volatile
    private var mAdvancedUrlParser: UrlParser? = null

    @Volatile
    private var mSuperUrlParser: UrlParser? = null
    private lateinit var mRetrofitUrlManager: RetrofitUrlManager
    private lateinit var urlManagerService: RetrofitUrlManagerServiceImpl

    private var urlParseMap: HashMap<String, OnUrlParserListener>? = null

    override fun init(retrofitUrlManager: RetrofitUrlManager) {
        this.mRetrofitUrlManager = retrofitUrlManager
        this.mDomainUrlParser = DomainUrlParser().apply { init(retrofitUrlManager) }
        val urlManagerService by rootKodein().instance<IRetrofitUrlManagerService>()
        this.urlManagerService = urlManagerService as RetrofitUrlManagerServiceImpl
    }

    override fun parseUrl(domainUrl: HttpUrl?, url: HttpUrl): HttpUrl {
        if (null == domainUrl) return url

        if (urlParseMap == null) {
            urlParseMap = HashMap()
        }
        var onUrlParserListener = urlParseMap!![domainUrl.toString()]
        if (onUrlParserListener != null) {
            val newUrl = onUrlParserListener.parseUrl(domainUrl, url)
            if (newUrl != null) {
                return newUrl
            }
        } else {
            val onUrlParserListeners = urlManagerService.getUrlParserListeners()
            if (onUrlParserListeners.isNotEmpty()) {
                onUrlParserListeners.forEach {
                    if (it.getDomainUrl() != null && domainUrl.toString() == it.getDomainUrl().toString()) {
                        onUrlParserListener = it
                    }
                }
                if (onUrlParserListener != null) {
                    urlParseMap!![domainUrl.toString()] = onUrlParserListener!!
                }
                val newUrl = onUrlParserListener?.parseUrl(domainUrl, url)
                if (newUrl != null) {
                    return newUrl
                }
            }
        }

        if (url.toString().contains(IDENTIFICATION_PATH_SIZE)) {
            if (mSuperUrlParser == null) {
                synchronized(this) {
                    if (mSuperUrlParser == null) {
                        mSuperUrlParser = SuperUrlParser()
                        mSuperUrlParser!!.init(mRetrofitUrlManager)
                    }
                }
            }
            return mSuperUrlParser!!.parseUrl(domainUrl, url)
        }

        //如果是高级模式则使用高级解析器
        if (mRetrofitUrlManager.isAdvancedModel) {
            if (mAdvancedUrlParser == null) {
                synchronized(this) {
                    if (mAdvancedUrlParser == null) {
                        mAdvancedUrlParser = AdvancedUrlParser()
                        mAdvancedUrlParser!!.init(mRetrofitUrlManager)
                    }
                }
            }
            return mAdvancedUrlParser!!.parseUrl(domainUrl, url)
        }
        return mDomainUrlParser.parseUrl(domainUrl, url)
    }
}
