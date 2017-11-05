package org.jeromegout.simplycloud.history;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.jeromegout.simplycloud.selection.fragments.FileUtil;
import org.jeromegout.simplycloud.send.UploadInfo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class UploadItem implements Parcelable {

    private String title;
    private List<String> content;
    private long size;
    private UploadInfo info;

	public UploadItem(List<Uri> uris, long size, UploadInfo info, String title) {
	    this.content = new ArrayList<>(uris.size());
        for (Uri uri: uris) {
            this.content.add(uri.getPath());
        }
        this.info = info;
	    this.size = size;
	    this.title = title;
    }

    protected UploadItem(Parcel in) {
        content = in.createStringArrayList();
        size = in.readLong();
        info = in.readParcelable(UploadInfo.class.getClassLoader());
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(content);
        dest.writeLong(size);
        dest.writeParcelable(info, flags);
        dest.writeString(title);
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

    public UploadInfo getInfo() {
        return info;
    }

    public long getSize() {
        return size;
    }

    public String getHumanReadableSize() {
        return FileUtil.getReadableSize(size);
    }

    public String getHumanReadableDate() {
        return DateFormat.getDateInstance().format(info.uploadDate.getTime());
    }

    public String getHumanReadableDateTime() {
        return DateFormat.getDateTimeInstance().format(info.uploadDate.getTime());
    }

    public String getHumanReadableTime() {
        return DateFormat.getTimeInstance().format(info.uploadDate.getTime());
    }

    public String getHumanReadableTitle() {
        return title != null && title.length() > 0 ? title : "Unnamed archive";
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UploadItem){
            UploadItem other = (UploadItem) obj;
            return info.uploadDate.getTimeInMillis() == other.getInfo().uploadDate.getTimeInMillis();
        }
        return super.equals(obj);
    }
}
