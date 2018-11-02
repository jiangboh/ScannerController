package com.bravo.system;

import android.os.Environment;

import com.bravo.utils.Logs;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by admin on 2018-11-2.
 */

public class FtpServerlet extends DefaultFtplet {
    private final String TAG = "FtpServerlet";

    private FtpServer mFtpServer;

    private final int mPort = 2121;

    private String mDirectory = Environment.getExternalStorageDirectory().getPath() + "/FtpFileTest";

    private final String mUser = "user";

    private final String mPassword = "password";

    private static FtpServerlet mInstance;

    public static FtpServerlet getInstance(){
        if(mInstance == null){
            mInstance = new FtpServerlet();
        }
        return mInstance;
    }

    public void setmDirectory(String mDirectory) {
        this.mDirectory = mDirectory;
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    startFtp();
                } catch (FtpException e) {
                    Logs.e(TAG,"FTP服务器打工失败。");
                }
            }
        }.start();
    }

    /**
     * FTP启动
     * @throws FtpException
     */
    private void startFtp() throws FtpException {

        if (null != mFtpServer && false == mFtpServer.isStopped()) {
            return;
        }

        File file = new File(mDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }

        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();

        // 设定端末番号
        listenerFactory.setPort(mPort);

        // 通过PropertiesUserManagerFactory创建UserManager然后向配置文件添加用户
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
        UserManager userManager = userManagerFactory.createUserManager();

        List<Authority> auths = new ArrayList<Authority>();
        Authority auth = new WritePermission();
        auths.add(auth);

        //添加用户
        BaseUser user = new BaseUser();
        user.setName(mUser);
        user.setPassword(mPassword);
        user.setHomeDirectory(mDirectory);
        user.setAuthorities(auths);
        userManager.save(user);

        // 设定Ftplet
        Map<String, Ftplet> ftpletMap = new HashMap<String, Ftplet>();
        ftpletMap.put("Ftplet", this);

        serverFactory.setUserManager(userManager);
        serverFactory.addListener("default", listenerFactory.createListener());
        serverFactory.setFtplets(ftpletMap);

        // 创建并启动FTPServer
        mFtpServer = serverFactory.createServer();
        mFtpServer.start();
    }

    /**
     * FTP停止
     */
    public void stop() {

        // FtpServer不存在和FtpServer正在运行中
        if (null != mFtpServer && false == mFtpServer.isStopped()) {
            mFtpServer.stop();
        }
    }

    @Override
    public FtpletResult onAppendStart(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
        System.out.println("onAppendStart");
        return super.onAppendStart(session, request);
    }

    @Override
    public FtpletResult onAppendEnd(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
        System.out.println("onAppendEnd");
        return super.onAppendEnd(session, request);
    }

    @Override
    public FtpletResult onLogin(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
        System.out.println("onLogin");
        return super.onLogin(session, request);
    }

    @Override
    public FtpletResult onConnect(FtpSession session) throws FtpException,
            IOException {
        System.out.println("onConnect");
        return super.onConnect(session);
    }

    @Override
    public FtpletResult onDisconnect(FtpSession session) throws FtpException,
            IOException {
        System.out.println("onDisconnect");
        return super.onDisconnect(session);
    }

    @Override
    public FtpletResult onUploadStart(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
        System.out.println("onUploadStart");
        return super.onUploadStart(session, request);
    }

    @Override
    public FtpletResult onUploadEnd(FtpSession session, FtpRequest request)
            throws FtpException, IOException {
        String FtpUploadPath    = mDirectory            + "/" + request.getArgument();
        //接收到文件后立即删除
        File uploadFile = new File(FtpUploadPath);
        uploadFile.delete();
        return super.onUploadEnd(session, request);
    }
}