package love.flicli.controller;

import android.content.Context;
import android.support.annotation.UiThread;

import love.flicli.MVC;
import love.flicli.model.Comment;
import love.flicli.model.FlickModel;
import love.flicli.view.View;

/**
 * Created by tommaso on 18/05/17.
 */

public class Controller {
    private final static String TAG = Controller.class.getName();
    private MVC mvc;

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    @UiThread
    public void flicker(Context context, String n) {
        //passiamo all servizio la stringa della ricerca più il contensto
        ApiController.searchFlick(context, n);
        showHistory();
    }

    public void getImageDetail(Context context, FlickModel flickModel) {
        mvc.model.storeDetailFlicker(flickModel);
        comment(context, flickModel.getId());
        downloadImage(context, flickModel.getUrl_z());
        favourite(context, flickModel.getId());
        showImage();
    }

   @UiThread
    public void recent(Context context) {
        //passiamo al servizio il contensto
        ApiController.getRecentFlick(context);
        showHistory();
    }

    @UiThread
    public void popular(Context context) {
        //passiamo al servizio il contensto
        ApiController.getPopularFlick(context);
        showHistory();
    }

    @UiThread
    public void comment(Context context, String image) {
        ApiController.getCommentFlick(context, image);
    }

    @UiThread
    public void favourite(Context context, String image) {
        ApiController.getFavourities(context, image);
    }

    public void downloadImage(Context context, String image) {
        ApiController.downloadImage(context, image);
    }

    /*
    public void lastAuthorImage(Context context, String author) {
        ApiController.getFlickByAuthor(context, author);
        showHistory();
    }

*/

    @UiThread
    public void showImage() { mvc.forEachView(View::showImage); }

    @UiThread
    public void showHistory() {
        mvc.forEachView(View::showHistory);
    }
}
