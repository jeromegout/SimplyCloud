package org.jeromegout.simplycloud.send;

import java.util.Calendar;

public class UploadInfo {

	public String downloadLink;
	public String deleteLink;
	public String fullPage;
	public String firstError;
	public String host;
	public Calendar uploadedDate;

	public String getFullPage() {
		return fullPage;
	}

	public void setFullPage(String fullPage) {
		this.fullPage = fullPage;
	}

	public UploadInfo(String downloadLink, String deleteLink, String host, Calendar date) {
	    this.host = host;
	    this.uploadedDate = date;
		this.firstError = null;
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
}
