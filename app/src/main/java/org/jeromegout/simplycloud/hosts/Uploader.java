package org.jeromegout.simplycloud.hosts;

import android.content.Context;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.jeromegout.simplycloud.Logging;

import java.io.File;
import java.util.UUID;

public class Uploader {

    protected String uploadURL;

    public void upload(Context context, File file, String url) {
        try {
            String uploadId = UUID.randomUUID().toString();
            MultipartUploader poster = (MultipartUploader) new MultipartUploader(context, uploadId, url)
                    .addFileToUpload(file.getPath(), "ufile", file.getName())
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2);
            poster.startUpload();
            uploadURL = poster.getURL();

            UploadSessionManager.instance.registerUploadSession(uploadId, this);
        } catch (Exception e) {
            Logging.e(e.getMessage(), e);
        }
    }

    /**
     * Subclass (specific uploader) can override this method to do some post process after upload is completed
     * @param context
     * @param info
     * @param response
     */
    public void onUploadCompleted(Context context, UploadInfo info, ServerResponse response) {
        //- no more need to listener global receiver, the session is over
        UploadSessionManager.instance.unregisterUploadSession(info.getUploadId());
    }

}
