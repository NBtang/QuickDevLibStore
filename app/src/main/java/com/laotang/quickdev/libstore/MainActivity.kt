package com.laotang.quickdev.libstore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.laotang.quickdev.libstore.app.router.ActivityRouter
import com.laotang.quickdev.livedata.eventbus.LiveDataEventBus
import com.laotang.quickext.observe
import kotterknife.bindView
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {
    private val btnMain: TextView by bindView(R.id.btn_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnMain.observe(this) {
            ActivityRouter.startSecondActivity(this)
//            EventBus.getDefault().post("test")

        }

        LiveDataEventBus.toLiveData(this, MutableLiveData<String>()).observe(this, Observer {
//            it
        })
    }
}
