package com.laotang.quickdev.rxactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

import io.reactivex.subjects.PublishSubject;

public class RxReportFragment extends Fragment {
    public static final int RX_ACTIVITY_RESULT_REQUEST_CODE = 1024;
    private SparseArray<PublishSubject<ActivityResultEntity>> mSubject = new SparseArray<>();

    public RxReportFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    PublishSubject<ActivityResultEntity> navigation(Intent intent,@Nullable Bundle options) {
        Random random1 = new Random(System.currentTimeMillis());
        int code1 = random1.nextInt(4000);
        Random random2 = new Random(System.currentTimeMillis());
        int code2 = random2.nextInt(4000);
        int requestCode = code1+code2;
        startActivityForResult(intent,requestCode,options);
        PublishSubject<ActivityResultEntity> subject = mSubject.get(requestCode);
        if(subject == null){
            subject = PublishSubject.create();
            mSubject.put(requestCode,subject);
        }
        return subject;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultEntity entity = new ActivityResultEntity();
        entity.requestCode = requestCode;
        entity.resultCode = resultCode;
        entity.data = data;
        PublishSubject<ActivityResultEntity> subject = mSubject.get(requestCode);
        if(subject!=null){
            subject.onNext(entity);
            subject.onComplete();
            mSubject.remove(requestCode);
        }
    }
}
