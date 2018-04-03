package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

//    @Autowired
//    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addCategory(String categoryName, @RequestParam(value = "parentId",defaultValue = "0") int parentId){
//        String loginToken = CookieUtil.readLoginToken(servletRequest);
//        User user = null;
//        if(!StringUtils.isEmpty(loginToken)){
//            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//            user = JsonUtil.string2Obj(userJsonStr,User.class);
//        }
//        if(user==null){
//            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            return iCategoryService.addCategory(categoryName,parentId);
//        }else {
//            return ServerResponse.createErrorMessageResponse("无权限操作,需要管理员权限");
//        }

        return iCategoryService.addCategory(categoryName,parentId);
    }

    @RequestMapping(value = "set_category_name.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setCategoryName(  Integer categoryId,String categoryName){
//        String loginToken = CookieUtil.readLoginToken(servletRequest);
//        User user = null;
//        if(!StringUtils.isEmpty(loginToken)){
//            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//            user = JsonUtil.string2Obj(userJsonStr,User.class);
//        }
//        if(user==null){
//            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            return iCategoryService.updateCategoryName(categoryId,categoryName);
//        }else {
//            return ServerResponse.createErrorMessageResponse("无权限操作,需要管理员权限");
//        }

        return iCategoryService.updateCategoryName(categoryId,categoryName);
    }

    /**
     * 获取仅下一级子结点(无递归)
     * @param categoryId
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory( @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
//        String loginToken = CookieUtil.readLoginToken(servletRequest);
//        User user = null;
//        if(!StringUtils.isEmpty(loginToken)){
//            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//            user = JsonUtil.string2Obj(userJsonStr,User.class);
//        }
//        if(user==null){
//            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            return iCategoryService.getChildrenParallelCategory(categoryId);
//        }else {
//            return ServerResponse.createErrorMessageResponse("无权限操作,需要管理员权限");
//        }

        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    /**
     * 递归获取子结点的Id
     * @param categoryId
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory( @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
//        String loginToken = CookieUtil.readLoginToken(servletRequest);
//        User user = null;
//        if(!StringUtils.isEmpty(loginToken)){
//            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//            user = JsonUtil.string2Obj(userJsonStr,User.class);
//        }
//        if(user==null){
//            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            return iCategoryService.selectCategoryAndChildrenById(categoryId);
//        }else {
//            return ServerResponse.createErrorMessageResponse("无权限操作,需要管理员权限");
//        }
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }
}
