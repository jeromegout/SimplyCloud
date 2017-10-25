package org.jeromegout.simplycloud.selection;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.jeromegout.simplycloud.selection.fragments.FileUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

;

public class SelectionModel {

    private final List<Uri> selection;
    private long selectionSize;
    public final static SelectionModel instance = new SelectionModel();
    private final List<SelectionListener> listeners;

    private SelectionModel() {
        this.selection = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public void clearSelection() {
        if (!selection.isEmpty()) {
            selection.clear();
            selectionSize = 0;
            notifyListeners();
        }
    }

    public void setSelection(@NonNull List<Uri> sel) {
        if (!selection.equals(sel)) {
            selection.clear();
            for (Uri uri : sel) {
                selection.add(uri);
                selectionSize += FileUtil.getFileSize(uri);
            }
            notifyListeners();
        }
    }

    public List<Uri> getSelection() {
        return new ArrayList<>(selection);
    }

    public long getSelectionSize() {
        return selectionSize;
    }

    private void notifyListeners() {
        for (SelectionListener listener : listeners) {
            listener.selectionChanged(selection.size(), selectionSize);
        }
    }

    public void selectElement(@NonNull Uri element) {
        if(!selection.contains(element)) {
            selection.add(element);
            selectionSize += FileUtil.getFileSize(element);
            notifyListeners();
        }
    }
    
    public void selectElement(@NonNull URI uri) {
        selectElement(Uri.parse(uri.toString()));
    }

    public void unselectElement(@NonNull Uri element) {
        if(selection.remove(element)) {
            selectionSize -= FileUtil.getFileSize(element);
            notifyListeners();
        }
    }

    public void unselectElement(URI uri) {
        unselectElement(Uri.parse(uri.toString()));
    }

    //- Listeners
    public void addSelectionListener(@NonNull SelectionListener listener) {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public boolean removeSelectionListener(@NonNull SelectionListener listener) {
        return listeners.remove(listener);
    }

    //- listener for model changes.
    public interface SelectionListener {

        void selectionChanged(int count, long amount);
    }

    public static long getURISize(Context context, Uri uri) {
        try (InputStream is = context.getContentResolver().openInputStream(uri)) {
            return is.available();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
