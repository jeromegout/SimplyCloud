package org.jeromegout.simplycloud.hosts;

import android.content.Context;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.net.MalformedURLException;

public class MultipartUploader extends MultipartUploadRequest {

    public MultipartUploader(Context context, String uploadId, String serverUrl) throws IllegalArgumentException, MalformedURLException {
        super(context, uploadId, serverUrl);
    }

    public String getURL() {

        return params.serverUrl;
    }
}
