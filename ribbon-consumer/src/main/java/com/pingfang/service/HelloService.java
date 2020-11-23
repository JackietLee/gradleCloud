package com.pingfang.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.pingfang.controller.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HelloService {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "helloFallback")
    public String helloService() {
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello",
                String.class).getBody();
    }

    public String helloFallback() {
        return "error";
    }

    @HystrixCommand(fallbackMethod = "defaultUser")
    public User getUserById(long id) {
        return restTemplate.getForObject("htp://HELLO-SERVICE/users/{1}",
                User.class,id);
    }
t
    public User defaultUser() {
        return new User();
    }
}
