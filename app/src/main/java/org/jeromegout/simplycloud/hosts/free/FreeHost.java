package org.jeromegout.simplycloud.hosts.free;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.jeromegout.simplycloud.Logging;
import org.jeromegout.simplycloud.R;
import org.jeromegout.simplycloud.history.HistoryModel;
import org.jeromegout.simplycloud.history.UploadItem;
import org.jeromegout.simplycloud.hosts.HostServices;
import org.jeromegout.simplycloud.hosts.Uploader;
import org.jeromegout.simplycloud.send.UploadLinks;

import java.io.File;
import java.util.Calendar;

public class FreeHost extends Uploader implements HostServices {

    public static final String HOST_ID = "dl.free.fr";
    private final static String FREE_URL = "http://dl.free.fr/upload.pl?b15059952545362975907183455737546";//$NON-NLS-1$

    @Override
    public String getHostId() {
        return HOST_ID;
    }

    @Override
    public int getHostLogoId() {
        return R.drawable.ic_free_logo;
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
    public void uploadArchive(Context context, File archive, String uploadId) {
        startUpload(uploadId);
        try {
            new MultipartUploadRequest(context, uploadId, FREE_URL)
                    .addFileToUpload(archive.getPath(), "ufile", archive.getName())
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();
        } catch (Exception e) {
            Logging.e(e.getMessage(), e);
        }
    }

    @Override
    public void onUploadCompleted(Context context, UploadInfo info, ServerResponse response) {
        super.onUploadCompleted(context, info, response); //- this calls super.finishUpload
        //- retrieving the monitoring url from the response
        String monURL = response.getHeaders().get("Location");
        Logging.d(monURL);
        //- retrieve links from the monitoring page
        getUploadInfos(context, monURL, info.getUploadId());
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

    /**
     * Given a monitoring url page, it returns the link of the uploaded file and the URL to delete it from server
     *
     * @param monURL monitoring URL (the URL that will contain download and delete links when finish)
     * @param uploadId the unique id used for the upload session (created by UploadActivity.sendArchive())
     * @return upload information (download and delete links)
     */
    private void getUploadInfos(final Context context, final String monURL, final String uploadId) {
        final RequestQueue queue = Volley.newRequestQueue(context);

        final StringRequest linksRequest = new StringRequest(Request.Method.GET, monURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (isContainingRefreshMaker(response)) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Logging.e(e.getMessage(), e);
                    }
                    Logging.d("REFRESH !!!!!");
                    // TODO update the item status (progress monitor) according to its uploadId
                    getUploadInfos(context, monURL, uploadId);
                } else {
                    //TODO update the item status : finished in the model
                    //- we reached the end of the page without finding the refresh marker
                    //- we can return the collected info
                    UploadLinks links = findLinks(response);
                    HistoryModel.instance.setLinks(uploadId, links);
                    UploadItem item = HistoryModel.instance.getUploadItem(uploadId);
                    if(item !=null && links != null) {
                        Toast.makeText(context, item.getTitle()+ " successfully uploaded on "+getHostId(), Toast.LENGTH_LONG).show();
                        Logging.d("FINISHED !!!!!!");
                        Logging.d("Download: "+links.downloadLink);
                        Logging.d("Delete:   "+links.deleteLink);
                    } else {
                        Toast.makeText(context, "Problem uploading "+item.getTitle()+ " on "+getHostId(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(linksRequest);
    }

    private boolean isContainingRefreshMaker(String line) {
        return line.trim().replaceAll(" ", "").contains("functiondoLoad(){setTimeout(\"refresh()\",2*1000);}");
    }

    private UploadLinks findLinks(String sb) {
        UploadLinks info = new UploadLinks(null, null, HOST_ID);
        int i = sb.indexOf("<a class=\"underline\" href=\"http://dl.free.fr/");
        if(i != -1) {
            int j = sb.indexOf("\" onclick=\"window.open('http://dl.free.fr/", i);
            if(j != -1) {
                String downloadURL = sb.substring(i+"<a class=\"underline\" href=\"".length(), j);
                i = sb.indexOf("<a class=\"underline\" href=\"http://dl.free.fr/rm.pl?h=", j);
                if(i != -1) {
                    j = sb.indexOf("\" onclick=\"window.open('http://dl.free.fr/rm.pl?h=", i);
                    if(j != -1) {
                        String deleteURL = sb.substring(i+"<a class=\"underline\" href=\"".length(), j);
                        info =  new UploadLinks(downloadURL, deleteURL+"&f=1", HOST_ID);
                    } else {
                        info.setError("End marker for delete link not found");
                    }
                } else {
                    info.setError("Begin marker for delete link not found");
                }
            } else {
                info.setError("End marker for download link not found");
            }
        } else {
            info.setError("Begin marker for download link not found");
        }
        //- keep page content in case of problem occurred
        if(info.hasError()) {
            info.setFullPage(sb);
        }
        //- time stamp the result
        info.uploadDate = Calendar.getInstance();
        return info;
    }
}
