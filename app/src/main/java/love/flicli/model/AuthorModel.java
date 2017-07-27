package love.flicli.model;

import net.jcip.annotations.Immutable;

/**
 * Created by jeko on 25/07/17.
 */

/*{ "person": ***{
    "id": "149382168@N02",
    "nsid": "149382168@N02",
     "ispro": 0,
     "can_buy_pro": 0,
     "iconserver": 0,
     "iconfarm": 0,
     "path_alias": "",
     "has_stats": 0,
    "username": { "_content": "andreacolato" },
    "realname": { "_content": "Andrea Colato" },
    "mbox_sha1sum": { "_content": "facbb07187230c4b9dd9351ab5a915d15b1366e4" },
    "location": { "_content": "" },
    "description": { "_content": "" },
    "photosurl": { "_content": "https:\/\/www.flickr.com\/photos\/149382168@N02\/" },
    "profileurl": { "_content": "https:\/\/www.flickr.com\/people\/149382168@N02\/" },
    "mobileurl": { "_content": "https:\/\/m.flickr.com\/photostream.gne?id=149361820" },
    "photos": {
        "firstdatetaken": { "_content": "" },
        "firstdate": { "_content": "" },
        "count": { "_content": 0 },
        "views": { "_content": 0 }
    }
}***, "stat": "ok" }*/

@Immutable
public class AuthorModel {
    private final String id;
    private final String nsid;
    private final String ispro;
    private final String can_buy_pro;
    private final String iconserver;
    private final String iconfarm;
    private final String path_alias;
    private final String has_stats;

    private final String username;
    private final String realname;
    private final String mbox_sha1sum;
    private final String location;
    private final String description;
    private final String photosurl;
    private final String profileurl;
    private final String mobileurl;
    private final String photos_firstdatetaken;
    private final String photos_firstdate;
    private final String photos_count;
    private final String views;

    private final String buddyIcon;

    public AuthorModel(String id, String nsid, String ispro, String can_buy_pro, String iconserver, String iconfarm, String path_alias, String has_stats, String username, String realname, String mbox_sha1sum, String location, String description, String photosurl, String profileurl, String mobileurl, String photos_firstdatetaken, String photos_firstdate, String photos_count, String views, String buddyIcon) {
        this.id = id;
        this.nsid = nsid;
        this.ispro = ispro;
        this.can_buy_pro = can_buy_pro;
        this.iconserver = iconserver;
        this.iconfarm = iconfarm;
        this.path_alias = path_alias;
        this.has_stats = has_stats;
        this.username = username;
        this.realname = realname;
        this.mbox_sha1sum = mbox_sha1sum;
        this.location = location;
        this.description = description;
        this.photosurl = photosurl;
        this.profileurl = profileurl;
        this.mobileurl = mobileurl;
        this.photos_firstdatetaken = photos_firstdatetaken;
        this.photos_firstdate = photos_firstdate;
        this.photos_count = photos_count;
        this.views = views;
        this.buddyIcon = buddyIcon;
    }

    public String getId() {
        return id;
    }

    public String getNsid() {
        return nsid;
    }

    public String getIspro() {
        return ispro;
    }

    public String getCan_buy_pro() {
        return can_buy_pro;
    }

    public String getIconserver() {
        return iconserver;
    }

    public String getIconfarm() {
        return iconfarm;
    }

    public String getPath_alias() {
        return path_alias;
    }

    public String getHas_stats() {
        return has_stats;
    }

    public String getUsername() {
        return username;
    }

    public String getRealname() {
        return realname;
    }

    public String getMbox_sha1sum() {
        return mbox_sha1sum;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotosurl() {
        return photosurl;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public String getMobileurl() {
        return mobileurl;
    }

    public String getPhotos_firstdatetaken() {
        return photos_firstdatetaken;
    }

    public String getPhotos_firstdate() {
        return photos_firstdate;
    }

    public String getPhotos_count() {
        return photos_count;
    }

    public String getViews() {
        return views;
    }
    public String getBuddyIcon () {
        return buddyIcon;
    }

}