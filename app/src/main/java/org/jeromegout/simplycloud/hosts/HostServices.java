package org.jeromegout.simplycloud.hosts;

import android.content.Context;

import org.jeromegout.simplycloud.send.UploadInfo;

import java.io.File;

public interface HostServices {

    //- returned values for archive validation (through the onUploadUpdate parameter)
    String OK = "OK";
    String KO = "KO";

    String getHostId();

    int getHostLogoId();

    void archiveStillAlive(Context context, String archiveURL, OnListener listener);

    void uploadArchive(Context context, File archive, HostServices.OnListener listener);

    void deleteArchive(Context context, String deleteURL, HostServices.OnListener listener);

    //- listener to be notified of upload events
    interface OnListener {

        //- notification for validity check or during upload
        void onUploadUpdate(String update);

        //- end upload notification
        void onUploadFinished(UploadInfo info);

        //- delete notification
        void onArchiveDeleted(boolean deletePerformed);
    }
}
