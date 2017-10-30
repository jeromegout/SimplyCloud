package org.jeromegout.simplycloud.history;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class HistoryModel implements Parcelable {

    public final static HistoryModel instance = new HistoryModel();

    private List<UploadItem> histories;
    private List<OnHistoryModelChangedistener> listeners;

    private HistoryModel() {
        histories = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void addHistory(UploadItem item) {
        if(! histories.contains(item)) {
            histories.add(item);
            notifyListeners();
        }
    }

    public void removeHistory(UploadItem item) {
        histories.remove(item);
        notifyListeners();
    }

    protected HistoryModel(Parcel in) {
        histories = in.createTypedArrayList(UploadItem.CREATOR);
    }

    public static final Creator<HistoryModel> CREATOR = new Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(histories);
    }

    public void setHistories(List<UploadItem> histories) {
        this.histories = histories;
        notifyListeners();
    }

    public List<UploadItem> getHistories() {
        return histories;
    }

    public UploadItem getUpload(int position) {
        if(position >= 0 && position < histories.size()) {
            return histories.get(position);
        }
        return null;
    }

    void addOnHistoryModelChangedListener(OnHistoryModelChangedistener listener) {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    void removeHistoryModelChangedListener(OnHistoryModelChangedistener listener) {
        listeners.remove(listener);
    }

    void notifyListeners() {
        for (OnHistoryModelChangedistener listerner : listeners) {
            listerner.onHistoryModelChanged();
        }
    }

    //- notification listener
    interface OnHistoryModelChangedistener {

        void onHistoryModelChanged();

    }
}
