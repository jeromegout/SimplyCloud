package org.jeromegout.simplycloud.hosts;

import android.content.Context;
import android.widget.Toast;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;

import org.jeromegout.simplycloud.Logging;
import org.jeromegout.simplycloud.history.HistoryModel;
import org.jeromegout.simplycloud.history.UploadItem;
import org.jeromegout.simplycloud.send.UploadLinks;

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
    protected void finishUpload(Context context, String uploadId, UploadLinks links) {
        //- no more need to listener global receiver, the session is over
        UploadSessionManager.instance.unregisterUploadSession(uploadId);
        //- update the history model with given links
        HistoryModel.instance.setLinks(uploadId, links);
        UploadItem item = HistoryModel.instance.getUploadItem(uploadId);
        if(item !=null && links != null) {
            Toast.makeText(context, item.getTitle()+ " successfully uploaded on "+links.hostId, Toast.LENGTH_LONG).show();
            Logging.d("FINISHED !!!!!!");
            Logging.d("Download: "+links.downloadLink);
            Logging.d("Delete:   "+links.deleteLink);
        } else {
            Toast.makeText(context, "Problem uploading "+item.getTitle()+ " on "+links.hostId, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method may be overridden by subclass in order to complete the upload session
     * @param context
     * @param info
     * @param response
     */
    protected void onUploadCompleted(Context context, UploadInfo info, ServerResponse response) {
        //- no more need to listener global receiver, the session is over
        UploadSessionManager.instance.unregisterUploadSession(info.getUploadId());
    }

}
