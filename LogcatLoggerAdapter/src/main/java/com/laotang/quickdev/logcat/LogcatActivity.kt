package com.laotang.quickdev.logcat

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

open class LogcatActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var mDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logcat_viewer)
        val logcatTextColorRes = intent.getIntExtra("logcatTextColorRes", 0)
        val logcatTextSize = intent.getIntExtra("logcatTextSize", 0)
        val logcatBgColorRes = intent.getIntExtra("logcatBgColorRes", 0)

        if (logcatBgColorRes != 0) {
            findViewById<View>(R.id.root_view).setBackgroundResource(logcatBgColorRes)
        }
        if (logcatTextColorRes != 0) {
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            toolbar.setTitleTextColor(resources.getColor(logcatTextColorRes))
        }

        recyclerView = findViewById(R.id.recyclerView)
        val adapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_logccat_log) {
            override fun convert(helper: BaseViewHolder, item: String) {
                val textView = helper.getView<TextView>(R.id.tv_log)
                textView.text = item
                if (logcatTextColorRes != 0) {
                    textView.setTextColor(resources.getColor(logcatTextColorRes))
                }
                if (logcatTextSize != 0) {
                    textView.textSize = logcatTextSize.toFloat()
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val logFilePath = LogcatLogDiskStrategy.getLogcatLoggerFilePath()
        if (logFilePath.isEmpty()) {
            return
        }
        val logFile = File(logFilePath)
        if (!logFile.exists()) {
            return
        }
        if (!logFile.isDirectory) {
            return
        }

        Observable.just(logFile)
            .map { dir ->
                val files = mutableListOf<File>()
                dir.listFiles()?.forEach {
                    files.add(it)
                }
                //按时间倒序排序，最新的日志文件在顶部
                files.sortByDescending {
                    it.lastModified()
                }
                return@map files
            }
            .flatMap {
                if (it.isEmpty()) {
                    return@flatMap Observable.just(mutableListOf<String>())
                } else {
                    return@flatMap Observable.create<List<String>> { emitter ->
                        val last = it[0].readLines()
                        val newData = mutableListOf<String>()
                        if (last.size < 100 && it.size > 1) {
                            //有可能是刚刚创建的日志文件，记录的日志不是很多，同时存在更早的日志文件，最多读取两个日志文件
                            newData.addAll(it[1].readLines())
                        }
                        newData.addAll(last)
                        emitter.onNext(newData)
                        emitter.onComplete()
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<String>> {
                override fun onComplete() {
                    if (ProgressDialogUtil.isShowing()) {
                        ProgressDialogUtil.dismissLoadingDialog()
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    mDisposable = d
                    ProgressDialogUtil.showLoadingDialog(this@LogcatActivity, "加载中", false)
                }

                override fun onNext(t: List<String>) {
                    if (ProgressDialogUtil.isShowing()) {
                        ProgressDialogUtil.dismissLoadingDialog()
                    }
                    adapter.setNewData(t)
                    recyclerView.scrollToPosition(t.size - 1)
                }

                override fun onError(e: Throwable) {
                    if (ProgressDialogUtil.isShowing()) {
                        ProgressDialogUtil.dismissLoadingDialog()
                    }
                }
            })
    }

    override fun onDestroy() {
        mDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        super.onDestroy()
    }

    class LogcatActivityIntentBuilder {
        private var logcatTextColorRes: Int = 0
        private var logcatTextSize: Int = 0
        private var logcatBgColorRes: Int = 0
        private var requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND

        fun setLogcatTextColor(@ColorRes logcatTextColorRes: Int): LogcatActivityIntentBuilder {
            this.logcatTextColorRes = logcatTextColorRes
            return this
        }

        fun setLogcatBgColorRes(@ColorRes logcatBgColorRes: Int): LogcatActivityIntentBuilder {
            this.logcatBgColorRes = logcatBgColorRes
            return this
        }

        fun setLogcatTextSize(logcatTextSize: Int): LogcatActivityIntentBuilder {
            this.logcatTextSize = logcatTextSize
            return this
        }

        fun setRequestedOrientation(requestedOrientation: Int): LogcatActivityIntentBuilder {
            this.requestedOrientation = requestedOrientation
            return this
        }

        fun build(context: Context): Intent {
            val intent = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            ) {
                Intent(context, LogcatLandscapeActivity::class.java)
            } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            ) {
                Intent(context, LogcatPortraitActivity::class.java)
            } else {
                Intent(context, LogcatActivity::class.java)
            }
            intent.putExtra("logcatTextColorRes", logcatTextColorRes)
            intent.putExtra("logcatTextSize", logcatTextSize)
            intent.putExtra("logcatBgColorRes", logcatBgColorRes)
            return intent
        }
    }

}