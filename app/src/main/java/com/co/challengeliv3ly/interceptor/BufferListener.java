package com.co.challengeliv3ly.interceptor;

import java.io.IOException;

import okhttp3.Request;

public interface BufferListener {
    String getJsonResponse(Request var1) throws IOException;
}
