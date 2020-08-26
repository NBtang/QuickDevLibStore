package com.laotang.quickdev.hermes.eventbus;


import com.laotang.quickdev.hermes.annotation.ClassId;
import com.laotang.quickdev.hermes.annotation.MethodId;

@ClassId("MainService")
public interface IMainService {

    @MethodId("register")
    void register(int pid, ISubService subService);

    @MethodId("unregister")
    void unregister(int pid);

    @MethodId("post")
    void post(Object event);

    @MethodId("postSticky")
    void postSticky(Object event);

    @MethodId("cancelEventDelivery")
    void cancelEventDelivery(Object event);

    @MethodId("getStickyEvent")
    Object getStickyEvent(String eventType);

    @MethodId("removeStickyEvent(String)")
    Object removeStickyEvent(String eventType);

    @MethodId("removeStickyEvent(Object)")
    boolean removeStickyEvent(Object event);

    @MethodId("removeAllStickyEvents")
    void removeAllStickyEvents();

}
