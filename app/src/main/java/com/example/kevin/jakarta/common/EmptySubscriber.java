package com.example.kevin.jakarta.common;

import rx.Subscriber;

/**
 * Created by wang.wenkai on 2016/8/1.
 */

public class EmptySubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T o) {

    }
}
