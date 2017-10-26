package org.jeromegout.simplycloud.send;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import java.io.File;
import java.util.Calendar;

public class FreeTransfer extends AsyncTask<Void, Integer, UploadInfo> {

    private final OnArchiveSentListener listener;
	private final File archive;
	private Context context;
	private ProgressBar progressBar;

	public FreeTransfer(Context context, File archive, ProgressBar pb, OnArchiveSentListener listener) {
		this.context = context;
		this.progressBar = pb;
		this.listener = listener;
		this.archive = archive;
	}

    @Override
    protected void onPreExecute() {
        progressBar.setProgress(0);
        progressBar.setMax(100);
    }

    @Override
	protected UploadInfo doInBackground(Void... params) {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(1);
        }
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(1509046557);
        return new UploadInfo("http://dl.free.fr/lsGq2kjQW",
                "http://dl.free.fr/rm.pl?h=lsGq2kjQW&i=84448806&s=K5zQ1QYBKI36GdNtf7YsboAbYKWsuM1h&f=1",
                "dl.free.fr",
                date);
    }

	@Override
	protected void onProgressUpdate(Integer... progress) {
		progressBar.incrementProgressBy(progress[0]);
	}

	@Override
	protected void onPostExecute(UploadInfo info) {
		if(listener != null) {
			listener.onArchiveSent(info);
		}
	}

	//- to be notified when archive is sent to dl.free.fr server
	interface OnArchiveSentListener {
		void onArchiveSent(UploadInfo info);
	}
}
