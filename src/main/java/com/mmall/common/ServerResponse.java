package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化为Json的时候,忽略掉为NULL的引用
public class ServerResponse<T> implements Serializable{

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status){
        this.status = status;
    }

    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    //序列化为Json时忽略此方法
    public boolean isSuccess(){
        return status==ResponceCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> ServerResponse<T> createSuccessResponse(){
        return new ServerResponse<T>(ResponceCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createSuccessMessageResponse(String message){
        return new ServerResponse<T>(ResponceCode.SUCCESS.getCode(),message);
    }

    public static <T> ServerResponse<T> createSuccessDataResponse(T data){
        return new ServerResponse<T>(ResponceCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createSuccessMessageDataResponse(String message,T data){
        return new ServerResponse<T>(ResponceCode.SUCCESS.getCode(),message,data);
    }
    public static <T> ServerResponse<T> createErrorResponse(){
        return new ServerResponse<T>(ResponceCode.ERROR.getCode());
    }

    public static <T> ServerResponse<T> createErrorMessageResponse(String message){
        return new ServerResponse<T>(ResponceCode.ERROR.getCode(),message);
    }

    public static <T> ServerResponse<T> createErrorcodeMessageResponse(int errorCode,String message){
        return new ServerResponse<T>(errorCode,message);
    }
}
