package com.dahai.demo.dubbo.controller.post;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by 大海 on 2017/9/10.
 */
@Controller
public class PostController {

    /**
     *
     * @param service 服务名
     * @param model 模块
     * @param method 方法
     * @param request 请求
     * @return 结果
     */
    @RequestMapping(value = "/post/{model}/{service}/{module}/{method}", method = RequestMethod.POST)
    public void post(@PathVariable String service, @PathVariable String model, @PathVariable String module, @PathVariable String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream is;
        String contentStr;
        Map param = new HashMap();
        String className = model + "." + service + "." + module;
        try
        {
            is = request.getInputStream();
            contentStr = inputStreamString(is);
            param = getSyWebContainer().stringToMap(className, method, contentStr);
        }
        catch (Exception e)
        {
//            ReturnDataSet returnDataSet = new ReturnDataSet();
//            returnDataSet.setReturnCode(SYS_ERROR_SERVICE);
        }
        response.getWriter().print(getSyWebContainer().invoke(className, method, param, false));

    }

    public Map stringToMap(String className, String methodName, String source)
    {
        className = "" + "." + className;
        String invokeName = className + "_" + methodName;
        String key = serviceAuthKey.get(invokeName);
        String res = DESUtil.decryptDES(source, key);
        if (res == null)
        {
            return null;
        }
        return JSON.parseObject(res, Map.class);
    }

    private String inputStreamString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[]   b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;)   {
            out.append(new String(b, 0, n,"UTF-8"));
        }
        return   out.toString();
    }
}
