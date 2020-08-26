package com.laotang.quickdev.shadow_helper;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IShadowInterface {
    Bitmap syncEncodeQRCode(String content, Bitmap logoBitmap, int size, int foregroundColor, int backgroundColor);

    String syncDecodeQRCode(Bitmap bitmap);

    List<File> imageCompress(List<String> filePaths, String setTargetDir, int ignoreBy, boolean focusAlpha) throws IOException;

    File imageCompress(String filePath, String setTargetDir, int ignoreBy, boolean focusAlpha) throws IOException;

    String addOSSClient(String accessKeyId, String accessKeySecret, String endpoint);

    String putObjectToOSS(String ossToken, String bucketName, String rawObjectKey, String uploadFilePath, ProgressCallback progressCallback);

    String presignPublicObjectURL(String ossToken, String bucketName, String objectKey);

    public interface ProgressCallback {
        public void onProgress(long currentSize, long totalSize);
    }
}
