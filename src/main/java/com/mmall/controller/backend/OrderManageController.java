package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponceCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.OrderVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    IUserService iUserService;

    @Autowired
    IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest servletRequest,
                                         @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()) {
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }
        return iOrderService.manageList(pageNum,pageSize);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpServletRequest servletRequest, Long orderNo){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()) {
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }
        return iOrderService.manageDetail(orderNo);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpServletRequest servletRequest, Long orderNo,
                                          @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()) {
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }
        return iOrderService.manageSearch(orderNo,pageNum,pageSize);
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse sendGoods(HttpServletRequest servletRequest, Long orderNo){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()) {
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }
        return iOrderService.manageSendGoods(orderNo);
    }

}
