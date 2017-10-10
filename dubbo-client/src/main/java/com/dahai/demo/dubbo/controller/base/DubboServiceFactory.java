package com.dahai.demo.dubbo.controller.base;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Luo
 */
public class DubboServiceFactory {

    private ApplicationConfig application;
    private RegistryConfig registry;

    private static class SingletonHolder {
        private static DubboServiceFactory INSTANCE = new DubboServiceFactory();
    }

    private DubboServiceFactory(){
        Properties prop = new Properties();
        ClassLoader loader = DubboServiceFactory.class.getClassLoader();

        try {
            prop.load(loader.getResourceAsStream("dubboconf.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(prop.getProperty("application.name"));
        //这里配置了dubbo的application信息*(demo只配置了name)*，因此demo没有额外的dubbo.xml配置文件
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(prop.getProperty("registry.address"));
        //这里配置dubbo的注册中心信息，因此demo没有额外的dubbo.xml配置文件

        this.application = applicationConfig;
        this.registry = registryConfig;

    }

    public static DubboServiceFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Object genericInvoke(String interfaceClass,
                                String methodName,
                                HttpServletRequest request){

        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(interfaceClass); // 接口名
        reference.setGeneric(true); // 声明为泛化接口

        //ReferenceConfig实例很重，封装了与注册中心的连接以及与提供者的连接，
        //需要缓存，否则重复生成ReferenceConfig可能造成性能问题并且会有内存和连接泄漏。
        //API方式编程时，容易忽略此问题。
        //这里使用dubbo内置的简单缓存工具类进行缓存

        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);
        // 用com.alibaba.dubbo.rpc.service.GenericService可以替代所有接口引用

        //获取到的方法参数类型是根据方法参数排序拍好的
        String[] methodParamType = getMethodParamType(interfaceClass, methodName);

        Map<String, String> params = getParams(request);
        //根据key排序
        Map<String, Object> map = sortMapByKey(params);
        //只要value,获取到所有的入参
        Object[] res = new Object[map.size()];
        int i = 0;
        // TODO: 2017/5/30 如果这里是integer类型为空的话会报错
        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            Object value = entry.getValue();
            System.out.println(value.getClass().getName());
            res[i] = value;
            i++;
        }

        return genericService.$invoke(methodName, methodParamType, res); //方法名，服务名，参数类型数组，参数数组
    }

    /**
     * 获取方法参数的类型
     */
    private String[] getMethodParamType(String pkgName,String reqMethod) {
        try {
            Class clazz = Class.forName(pkgName);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (reqMethod.equals(methodName)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    String[] params=new String[parameterTypes.length];
                    int i=0;
                    for (Class<?> clas : parameterTypes) {
                        String parameterName = clas.getName();
                        params[i++]=parameterName;
                    }
                    return params;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    /**
     * 获取请求参数
     */
    private Map<String,String> getParams(HttpServletRequest request) {
        Map<String,String> map = new HashMap<String,String>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                map.put(paramName, paramValue);
            }
        }
        return map;
    }

    /**
     * 使用 Map按key进行倒叙排序
     */
    private Map<String, Object> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return new HashMap<String, Object>();
        }

        Map<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }


    class MapKeyComparator implements Comparator<String> {

        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }
}