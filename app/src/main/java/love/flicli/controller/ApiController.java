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
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import love.flicli.FlickerAPI;
import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.model.Comment;
import love.flicli.model.FlickModel;

import static android.R.attr.icon;
import static android.R.attr.id;
import static love.flicli.R.drawable.comment;

/**
 * Created by tommaso on 09/05/17.
 */

public class ApiController extends IntentService {
    private final static String TAG = ApiController.class.getName();
    private final static String ACTION_FLICKER = "searchFlick";
    private final static String ACTION_RECENT = "getRecentFlick";
    private final static String ACTION_POPULAR = "getPopularFlick";
    private final static String ACTION_DETAIL = "getDetailFlick";
    private final static String ACTION_AUTHOR = "getFlickByAuthor";

    private final static String PARAM_ID = "paramId";
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
        intent.putExtra(PARAM_ID, param);
        context.startService(intent);
    }

    @UiThread
    static void getRecentFlick(Context context) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_RECENT);
        context.startService(intent);
    }

    static void getPopularFlick(Context context) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_POPULAR);
        context.startService(intent);
    }


    @UiThread
    static void getDetailFlick(Context context, int pos) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_DETAIL);
        intent.putExtra(PARAM_ID, pos);
        context.startService(intent);
    }

    @UiThread
    static void getFlickByAuthor(Context context, String author) {
        Intent intent = new Intent(context, ApiController.class);
        intent.setAction(ACTION_AUTHOR);
        search = author;
        intent.putExtra(PARAM_ID, search);
        context.startService(intent);
    }

    @WorkerThread
    protected void onHandleIntent(Intent intent) {
        String param = "";
        FlickerAPI flickerAPI = ((FlicliApplication) getApplication()).getFlickerAPI();

        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        JSONArray jPhoto = null;

        try {
            switch (intent.getAction()) {
                case ACTION_FLICKER:
                    // Empty list of Flickers
                    mvc.model.freeFlickers();

                    param = (String) intent.getSerializableExtra(PARAM_ID);

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

                case ACTION_DETAIL:

                    int pos = (int) intent.getSerializableExtra(PARAM_ID);
                    FlickModel flick = mvc.model.getFlickers().get(pos);

                    JSONObject jComment = makeRequest(flickerAPI.photos_getComments(flick.getId())).getJSONObject("comments");
                    JSONArray jFavourities = makeRequest(flickerAPI.photo_getFav(flick.getId())).getJSONObject("photo").getJSONArray("person");

                    _generateFlickDetail(flick, jComment, jFavourities);

                    break;
                case ACTION_AUTHOR:
                    mvc.model.freeFlickers();

                    String author = (String) intent.getSerializableExtra(PARAM_ID);
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

    private void _generateFlickDetail(FlickModel flick, JSONObject jComment, JSONArray jFavourities) throws IOException {
        MVC mvc = ((FlicliApplication) getApplication()).getMVC();

        // Comments
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String id = "";
        String author = "";
        String author_is_deleted = "";
        String authorname = "";
        String iconserver = "";
        String iconfarm = "";
        String datecreate = "";
        String permalink = "";
        String path_alias = "";
        String realname = "";
        String _content = "";

        try {
            for (int i = 0; i < jComment.length(); i++) {
                JSONObject jsonComments = jComment.getJSONArray("comment").getJSONObject(i);

                id = (jsonComments.isNull("id")) ? "" : jsonComments.getString("id");
                author = (jsonComments.isNull("author")) ? "" : jsonComments.getString("author");
                authorname = (jsonComments.isNull("authorname")) ? "" : jsonComments.getString("authorname");
                author_is_deleted = (jsonComments.isNull("author_is_deleted")) ? "" : jsonComments.getString("author_is_deleted");
                iconserver = (jsonComments.isNull("iconserver")) ? "" : jsonComments.getString("iconserver");
                iconfarm = (jsonComments.isNull("iconfarm")) ? "" : jsonComments.getString("iconfarm");
                datecreate = (jsonComments.isNull("datecreate")) ? "" : jsonComments.getString("datecreate");
                permalink = (jsonComments.isNull("permalink")) ? "" : jsonComments.getString("permalink");
                path_alias = (jsonComments.isNull("pathalias")) ? "" : jsonComments.getString("pathalias");
                realname = (jsonComments.isNull("realname")) ? "" : jsonComments.getString("realname");
                _content = (jsonComments.isNull("_content")) ? "" : jsonComments.getString("_content");

                Comment comment = new Comment(id, author, author_is_deleted, authorname, iconserver, iconfarm, datecreate, permalink, path_alias, realname, _content);
                comments.add(comment);
            }
        } catch (Exception e) {}

        // Photos
        String image = flick.getUrl_z().replace("_z", "_h");
        Bitmap bitmap_z = BitmapFactory.decodeStream((new URL(image)).openStream());

        mvc.model.storeDetail(flick.getId(), jFavourities.length(), comments, bitmap_z);
    }
}