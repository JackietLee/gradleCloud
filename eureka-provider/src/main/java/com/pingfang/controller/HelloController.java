package com.pingfang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
public class HelloController {
    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String index() {
        List<String> services = client.getServices();
        for (String sevice : services) {
            List<ServiceInstance> instances = client.getInstances(sevice);
            for (ServiceInstance instance : instances) {

                System.out.println(instance.getHost()+"    "+instance.getServiceId());
            }
        }
        return "Hello World";
    }
}
