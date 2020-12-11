package com.pingfang.command;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import com.pingfang.controller.User;
import com.pingfang.service.HelloService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserCollapseCommand extends HystrixCollapser<List<User>, User, Long> {
    private HelloService helloService;
    private Long userid;
    public UserCollapseCommand(HelloService userService, Long userid) {
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey
                ("userCollapseCommand")).andCollapserPropertiesDefaults(
                HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
        this.helloService = userService;
        this.userid = userid;
    }
    @Override
    public Long getRequestArgument () {
        return userid;
    }

    @Override
    protected HystrixCommand<List<User>> createCommand(Collection<CollapsedRequest<User, Long>> collapsedRequests) {
        List<Long> userids = new ArrayList<>(collapsedRequests.size());
        userids.addAll(collapsedRequests.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));
        return new UserBatchCommand(helloService, userids);
    }

    @Override
    protected void mapResponseToRequests(List<User> batchResponse,
            Collection<CollapsedRequest<User, Long>> collapsedRequests){
        int count = 0;
        for (CollapsedRequest<User, Long> collapsedRequest : collapsedRequests) {
            User user = batchResponse.get(count++);
            collapsedRequest.setResponse(user);
        }
    }
}
