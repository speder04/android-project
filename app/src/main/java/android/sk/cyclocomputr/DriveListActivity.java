package android.sk.cyclocomputr;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.sk.cyclocomputr.contentprovider.MyCycloComputrContentProvider;
import android.sk.cyclocomputr.database.DrivingListTable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class DriveListActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;

    private ListView listView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_list);
        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.emptyTextView);
        textView.setVisibility(TextView.INVISIBLE);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            private long id;

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                this.id =id;
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setMessage("Delete this record?").setTitle("Delete");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRow();
                        filldata();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }

            private void deleteRow() {
                getContentResolver().delete(MyCycloComputrContentProvider.CONTENT_URI_DRIVING_LIST, "_id" + "='" + id + "'", null);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(parent.getContext(), DriveDetailActivity.class);
                Uri uri = Uri.parse(MyCycloComputrContentProvider.CONTENT_URI_DRIVING_LIST + "/" + id);
                i.putExtra(MyCycloComputrContentProvider.CONTENT_ITEM_TYPE, uri);

                startActivity(i);
            }
        });

        filldata();
        //registerForContextMenu(getListView());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {DrivingListTable.COLUMN_ID, DrivingListTable.COLUMN_DATE};
        CursorLoader cursorLoader = new CursorLoader(this, MyCycloComputrContentProvider.CONTENT_URI_DRIVING_LIST, projection, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void filldata() {
        String[] from = new String[] {DrivingListTable.COLUMN_DATE};
        int[] to = new int[] {R.id.label};

        getSupportLoaderManager().initLoader(0, null, this);

        adapter = new SimpleCursorAdapter(this, R.layout.drive_row, null, from, to, 0);

        listView.setAdapter(adapter);
    }
}
