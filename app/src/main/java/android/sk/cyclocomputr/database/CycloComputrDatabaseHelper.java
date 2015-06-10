package android.sk.cyclocomputr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Fero on 6. 6. 2015.
 */
public class CycloComputrDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cyclocomputr.db";
    private static final int DATABASE_VERSION = 1;

    public CycloComputrDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DrivingListTable.onCreate(db);
        PointsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DrivingListTable.onUpgrade(db, oldVersion, newVersion);
        PointsTable.onUpgrade(db, oldVersion, newVersion);
    }
}
