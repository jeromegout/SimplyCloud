package org.jeromegout.simplycloud.send;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.selection.fragments.FileItem;
import org.jeromegout.simplycloud.selection.fragments.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSendAdapter extends RecyclerView.Adapter<FileSendAdapter.Holder> {

	private List<FileItem> files;

	class Holder extends RecyclerView.ViewHolder {
		ImageView icon;
		TextView name;

		public Holder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.item_filename);
			icon = (ImageView) view.findViewById(R.id.item_icon_imageview);
		}
	}

	public FileSendAdapter(List<Uri> uris, Context context) {
		files = new ArrayList<>(uris.size());
		File file;
		for (Uri uri : uris ) {
			file = new File(uri.getPath());
			Drawable drawable = FileUtil.getDrawable(context, file);
			files.add(new FileItem(uri.getLastPathSegment(), drawable));
		}
	}

	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_send, parent, false);
		return new Holder(itemView);
	}

	@Override
	public void onBindViewHolder(Holder holder, int position) {
		FileItem item = files.get(position);
		holder.icon.setImageDrawable(item.getIcon());
		holder.name.setText(item.getName());
	}

	@Override
	public int getItemCount() {
		return files.size();
	}
}