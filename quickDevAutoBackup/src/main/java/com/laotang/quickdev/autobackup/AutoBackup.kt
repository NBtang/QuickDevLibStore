package com.laotang.quickdev.autobackup

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import java.io.File

class AutoBackup private constructor(private val gson:Gson,private val cacheDirPath:String):Application.ActivityLifecycleCallbacks{

    private var mACache:ACache?=null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if(mACache == null){
            val cacheFile = if(cacheDirPath.isEmpty()){
                getCacheFile(activity)
            }else{
                File(cacheDirPath)
            }
            mACache = ACache.get(cacheFile)
        }
        if(activity is FragmentActivity){
            if(activity.javaClass.isAnnotationPresent(Backup::class.java)){
                val backup = activity.javaClass.getAnnotation(Backup::class.java)
                if(backup.value){
                    val viewModel = ViewModelProviders.of(activity).get(AutoBackupViewModel::class.java)
                    viewModel.setGson(gson)
                    viewModel.setACache(mACache!!)
                    activity.lifecycle.addObserver(viewModel)
                }
            }
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object :FragmentManager.FragmentLifecycleCallbacks(){
                override fun onFragmentCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    savedInstanceState: Bundle?
                ) {
                    super.onFragmentCreated(fm, f, savedInstanceState)
                    if(f.javaClass.isAnnotationPresent(Backup::class.java)){
                        val backup = f.javaClass.getAnnotation(Backup::class.java)
                        if(backup.value){
                            val viewModel = ViewModelProviders.of(f).get(AutoBackupViewModel::class.java)
                            viewModel.setGson(gson)
                            viewModel.setACache(mACache!!)
                            f.lifecycle.addObserver(viewModel)
                        }
                    }
                }
            }, true)
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    private fun clearAll(){
        mACache?.apply {
            clear()
        }
    }

    private fun getCacheFile(context: Context): File {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            var file: File? = null
            file = context.externalCacheDir//获取系统管理的sd卡缓存文件
            if (file == null) {//如果获取的文件为空,就使用自己定义的缓存文件夹做缓存路径
                file = File(getCacheFilePath(context))
                makeDirs(file)
            }
            file
        } else {
            context.cacheDir
        }
    }

    private fun makeDirs(file: File): File {
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    /**
     * 获取自定义缓存文件地址
     *
     * @param context
     * @return
     */
    private fun getCacheFilePath(context: Context): String {
        val packageName = context.packageName
        return Environment.getExternalStorageDirectory().path + packageName
    }

    class Manager internal constructor(private val viewModel: AutoBackupViewModel){
        fun <T> getBackupData(key: String,clazz: Class<T>): Observable<T> {
            return viewModel.getBackupData(key,clazz)
        }

        fun clear(key:String){
            viewModel.clear(key)
        }

        fun clearAll(){
            viewModel.clearAll()
        }
    }

    class Transaction internal constructor(private val viewModel: AutoBackupViewModel){

        private var onSaveInstanceStateList:MutableList<OnSaveInstanceState>?=null

        fun addSaveInstanceStateListener(onSaveInstanceState:OnSaveInstanceState):Transaction{
            if(onSaveInstanceStateList == null){
                onSaveInstanceStateList = mutableListOf()
            }
            onSaveInstanceStateList!!.add(onSaveInstanceState)
            return this
        }

        fun commit(){
            onSaveInstanceStateList?.apply {
                viewModel.setSaveInstanceStateListener(this)
            }
        }

        interface OnSaveInstanceState{
            fun onSave():BackupData?
        }
    }

    companion object {
        private var gson:Gson?=null
        private var cacheDirPath:String?=null

        private val HOLDER: AutoBackup by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AutoBackup(gson?:GsonBuilder().create(),cacheDirPath?:"")
        }

        fun getInstance():AutoBackup{
            return HOLDER
        }

        fun setGson(gson:Gson){
            this.gson = gson
        }

        fun cacheDirPath(path:String){
            this.cacheDirPath = path
        }

        fun clearAll(){
            getInstance().clearAll()
        }
    }
}

fun FragmentActivity.getAutoBackupManager():AutoBackup.Manager{
    val viewModel = ViewModelProviders.of(this).get(AutoBackupViewModel::class.java)
    return AutoBackup.Manager(viewModel)
}

fun Fragment.getAutoBackupManager():AutoBackup.Manager{
    val viewModel = ViewModelProviders.of(this).get(AutoBackupViewModel::class.java)
    return AutoBackup.Manager(viewModel)
}

fun FragmentActivity.getAutoBackupTransaction():AutoBackup.Transaction{
    val viewModel = ViewModelProviders.of(this).get(AutoBackupViewModel::class.java)
    return AutoBackup.Transaction(viewModel)
}

fun Fragment.getAutoBackupTransaction():AutoBackup.Transaction{
    val viewModel = ViewModelProviders.of(this).get(AutoBackupViewModel::class.java)
    return AutoBackup.Transaction(viewModel)
}