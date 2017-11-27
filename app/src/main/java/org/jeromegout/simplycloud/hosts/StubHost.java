package org.jeromegout.simplycloud.hosts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.send.UploadLinks;

import java.io.File;

/**
 * This class is not intended to be used in final application.
 * This is only fr debug purpose.
 * Created by jeromegout on 26/11/2017.
 */

public class StubHost extends Uploader implements HostServices {

    public static final String HOST_ID = "Stub Host";


    @Override
    public String getHostId() {
        return HOST_ID;
    }

    @Override
    public int getHostLogoId() {
        return R.drawable.ic_stub_logo;
    }

    @Override
    public void archiveStillAlive(Context context, String archiveURL, OnListener listener) {
        //- file is still there
        listener.onUploadUpdate(HostServices.OK);
    }

    @Override
    public void uploadArchive(final Context context, File archive, final String uploadId) {
        //- useless call because no actual upload is done, broadcastreceiver won't receive any message
        // startUpload(uploadId);
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                UploadLinks links = new UploadLinks("download link", "delete link", getHostId());
                finishUpload(context, uploadId, links);
            }
        }.execute();
    }

    @Override
    public void deleteArchive(Context context, String deleteURL, OnListener listener) {
        listener.onArchiveDeleted(true);
    }
}
