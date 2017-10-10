package com.dahai.demo.dubbo.controller;

import com.dahai.demo.dubbo.controller.base.DubboServiceFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 *
 * Created by 大海 on 2017/5/21.
 */
@Controller
public class TestController {

    @RequestMapping(value = "testGet")
    public void testGet(HttpServletRequest request,HttpServletResponse response) {

        String method = request.getMethod();
        if (method.equals("POST")) {
            try {
                ServletInputStream inputStream = request.getInputStream();
                int len=0;
                byte[] bytes=new byte[1024];
                while ((len=inputStream.read(bytes))!=-1) {
                    String s=new String(bytes,0,len,"UTF-8");
                    System.out.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            List<Map<String, String>> params = getParams(request);

            System.out.println(params);
        }

        List<Map<String,String>> maps=new ArrayList<Map<String, String>>();
        HashMap<String, String> e1 = new HashMap<String, String>();
        e1.put("dahai","大海");
        maps.add(e1);

        try {
            response.getWriter().print(maps);
            response.getWriter().close();
            request.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "dahai/get/{modele}/{service}/{method}",produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8",method = RequestMethod.GET)
    public void getUser(@PathVariable String modele,
                          @PathVariable String service,
                          @PathVariable String method,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        DubboServiceFactory dubbo = DubboServiceFactory.getInstance();
        response.getWriter().print(dubbo.genericInvoke("com.dahai.demo.dubbo.api.service."+modele+"."+service, method, request));
    }


    @RequestMapping(value = "post/testPost",  produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8", method = RequestMethod.POST)
    public void testPost(@RequestParam String name,HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print("你输入的名字是："+name);
    }


    private List<Map<String,String>> getParams(HttpServletRequest request) {
        List<Map<String,String>> maps=new ArrayList<Map<String, String>>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            Map<String,String> map = new HashMap<String,String>();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                map.put(paramName, paramValue);
            }
            if (!map.isEmpty()) {
                maps.add(map);
            }
        }
        return maps;
    }
}
