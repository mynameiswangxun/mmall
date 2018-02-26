package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface IOrderService {
    ServerResponse<Map<String,String>> pay(Integer userId, Long orderNo, String path);
    ServerResponse aliCallBack(Map<String,String> params);
    ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo);
    ServerResponse createOrder(Integer userId,Integer shippingId);
    ServerResponse cancelOrder(Integer userId,Long orderNo);
    ServerResponse getOrderCartProduct(Integer userId);
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    ServerResponse<PageInfo> getOrderList(Integer userId, Integer pageNum, Integer pageSize);
    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);
    ServerResponse<OrderVo> manageDetail(Long orderNo);
    ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum,int pageSize);
    ServerResponse manageSendGoods(Long orderNo);
}
