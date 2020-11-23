package com.pingfang.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Slf4j
@RestController
public class HelloController {
    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String index() throws Exception{
        List<String> services = client.getServices();
        for (String sevice : services) {
            List<ServiceInstance> instances = client.getInstances(sevice);
            for (ServiceInstance instance : instances) {

                System.out.println(instance.getHost()+"    "+instance.getServiceId());
            }
        }
        int sleepTime = new Random().nextInt(3000);
        log.info("sleepTime:" + sleepTime);
        Thread.sleep(sleepTime);
        return "Hello World";
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User user(@PathVariable long id) throws Exception{

        return new User();
    }
}
