package org.jeromegout.simplycloud.selection.fragments;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.webkit.MimeTypeMap;

import org.jeromegout.simplycloud.Logging;
import org.jeromegout.simplycloud.R;

import java.io.File;
import java.net.URISyntaxException;
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

	public static boolean externalMemoryAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSizeLong();
		long availableBlocks = stat.getAvailableBlocksLong();
		return availableBlocks * blockSize;
	}

	public static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSizeLong();
			long availableBlocks = stat.getAvailableBlocksLong();
			return availableBlocks * blockSize;
		} else {
			return -1;
		}
	}

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                Logging.e(e);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
