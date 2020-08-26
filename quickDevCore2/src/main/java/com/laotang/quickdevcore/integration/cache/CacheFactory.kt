package com.laotang.quickdevcore.integration.cache

import android.content.Context

class CacheFactory(val context: Context): Cache.Factory {
    override fun <K,V> build(type: CacheType): Cache<K, V> {
        //若想自定义 LruCache 的 size, 或者不想使用 LruCache, 想使用自己自定义的策略
        //使用 GlobalConfigModule.Builder#cacheFactory() 即可扩展
        return when (type.getCacheTypeId()) {
            CacheType.EXTRAS_TYPE_ID-> IntelligentCache(type.calculateCacheSize(context))
            else -> LruCacheProxy(type.calculateCacheSize(context))
        }
    }
}