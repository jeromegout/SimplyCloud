package org.jeromegout.simplycloud.hosts.free;

import android.os.AsyncTask;
import android.widget.TextView;

import org.jeromegout.simplycloud.send.UploadInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class FreeTransfer extends AsyncTask<Void, String, UploadInfo> implements MultiPartPoster.OnProgressListener {

	public static final String FREE_HOST_ID = "dl.free.fr";
	private final OnArchiveSentListener listener;
	private final File archive;
	private  TextView statusView;

	public FreeTransfer(File file, TextView statusView, OnArchiveSentListener listener) {
		this.archive = file;
		this.statusView = statusView;
		this.listener = listener;
	}

	private final static String FREE_URL = "http://dl.free.fr/upload.pl?b15059952545362975907183455737546";//$NON-NLS-1$
	private final static String LF = System.getProperty("line.separator");//$NON-NLS-1$

	@Override
	protected UploadInfo doInBackground(Void... params) {
		try {
			MultiPartPoster poster = new MultiPartPoster(FREE_URL, this);
			poster.addFilePart(archive);
			publishProgress("File uploaded on host server");
			poster.post();
			return getUploadInfos(poster.getUrl(), "");
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		statusView.setText(progress[0]);
	}

	@Override
	protected void onPostExecute(UploadInfo infos) {
		if(listener != null) {
			listener.onArchiveSent(infos);
		}
		//- releaase reference
        statusView = null;
	}

	/**
	 * Given a monitoring url page, it returns the link of the uploaded file and the URL to delete it from server
	 *
	 * @param monURL monitoring URL (the URL that will contain download and delete links when finish)
	 * @return upload information (download and delete links)
	 * @throws IOException if an I/O exception occurs or if response code is not OK
	 */
	private UploadInfo getUploadInfos(String monURL, String progress) throws IOException {
//		System.out.println("Get Infos: GET:"+monURL);
		UploadInfo infos;

		publishProgress("Retrieving link from host server "+progress);
		URL mon = new URL(monURL);
		HttpURLConnection httpConn = (HttpURLConnection) mon.openConnection();
		httpConn.setRequestMethod("GET"); //$NON-NLS-1$

		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()))) {
				String line;
				boolean shouldRefresh = false;
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null && !shouldRefresh) {
					if (isContainingRefreshMaker(line)) {
						shouldRefresh = true;
					}
					sb.append(line).append(LF);
				}
				if (shouldRefresh) {
					//- we've found the marker that indicates that upload process is not finished
					//- need to wait and send back a request
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						//- nothing to do
					}
					infos = getUploadInfos(monURL, progress+".");
				} else {
					//- we reached the end of the page without finding the refresh marker
					//- we can return the collected info
					infos = findLinks(sb);
				}
				httpConn.disconnect();
				return infos;
			}
		} else {
			publishProgress("Server returned non-OK status: " + status);
			throw new IOException("Server returned non-OK status: " + status);
		}
	}

	private boolean isContainingRefreshMaker(String line) {
		return line.trim().replaceAll(" ", "").contains("functiondoLoad(){setTimeout(\"refresh()\",2*1000);}");
	}

	private UploadInfo findLinks(StringBuilder sb) {
		UploadInfo info = new UploadInfo(null, null, FREE_HOST_ID);
		int i = sb.indexOf("<a class=\"underline\" href=\"http://dl.free.fr/");
		if(i != -1) {
			int j = sb.indexOf("\" onclick=\"window.open('http://dl.free.fr/", i);
			if(j != -1) {
				String downloadURL = sb.substring(i+"<a class=\"underline\" href=\"".length(), j);
				i = sb.indexOf("<a class=\"underline\" href=\"http://dl.free.fr/rm.pl?h=", j);
				if(i != -1) {
					j = sb.indexOf("\" onclick=\"window.open('http://dl.free.fr/rm.pl?h=", i);
					if(j != -1) {
						String deleteURL = sb.substring(i+"<a class=\"underline\" href=\"".length(), j);
						info =  new UploadInfo(downloadURL, deleteURL+"&f=1", FREE_HOST_ID);
					} else {
						info.setError("End marker for delete link not found");
					}
				} else {
					info.setError("Begin marker for delete link not found");
				}
			} else {
				info.setError("End marker for download link not found");
			}
		} else {
			info.setError("Begin marker for download link not found");
		}
		//- keep page content in case of problem occurred
		if(info.hasError()) {
			info.setFullPage(sb.toString());
			publishProgress(info.firstError);
		} else {
			publishProgress("File is now available on host server");
		}
		//- time stamp the result
		info.uploadDate = Calendar.getInstance();
		return info;
	}

	private static StringBuilder readFile(String file) throws IOException {
		String         line;
		StringBuilder  stringBuilder = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(LF);
			}
			return stringBuilder;
		}
	}

	@Override
	public void onProgressUpdate(String progress) {
		publishProgress(progress);
	}

	//- to be notified when archive is sent to dl.free.fr server
    public interface OnArchiveSentListener {
		void onArchiveSent(UploadInfo info);
	}
}
