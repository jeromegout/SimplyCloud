package org.jeromegout.simplycloud.selection;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectionModel {

    private final List<Uri> selection;
    public final static SelectionModel instance = new SelectionModel();
    private final List<SelectionListener> listeners;


    private SelectionModel() {
        this.selection = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public void clearSelection() {
        if (!selection.isEmpty()) {
            selection.clear();
            notifyListeners();
        }
    }

    public void setSelection(@NonNull List<Uri> sel) {
        if (!selection.equals(sel)) {
            selection.clear();
            selection.addAll(sel);
            notifyListeners();
        }
    }

    public List<Uri> getSelection() {
        return new ArrayList<>(selection);
    }


    private void notifyListeners() {
        for (SelectionListener listener : listeners) {
            listener.selectionChanged();
        }
    }

    public void selectElement(@NonNull Uri element) {
        if(!selection.contains(element)) {
            selection.add(element);
            notifyListeners();
        }
    }

    public void unselectElement(@NonNull Uri element) {
        if(selection.remove(element)) {
            notifyListeners();
        }
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

        void selectionChanged();
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
