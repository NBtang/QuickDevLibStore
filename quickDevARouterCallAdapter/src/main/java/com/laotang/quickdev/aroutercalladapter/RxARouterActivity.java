package com.laotang.quickdev.aroutercalladapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.laotang.quickdev.rxactivity.RxReportFragment;

public class RxARouterActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        String relativeUrl = intent.getStringExtra("relativeUrl");
        Bundle bundle = intent.getBundleExtra("bundle");
        int flags = intent.getIntExtra("flags", -1);
        boolean isGreenChannel = intent.getBooleanExtra("isGreenChannel", false);
        Postcard postcard = ARouter.getInstance()
                .build(relativeUrl)
                .with(bundle)
                .withFlags(flags);
        if (isGreenChannel) {
            postcard.greenChannel();
        }
        postcard.navigation(this, RxReportFragment.RX_ACTIVITY_RESULT_REQUEST_CODE, new NavCallback(){

            @Override
            public void onArrival(Postcard postcard) {

            }

            @Override
            public void onLost(Postcard postcard) {
                finish();
            }

            @Override
            public void onInterrupt(Postcard postcard) {
                finish();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RxReportFragment.RX_ACTIVITY_RESULT_REQUEST_CODE) {
            setResult(resultCode, data);
        }
        finish();
    }
}
