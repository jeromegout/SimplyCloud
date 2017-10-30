package org.jeromegout.simplycloud.history;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.send.FreeSendActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class UploadAdapter extends RecyclerView.Adapter <UploadAdapter.Holder> implements HistoryModel.OnHistoryModelChangedistener {


    private final Context context;

    class Holder extends RecyclerView.ViewHolder {

        ImageView dateIcon;

        TextView uploadTitle;
        TextView uploadDetails;
        Holder(View itemView) {
            super(itemView);
            dateIcon = (ImageView) itemView.findViewById(R.id.dateImage);
            uploadTitle = (TextView) itemView.findViewById(R.id.uploadTitle);
            uploadDetails = (TextView) itemView.findViewById(R.id.uploadDetails);

        }
        public void bind(final UploadItem item) {
            Calendar date = item.getInfo().uploadDate;
            dateIcon.setImageDrawable(createIcon(date));
            uploadTitle.setText(item.getContent().size()+" files ("+item.getSize()+")");
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
            uploadDetails.setText(dateFormat.format(date.getTime()));
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

    private void openUploadHistory(UploadItem item) {
        Intent intent = new Intent(context, FreeSendActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selection", (ArrayList<? extends Parcelable>) item.getContent());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
