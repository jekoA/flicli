package love.flicli.model;

import android.graphics.Bitmap;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Objects;

import static android.R.attr.mode;
import static android.R.attr.name;
import static android.R.attr.syncable;
import static android.R.attr.thickness;
import static android.R.attr.value;

/**
 * Created by tommaso on 19/05/17.
 */

/*
 * This class represent every single photo.
 * When we make a request and store the results, we managed the photo in this format.
 */

@ThreadSafe
public class FlickModel {
    // Default attribues
    private String id;
    private String owner;
    private String secret;
    private String server;
    private String title;
    private int farm;
    private int ispublic;
    private int isfriend;
    private int isfamily;

    // Optional attributes
    private String description;
    private String license;
    private String date_upload;
    private String date_taken;
    private String owner_name;
    private String icon_server;
    private String original_format;
    private String last_update;
    private String geo;
    private String tags;
    private String machine_tags;
    private String o_dims;
    private String views;
    private String media;
    private String path_alias;
    private String url_sq;
    private String url_t;
    private String url_s;
    private String url_q;
    private String url_m;
    private String url_n;
    private String url_z;
    private String url_c;
    private String url_l;
    private String url_o;
    private String favourities;

    //sincronizzati
    private Bitmap bitmap_url_s;
    private Bitmap bitmap_url_h;
    private ArrayList<CommentModel> comments;

    public FlickModel(String id){
        this.id = id;
        owner = "";
        secret = "";
        server = "";
        title = "";
        description = "";
        license = "";
        date_upload = "";
        date_taken = "";
        owner_name = "";
        icon_server = "";
        original_format = "";
        last_update = "";
        geo = "";
        tags = "";
        machine_tags = "";
        o_dims = "";
        views = "";
        media = "";
        path_alias = "";
        url_sq = "";
        url_t = "";
        url_s = "";
        url_q = "";
        url_m = "";
        url_n = "";
        url_z = "";
        url_c = "";
        url_l = "";
        url_o = "";
        favourities = "0";
        farm = 0;
        ispublic = 0;
        isfriend = 0;
        isfamily = 0;
        bitmap_url_s = null;
        bitmap_url_h = null;
        comments = new ArrayList<CommentModel>();
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public String getTitle() {
        return title;
    }

    public int getFarm() {
        return farm;
    }

    public int getIspublic() {
        return ispublic;
    }

    public int getIsfriend() {
        return isfriend;
    }

    public int getIsfamily() {
        return isfamily;
    }

    public String getDescription() {
        return description;
    }

    public String getLicense() {
        return license;
    }

    public String getDate_upload() {
        return date_upload;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public String getIcon_server() {
        return icon_server;
    }

    public String getOriginal_format() {
        return original_format;
    }

    public String getLast_update() {
        return last_update;
    }

    public String getGeo() {
        return geo;
    }

    public String getTags() {
        return tags;
    }

    public String getMachine_tags() {
        return machine_tags;
    }

    public String getO_dims() {
        return o_dims;
    }

    public String getViews() {
        return views;
    }

    public String getMedia() {
        return media;
    }

    public String getUrl_q() {
        return url_q;
    }

    public String getUrl_sq() {
        return url_sq;
    }

    public String getUrl_m() {
        return url_m;
    }

    public String getUrl_n() {
        return url_n;
    }

    public String getUrl_z() {
        return url_z;
    }

    public String getUrl_c() {
        return url_c;
    }

    public String getUrl_l() {
        return url_l;
    }

    public String getUrl_o() {
        return url_o;
    }

    public Bitmap getBitmap_url_s() {
        return this.bitmap_url_s;
    }

    public String getFavourities() { return this.favourities; }

    public void freeComment() {
        this.comments = null;
    }

    public void freeBitMapHD() {
        this.bitmap_url_h = null;
    }

    public ArrayList<CommentModel> getComments() {
            return this.comments;
    }

    public  void setComments(ArrayList<CommentModel> comments) {
        this.comments = comments;
    }

        //CONTROLlARE NON SONO SICURO
    public void setBitmap_url_h(Bitmap image) {
            this.bitmap_url_h = image;
    }

    public void setBitmap_url_s(Bitmap image) {
        this.bitmap_url_s = image;
    }

    public  Bitmap getBitmap_url_hd() {
            return this.bitmap_url_h;
    }

    private String _setAttribute(String param) {
        return (param != null) ? param : "";
    }

    public void reflectJson(String name, String object) throws NoSuchFieldException, IllegalAccessException, JSONException {
        Field field = this.getClass().getDeclaredField(name);
        field.set(this, object);
    }

    public void setFavourities(String param) {
       this.favourities = param;
    }
}