package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class FTPUtil {

    private static String fitIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String fitUser = PropertiesUtil.getProperty("ftp.user");
    private static String fitPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pasw;
    private FTPClient ftpClient;

    public FTPUtil(String ip,int port,String user,String pasw){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pasw = pasw;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException{
        FTPUtil ftpUtil = new FTPUtil(fitIp,21,fitUser,fitPass);
        log.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        log.info("开始连接ftp服务器,结束上传,上传结果:{}",result);
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException{
        boolean uploaded = true;
        FileInputStream fileInputStream = null;

        if(connectServer(ip,port,user,pasw)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File file:fileList) {
                    fileInputStream = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(),fileInputStream);
                }
            } catch (IOException e) {
                log.error("上传文件异常",e);
                uploaded = false;
            } finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }else {
            uploaded = false;
        }
        return uploaded;
    }

    private boolean connectServer(String ip,int port,String user,String pasw){
        boolean isSuccess = true;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pasw);
        } catch (IOException e) {
            log.error("连接FTP服务器失败",e);
            isSuccess = false;
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasw() {
        return pasw;
    }

    public void setPasw(String pasw) {
        this.pasw = pasw;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
