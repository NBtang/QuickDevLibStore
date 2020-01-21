package com.laotang.quickdev.httplogging;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

import static okhttp3.internal.platform.Platform.INFO;

public final class HttpSafetyInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    public static final String SECRET_HEADER = "Secret-Header";

    public static class Level {
        public static final String NONE = "NONE";
        public static final String ENCRYPT = "ENCRYPT";
        public static final String DECRYPT = "DECRYPT";
        public static final String BOTH = "BOTH";
    }

    public interface OnSecretCallback {
        RequestBody encrypt(String url, String value, MediaType contentType);

        ResponseBody decrypt(String url, String value, MediaType contentType);
    }

    public HttpSafetyInterceptor(OnSecretCallback onSecretCallback) {
        this.onSecretCallback = onSecretCallback;
    }

    private final OnSecretCallback onSecretCallback;

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder newBuilder = request.newBuilder();
        String secret = obtainSecretFromHeaders(request);
        if (secret == null) {
            return chain.proceed(request);
        }
        if (Level.NONE.equals(secret)) {
            return chain.proceed(request);
        }
        boolean needEncrypt = false;
        boolean needDecrypt = false;

        switch (secret) {
            case Level.BOTH:
                needEncrypt = true;
                needDecrypt = true;
                break;
            case Level.ENCRYPT:
                needEncrypt = true;
                break;
            case Level.DECRYPT:
                needDecrypt = true;
                break;
        }

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        String url = request.url().toString();
        Request newRequest;
        if (needEncrypt && hasRequestBody && requestBody.contentLength()>0) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (!isPlaintext(buffer)) {
                return chain.proceed(request);
            }
            String method = request.method();
            RequestBody newRequestBody = onSecretCallback.encrypt(url, buffer.readString(charset), contentType);
            if ("POST".equals(method)) {
                newBuilder.post(newRequestBody);
            } else if ("PUT".equals(method)) {
                newBuilder.put(newRequestBody);
            } else if ("PATCH".equals(method)) {
                newBuilder.patch(newRequestBody);
            } else if ("DELETE".equals(method)) {
                newBuilder.delete(newRequestBody);
            }
            newBuilder.removeHeader(SECRET_HEADER);
            newRequest = newBuilder.build();
        } else {
            newRequest = request;
        }

        Response response = chain.proceed(newRequest);

        Response.Builder newResponseBuild = response.newBuilder();
        ResponseBody responseBody = response.body();
        if (!bodyHasUnknownEncoding(response.headers()) && HttpHeaders.hasBody(response)) {
            if (needDecrypt) {
                Headers headers = response.headers();
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer responseBodyBuffer = source.buffer();

                if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                    GzipSource gzippedResponseBody = null;
                    try {
                        gzippedResponseBody = new GzipSource(responseBodyBuffer.clone());
                        responseBodyBuffer = new Buffer();
                        responseBodyBuffer.writeAll(gzippedResponseBody);
                    } finally {
                        if (gzippedResponseBody != null) {
                            gzippedResponseBody.close();
                        }
                    }
                }

                Charset responseCharset = UTF8;
                MediaType responseContentType = responseBody.contentType();
                if (responseContentType != null) {
                    responseCharset = responseContentType.charset(UTF8);
                }
                if (!isPlaintext(responseBodyBuffer)) {
                    return response;
                }
                long contentLength = responseBody.contentLength();
                if (contentLength != 0) {
                    ResponseBody newResponseBody = onSecretCallback.decrypt(url, responseBodyBuffer.clone().readString(responseCharset), responseContentType);
                    return newResponseBuild.body(newResponseBody).build();
                }
            }
        }
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }

    private String obtainSecretFromHeaders(Request request) {
        List<String> headers = request.headers(SECRET_HEADER);
        if (headers == null || headers.size() == 0)
            return null;
        if (headers.size() > 1)
            throw new IllegalArgumentException("Only one Secret-Header in the headers");
        return request.header(SECRET_HEADER);
    }
}
