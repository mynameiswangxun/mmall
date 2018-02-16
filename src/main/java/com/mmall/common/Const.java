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
}
