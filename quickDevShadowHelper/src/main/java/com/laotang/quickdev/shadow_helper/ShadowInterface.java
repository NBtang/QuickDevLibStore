package com.laotang.quickdev.shadow_helper;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ShadowInterface implements IShadowInterface {
    private static final ShadowInterface ourInstance = new ShadowInterface();

    public static ShadowInterface getInstance() {
        return ourInstance;
    }

    private IShadowInterface impl;

    private ShadowInterface() {
    }

    public void setImpl(IShadowInterface impl) {
        this.impl = impl;
    }

    @Override
    public Bitmap syncEncodeQRCode(String content, Bitmap logoBitmap, int size, int foregroundColor, int backgroundColor) {
        if (impl == null) {
            throw new RuntimeException("please set impl");
        }
        return impl.syncEncodeQRCode(content, logoBitmap, size, foregroundColor, backgroundColor);
    }

    @Override
    public String syncDecodeQRCode(Bitmap bitmap) {
        if (impl == null) {
            throw new RuntimeException("please set impl");
        }
        return impl.syncDecodeQRCode(bitmap);
    }

    @Override
    public List<File> imageCompress(List<String> filePaths, String setTargetDir, int ignoreBy, boolean focusAlpha) throws IOException {
        if (impl == null) {
            throw new RuntimeException("please set impl");
        }
        return impl.imageCompress(filePaths, setTargetDir, ignoreBy, focusAlpha);
    }

    @Override
    public File imageCompress(String filePath, String setTargetDir, int ignoreBy, boolean focusAlpha) throws IOException {
        if (impl == null) {
            throw new RuntimeException("please set impl");
        }
        return impl.imageCompress(filePath, setTargetDir, ignoreBy, focusAlpha);
    }

    @Override
    public String addOSSClient(String accessKeyId, String accessKeySecret, String endpoint) {
        if (impl == null) {
            throw new RuntimeException("please set impl");
        }
        return impl.addOSSClient(accessKeyId, accessKeySecret, endpoint);
    }

    @Override
    public String putObjectToOSS(String ossToken, String bucketName, String rawObjectKey, String uploadFilePath, ProgressCallback progressCallback) {
        if (impl == null) {
            throw new RuntimeException("please set impl");
        }
        return impl.putObjectToOSS(ossToken, bucketName, rawObjectKey, uploadFilePath, progressCallback);
    }

    @Override
    public String presignPublicObjectURL(String ossToken, String bucketName, String objectKey) {
        if (impl == null) {
            throw new RuntimeException("please set impl");
        }
        return impl.presignPublicObjectURL(ossToken, bucketName, objectKey);
    }

}
