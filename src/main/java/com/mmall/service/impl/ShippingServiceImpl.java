package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount>0){
            Map resultMap = Maps.newHashMap();
            resultMap.put("shippingId",shipping.getId());
            return ServerResponse.createSuccessMessageDataResponse("新建地址成功",resultMap);
        }
        return ServerResponse.createErrorMessageResponse("新建地址失败");
    }

    @Override
    public ServerResponse del(Integer userId, Integer shippingId){
        int resCount = shippingMapper.deleteByShippingIdAndUserId(userId,shippingId);
        if(resCount>0){
            return ServerResponse.createSuccessMessageResponse("删除地址成功");
        }
        return ServerResponse.createErrorMessageResponse("删除地址失败");
    }

    @Override
    public ServerResponse update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount>0){
            return ServerResponse.createSuccessMessageResponse("更新地址成功");
        }
        return ServerResponse.createErrorMessageResponse("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(userId,shippingId);
        if(shipping==null) {
            return ServerResponse.createErrorMessageResponse("查询不到该地址");
        }else{
            return ServerResponse.createSuccessMessageDataResponse("查询成功",shipping);
        }
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createSuccessDataResponse(pageInfo);
    }
}
