package com.pingfang.controller;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import lombok.Getter;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class Test extends HystrixObservableCommand<User> {
    @lombok.Setter
    @Getter
    private String[] ids;// 模拟前端的批处理，例如需要查看id为1,2,3,4的记录
    public Test(String[] ids) {
        super(HystrixCommandGroupKey.Factory.asKey("usercommand"));// 调用父类构造方法
        this.ids = ids;
    }

    @Override
    protected Observable<User> construct() {
        System.out.println(Thread.currentThread().getName()+"is running......");
        return Observable.create(new Observable.OnSubscribe<User>() {
            /*
             * Observable有三个关键的事件方法，分别为onNext，onCompleted，onError
             */
            @Override
            public void call(Subscriber<? super User> observer) {
                try {// 写业务逻辑，注意try-catch
                    if (!observer.isUnsubscribed()) {
                        for (String id : ids) {

                            User u = new User();
//                            TimeUnit.SECONDS.sleep(3);
                            observer.onNext(u);
                        }
                        observer.onCompleted();
                    }
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
    /**
     * fallback方法的写法，覆写resumeWithFallback方法
     * 当调用出现异常时，会调用该降级方法
     */
    @Override
    public Observable<User> resumeWithFallback() {
        Throwable executionException = getExecutionException();
        if(executionException==null){

        }
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> observer) {
                try {
                    if (!observer.isUnsubscribed()) {
                        User u = new User();
                        observer.onNext(u);
                        observer.onCompleted();
                    }
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
