package com.dahai.demo.dubbo.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dahai.demo.dubbo.api.service.testmodule.TestService;

/**
 *
 * Created by 大海 on 2017/5/21.
 */
@Service(interfaceName = "com.dahai.demo.dubbo.api.service.testmodule.TestService", timeout = 2000)
public class TestServiceImpl implements TestService {

    public String getName(String name) {
        System.out.println("print ---> " + name);
        return name;
    }

    public String getData() {
        System.out.println("空数据");
        return "空数据";
    }
}
