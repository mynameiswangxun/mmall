package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createErrorMessageResponse("用户名不存在");
        }

        //MD5
        password = MD5Util.MD5EncodeUtf8(password);

        User user =  userMapper.selectLogin(username,password);
        if(user==null){
            return ServerResponse.createErrorMessageResponse("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createSuccessMessageDataResponse("登陆成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> response = checkValid(user.getUsername(), Const.USERNAME);
        if(!response.isSuccess()){
            return response;
        }
        response = checkValid(user.getEmail(),Const.EMAIL);
        if(!response.isSuccess()){
            return response;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount==0){
            return ServerResponse.createErrorMessageResponse("注册失败");
        }

        return ServerResponse.createSuccessMessageResponse("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServerResponse.createErrorMessageResponse("用户账号已存在");
                }
            }else if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createErrorMessageResponse("用户邮箱已被注册");
                }
            }else {
                return ServerResponse.createErrorMessageResponse("参数错误");
            }
        }else{
            return ServerResponse.createErrorMessageResponse("参数错误");
        }
        return ServerResponse.createSuccessMessageResponse("校验成功");
    }

    @Override
    public ServerResponse<String> selelctQuestion(String username) {
        if(checkValid(username,Const.USERNAME).isSuccess()){
            return ServerResponse.createErrorMessageResponse("用户账号不存在");
        }
        String question = userMapper.selectQuetionByUsername(username);
        if(!StringUtils.isNotBlank(question)){
            return ServerResponse.createErrorMessageResponse("密码找回问题为空!");
        }
        return ServerResponse.createSuccessDataResponse(question);
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username,forgetToken);
            return ServerResponse.createSuccessDataResponse(forgetToken);
        }
        return ServerResponse.createErrorMessageResponse("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createErrorMessageResponse("参数错误,token需要传递");
        }
        if(checkValid(username,Const.USERNAME).isSuccess()){
            return ServerResponse.createErrorMessageResponse("用户账号不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);

        if(StringUtils.isBlank(token)){
            return ServerResponse.createErrorMessageResponse("token无效或者过期");
        }

        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount>0){
                return ServerResponse.createSuccessMessageResponse("修改密码成功");
            }
        }else{
            return ServerResponse.createErrorMessageResponse("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createErrorMessageResponse("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew) {
        //防止横向越权,要检验用户旧密码
        int resultCount = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(resultCount == 0){
            return ServerResponse.createErrorMessageResponse("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createSuccessMessageResponse("密码更新成功");
        }
        return ServerResponse.createErrorMessageResponse("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username不能更新
        //email校验
        int resultCount = userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if(resultCount>0){
            return ServerResponse.createErrorMessageResponse("email已存在,请更换email后再尝试");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultCount>0){
            return ServerResponse.createSuccessMessageDataResponse("更新个人信息成功",updateUser);
        }
        return ServerResponse.createErrorMessageResponse("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createErrorMessageResponse("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createSuccessDataResponse(user);
    }

    @Override
    public ServerResponse<String> checkAdminRole(User user) {
        if(user!=null && user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createSuccessResponse();
        }else {
            return ServerResponse.createErrorResponse();
        }
    }


}
