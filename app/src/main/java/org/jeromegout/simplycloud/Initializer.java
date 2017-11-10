package org.jeromegout.simplycloud;

import android.app.Application;

import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import org.jeromegout.simplycloud.hosts.HostManager;
import org.jeromegout.simplycloud.hosts.free.FreeHost;

import okhttp3.OkHttpClient;


public class Initializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        OkHttpClient client = new OkHttpClient();
        UploadService.HTTP_STACK = new OkHttpStack(client);

        //- by default dl.free.fr is used
        HostManager.instance.setCurrentId(FreeHost.HOST_ID);
    }
}
