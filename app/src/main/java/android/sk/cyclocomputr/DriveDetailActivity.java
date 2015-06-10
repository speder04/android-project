package android.sk.cyclocomputr;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.sk.cyclocomputr.contentprovider.MyCycloComputrContentProvider;
import android.sk.cyclocomputr.database.DrivingListTable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class DriveDetailActivity extends ActionBarActivity {

    private TextView distanceTextView;
    private TextView distanceUnitTextView;
    private TextView timeTextView;
    private TextView maxSpeedTextView;
    private TextView avgSpeedTextView;
    private TextView maxSpeedUnitTextView;
    private TextView avgSpeedUnitTextView;

    private String speedUnit;
    private String distanceUnit;
    private double speedConstant;
    private double distanceConstant;

    private long time;
    private double maxSpeed;
    private double distance;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_detail);

        distanceTextView = (TextView) findViewById(R.id.distanceDetailTextView);
        distanceUnitTextView = (TextView) findViewById(R.id.distanceUnitDetailTextView);
        timeTextView = (TextView) findViewById(R.id.timeDetailTextView);
        maxSpeedTextView = (TextView) findViewById(R.id.maxSpeedDetailTextView);
        avgSpeedTextView = (TextView) findViewById(R.id.avgSpeedDetailTextView);
        maxSpeedUnitTextView = (TextView) findViewById(R.id.maxSpeedUnitDetailTextView);
        avgSpeedUnitTextView = (TextView) findViewById(R.id.avgSpeedUnitDetailTextView);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String speedUnit = settings.getString("speedUnit", "1");
        String distanceUnit = settings.getString("distanceUnit", "1");

        this.speedUnit = speedUnit;
        this.distanceUnit = distanceUnit;

        switch (speedUnit){
            case "1":
                maxSpeedUnitTextView.setText("m/s");
                avgSpeedUnitTextView.setText("m/s");
                speedConstant = 1;
                break;
            case "2":
                maxSpeedUnitTextView.setText("km/h");
                avgSpeedUnitTextView.setText("km/h");
                speedConstant = 3.6;
                break;
            case "3":
                maxSpeedUnitTextView.setText("mi/h");
                avgSpeedUnitTextView.setText("mi/h");
                speedConstant = 2.23693629;
                break;
        }

        switch (distanceUnit) {
            case "1":
                distanceUnitTextView.setText("km");
                distanceConstant = 1;
                break;
            case "2":
                distanceUnitTextView.setText("mi");
                distanceConstant = 0.62137;
                break;
        }

        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
        uri = (savedInstanceState == null) ? null : (Uri) savedInstanceState
                .getParcelable(MyCycloComputrContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            uri = extras
                    .getParcelable(MyCycloComputrContentProvider.CONTENT_ITEM_TYPE);

            fillData(uri);
        } else {
            if (uri != null) {
                fillData(uri);
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "uri je null", Toast.LENGTH_SHORT);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drive_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_graph) {
            Intent i = new Intent(this, GraphActivity.class);

            String idRow = uri.getLastPathSegment();
            Uri driveUri = Uri.parse(MyCycloComputrContentProvider.CONTENT_URI_POINT + "/" + idRow);
            i.putExtra(MyCycloComputrContentProvider.CONTENT_ITEM_TYPE2, driveUri);

            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void fillData(Uri uri) {
        String[] projection = {
                DrivingListTable.COLUMN_DATE,
                DrivingListTable.COLUMN_TIME,
                DrivingListTable.COLUMN_DISTANCE,
                DrivingListTable.COLUMN_MAX_SPEED,
        };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DrivingListTable.COLUMN_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DrivingListTable.COLUMN_TIME));
            double distance = cursor.getDouble(cursor.getColumnIndexOrThrow(DrivingListTable.COLUMN_DISTANCE));
            double maxSpeed = cursor.getDouble(cursor.getColumnIndexOrThrow(DrivingListTable.COLUMN_MAX_SPEED));

            String s;
            DecimalFormat df = new DecimalFormat("#.##");

            //Toast.makeText(this, time, Toast.LENGTH_SHORT).show();
            this.time = Long.parseLong(time);
            s = df.format((distance / 1000) * distanceConstant);
            distanceTextView.setText(s);

            df = new DecimalFormat("#.#");
            s = df.format(maxSpeed * speedConstant);
            maxSpeedTextView.setText(s);

            s = df.format((distance/this.time)*speedConstant);
            avgSpeedTextView.setText(s);

            setTime();
        }

        cursor.close();
    }

    public void setTime() {
        long secs = time;
        long mins = time/60;
        long hrs = (time/60)/60;

        secs = secs % 60;
        String seconds = String.valueOf(secs);
        if (secs == 0) {
            seconds = "00";
        }
        if (secs < 10 && secs > 0) {
            seconds = "0" + seconds;
        }

        mins = mins % 60;
        String minutes = String.valueOf(mins);
        if (mins == 0) {
            minutes = "00";
        }
        if (mins < 10 && mins > 0) {
            minutes = "0" + minutes;
        }

        String hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }

        timeTextView.setText(hours + ":" + minutes + ":" + seconds);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MyCycloComputrContentProvider.CONTENT_ITEM_TYPE, uri);
    }
}
