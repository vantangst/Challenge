package com.co.challengeliv3ly.interceptor;


import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoggingInterceptor implements Interceptor {
    private final boolean isDebug;
    private final Builder builder;

    private LoggingInterceptor(Builder builder) {
        this.builder = builder;
        this.isDebug = builder.isDebug;
    }

    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        HashMap<String, String> headerMap = this.builder.getHeaders();
        String rSubtype;
        String key;
        if (headerMap.size() > 0) {
            Request.Builder requestBuilder = request.newBuilder();

            for (Object o : headerMap.keySet()) {
                rSubtype = (String) o;
                key = headerMap.get(rSubtype);
                assert key != null;
                requestBuilder.addHeader(rSubtype, key);
            }

            request = requestBuilder.build();
        }

        HashMap<String, String> queryMap = this.builder.getHttpUrl();
        if (queryMap.size() > 0) {
            okhttp3.HttpUrl.Builder httpUrlBuilder = request.url().newBuilder(request.url().toString());

            for (Object o : queryMap.keySet()) {
                key = (String) o;
                String value = queryMap.get(key);
                assert httpUrlBuilder != null;
                httpUrlBuilder.addQueryParameter(key, value);
            }

            request = request.newBuilder().url(httpUrlBuilder.build()).build();
        }

        if (this.isDebug && this.builder.getLevel() != Logger.Level.NONE) {
            RequestBody requestBody = request.body();
            rSubtype = null;
            if (requestBody != null && requestBody.contentType() != null) {
                rSubtype = requestBody.contentType().subtype();
            }

            Executor executor = this.builder.executor;
            if (this.isNotFileRequest(rSubtype)) {
                if (executor != null) {
                    executor.execute(createPrintJsonRequestRunnable(this.builder, request));
                } else {
                    Printer.printJsonRequest(this.builder, request);
                }
            } else if (executor != null) {
                executor.execute(createFileRequestRunnable(this.builder, request));
            } else {
                Printer.printFileRequest(this.builder, request);
            }

            long st = System.nanoTime();
            Response response;
            if (this.builder.isMockEnabled) {
                try {
                    TimeUnit.MILLISECONDS.sleep(this.builder.sleepMs);
                } catch (InterruptedException var24) {
                    var24.printStackTrace();
                }

                response = new Response.Builder()
                        .body(ResponseBody.create(MediaType.parse("application/json"), this.builder.listener.getJsonResponse(request)))
                        .request(chain.request()).protocol(Protocol.HTTP_2)
                        .message("Mock")
                        .code(200)
                        .build();
            } else {
                response = chain.proceed(request);
            }

            long chainMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - st);
            List<String> segmentList = request.url().encodedPathSegments();
            String header = response.headers().toString();
            int code = response.code();
            boolean isSuccessful = response.isSuccessful();
            String message = response.message();
            ResponseBody responseBody = response.body();
            MediaType contentType = responseBody.contentType();
            String subtype = null;
            if (contentType != null) {
                subtype = contentType.subtype();
            }

            if (this.isNotFileRequest(subtype)) {
                String bodyString = Printer.getJsonString(responseBody.string());
                String url = response.request().url().toString();
                if (executor != null) {
                    executor.execute(createPrintJsonResponseRunnable(this.builder, chainMs, isSuccessful, code, header, bodyString, segmentList, message, url));
                } else {
                    Printer.printJsonResponse(this.builder, chainMs, isSuccessful, code, header, bodyString, segmentList, message, url);
                }

                ResponseBody body = ResponseBody.create(contentType, bodyString);
                return response.newBuilder().body(body).build();
            } else {
                if (executor != null) {
                    executor.execute(createFileResponseRunnable(this.builder, chainMs, isSuccessful, code, header, segmentList, message));
                } else {
                    Printer.printFileResponse(this.builder, chainMs, isSuccessful, code, header, segmentList, message);
                }

                return response;
            }
        } else {
            return chain.proceed(request);
        }
    }

    private boolean isNotFileRequest(String subtype) {
        return subtype != null && (subtype.contains("json") || subtype.contains("xml") || subtype.contains("plain") || subtype.contains("html"));
    }

    private static Runnable createPrintJsonRequestRunnable(final Builder builder, final Request request) {
        return new Runnable() {
            public void run() {
                Printer.printJsonRequest(builder, request);
            }
        };
    }

    private static Runnable createFileRequestRunnable(final Builder builder, final Request request) {
        return new Runnable() {
            public void run() {
                Printer.printFileRequest(builder, request);
            }
        };
    }

    private static Runnable createPrintJsonResponseRunnable(final Builder builder, final long chainMs, final boolean isSuccessful, final int code, final String headers, final String bodyString, final List<String> segments, final String message, final String responseUrl) {
        return new Runnable() {
            public void run() {
                Printer.printJsonResponse(builder, chainMs, isSuccessful, code, headers, bodyString, segments, message, responseUrl);
            }
        };
    }

    private static Runnable createFileResponseRunnable(final Builder builder, final long chainMs, final boolean isSuccessful, final int code, final String headers, final List<String> segments, final String message) {
        return new Runnable() {
            public void run() {
                Printer.printFileResponse(builder, chainMs, isSuccessful, code, headers, segments, message);
            }
        };
    }

    public static class Builder {
        private static String TAG = "LoggingI";
        private final HashMap<String, String> headers;
        private final HashMap<String, String> queries;
        private boolean isLogHackEnable = false;
        private boolean isDebug;
        private int type = 4;
        private String requestTag;
        private String responseTag;
        private Logger.Level level;
        private Logger logger = Logger.DEFAULT;
        private Executor executor;
        private boolean isMockEnabled;
        private long sleepMs;
        private BufferListener listener;

        public Builder() {
            this.level = Logger.Level.BASIC;
            this.headers = new HashMap<>();
            this.queries = new HashMap<>();
        }

        int getType() {
            return this.type;
        }

        Logger.Level getLevel() {
            return this.level;
        }

        public Builder setLevel(Logger.Level level) {
            this.level = level;
            return this;
        }

        HashMap<String, String> getHeaders() {
            return this.headers;
        }

        HashMap<String, String> getHttpUrl() {
            return this.queries;
        }

        String getTag(boolean isRequest) {
            if (isRequest) {
                return TextUtils.isEmpty(this.requestTag) ? TAG : this.requestTag;
            } else {
                return TextUtils.isEmpty(this.responseTag) ? TAG : this.responseTag;
            }
        }

        Logger getLogger() {
            return this.logger;
        }

        Executor getExecutor() {
            return this.executor;
        }

        boolean isLogHackEnable() {
            return this.isLogHackEnable;
        }

        public Builder addHeader(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder addQueryParam(String name, String value) {
            this.queries.put(name, value);
            return this;
        }

        public Builder tag(String tag) {
            TAG = tag;
            return this;
        }

        public Builder request(String tag) {
            this.requestTag = tag;
            return this;
        }

        public Builder response(String tag) {
            this.responseTag = tag;
            return this;
        }

        public Builder loggable(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        public Builder log(int type) {
            this.type = type;
            return this;
        }

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder enableMock(boolean useMock, long sleep, BufferListener listener) {
            this.isMockEnabled = useMock;
            this.sleepMs = sleep;
            this.listener = listener;
            return this;
        }

        public Builder enableAndroidStudio_v3_LogsHack(boolean useHack) {
            this.isLogHackEnable = useHack;
            return this;
        }

        public LoggingInterceptor build() {
            return new LoggingInterceptor(this);
        }
    }
}

