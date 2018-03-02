package com.mmall.common;


import com.google.common.collect.Sets;

import java.util.Set;

public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public static class Role{
        //普通用户
        public static final int ROLE_CUSTOMER = 0;
        //管理员用户
        public static final int ROLE_ADMIN = 1;
    }

    public static class ProductListOrderBy{
        public static Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    public static class Cart{
        public static int CHECKED = 1;
        public static int UNCHECKED = 0;

        public static String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        public static String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public enum ProductStatusEum {
        ON_SALE("上线", 1);
        private String value;
        private int code;

        ProductStatusEum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY("在线支付", 1);
        private String value;
        private int code;

        PaymentTypeEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static String getDesc(int code){
            for (PaymentTypeEnum paymentTypeEnum:PaymentTypeEnum.values()){
                if(code==paymentTypeEnum.getCode()){
                    return paymentTypeEnum.value;
                }
            }
            return "";
        }
    }

    public enum OrderStatusEumm{
        CANCEL(0,"已取消"),
        NOPAY(10,"未支付"),
        PAYD(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        OrderStatusEumm(int code,String value){
            this.value = value;
            this.code = code;
        }
        public static String getDesc(int code){
            for (OrderStatusEumm orderStatusEumm:OrderStatusEumm.values()){
                if(code==orderStatusEumm.getCode()){
                    return orderStatusEumm.value;
                }
            }
            return "";
        }

    }

    public static class AlipayCallback{
        public static String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        public static String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        public static String RESPONCE_SUCESS = "sucess";
        public static String RESPONCE_FAILED = "failed";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        PayPlatformEnum(int code,String value){
            this.value = value;
            this.code = code;
        }

        public static String getDesc(int code){
            for (PayPlatformEnum payPlatformEnum:PayPlatformEnum.values()){
                if(code==payPlatformEnum.getCode()){
                    return payPlatformEnum.value;
                }
            }
            return "";
        }

    }
}
