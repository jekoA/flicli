package love.flicli.controller;
import android.app.IntentService;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import love.flicli.FlickerAPI;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.model.Comment;
import love.flicli.model.FlickModel;

import static love.flicli.R.id.cancel_action;
import static love.flicli.R.id.comments;
import static love.flicli.R.id.image;
import static love.flicli.R.id.populImage;

/**
 * Created by tommaso on 09/05/17.
 */

public class ApiController extends IntentService {
    private final static String TAG = ApiController.class.getName();
    private final static String ACTION_FLICKER = "searchFlick";
    private final static String ACTION_RECENT = "getRecentFlick";
    private final static String ACTION_POPULAR = "getPopularFlick";
    private final static String ACTION_COMMENT = "getCommentFlick";
    private final static String ACTION_AUTHOR = "getFlickByAuthor";
    private final static String ACTION_FAVOURITE = "getFavourities";
    private final static String ACTION_DOWNLOAD = "donwloadImage";

    private final static String PARAM = "param";
    private static String search = "";

    public ApiController() {
        super("ApiController");
    }

    @WorkerThread
    private JSONObject makeRequest(String endpoint) {
        String response = "";
        String line = "";
        BufferedReader in = null;
        JSONObject json = null;

        try {
            URL url = new URL(endpoint);
            URLConnection conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = in.readLine()) != null) {
                Log.d(TAG, "STARTING SEARCH OF" + line);
                response += line + "\n";
            }

            in.close();

            json = new JSONObject(response);

        } catch (IOException e) {
            Log.d(TAG, "I/O error", e);
            return null;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        return json;
    }

    @UiThread
    static void searchFlick(Context context, String param) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_FLICKER);
        intent.putExtra(PARAM, param);
        context.startService(intent);
    }

    @UiThread
    static void getRecentFlick(Context context) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_RECENT);
        context.startService(intent);
    }

    @UiThread
    static void getPopularFlick(Context context) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_POPULAR);
        context.startService(intent);
    }

    @UiThread
    static void getCommentFlick(Context context, String photo_id) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_COMMENT);
        intent.putExtra(PARAM, photo_id);
        context.startService(intent);
    }

    @UiThread
    static void getFavourities(Context context, String photo_id) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_FAVOURITE);
        intent.putExtra(PARAM, photo_id);
        context.startService(intent);
    }

    @UiThread
    static void downloadImage(Context context, String urlImage) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(PARAM, urlImage);
        context.startService(intent);
    }

    @UiThread
    static void getFlickByAuthor(Context context, String author) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_AUTHOR);
        search = author;
        intent.putExtra(PARAM, search);
        context.startService(intent);
    }

    @WorkerThread
    protected void onHandleIntent(Intent intent) {
        String param = "";
        FlickerAPI flickerAPI = ((FlicliApplication) getApplication()).getFlickerAPI();

        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        JSONArray jPhoto = null;
        JSONArray jComment = null;

        try {
            switch (intent.getAction()) {
                case ACTION_FLICKER:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    param = (String) intent.getSerializableExtra(PARAM);

                    jPhoto = makeRequest(flickerAPI.photos_search(param)).getJSONObject("photos").getJSONArray("photo");
                    _generateFlickers(jPhoto);

                    break;

                case ACTION_RECENT:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    jPhoto = makeRequest(flickerAPI.photos_getRecent()).getJSONObject("photos").getJSONArray("photo");
                    //_generateFlickers(jPhoto);
                    _generateFlickers(jPhoto);
                    break;

                case ACTION_POPULAR:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    jPhoto = makeRequest(flickerAPI.photos_getPopular()).getJSONObject("photos").getJSONArray("photo");
                    _generateFlickers(jPhoto);

                    break;

                case ACTION_COMMENT:
                    param = (String) intent.getSerializableExtra(PARAM);

                    jComment = makeRequest(flickerAPI.photos_getComments(param)).getJSONObject("comments").getJSONArray("comment");
                    _generateComments(jComment, param);

                    break;

                case ACTION_FAVOURITE:
                    param = (String) intent.getSerializableExtra(PARAM);

                    jComment = makeRequest(flickerAPI.photo_getFav(param)).getJSONObject("photo").getJSONArray("person");
                    _setFavourities(jComment, param);

                    break;

                case ACTION_DOWNLOAD:
                    param = (String) intent.getSerializableExtra(PARAM);
                    _downloadHighDefinitionImage(param);

                    break;

                case ACTION_AUTHOR:
                    mvc.model.freeFlickers();

                    String author = (String) intent.getSerializableExtra(PARAM);
                    jPhoto = makeRequest(flickerAPI.photo_getAuthor(author)).getJSONObject("photos").getJSONArray("photo");
                    _generateFlickers(jPhoto);

                    break;
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void _generateFlickers(JSONArray elements) throws JSONException, IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        for (int i = 0; i < elements.length(); i++) {
            JSONObject photo = elements.getJSONObject(i);
            Iterator<String> keys= photo.keys();

            // Generate new object based on first param, that is id of flickr
            FlickModel flick = new FlickModel(photo.getString(keys.next()));

            while (keys.hasNext()) {
                String keyValue = keys.next();

                try {
                    flick.reflectJson(keyValue, photo.getString(keyValue));
                } catch (Exception e) {}
            }

            flick.setBitmap_url_s(BitmapFactory.decodeStream(new URL(flick.getUrl_sq()).openStream()));

            mvc.model.storeFactorization(flick);
        }
    }

    private void _generateComments(JSONArray elements, String photo_id) throws JSONException, IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        ArrayList<Comment> comments = new ArrayList<Comment>();

        for (int i = 0; i < elements.length(); i++) {
            JSONObject photo = elements.getJSONObject(i);

            Comment comment = new Comment(photo.getString("id"));
            comment.setAuthor(photo.getString("author"));
            comment.setAuthor_is_deleted(photo.getString("author_is_deleted"));
            comment.setAuthorname(photo.getString("authorname"));
            comment.setIconserver(photo.getString("iconserver"));
            comment.setIconfarm(photo.getString("iconfarm"));
            comment.setDatecreate(photo.getString("datecreate"));
            comment.setPermalink(photo.getString("permalink"));
            comment.setPath_alias(photo.getString("path_alias"));
            comment.setRealname(photo.getString("realname"));
            comment.set_content(photo.getString("_content"));

            comments.add(comment);
        }

        mvc.model.storeComments(comments, photo_id);
    }

    private void _setFavourities(JSONArray elements, String photo_id) throws JSONException, IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        mvc.model.setFavourities(photo_id, elements.length());
    }

    private void _downloadHighDefinitionImage(String image) throws IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();
        image = image.replace("_z", "_h");
        Bitmap bitmap_z = null;
        bitmap_z = BitmapFactory.decodeStream((new URL(image)).openStream());

        mvc.model.setBitMap_h(mvc.model.getDetailFlicker().getId(), bitmap_z);
    }
}