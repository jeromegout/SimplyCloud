package org.jeromegout.simplycloud.history;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.hosts.HostManager;
import org.jeromegout.simplycloud.hosts.HostServices;
import org.jeromegout.simplycloud.send.UploadActivity;
import org.jeromegout.simplycloud.send.UploadLinks;
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
            dateIcon = itemView.findViewById(R.id.dateImage);
            uploadTitle = itemView.findViewById(R.id.uploadTitle);
            uploadDetails = itemView.findViewById(R.id.uploadDetails);
            uploadDate = itemView.findViewById(R.id.uploadDate);
            uploadSize = itemView.findViewById(R.id.uploadSize);
        }

        void bind(final UploadItem item) {
            boolean completed = item.getLinks() != null;
            dateIcon.setImageDrawable(createIcon(item.getUploadDate()));
            uploadTitle.setText(item.getHumanReadableTitle());
            uploadDetails.setText(String.format("%d files", item.getContent().size()));
            uploadDate.setText(item.getHumanReadableDateTime());
            uploadSize.setText(item.getHumanReadableSize());
            if(completed) {
                dateIcon.setAlpha(1f);
                setTextAppearance(uploadTitle, R.style.HistoryTitle_completed);
                setTextAppearance(uploadDetails, R.style.HistoryDetails_completed);
                setTextAppearance(uploadDate, R.style.HistoryDate_completed);
                setTextAppearance(uploadSize, R.style.HistorySize_completed);
            } else {
                dateIcon.setAlpha(0.38f);
                setTextAppearance(uploadTitle, R.style.HistoryTitle_uncompleted);
                setTextAppearance(uploadDetails, R.style.HistoryDetails_uncompleted);
                setTextAppearance(uploadDate, R.style.HistoryDate_uncompleted);
                setTextAppearance(uploadSize, R.style.HistorySize_uncompleted);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUploadHistory(item);
                }
            });
        }
    }

    private void setTextAppearance(TextView view, int id) {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            view.setTextAppearance(id);
        } else {
            view.setTextAppearance(this.context, id);
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
    public void onUploadItemChanged(UploadItem item) {
        notifyItemChanged(HistoryModel.instance.getItemPosition(item));
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
        UploadItem item = HistoryModel.instance.getUploadItem(position);
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
        if(item.getLinks() == null) {
            Toast.makeText(context, "Upload not yet completed ... please wait", Toast.LENGTH_SHORT).show();
        } else {
            HostServices host = HostManager.instance.getHostById(item.getLinks().hostId);
            host.archiveStillAlive(context, item.getLinks().downloadLink, new HostServices.OnListener() {
                @Override
                public void onUploadUpdate(String update) {
                    if (HostServices.OK.equals(update)) {
                        openShareItem(item);
                    } else {
                        //- no more archive on the server, need to upload it again
                        openSendItem(item);
                    }
                }

                @Override
                public void onUploadFinished(UploadLinks info) {
                }

                @Override
                public void onArchiveDeleted(boolean deletePerformed) {
                }
            });
        }
    }

    private void openSendItem(UploadItem item) {
        Toast.makeText(context, "Upload is deprecated, need to send it again", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("item", item.getUploadId());
        context.startActivity(intent);
    }
}
