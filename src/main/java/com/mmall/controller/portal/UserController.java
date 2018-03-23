package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponceCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;
    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(HttpSession session,HttpServletResponse servletResponse,String username, String password){
        //service -> dao
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
           // session.setAttribute(Const.CURRENT_USER,response.getData());
            CookieUtil.writeLoginToken(servletResponse,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 用户注销
     * @param servletRequest
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest servletRequest,HttpServletResponse servletResponse){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        CookieUtil.delLoginToken(servletRequest,servletResponse);
        RedisPoolUtil.del(loginToken);
        return ServerResponse.createSuccessResponse();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 密码或者邮箱认证
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取当前用户信息
     * @param servletRequest
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest servletRequest){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createErrorMessageResponse("登录信息已过期");
        }
        return ServerResponse.createSuccessDataResponse(user);
    }

    /**
     * 根据账号获得密码找回问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selelctQuestion(username);
    }

    /**
     * 检查问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){

        return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 修改密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){

        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    /**
     * 登录状态修改密码
     * @param servletRequest
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest servletRequest,String passwordOld,String passwordNew){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if(user==null){
            return ServerResponse.createErrorMessageResponse("用户未登录");
        }
        return iUserService.resetPassword(user,passwordOld,passwordNew);
    }

    /**
     * 更新用户信息
     * @param servletRequest
     * @param user
     * @return
     */
    @RequestMapping(value = "update_informantion.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpServletRequest servletRequest,User user){
        //User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User currentUser = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisPoolUtil.get(loginToken);
            currentUser = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if(currentUser==null){
            return ServerResponse.createErrorMessageResponse("用户未登录");
        }
        user.setId(currentUser.getId());

        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(user.getUsername());
            //session.setAttribute(Const.CURRENT_USER,response.getData());
            String newUserJason = JsonUtil.obj2String(response.getData());
            RedisPoolUtil.setEx(loginToken,newUserJason,Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 获得用户信息
     * @param servletRequest
     * @return
     */
    @RequestMapping(value = "get_informantion.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest servletRequest){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User currentUser = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisPoolUtil.get(loginToken);
            currentUser = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        if(currentUser==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }

}
