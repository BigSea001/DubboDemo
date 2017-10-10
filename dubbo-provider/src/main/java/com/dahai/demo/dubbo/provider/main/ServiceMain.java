package com.dahai.demo.dubbo.provider.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * Created by 大海 on 2017/5/21.
 */
public class ServiceMain {

    public static void main (String[] arg) {
//        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("classpath:spring-dubbo.xml");
//
//        context.start();
//        try {
//            Thread.sleep(2000000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        com.alibaba.dubbo.container.Main.main(arg);
    }
}
