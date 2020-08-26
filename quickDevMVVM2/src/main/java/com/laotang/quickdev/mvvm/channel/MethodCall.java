package com.laotang.quickdev.mvvm.channel;

import org.json.JSONObject;

import java.util.Map;

public final class MethodCall {
    public final String method;
    public final Object arguments;

    public MethodCall(String method, Object arguments) {
        if (method == null) {
            throw new AssertionError("Parameter method must not be null.");
        } else {
            this.method = method;
            this.arguments = arguments;
        }
    }

    public <T> T arguments() {
        return (T) this.arguments;
    }

    public boolean hasArgument(String key) {
        if (this.arguments == null) {
            return false;
        } else if (this.arguments instanceof Map) {
            return ((Map)this.arguments).containsKey(key);
        } else if (this.arguments instanceof JSONObject) {
            return ((JSONObject)this.arguments).has(key);
        } else {
            throw new ClassCastException();
        }
    }
}
