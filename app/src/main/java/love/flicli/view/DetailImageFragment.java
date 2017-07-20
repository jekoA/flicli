package love.flicli.view;
import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import love.flicli.FlicliApplication;
import love.flicli.MVC;
import love.flicli.R;
import love.flicli.Util;
import love.flicli.model.Comment;
import love.flicli.model.FlickModel;
import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by tommaso on 03/06/17.
 */

public class DetailImageFragment extends Fragment implements AbstractFragment {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Menu menu;
    private MVC mvc;
    private final static String TAG = DetailImageFragment.class.getName();
    ImageView imageView = null;
    TextView viewTextView = null;
    TextView commentTextView = null;
    TextView favTextView = null;
    ProgressBar progress = null;
    ListView list = null;
    private File tempFile = null;
    private FlickModel flickModel;
    private int pos = 0;

    @Override @UiThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(getActivity());
        setHasOptionsMenu(true);
    }

    @Override @UiThread
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_image_fragment, container, false);
        imageView = (ImageView) view.findViewById(R.id.imageContent);
        viewTextView = (TextView) view.findViewById(R.id.views);
        commentTextView = (TextView) view.findViewById(R.id.comments);
        favTextView = (TextView) view.findViewById(R.id.favourite);
        progress = (ProgressBar) view.findViewById(R.id.indeterminateBar);
        progress.setVisibility(View.VISIBLE);
        pos = ((MainActivity) getActivity()).position;

        list = (ListView) view.findViewById(R.id.list_item);
        return view;
    }

    @Override @UiThread
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mvc = ((FlicliApplication) getActivity().getApplication()).getMVC();
        flickModel = mvc.model.getFlickers().get(pos);
        onModelChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_menu, menu);
        this.menu = menu;

    }

    @Override @UiThread
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_author) {
            showAuthorLastImage(mvc.model.getFlickers().get(pos).getOwner());
        } else if (item.getItemId() == R.id.version) {
            mvc.controller.showVersion();
        } else if (item.getItemId() == R.id.menu_item_share) {
            if (mvc.model.getFlickers().get(pos).getBitmap_url_hd() != null)
                actionShare();
            else {
                Toast toast = Toast.makeText(getActivity().getApplication(), "Immage non pronta aspettare il caricamento", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Delete the temporary file for share image with other application
        if (tempFile != null)
            tempFile.delete();
    }

    @Override @UiThread
    public void onModelChanged() {

        //Image
        if (flickModel.getBitmap_url_hd() != null)
            progress.setVisibility(View.INVISIBLE);

        imageView.setImageBitmap(flickModel.getBitmap_url_hd());

        viewTextView.setText(flickModel.getViews());

        commentTextView.setText(String.valueOf(flickModel.getComments().size()));
        favTextView.setText(flickModel.getFavourities());
        
        //Comments
        list.setAdapter(new CommentAdapter());

    }

    @UiThread
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private class CommentAdapter extends ArrayAdapter<Comment> {
        private final ArrayList<Comment> messages = mvc.model.getFlickers().get(pos).getComments();

        private CommentAdapter() {
            super(getActivity(), R.layout.list_adapter, mvc.model.getFlickers().get(pos).getComments());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_adapter, parent, false);
            }

            if (messages != null) {
                Comment message = messages.get(position);
                String date = "";
                String text = "";
                String author = "";
                date = getCalendarDate(Long.parseLong(message.getDatecreate()));
                author = message.getAuthorname();
                text = message.get_content();

                ((TextView) row.findViewById(R.id.author)).setText(author);

                //create date test
                String util = "\t";
                util = util.concat(date);

                ((TextView) row.findViewById(R.id.date)).setText(util);
                ((TextView) row.findViewById(R.id.message)).setText(text);
            }
            return row;
        }
    }

    @UiThread
    private String getCalendarDate(Long date) {
        java.util.Date d = new java.util.Date(date*1000L);
        String itemDateStr = new SimpleDateFormat("dd-MMM HH:mm").format(d);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        long time= System.currentTimeMillis();

        Calendar currentDate = Calendar.getInstance();
        d = new java.util.Date(time);
        currentDate.setTime(d);

        if (date == 0)
            return "";
        else if (cal.get(Calendar.YEAR) < currentDate.get(Calendar.YEAR)) {
            //ANNO DEL COMMENTO < DELL'ANNO CORRENTE

            String diff = currentDate.get(Calendar.YEAR) - cal.get(Calendar.YEAR) + "";
            if ((currentDate.get(Calendar.YEAR) - cal.get(Calendar.YEAR)) > 1)
                diff = diff.concat(" years ago");
            else
                diff = diff.concat(" year ago");
            return diff;
        } else if(cal.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
            //STESSO ANNO DELL'ANNO CORRENTE E STESSO MESE

            if (currentDate.get(Calendar.DAY_OF_MONTH) > cal.get(Calendar.DAY_OF_MONTH)){
                // SE IL MESE CORRENTE E' UGUALE CONFRONTO I GIORNI, SE IL GIORNO CORRENTE E MAGGIORE DI QUELLO
                //DEL COMMENTO ALLORA RITORNO LA DIFFERENZA DEI GIORNI

                int diff = currentDate.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH);
                String difference = diff + "";

                if (diff == 1)
                    difference = difference.concat(" day ago");
                else
                    difference = difference.concat(" days ago");

                return difference;
            } else {
                //STESSO GIORNO DEL GIORNO CORRENTE

                //CONFRONTO L'ORA DI CARICAMENTO DEL COMMENTO
                if (currentDate.get(Calendar.HOUR_OF_DAY) > cal.get(Calendar.HOUR_OF_DAY)) {
                    int diff = currentDate.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY);
                    String difference = diff + "";

                    if (diff == 1)
                        difference = difference.concat(" hour ago");
                    else
                        difference = difference.concat(" hours ago");

                    return difference;
                } else {
                    //STESSA ORA RITORNO LA DIFFERENZA DEI MINUTI
                    int diff = currentDate.get(Calendar.MINUTE) - cal.get(Calendar.MINUTE);
                    String difference = diff + "";
                    difference = difference.concat(" minutes ago");
                    return difference;
                }
            }
        } else {
            //SE I MESI SONO DIVERSI, RITORNO LA DIFFERENZA DI MESI
            int diff = currentDate.get(Calendar.MONTH) - cal.get(Calendar.MONTH);
            String difference = diff + "";
            if (diff > 1)
                difference = difference.concat(" months ago");
            else
                difference = difference.concat(" month ago");
            return difference;
        }
    }

    public void actionShare() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        tempFile = Util.getImageUri(getActivity().getApplication(), mvc.model.getFlickers().get(pos).getBitmap_url_hd());
        Uri r = getUriForFile(getActivity().getApplication(), "love.flicli.fileprovider", tempFile);
        intent.putExtra(Intent.EXTRA_STREAM, r);
        startActivity(Intent.createChooser(intent, "..."));
    }

    @UiThread
    public void showAuthorLastImage(String author) {
        mvc.controller.lastAuthorImage(getActivity(), author);
    }
}
