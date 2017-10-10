package com.dahai.demo.dubbo.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dahai.demo.dubbo.api.service.testmodule.AddService;

/**
 *
 * Created by 大海 on 2017/5/29.
 */
@Service(interfaceName = "com.dahai.demo.dubbo.api.service.testmodule.AddService", timeout = 2000)
public class AddServiceImpl implements AddService {


    public void addData(String data) {
        System.out.println(data);
    }

    public String getData(String data,Integer id) {
        System.out.println(data+ id);
        return data+id;
    }
}
