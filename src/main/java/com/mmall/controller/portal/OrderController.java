package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponceCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import com.mmall.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {

    @Autowired
    IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest servletRequest , Integer shippingId){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if (user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),ResponceCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse<Map<String,String>> pay(HttpServletRequest servletRequest, Long orderNo, HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if (user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),ResponceCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(),orderNo,path);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = new HashMap<>();

        Map requestParams = request.getParameterMap();
        for (Iterator iterator = requestParams.keySet().iterator();iterator.hasNext();) {
            String name = (String)iterator.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name,valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        try {
            boolean rsaCheckV1flag = AlipaySignature.rsaCheckV1(params, Configs.getAlipayPublicKey(),
                    "utf-8",Configs.getSignType());
            if(!rsaCheckV1flag){
                System.out.println(Configs.getAlipayPublicKey());
                System.out.println(Configs.getSignType());
                log.info("非法请求,验证不通过");
                return ServerResponse.createErrorMessageResponse("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常",e);
        }

        //todo 验证各种数据

        ServerResponse serverResponse = iOrderService.aliCallBack(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONCE_SUCESS;
        }else {
            return Const.AlipayCallback.RESPONCE_FAILED;
        }
    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest servletRequest, Long orderNo){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if (user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),ResponceCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createSuccessDataResponse(true);
        }else{
            return ServerResponse.createSuccessDataResponse(false);
        }
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest servletRequest, Long orderNo){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if (user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),ResponceCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancelOrder(user.getId(),orderNo);
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest servletRequest){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if (user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),ResponceCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpServletRequest servletRequest, Long orderNo){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if (user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),ResponceCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest servletRequest,
                                         @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if (user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),ResponceCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }
}
