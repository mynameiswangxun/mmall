package com.mmall.controller.common;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("preHandle");
        //强转成HandlerMethod
        HandlerMethod handlerMethod = (HandlerMethod)o;

        //解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator<Map.Entry> iterator = paramMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = iterator.next();
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;

            //request的paramterMap中的value是String数组
            Object obj = entry.getValue();
            if(obj instanceof String[]){
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
            if(iterator.hasNext()){
                requestParamBuffer.append("&");
            }
        }

        if(StringUtils.equals(className,"UserManageController") && StringUtils.equals(methodName,"login")){
            log.info("拦截白名单:{}.{}()",className,methodName);
            return true;
        }

        log.info("拦截器拦截{}.{}(),参数{}",className,methodName,requestParamBuffer);
        //上传由于富文本有特殊返回要求,因此作特殊处理
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息");
            httpServletResponse.reset();//一定要调用reset()方法,不然会抛出异常
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");//设置返回值类型
            PrintWriter out = httpServletResponse.getWriter();

            if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                Map resultMap = Maps.newHashMap();
                resultMap.put("success",false);
                resultMap.put("msg","用户未登录,无法获取当前用户的信息");
                out.print(JsonUtil.obj2String(resultMap));
            }else {
                out.print(JsonUtil.obj2String(ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息")));
            }

            out.flush();
            out.close();

            return false;
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
//            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"登录信息过期,请重新登录");
            httpServletResponse.reset();//一定要调用reset()方法,不然会抛出异常
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");//设置返回值类型
            PrintWriter out = httpServletResponse.getWriter();

            if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                Map resultMap = Maps.newHashMap();
                resultMap.put("success",false);
                resultMap.put("msg","用户登录信息过期,请重新登录");
                out.print(JsonUtil.obj2String(resultMap));
            }else {
                out.print(JsonUtil.obj2String(ServerResponse.createErrorMessageResponse("用户登录信息过期,请重新登录")));
            }

            out.flush();
            out.close();

            return false;
        }
        if(user.getRole().intValue()!=Const.Role.ROLE_ADMIN){
//            return ServerResponse.createErrorMessageResponse("用户权限不够");
            httpServletResponse.reset();//一定要调用reset()方法,不然会抛出异常
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");//设置返回值类型
            PrintWriter out = httpServletResponse.getWriter();

            if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                Map resultMap = Maps.newHashMap();
                resultMap.put("success",false);
                resultMap.put("msg","用户权限不够");
                out.print(JsonUtil.obj2String(resultMap));
            }else {
                out.print(JsonUtil.obj2String(ServerResponse.createErrorMessageResponse("用户权限不够")));
            }

            out.flush();
            out.close();

            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
