package love.flicli.view;

import android.support.annotation.UiThread;

import love.flicli.model.FlickModel;

/**
 * Created by tommaso on 18/05/17.
 */

public interface View{

    @UiThread
    void showHistory();

    @UiThread
    void onModelChanged();

    @UiThread
    void showDetail();

    @UiThread
    void showAuthor();

    @UiThread
    void showLastImageAuthor();
}
