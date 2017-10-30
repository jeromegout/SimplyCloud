package org.jeromegout.simplycloud.history;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.jeromegout.simplycloud.send.UploadInfo;

import java.util.Calendar;
import java.util.List;

public class UploadItem implements Parcelable {

    private List<Uri> content;
    private long size;
    private UploadInfo info;

	public UploadItem(List<Uri> uris, long size, UploadInfo info) {
	    this.content = uris;
	    this.info = info;
	    this.size = size;
    }

    protected UploadItem(Parcel in) {
        content = in.createTypedArrayList(Uri.CREATOR);
        info = in.readParcelable(UploadInfo.class.getClassLoader());
        size = in.readLong();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(info, flags);
        dest.writeList(content);
        dest.writeLong(size);
    }

    public List<Uri> getContent() {
        return content;
    }

    public UploadInfo getInfo() {
        return info;
    }

    public long getSize() {
        return size;
    }
}
