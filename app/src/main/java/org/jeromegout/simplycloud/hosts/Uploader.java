package org.jeromegout.simplycloud.hosts;

import android.content.Context;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

/**
 * Base class for all uploader. Main method is upload
 */
public class Uploader {

    /**
     * Subclass (specific uploader) should call this method before start the upload session
     *
     * @param uploadId
     */
    protected void startUpload(String uploadId) {
        UploadSessionManager.instance.registerUploadSession(uploadId, this);
    }

    /**
     * Subclass (specific uploader) should call this method when upload is finished
     */
    protected void finishUpload(String uploadId) {
        //- no more need to listener global receiver, the session is over
        UploadSessionManager.instance.unregisterUploadSession(uploadId);
    }

    /**
     * Method may be overridden by subclass in order to complete the upload session
     * @param context
     * @param info
     * @param response
     */
    protected void onUploadCompleted(Context context, UploadInfo info, ServerResponse response) {
        finishUpload(info.getUploadId());
    }

}
