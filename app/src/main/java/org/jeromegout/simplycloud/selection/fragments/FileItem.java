package org.jeromegout.simplycloud.selection.fragments;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.jeromegout.simplycloud.selection.SelectionModel;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class FileItem extends File {
	private Drawable icon;

	public FileItem(String path, Drawable icon) {
		super(path);
		this.icon = icon;
	}

	public Drawable getIcon() {
		return icon;
	}

	public String getDetails() {
		if(isDirectory()) {
			return String.format("with %d file(s)", getDirectoryFiles().length);
		} else {
			return FileUtil.getReadableFileSize(this);
		}
	}

	/**
	 * Returns all files (only) children of the current instance (if it is a directory) or empty array
	 * @return array of File or null
	 */
	public File[] getDirectoryFiles() {
		if(isDirectory() && canRead()) {
			return listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.canRead() && file.isFile();
				}
			});
		}
		return new File[0];
	}

	//- We want our list to show directories followed by files below them
	@Override
	public int compareTo(@NonNull File pathname) {
		if (isDirectory() && pathname.isFile()) {
			return -1;
		}
		if (isFile() && pathname.isDirectory()) {
			return 1;
		}
		return 0;
	}

	public boolean isSelected() {
		List<Uri> currentSel = SelectionModel.instance.getSelection();
		for (Uri uri : currentSel) {
			if(getAbsolutePath().equals(uri.getPath())) {
				return true;
			}
		}
		return false;
	}
}
