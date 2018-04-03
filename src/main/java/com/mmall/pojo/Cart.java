package com.mmall.pojo;

import lombok.*;

import java.util.Date;
//lombok 的@Data包括getter setter equals和hashCode和toString(所有成員变量) canEqual,不包括构造器
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Cart {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;

}