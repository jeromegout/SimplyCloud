package org.jeromegout.simplycloud.history;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.hosts.HostServices;
import org.jeromegout.simplycloud.hosts.free.FreeHost;
import org.jeromegout.simplycloud.send.UploadActivity;
import org.jeromegout.simplycloud.send.UploadInfo;
import org.jeromegout.simplycloud.share.ShareActivity;

import java.util.ArrayList;
import java.util.Calendar;


public class UploadAdapter extends RecyclerView.Adapter <UploadAdapter.Holder> implements HistoryModel.OnHistoryModelChangedListener {


    private final Context context;

    class Holder extends RecyclerView.ViewHolder {

        ImageView dateIcon;
        TextView uploadTitle;
        TextView uploadDetails;
        TextView uploadDate;
        TextView uploadSize;

        Holder(View itemView) {
            super(itemView);
            dateIcon = (ImageView) itemView.findViewById(R.id.dateImage);
            uploadTitle = (TextView) itemView.findViewById(R.id.uploadTitle);
            uploadDetails = (TextView) itemView.findViewById(R.id.uploadDetails);
            uploadDate = (TextView) itemView.findViewById(R.id.uploadDate);
            uploadSize = (TextView) itemView.findViewById(R.id.uploadSize);
        }

        void bind(final UploadItem item) {
            dateIcon.setImageDrawable(createIcon(item.getInfo().uploadDate));
            uploadTitle.setText(item.getHumanReadableTitle());
            uploadDetails.setText(String.format("%d files", item.getContent().size()));
            uploadDate.setText(item.getHumanReadableDateTime());
            uploadSize.setText(item.getHumanReadableSize());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUploadHistory(item);
                }
            });
        }
    }

    public UploadAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onHistoryModelChanged() {
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        HistoryModel.instance.addOnHistoryModelChangedListener(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        HistoryModel.instance.removeHistoryModelChangedListener(this);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_upload, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        UploadItem item = HistoryModel.instance.getUpload(position);
        holder.bind(item);
    }

    private Drawable createIcon(Calendar date) {
        //TODO create an icon the given date
        return ContextCompat.getDrawable(context,R.drawable.ic_today_black_24dp);
    }

    @Override
    public int getItemCount() {
        return HistoryModel.instance.getHistories().size();
    }

    private void openUploadHistory(final UploadItem item) {
        FreeHost free = new FreeHost();
        free.archiveStillAlive(context, item.getInfo().downloadLink, new HostServices.OnListener() {
            @Override
            public void onUploadUpdate(String update) {
                if(HostServices.OK.equals(update)) {
                    openShareItem(item);
                } else {
                    //- no more archive on the server, need to upload it again
                    openSendItem(item);
                }
            }
            @Override
            public void onUploadFinished(UploadInfo info) {}
            @Override
            public void onArchiveDeleted(boolean deletePerformed) {}
        });
    }

    private void openSendItem(UploadItem item) {
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, "Upload is deprecated, need to send it again", Snackbar.LENGTH_SHORT).show();
        Intent intent = new Intent(context, UploadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selection", (ArrayList<? extends Parcelable>) item.getContent());
        intent.putExtras(bundle);
        //- pass the bare title (not the human readable one)
        intent.putExtra("title", item.getTitle());
        //- remove this item from the model since is deprecated (a new one will be created in sendActivity)
        HistoryModel.instance.removeHistory(item);
        context.startActivity(intent);
    }

    private void openShareItem(UploadItem item) {
        Intent intent = new Intent(context, ShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("uploadItem", item);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}