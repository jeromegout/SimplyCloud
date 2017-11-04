package org.jeromegout.simplycloud.history;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HistoryModel {

    public final static HistoryModel instance = new HistoryModel();

    private List<UploadItem> histories;
    private List<OnHistoryModelChangedListener> listeners;
    private Context context;


    //- notification listener
    interface OnHistoryModelChangedListener {

        void onHistoryModelChanged();

    }

    private HistoryModel() {
        histories = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void addHistory(UploadItem item) {
        if(! histories.contains(item)) {
            histories.add(item);
            notifyListeners();
            saveModel();
        }
    }

    public void removeHistory(UploadItem item) {
        histories.remove(item);
        notifyListeners();
        saveModel();
    }

    private void setHistories(List<UploadItem> histories, boolean saveModel) {
        this.histories = histories;
        notifyListeners();
        if(saveModel) saveModel();
    }

    public void setHistories(List<UploadItem> histories) {
        setHistories(histories, true);
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

    void addOnHistoryModelChangedListener(OnHistoryModelChangedListener listener) {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    void removeHistoryModelChangedListener(OnHistoryModelChangedListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (OnHistoryModelChangedListener listener : listeners) {
            listener.onHistoryModelChanged();
        }
    }

    private void saveModel() {
        if(context != null) {
            try {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                try (OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("historyDB", MODE_PRIVATE))) {
                    String s = gson.toJson(histories);
                    writer.write(s.replace("\\\\", "\\"));
                }
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage(), e);
            }
        }
    }

    public void restoreHistories(Context context) {
        try {
            this.context = context;
            FileInputStream fi = context.openFileInput("historyDB");
            List<UploadItem> uploadItems = readJsonStream(fi);
            setHistories(uploadItems, false);
        } catch (FileNotFoundException e) {
            //- nothing to do, file doesn't exist
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage(), e);
        }
    }

    private List<UploadItem> readJsonStream(InputStream in) throws IOException {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<UploadItem>>(){}.getType();
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            return gson.fromJson(reader, collectionType);
        }
    }
}
