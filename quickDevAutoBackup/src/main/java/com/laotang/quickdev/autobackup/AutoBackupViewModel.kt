package com.laotang.quickdev.autobackup

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class AutoBackupViewModel(application: Application) : AndroidViewModel(application),LifecycleObserver {

    private var owner: LifecycleOwner? = null
    private lateinit var mACache:ACache
    private lateinit var mGson: Gson
    private var onSaveInstanceStateList:MutableList<AutoBackup.Transaction.OnSaveInstanceState>?=null

    internal fun setSaveInstanceStateListener(onSaveInstanceStateList:MutableList<AutoBackup.Transaction.OnSaveInstanceState>){
        this.onSaveInstanceStateList = onSaveInstanceStateList
    }

    internal fun setACache(aCache:ACache){
        this.mACache = aCache
    }

    internal fun setGson(gson: Gson){
        this.mGson = gson
    }


    fun <T> getBackupData(key:String,clazz:Class<T>): Observable<T>{
        return Observable.defer<T> {
            Observable.create {
                val value = mACache.getAsString(key)
                if (value != null) {
                    it.onNext(mGson.fromJson(value, clazz))
                }
                it.onComplete()
            }
        }
    }

    internal fun clear(key:String){
        try {
            mACache.file(key)?.apply {
                mACache.remove(key)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    internal fun clearAll(){
        try {
            mACache.clear()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        this.owner = owner
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        onSaveInstanceStateList?.apply {
            Observable.create<BackupData> { emitter->
                try {
                    forEach {
                        it.onSave()?.apply {
                            emitter.onNext(this)
                        }
                    }
                    emitter.onComplete()
                }catch (e:Exception){
                    e.printStackTrace()
                    emitter.onError(e)
                }
            }.toFlowable(BackpressureStrategy.BUFFER)
                .observeOn(Schedulers.io())
                .compose {
                    it.flatMap { entry ->
                        mACache.put(entry.key, mGson.toJson(entry.data))
                        Flowable.just(Unit)
                    }
                }.subscribe()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        onSaveInstanceStateList?.apply {
            clear()
        }
        onSaveInstanceStateList = null
        owner?.apply {
            lifecycle.removeObserver(this@AutoBackupViewModel)
        }
        owner = null
    }
}