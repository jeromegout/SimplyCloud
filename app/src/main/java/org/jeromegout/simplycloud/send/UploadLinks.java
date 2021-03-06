package org.jeromegout.simplycloud.send;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class UploadLinks implements Parcelable {

	public String downloadLink;
	public String deleteLink;
	public String fullPage;
	public String firstError;
	public String hostId;
	public Calendar uploadDate;

    public String getFullPage() {
		return fullPage;
	}

	public void setFullPage(String fullPage) {
		this.fullPage = fullPage;
	}

	public UploadLinks(String downloadLink, String deleteLink, String host) {
		this.firstError = null;
		this.hostId = host;
		if(downloadLink != null) {
			this.downloadLink = downloadLink.replaceAll("&amp;", "&");
		} else {
			this.downloadLink = null;
			this.firstError = "download link is NULL";
		}
		if(deleteLink != null) {
			this.deleteLink = deleteLink.replaceAll("&amp;", "&");
		} else {
			this.deleteLink = null;
			if(this.firstError == null) {
				this.firstError = "delete link is NULL";
			} else {
				//- both null
				this.firstError = "both download and delete links are NULL";
			}
		}
	}

	public boolean hasError() {
		return firstError != null;
	}

	public String getError() {
		return firstError;
	}

	public void setError(String error) {
		this.firstError = error;
	}

    protected UploadLinks(Parcel in) {
        downloadLink = in.readString();
        deleteLink = in.readString();
        fullPage = in.readString();
        firstError = in.readString();
        hostId = in.readString();
        uploadDate = Calendar.getInstance();
        uploadDate.setTimeInMillis(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(downloadLink);
        dest.writeString(deleteLink);
        dest.writeString(fullPage);
        dest.writeString(firstError);
        dest.writeString(hostId);
        dest.writeLong(uploadDate.getTimeInMillis());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UploadLinks> CREATOR = new Creator<UploadLinks>() {
        @Override
        public UploadLinks createFromParcel(Parcel in) {
            return new UploadLinks(in);
        }

        @Override
        public UploadLinks[] newArray(int size) {
            return new UploadLinks[size];
        }
    };
}
