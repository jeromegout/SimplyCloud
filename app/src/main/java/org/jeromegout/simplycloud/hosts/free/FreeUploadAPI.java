package org.jeromegout.simplycloud.hosts.free;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jeromegout.simplycloud.hosts.HostServices;

import java.io.File;

public class FreeUploadAPI implements HostServices {

    @Override
    public String getHostId() {
        return "dl.free.fr";
    }

    @Override
    public void archiveStillAlive(Context context, String archiveURL, @NonNull final OnListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET, archiveURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(isContainingValidArchiveMarker(response)) {
                    listener.onUploadUpdate(HostServices.OK);
                } else {
                    listener.onUploadUpdate(HostServices.KO);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onUploadUpdate(HostServices.KO);
            }
        }));
    }

    @Override
    public void uploadArchive(Context context, File archive, OnListener listener) {

    }

    @Override
    public void deleteArchive(Context context, String deleteURL, @NonNull final OnListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET, deleteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean deprecated = isContainingDeprecatedMarker(response);
                boolean deleted = isContainingDeletionSuccessMarker(response);
                listener.onArchiveDeleted(deprecated || deleted);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onArchiveDeleted(false);
            }
        }));
    }

    private boolean isContaining404Marker(String page){
        return page.contains("ERREUR 404 - Document non trouvé");
    }

    private boolean isContainingValidArchiveMarker(String page) {
        return page.contains("name=\"send\" value=\"Valider et t&eacute;l&eacute;charger le fichier\"");
    }

    private boolean isContainingDeletionSuccessMarker(String page) {
        return page.contains("a &eacute;t&eacute; supprim&eacute; avec succ&egrave;s.");
    }

    private  boolean isContainingDeprecatedMarker(String page) {
        return page.contains("Fichier perim&eacute ou d&eacute;j&agrave; supprim&eacute;");
    }
}
