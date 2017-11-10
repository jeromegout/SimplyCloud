package org.jeromegout.simplycloud.hosts;

import android.content.Context;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Upload session manager singleton, Uploader root class registers its upload session (using its uploadId)
 * Global UploadBroadCastReceiver uses this manager to retrieve which uploader to talk to.
 * The onCompleteUpload method of Uploader is called at this moment.
 * Once the upload is completed the Uploader is automatically unregistered.
 */
public class UploadSessionManager {

    public static final UploadSessionManager instance = new UploadSessionManager();
    private Map<String, Uploader> uploadSessions;

    private UploadSessionManager() {
        uploadSessions = new HashMap<>();
    }

    void onUploadCompleted(Context context, UploadInfo info, ServerResponse response) {
        Uploader uploader = uploadSessions.get(info.getUploadId());
        if(uploader != null) {
            uploader.onUploadCompleted(context, info, response);
        }
    }

    void registerUploadSession(String uploadId, Uploader uploader) {
        uploadSessions.put(uploadId, uploader);
    }

    void unregisterUploadSession(String uploadId) {
        uploadSessions.remove(uploadId);
    }
}
