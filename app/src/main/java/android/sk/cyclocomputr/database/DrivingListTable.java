package android.sk.cyclocomputr.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Fero on 6. 6. 2015.
 */
public class DrivingListTable {

    public static final String TABLE_DRIVING_LIST = "driving_list";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_MAX_SPEED = "max_speed";
    public static final String COLUMN_TIME = "time";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_DRIVING_LIST
            + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_DATE + " text not null,"
            + COLUMN_TIME + " text not null,"
            + COLUMN_DISTANCE + " text not null,"
            + COLUMN_MAX_SPEED + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(DrivingListTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all data");
        database.execSQL("DROP TABLE IF EXISTS" + TABLE_DRIVING_LIST);
        onCreate(database);
    }
}
