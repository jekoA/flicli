package love.flicli.model;
import android.graphics.Bitmap;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.json.JSONArray;
import org.w3c.dom.Comment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import love.flicli.MVC;
import love.flicli.view.View;

import static android.os.Build.VERSION_CODES.M;
import static love.flicli.R.id.comments;


@ThreadSafe
public class Model {
    public final static String FORMAT_DATE = "yyyy/MM/dd";

    private MVC mvc;

    @GuardedBy("Itself")
    private final ArrayList<FlickModel> flickers = new ArrayList<>();

    @GuardedBy("Itself")
    private AuthorModel author = null;

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    public synchronized void setAuthorModel(AuthorModel author) {
        this.author = author;
        mvc.forEachView(View::onModelChanged);
    }

    public synchronized void freeAuthorModel() {
        this.author = null;
    }

    public synchronized AuthorModel getAuthorModel() {
            return this.author;
    }

    public void storeFlick(FlickModel flick) {
        synchronized (flickers) {
            for (FlickModel currentFlick : this.flickers) {
                if (currentFlick.getId() == flick.getId()) {
                    return;
                }
            }

            this.flickers.add(flick);
        }

        mvc.forEachView(View::onModelChanged);
    }

    public void storeDetail(String photo_id, int favs, ArrayList<CommentModel> comments, Bitmap bitmap_z ) {
        synchronized (flickers) {
            for (FlickModel flick : mvc.model.getFlickers()) {
                if (flick.getId().compareTo(photo_id) == 0) {
                    flick.setComments(comments);
                    flick.setFavourities(String.valueOf(favs));
                    flick.setBitmap_url_h(bitmap_z);
                }
            }
        }

        mvc.forEachView(View::onModelChanged);
    }

    public ArrayList<FlickModel> getFlickers() {
        synchronized (flickers) {
            return this.flickers;
        }
    }

    public void freeFlickers() {
        synchronized (flickers) {
            this.flickers.clear();
        }
    }
}
