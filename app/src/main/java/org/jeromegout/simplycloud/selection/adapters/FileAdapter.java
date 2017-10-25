package org.jeromegout.simplycloud.selection.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.selection.fragments.FileItem;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileHolder> {
	private List<FileItem> items;
	private FileHolder.OnItemClickListener listener;

	public FileAdapter(FileHolder.OnItemClickListener listener) {
		this.listener = listener;
		items = new ArrayList<>();
	}

	@Override
	public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return new FileHolder(listener, inflater.inflate(R.layout.item_file_selection, parent, false));
	}

	@Override
	public void onBindViewHolder(final FileHolder holder, int position) {
		holder.bind(items.get(position));
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void setItems(List<FileItem> items) {
		this.items = items;
		notifyDataSetChanged();
	}
}
