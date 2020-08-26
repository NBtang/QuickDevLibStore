package com.laotang.quickdev.mvvm.channel;

import java.util.ArrayList;
import java.util.List;

public class MethodChannel {

    private List<MethodCallHandler> handlerList;

    public void addMethodCallHandler(MethodCallHandler handler) {
        if (handlerList == null) {
            handlerList = new ArrayList<>();
        }
        if (handlerList.contains(handler)) {
            return;
        }
        handlerList.add(handler);
    }

    public void invoke(String method, Object arguments) {
        MethodCall methodCall = new MethodCall(method, arguments);
        for (MethodCallHandler handler : handlerList) {
            handler.onMethodCall(methodCall);
        }
    }

    public interface MethodCallHandler {
        void onMethodCall(MethodCall call);
    }
}
