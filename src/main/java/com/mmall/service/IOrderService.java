package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
    ServerResponse<Map<String,String>> pay(Integer userId, Long orderNo, String path);
    ServerResponse aliCallBack(Map<String,String> params);
    ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo);
}
