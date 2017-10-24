package org.jeromegout.simplycloud.selection.adapters;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;

import java.io.File;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Cursor data;
    private Callbacks callbacks;


    public void setData(Cursor data) {
        if (data != this.data) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//TODO inflate holder view for both bucket and image

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (data != null && !data.isClosed()) {
            return data.getCount();
        }
        return 0;
    }

    private String getLabel(int position) {
        assert data != null; // It is supposed not be null here
        data.moveToPosition(position);
//        if (mViewType == VIEW_TYPE_MEDIA) {
//            return data.getString(data.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
//        } else {
//            return data.getString(data.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
//        }
        return  "";
    }

    private Uri getData(int position) {
        assert data != null; // It is supposed not be null here
        data.moveToPosition(position);
        return Uri.fromFile(new File(data.getString(data.getColumnIndex(MediaStore.Images.Media.DATA))));
    }

    private long getBucketId(int position) {
        assert data != null; // It is supposed not be null here
        data.moveToPosition(position);
        return data.getLong(data.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
    }


    //- Holders

    abstract class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;

        private ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    private class BucketViewHolder extends ViewHolder implements View.OnClickListener {

        private final TextView textView;

        private BucketViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            if (callbacks != null) {
                callbacks.onBucketClick(getItemId(), getLabel(position));
            }
        }

    }



    class MediaViewHolder extends ViewHolder implements View.OnClickListener {

        final CheckedTextView checkView;

        private MediaViewHolder(View itemView) {
            super(itemView);
            checkView = (CheckedTextView) itemView.findViewById(R.id.check);
            checkView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            if (v == checkView) {
                boolean selectionChanged = handleChangeSelection(position);
//                if (selectionChanged) {
//                    notifyItemChanged(position, SELECTION_PAYLOAD);
//                }
//                if (callbacks != null) {
//                    if (selectionChanged) {
//                        callbacks.onSelectionUpdated(selection.size());
//                    } else {
//                        mCallbacks.onMaxSelectionReached();
//                    }
//                }
            } else {
//                if (mCallbacks != null) {
//                    mCallbacks.onMediaClick(mImageView, checkView, getBucketId(position), position);
//                }
            }
        }
    }

    private boolean handleChangeSelection(int position) {
        Uri data = getData(position);
//        if (!isSelected(position)) {
//            if (mSelection.size() == mMaxSelection) {
//                return false;
//            }
//            mSelection.add(data);
//        } else {
//            mSelection.remove(data);
//        }
        return true;
    }

    //- Callbacks
    public interface Callbacks {

        void onBucketClick(long bucketId, String label);

        void onMediaClick(View imageView, View checkView, long bucketId, int position);

        void onSelectionUpdated(int count);
    }

}
