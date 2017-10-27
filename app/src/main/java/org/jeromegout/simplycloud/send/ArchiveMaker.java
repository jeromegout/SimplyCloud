package org.jeromegout.simplycloud.send;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ArchiveMaker extends AsyncTask<Void, String, File> {

	private final OnArchiveCreatedListener listener;
	private final List<Uri> files;
	private final TextView statusView;
	private Context context;
	private ProgressBar progressBar;

	public ArchiveMaker(Context context, List<Uri> files, ProgressBar pb, TextView statusView, OnArchiveCreatedListener listener) {
		this.context = context;
		this.progressBar = pb;
		this.statusView = statusView;
		this.listener = listener;
		this.files = files;
	}

	@Override
	protected File doInBackground(Void... params) {
		final int BUFFER = 2048;
		BufferedInputStream origin;
		File outputDir = context.getCacheDir();
		try {
			String appName = context.getResources().getString(R.string.app_name)+"-dl.free.fr";
			File outputFile = File.createTempFile(appName, ".zip", outputDir);
			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
			byte data[] = new byte[BUFFER];

			for(Uri uri : files) {
				publishProgress("Packaging file '"+uri.getLastPathSegment()+"'");
				origin = new BufferedInputStream(new FileInputStream(uri.getPath()), BUFFER);
				ZipEntry entry = new ZipEntry(uri.getLastPathSegment());
				zos.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					zos.write(data, 0, count);
					publishProgress("#"+count);
				}
				origin.close();
			}
			zos.close();
			return outputFile;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		String progres = progress[0];
		if(progres.startsWith("#")) {
			int nb = Integer.parseInt(progres.substring(1));
			progressBar.incrementProgressBy(nb);
		} else {
			statusView.setText(progres);
		}
	}

	@Override
	protected void onPostExecute(File file) {
		if(listener != null) {
			listener.onArchiveCreated(file);
		}
	}

	//- to be notified when archive is created
	interface OnArchiveCreatedListener {
		void onArchiveCreated(File archive);
	}
}
