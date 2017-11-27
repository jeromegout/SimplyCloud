package org.jeromegout.simplycloud;

import android.app.Application;

import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import org.jeromegout.simplycloud.history.HistoryModel;
import org.jeromegout.simplycloud.hosts.HostManager;
import org.jeromegout.simplycloud.hosts.StubHost;
import org.jeromegout.simplycloud.hosts.free.FreeHost;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class Initializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .build();
        UploadService.HTTP_STACK = new OkHttpStack(client);


        //- hostManager
        HostManager.instance.init();
        //- by default dl.free.fr is used
//        HostManager.instance.setCurrentId(FreeHost.HOST_ID);
        HostManager.instance.setCurrentId(StubHost.HOST_ID);


        //- Model
        HistoryModel.instance.setContext(this);
        HistoryModel.instance.restoreHistories();
    }
}
