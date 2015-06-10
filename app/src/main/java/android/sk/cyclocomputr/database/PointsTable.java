package android.sk.cyclocomputr.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Fero on 6. 6. 2015.
 */
public class PointsTable {

    public static final String TABLE_POINTS = "points";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_DRIVE = "id_drive";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ALTITUDE = "altitude";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_POINTS
            + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_ID_DRIVE + " integer,"
            + COLUMN_LATITUDE + " text not null,"
            + COLUMN_LONGITUDE + " text not null,"
            + COLUMN_ALTITUDE + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(PointsTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all data");
        database.execSQL("DROP TABLE IF EXISTS" + TABLE_POINTS);
        onCreate(database);
    }
}
