package org.jeromegout.simplycloud.selection.fragments;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.webkit.MimeTypeMap;

import org.jeromegout.simplycloud.R;

import java.io.File;
import java.text.DecimalFormat;

public class FileUtil {

	public static Drawable getDrawable(Context context, File file) {
		int drawableId;
		if (file.isDirectory()) {
			drawableId = R.drawable.ic_file_folder;
		} else {
			String mimeType = getMimeType(file);
			if(mimeType == null) {
				drawableId = R.drawable.ic_file_default;
			} else if(mimeType.startsWith("video")) {
				drawableId = R.drawable.ic_file_video;
			} else if(mimeType.startsWith("audio")) {
				drawableId = R.drawable.ic_file_music;
			} else if(mimeType.startsWith("image")) {
				drawableId = R.drawable.ic_file_photo;
			} else if(mimeType.equals("application/zip")) {
				drawableId = R.drawable.ic_file_zip;
			} else if(mimeType.equals("application/pdf")) {
				drawableId = R.drawable.ic_file_pdf;
			} else if(mimeType.equals("application/msword")) {
				drawableId = R.drawable.ic_file_word;
			} else if(mimeType.equals("application/vnd.ms-excel")) {
				drawableId = R.drawable.ic_file_excel;
			} else if(mimeType.equals("text/plain")) {
				drawableId = R.drawable.ic_file_text;
			} else if(mimeType.equals("text/html")) {
				drawableId = R.drawable.ic_file_html;
			} else {
				drawableId = R.drawable.ic_file_default;
			}
		}
		return ContextCompat.getDrawable(context, drawableId);
	}

	public static String getReadableSize(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String getReadableFileSize(File file) {
		return getReadableSize(file.length());
	}

	public static long getFileSize(Uri uri) {
		return new File(uri.getPath()).length();
	}

	private static String getMimeType(File file) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
		if (extension != null) {
			type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		}
		return type;
	}
}
