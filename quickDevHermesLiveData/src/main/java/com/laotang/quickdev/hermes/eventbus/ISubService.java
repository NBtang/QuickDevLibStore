package com.laotang.quickdev.hermes.eventbus;


import com.laotang.quickdev.hermes.annotation.MethodId;

public interface ISubService {

    @MethodId("post")
    void post(Object event);

    @MethodId("cancelEventDelivery")
    void cancelEventDelivery(Object event);

}
