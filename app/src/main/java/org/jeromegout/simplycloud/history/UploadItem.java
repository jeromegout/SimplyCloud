package org.jeromegout.simplycloud.history;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.jeromegout.simplycloud.selection.fragments.FileUtil;
import org.jeromegout.simplycloud.send.UploadLinks;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class UploadItem implements Parcelable {

    private List<String> content;
    private long size;
    private UploadLinks links;
    private String title;
    private String uploadId;

    public UploadItem(List<Uri> uris, long size, String title, String uploadId) {
        this(uris, size, null, title, uploadId);
    }

	public UploadItem(List<Uri> uris, long size, UploadLinks links, String title, String uploadId) {
	    this.content = new ArrayList<>(uris.size());
        for (Uri uri: uris) {
            this.content.add(uri.getPath());
        }
        this.links = links;
	    this.size = size;
	    this.title = title;
	    this.uploadId = uploadId;
    }

    protected UploadItem(Parcel in) {
        content = in.createStringArrayList();
        size = in.readLong();
        links = in.readParcelable(UploadLinks.class.getClassLoader());
        title = in.readString();
        uploadId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(content);
        dest.writeLong(size);
        dest.writeParcelable(links, flags);
        dest.writeString(title);
        dest.writeString(uploadId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UploadItem> CREATOR = new Creator<UploadItem>() {
        @Override
        public UploadItem createFromParcel(Parcel in) {
            return new UploadItem(in);
        }

        @Override
        public UploadItem[] newArray(int size) {
            return new UploadItem[size];
        }
    };

    public List<Uri> getContent() {
        List<Uri> res = new ArrayList<>(content.size());
        for (String s: content) {
            res.add(Uri.parse(s));
        }
        return res;
    }

    public void setLinks(UploadLinks links) {
        this.links = links;
    }

    public UploadLinks getLinks() {
        return links;
    }

    public long getSize() {
        return size;
    }

    public String getHumanReadableSize() {
        return FileUtil.getReadableSize(size);
    }

    public String getHumanReadableDate() {
        return DateFormat.getDateInstance().format(links.uploadDate.getTime());
    }

    public String getHumanReadableDateTime() {
        return DateFormat.getDateTimeInstance().format(links.uploadDate.getTime());
    }

    public String getHumanReadableTime() {
        return DateFormat.getTimeInstance().format(links.uploadDate.getTime());
    }

    public String getHumanReadableTitle() {
        return title != null && title.length() > 0 ? title : "Unnamed archive";
    }

    public String getTitle() {
        return title;
    }

    public String getUploadId() {
        return uploadId;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UploadItem){
            UploadItem other = (UploadItem) obj;
            return uploadId.equals(other.uploadId);
        }
        return super.equals(obj);
    }
}
