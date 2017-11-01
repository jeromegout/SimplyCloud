package org.jeromegout.simplycloud.history;

import android.net.Uri;

import org.jeromegout.simplycloud.send.UploadInfo;

import java.util.ArrayList;
import java.util.List;

public class UploadItem  {

    private List<String> content;
    private long size;
    private UploadInfo info;

	public UploadItem(List<Uri> uris, long size, UploadInfo info) {
	    this.content = new ArrayList<>(uris.size());
        for (Uri uri: uris) {
            this.content.add(uri.getPath());
        }
        this.info = info;
	    this.size = size;
    }

    public List<Uri> getContent() {
        List<Uri> res = new ArrayList<>(content.size());
        for (String s: content) {
            res.add(Uri.parse(s));
        }
        return res;
    }

    UploadInfo getInfo() {
        return info;
    }

    public long getSize() {
        return size;
    }
}
