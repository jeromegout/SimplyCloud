package org.jeromegout.simplycloud.send;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

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

	private OnArchiveCreationListener listener;
	private List<Uri> files;
	private File outputFile;

	public ArchiveMaker files(List<Uri> files) {
		this.files = files;
		return this;
	}

	public ArchiveMaker into(File outputFile) {
		this.outputFile = outputFile;
		return this;
	}

	public ArchiveMaker withListener(@NonNull OnArchiveCreationListener listener){
		this.listener = listener;
		return this;
	}

	@Override
	protected File doInBackground(Void... params) {
		final int BUFFER = 2048;
		BufferedInputStream origin;
		try {
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
	protected void onProgressUpdate(String... p) {
		String progress = p[0];
		if(progress.startsWith("#")) {
			int nb = Integer.parseInt(progress.substring(1));
			listener.onArchiveUpdate(nb);
		} else {
			listener.onArchiveStepUpdate(progress);
		}
	}

	@Override
	protected void onPostExecute(File file) {
		if(listener != null) {
			listener.onArchiveCreated(file);
		}
	}

    //- to be notified when archive is created
	interface OnArchiveCreationListener {
		void onArchiveUpdate(int bytesNum);
		void onArchiveStepUpdate(String step);
		void onArchiveCreated(File archive);
	}
}
