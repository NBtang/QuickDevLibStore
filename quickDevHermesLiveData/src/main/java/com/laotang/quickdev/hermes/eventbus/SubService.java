package com.laotang.quickdev.hermes.eventbus;

import com.laotang.quickdev.hermes.annotation.MethodId;

import org.greenrobot.eventbus.EventBus;


public class SubService implements ISubService {

    private static volatile SubService sInstance = null;

    private EventBus mEventBus;

    private SubService() {
        mEventBus = EventBus.getDefault();
    }

    public static SubService getInstance() {
        if (sInstance == null) {
            synchronized (SubService.class) {
                if (sInstance == null) {
                    sInstance = new SubService();
                }
            }
        }
        return sInstance;
    }

    @MethodId("post")
    @Override
    public void post(Object event) {
        mEventBus.post(event);
    }

    @MethodId("cancelEventDelivery")
    @Override
    public void cancelEventDelivery(Object event) {
        mEventBus.cancelEventDelivery(event);
    }

}
