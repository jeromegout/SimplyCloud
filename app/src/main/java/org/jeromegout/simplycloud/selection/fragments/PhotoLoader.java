package org.jeromegout.simplycloud.selection.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public class PhotoLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BUCKET_LOADER = 1;
    private static final int MEDIA_LOADER = 2;
    private static final String BUCKET_ID = MediaStore.Images.Media.BUCKET_ID;

    private FragmentActivity activity;
    private Callbacks callbacks;

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == BUCKET_LOADER) {
            return new CursorLoader(activity,
                    PhotoQuery.GALLERY_URI,
                    PhotoQuery.BUCKET_PROJECTION,
                    String.format("%s AND %s", "1", PhotoQuery.BUCKET_SELECTION),
                    null,
                    PhotoQuery.BUCKET_SORT_ORDER);
        } else if(id == MEDIA_LOADER){
            return new CursorLoader(activity,
                    PhotoQuery.GALLERY_URI,
                    PhotoQuery.IMAGE_PROJECTION,
                    String.format("%s=%s AND %s", MediaStore.Images.Media.BUCKET_ID, args.getLong(BUCKET_ID), "1"),
                    null,
                    PhotoQuery.MEDIA_SORT_ORDER);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(callbacks != null) {
            if (loader.getId() == BUCKET_LOADER) {
                callbacks.onBucketLoadFinished(data);
            } else if(loader.getId() == MEDIA_LOADER) {
                callbacks.onMediaLoadFinished(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {}

    public void onAttach(@NonNull FragmentActivity activity, @NonNull Callbacks callbacks) {
        this.activity = activity;
        this.callbacks = callbacks;
    }

    public void onDetach() {
        this.activity = null;
        this.callbacks = null;
    }

    public void loadPhotoBuckets() {
        if(activity != null) {
            activity.getSupportLoaderManager().restartLoader(BUCKET_LOADER, null, this);
        }
    }

    public void loadPhotoBucket(long bucketId) {
        if(activity != null) {
            Bundle args = new Bundle();
            args.putLong(BUCKET_ID, bucketId);
            activity.getSupportLoaderManager().restartLoader(MEDIA_LOADER, args, this);
        }
    }

    //- Notification of elements loading
    public interface Callbacks {

        void onBucketLoadFinished(Cursor data);

        void onMediaLoadFinished(Cursor data);
    }




}
