package android.sk.cyclocomputr;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.sk.cyclocomputr.contentprovider.MyCycloComputrContentProvider;
import android.sk.cyclocomputr.database.DrivingListTable;
import android.sk.cyclocomputr.database.PointsTable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar.Tab;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener{

    // swipe + tabs: http://www.feelzdroid.com/2014/10/android-action-bar-tabs-swipe-views.html

    private static final String PREFS_NAME = "CycloComputrPrefs";

    private ViewPager tabsViewPager;
    private TabsAdapter mTabsAdapter;

    private LocationManager locationManager1;
    private LocationManager locationManager2;
    private LocationManager locationManager3;
    private LocationListener locationListener;
    private LocationListener distanceListener;
    private LocationListener pointListener;

    private double maxSpeed = 0;
    private double distance = 0;
    private String speedUnit;
    private double speedConstant = 1;
    private String distanceUnit;
    private double distanceConstant = 1;
    private long secondsOfDrive = 0;
    private double totalDistance = 0;
    private long totalTime;
    private boolean nightMode;

    private Location lastLocation = null;

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private boolean isSaved;
    private final int REFRESH_RATE = 1000;

    private Uri uri;
    private long currentDriveId = -1;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPrefSett();
        if (nightMode) {
            setTheme(R.style.NightTheme);
        } else {
            setTheme(R.style.DayTheme);
        }
        setContentView(R.layout.activity_main);

        mPlanetTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    Intent i = new Intent(parent.getContext(), SetingsActivity.class);
                    startActivity(i);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }

                if (position == 0) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                }

                if (position == 1) {
                    Intent i = new Intent(parent.getContext(), DriveListActivity.class);
                    startActivity(i);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
            }
        });

        // disable screen timeout
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // taby a swipe view
        tabsViewPager = (ViewPager) findViewById(R.id.tabs_pager);
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
        tabsViewPager.setAdapter(mTabsAdapter);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab tachoTab = getSupportActionBar().newTab().setText("Tachometer").setTabListener(this);
        Tab speedoTab = getSupportActionBar().newTab().setText("Speedmeter").setTabListener(this);

        getSupportActionBar().addTab(tachoTab);
        getSupportActionBar().addTab(speedoTab);

        tabsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        locationManager1 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager2 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager3 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // listener pre rychlost
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.hasSpeed()) {
                    double speed = location.getSpeed();
                    maxSpeedChange(speed);
                    speed *= speedConstant;
                    DecimalFormat df = new DecimalFormat("#.#");
                    String s = String.valueOf(df.format(speed));
                    mTabsAdapter.setSpeedText(s);
                }
                if (location.hasAccuracy()) {
                    double accuracy = location.getAccuracy();
                    DecimalFormat df = new DecimalFormat("#");
                    String s = df.format(accuracy) + " m";
                    mTabsAdapter.setAccuracyText(s);
                } else {
                    mTabsAdapter.setAccuracyText("unavailable");
                }
                if (location.hasBearing()) {
                    double bearing = location.getBearing();
                    DecimalFormat df = new DecimalFormat("#");
                    String s = df.format(bearing);
                    mTabsAdapter.setCompasText(s);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            public void maxSpeedChange(double speed) {
                if (maxSpeed < speed) {
                    maxSpeed = speed;
                    speed *= speedConstant;
                    DecimalFormat df = new DecimalFormat("#.#");
                    String s = String.valueOf(df.format(speed));
                    mTabsAdapter.setMaxSpeedText(s);
                }
            }
        };


        // listener pre vzdialenost
        distanceListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.hasAccuracy()) {
                    double acc = location.getAccuracy();
                    if (acc < 50) {
                        if (lastLocation == null) {
                            lastLocation = location;
                        } else {
                            double d = lastLocation.distanceTo(location);
                            if (d > 20) {
                                distance += d;
                                totalDistance += d;
                                lastLocation = location;
                                DecimalFormat df = new DecimalFormat("#.##");
                                String s = String.valueOf(df.format((distance/1000)*distanceConstant));
                                mTabsAdapter.setDistanceText(s);

                                s = String.valueOf(df.format((totalDistance/1000)*distanceConstant));
                                mTabsAdapter.setTotalDistanceText(s);

                                df = new DecimalFormat("#.#");
                                s = String.valueOf(df.format((distance/secondsOfDrive)*speedConstant));
                                mTabsAdapter.setAvgSpeedText(s);
                            }
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        pointListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.hasAccuracy()) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    double alt = -1;
                    if (location.hasAltitude()) {
                        alt = location.getAltitude();
                    }
                    addPoint(lat, lon, alt);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        createNewDrive();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        tabsViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        startTime = System.currentTimeMillis();
        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 500);
        mHandler.removeCallbacks(setDistance);
        mHandler.postDelayed(setDistance, 200);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String speedUnit = settings.getString("speedUnit", "1");
        this.speedUnit = speedUnit;

        final String distanceUnit = settings.getString("distanceUnit", "1");
        this.distanceUnit = distanceUnit;

        boolean nightMode = settings.getBoolean("nightmode", false);
        if (nightMode != this.nightMode) {
            Intent i = new Intent(this, RestartActivity.class);
            startActivity(i);
            finish();
        }

        setConstants();

        mHandler.removeCallbacks(setUnits);
        mHandler.postDelayed(setUnits, 200);
        //Toast.makeText(this, this.speedUnit, Toast.LENGTH_LONG).show();



        locationManager1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15 * 1000, 0, locationListener);
        locationManager2.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15 * 1000, 0, distanceListener);
        locationManager3.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15 * 1000, 0, pointListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        totalTime += secondsOfDrive;
        editor.putLong("totalTime", totalTime);
        editor.putFloat("totalDistance", (float) totalDistance);
        //Toast.makeText(this, String.valueOf(totalTime), Toast.LENGTH_SHORT).show();
        editor.commit();

        updateDrive();

        mHandler.removeCallbacks(startTimer);

        // vypnutie listenerov
        locationManager1.removeUpdates(locationListener);
        locationManager2.removeUpdates(distanceListener);
        locationManager3.removeUpdates(pointListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Runnable startTimer = new Runnable() {
        @Override
        public void run() {
            if (isSaved) {
                elapsedTime += System.currentTimeMillis() - startTime;
            } else {
                elapsedTime = System.currentTimeMillis() - startTime;
            }
            updateTimer(elapsedTime);
            updateTotalTimer((totalTime * 1000) + elapsedTime);
            mHandler.postDelayed(this, REFRESH_RATE);
        }
    };

    private Runnable setUnits = new Runnable() {
        @Override
        public void run() {
            if (mTabsAdapter.getIsCreate()) {
                setUnitsText();
            } else {
                mHandler.postDelayed(this, 200);
            }
        }
    };

    private Runnable setDistance = new Runnable() {
        @Override
        public void run() {
            if (mTabsAdapter.getIsCreate()) {
                DecimalFormat df = new DecimalFormat("#.##");
                String s = String.valueOf(df.format((totalDistance/1000)*distanceConstant));
                mTabsAdapter.setTotalDistanceText(s);
            } else {
                mHandler.postDelayed(this, 200);
            }
        }
    };

    private void updateTimer(float time) {
        secondsOfDrive = (long) (time/1000);
        long secs = (long) (time/1000);
        long mins = (long) ((time/1000)/60);
        long hrs = (long) (((time/1000)/60)/60);

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

        hrs = hrs % 60;
        String hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }

        mTabsAdapter.setTimeText(hours + ":" + minutes + ":" + seconds);
    }

    public void setUnitsText() {
        switch (speedUnit) {
            case "1":
                mTabsAdapter.setSpeedUnitText("m/s");
                mTabsAdapter.setMaxAvgSpeedUnitText("m/s");
                break;
            case "2":
                mTabsAdapter.setSpeedUnitText("km/h");
                mTabsAdapter.setMaxAvgSpeedUnitText("km/h");
                break;
            case "3":
                mTabsAdapter.setSpeedUnitText("mi/h");
                mTabsAdapter.setMaxAvgSpeedUnitText("mi/h");
                break;
        }

        switch (distanceUnit) {
            case "1":
                mTabsAdapter.setDistanceUnitText("km");
                break;
            case "2":
                mTabsAdapter.setDistanceUnitText("mi");
                break;
        }
    }

    public void setConstants() {
        switch (speedUnit) {
            case "1":
                speedConstant = 1;
                break;
            case "2":
                speedConstant = 3.6;
                break;
            case "3":
                speedConstant = 2.23693629;
                break;
        }

        switch (distanceUnit) {
            case "1":
                distanceConstant = 1;
                break;
            case "2":
                distanceConstant = 0.62137;
                break;
        }
    }

    public void createNewDrive() {
        Calendar c = GregorianCalendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy kk:mm");
        date = sdf.format(c.getTime());
        String time = "0";
        double distance = 0;
        double maxSpeed = 0;

        ContentValues values = new ContentValues();
        values.put(DrivingListTable.COLUMN_DATE, date);
        values.put(DrivingListTable.COLUMN_TIME, time);
        values.put(DrivingListTable.COLUMN_DISTANCE, distance);
        values.put(DrivingListTable.COLUMN_MAX_SPEED, maxSpeed);

        uri = getContentResolver().insert(MyCycloComputrContentProvider.CONTENT_URI_DRIVING_LIST, values);

        //Toast.makeText(this, uri.getLastPathSegment(), Toast.LENGTH_LONG).show();

        currentDriveId = Long.parseLong(uri.getLastPathSegment());
    }

    public void addPoint(double lat, double lon, double alt) {
        ContentValues values = new ContentValues();
        values.put(PointsTable.COLUMN_ID_DRIVE, currentDriveId);
        values.put(PointsTable.COLUMN_LATITUDE, lat);
        values.put(PointsTable.COLUMN_LONGITUDE, lon);
        values.put(PointsTable.COLUMN_ALTITUDE, alt);

        if (currentDriveId == -1) {
            Toast.makeText(this, "Drive error", Toast.LENGTH_LONG).show();
        } else {
            Uri uri = getContentResolver().insert(MyCycloComputrContentProvider.CONTENT_URI_POINT, values);
            //Toast.makeText(this, uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateTotalTimer(float time) {
        long secs = (long) (time/1000);
        long mins = (long) ((time/1000)/60);
        long hrs = (long) (((time/1000)/60)/60);
        long days = (long) ((((time/1000)/60)/60)/24);

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

        hrs = hrs % 24;
        String hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }

        String day = String.valueOf(days) + "d";

        mTabsAdapter.setTotalTimeText(day + " " + hours + ":" + minutes + ":" + seconds);
    }

    public void updateDrive() {
        String time = String.valueOf(secondsOfDrive);
        double distance = this.distance;
        double maxSpeed = this.maxSpeed;

        ContentValues values = new ContentValues();
        values.put(DrivingListTable.COLUMN_DATE, date);
        values.put(DrivingListTable.COLUMN_TIME, time);
        values.put(DrivingListTable.COLUMN_DISTANCE, distance);
        values.put(DrivingListTable.COLUMN_MAX_SPEED, maxSpeed);

        int row = getContentResolver().update(
                MyCycloComputrContentProvider.CONTENT_URI_DRIVING_LIST,
                values,
                "_id" + "='" + currentDriveId + "'",
                null
        );

        //Toast.makeText(this, String.valueOf(row), Toast.LENGTH_LONG).show();
    }

    public void setPrefSett() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        double totalDistance = settings.getFloat("totalDistance", 0);
        this.totalDistance = totalDistance;

        long totalTime = settings.getLong("totalTime", 0);
        //Toast.makeText(this, String.valueOf(totalTime), Toast.LENGTH_SHORT).show();
        this.totalTime = totalTime;

        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean nightMode = settings.getBoolean("nightmode", false);
        this.nightMode = nightMode;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putDouble("maxSpeed", maxSpeed);
        outState.putDouble("distance", distance);
        outState.putLong("startTime", startTime);
        outState.putLong("elapsedTime", elapsedTime);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        maxSpeed = savedInstanceState.getDouble("maxSpeed");
        distance = savedInstanceState.getDouble("distance");
        startTime = savedInstanceState.getLong("startTime");
        elapsedTime = savedInstanceState.getLong("elapsedTime");
        isSaved = true;

        mHandler.postDelayed(startTimer, 1000);
    }
}
