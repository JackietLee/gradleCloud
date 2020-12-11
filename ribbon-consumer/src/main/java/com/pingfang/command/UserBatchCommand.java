package com.pingfang.command;

import com.netflix.hystrix.HystrixCommand;
import com.pingfang.controller.User;
import com.pingfang.service.HelloService;

import java.util.List;

import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey;

public class UserBatchCommand extends HystrixCommand<List<User>> {
        HelloService helloService;
        List<Long> userids;
        public UserBatchCommand(HelloService userService, List<Long> userids) {
            super(Setter.withGroupKey(asKey("userServiceCommand")));
            this.userids = userids;
            this.helloService = userService;
        }
        @Override
        protected List<User> run()throws Exception{
                return helloService.findAll(userids);
        }
}
