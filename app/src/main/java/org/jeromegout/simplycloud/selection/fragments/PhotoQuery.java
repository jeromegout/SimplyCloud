package org.jeromegout.simplycloud.selection.fragments;


import android.net.Uri;
import android.provider.MediaStore;

public interface PhotoQuery {

    final Uri GALLERY_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    final String[] IMAGE_PROJECTION = {
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATA
    };

    final String[] BUCKET_PROJECTION = {
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATA
    };

    final String BUCKET_SELECTION = "1) GROUP BY (1";

    final String MEDIA_SORT_ORDER = MediaStore.Images.Media.DATE_TAKEN + " DESC";
    final String BUCKET_SORT_ORDER = "MAX(" + MediaStore.Images.Media.DATE_TAKEN + ") DESC";

}
