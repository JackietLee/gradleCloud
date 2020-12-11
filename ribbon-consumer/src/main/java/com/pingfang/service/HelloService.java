package com.pingfang.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;

import com.pingfang.controller.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class HelloService {

    @Autowired
    RestTemplate restTemplate;

    @CacheResult
    @HystrixCommand(fallbackMethod = "helloFallback")
    public String helloService() {
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello",
                String.class).getBody();
    }

    @CacheResult(cacheKeyMethod = "getUserByIdCacheKey")
    @HystrixCommand(fallbackMethod = "helloFallback",commandKey = "getById")
    public User getById(Long id) {
        return restTemplate.getForObject("http://HELLO-SERVICE/users/{1}",
                User.class, id);
    }

    private Long getUserByIdCacheKey(Long id) {
        return id;
    }

    public String helloFallback() {
        return "error";
    }

    @HystrixCollapser(batchMethod= "findAll", collapserProperties = {
            @HystrixProperty(name="TimerDelayInMilliseconds", value = "100")
    })
//    @HystrixCommand(fallbackMethod = "defaultUser")
    public User getUserById(@CacheKey("id") long id) {
        return restTemplate.getForObject("http://HELLO-SERVICE/users/{1}",
                User.class,id);
    }

    public User defaultUser() {
        return new User();
    }

    @CacheRemove(commandKey = "getById")
    @HystrixCommand
    public User update(@CacheKey("id") User user) {
        return restTemplate.postForObject("http://USER-SERVICE/users", user,
                User.class);
    }

    @HystrixCommand
    public List<User> findAll(List<Long> userIds) {
        return restTemplate.getForObject("http://USER-SERVICE/users?ids={1}",
                List.class, StringUtils.join (userIds, ", "));
    }
}
