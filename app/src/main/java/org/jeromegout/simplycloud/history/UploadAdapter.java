package org.jeromegout.simplycloud.history;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class UploadAdapter extends RecyclerView.Adapter <UploadAdapter.Holder> {

    private List<UploadItem> items;


    public class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }


    public UploadAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if(items == null) return 0;
        return items.size();
    }

    void setItems(List<UploadItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
