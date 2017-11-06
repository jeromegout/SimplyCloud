package org.jeromegout.simplycloud.selection.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.selection.SelectionModel;
import org.jeromegout.simplycloud.selection.fragments.FileItem;

import java.io.File;


public class FileHolder extends RecyclerView.ViewHolder {
	private final ImageView folderImage;
	private ImageView fileImage;
	private TextView fileName;
	//- for folder, shows the number of child, for regular file shows its size
	private TextView fileDetails;
	private CheckBox selectionBox;
	private OnItemClickListener listener;

	public FileHolder(OnItemClickListener listener, View itemView) {
		super(itemView);
		this.listener = listener;
		this.fileImage = (ImageView) itemView.findViewById(R.id.item_icon_imageview);
		this.fileName = (TextView) itemView.findViewById(R.id.item_filename);
		this.fileDetails = (TextView) itemView.findViewById(R.id.item_file_details);
		this.selectionBox = (CheckBox) itemView.findViewById(R.id.item_file_checkbox);
		this.folderImage = (ImageView) itemView.findViewById(R.id.dir_icon_inside);
	}

	public void bind(final FileItem item) {
		fileImage.setImageDrawable(item.getIcon());
		fileName.setText(item.getName());
		fileDetails.setText(item.getDetails());
		selectionBox.setOnCheckedChangeListener(null);
		selectionBox.setChecked(item.isSelected());
		selectionBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				selectItem(item, isChecked);
			}
		});
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(item.isFile()) {
					selectionBox.performClick();
				} else {
					if(listener != null) listener.onItemClick(item);
 				}
			}
		});
		if(item.isDirectory()) {
			folderImage.setVisibility(View.VISIBLE);
		} else {
			folderImage.setVisibility(View.GONE);
		}
	}

	protected void selectItem(FileItem item, boolean isChecked) {
		if (item.isFile()) {
			if (isChecked) {
				SelectionModel.instance.selectElement(item.toURI());
			} else {
				SelectionModel.instance.unselectElement(item.toURI());
			}
		} else {
			//- item is a directory, select all child (only simple files)
			File[] child = item.getDirectoryFiles();
			for (File file : child) {
				if (isChecked) {
					SelectionModel.instance.selectElement(file.toURI());
				} else {
					SelectionModel.instance.unselectElement(file.toURI());
				}
			}
		}
	}

	//- Listener to be notified of click on item (not selection of item)
	public interface OnItemClickListener {
		void onItemClick(FileItem item);
	}
}
