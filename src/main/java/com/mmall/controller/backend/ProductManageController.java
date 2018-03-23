package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponceCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse<String> productSave(HttpServletRequest servletRequest, Product product){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }else {
            return iProductService.saveOrUpdateProduct(product);
        }
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> setSaleStatus(HttpServletRequest servletRequest, Integer productId,Integer status){
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }else {
            return iProductService.setSaleStatus(productId,status);
        }
    }

    @RequestMapping("get_detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpServletRequest servletRequest, Integer productId){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }else {
            return iProductService.manageProductDetail(productId);
        }
    }

    @RequestMapping("get_list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpServletRequest servletRequest, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }else {
            return iProductService.getProductList(pageNum,pageSize);
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(String productName,Integer productId,HttpServletRequest servletRequest,@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }else {
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }
    }
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse<Map> upload(HttpServletRequest servletRequest,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createErrorMessageResponse("用户未登录,无法获取当前用户的信息");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createErrorMessageResponse("用户权限不够");
        }else {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createSuccessDataResponse(fileMap);
        }
    }
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpServletRequest servletRequest, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        String loginToken = CookieUtil.readLoginToken(servletRequest);
        User user = null;
        if(!StringUtils.isEmpty(loginToken)){
            String userJsonStr = RedisPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }
        Map resultMap = Maps.newHashMap();
        if(user==null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录");

            return resultMap;
        }
        if(!iUserService.checkAdminRole(user).isSuccess()){
            resultMap.put("success",false);
            resultMap.put("msg","用户权限不够");
            return resultMap;
        }else {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);

            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }
    }
}
