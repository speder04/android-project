package android.sk.cyclocomputr.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.sk.cyclocomputr.database.CycloComputrDatabaseHelper;
import android.sk.cyclocomputr.database.DrivingListTable;
import android.sk.cyclocomputr.database.PointsTable;
import android.text.TextUtils;

/**
 * Created by Fero on 6. 6. 2015.
 */
public class MyCycloComputrContentProvider extends ContentProvider {

    private CycloComputrDatabaseHelper database;

    private static final int DRIVING_LISTS = 10;
    private static final int DRIVING_LIST_ID = 20;
    private static final int POINTS = 30;
    private static final int POINT_ID_DRIVE = 40;

    private static final String AUTHORITY = "android.sk.cyclocomputr.contentprovider";

    private static final String BASE_PATH_DRIVING_LIST = "drivinglists";
    private static final String BASE_PATH_POINT = "points";

    public static final Uri CONTENT_URI_DRIVING_LIST = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH_DRIVING_LIST);
    public static final Uri CONTENT_URI_POINT = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH_POINT);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/drivinglists";

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/drivinglist";

    public static final String CONTENT_TYPE2 = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/points";

    public static final String CONTENT_ITEM_TYPE2 = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/point";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DRIVING_LIST, DRIVING_LISTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_DRIVING_LIST + "/#", DRIVING_LIST_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_POINT, POINTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_POINT + "/#", POINT_ID_DRIVE);
    }

    @Override
    public boolean onCreate() {
        database = new CycloComputrDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case DRIVING_LIST_ID:
                queryBuilder.appendWhere(DrivingListTable.COLUMN_ID + "=" + uri.getLastPathSegment());
            case DRIVING_LISTS:
                queryBuilder.setTables(DrivingListTable.TABLE_DRIVING_LIST);
                break;
            case POINT_ID_DRIVE:
                queryBuilder.appendWhere(PointsTable.COLUMN_ID_DRIVE + "=" + uri.getLastPathSegment());
            case POINTS:
                queryBuilder.setTables(PointsTable.TABLE_POINTS);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        Uri uriReturn = null;
        long id = 0;
        switch (uriType) {
            case DRIVING_LISTS:
                id = db.insert(DrivingListTable.TABLE_DRIVING_LIST, null, values);
                uriReturn = Uri.parse(BASE_PATH_DRIVING_LIST + "/" + id);
                break;
            case POINTS:
                id = db.insert(PointsTable.TABLE_POINTS, null, values);
                uriReturn = Uri.parse(BASE_PATH_POINT + "/" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return uriReturn;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        String id;
        switch (uriType) {
            case DRIVING_LISTS:
                rowsDeleted = sqlDB.delete(DrivingListTable.TABLE_DRIVING_LIST, selection,
                        selectionArgs);
                break;
            case DRIVING_LIST_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DrivingListTable.TABLE_DRIVING_LIST,
                            DrivingListTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(DrivingListTable.TABLE_DRIVING_LIST,
                            DrivingListTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case POINTS:
                rowsDeleted = sqlDB.delete(PointsTable.TABLE_POINTS, selection,
                        selectionArgs);
                break;
            case POINT_ID_DRIVE:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(PointsTable.TABLE_POINTS,
                            PointsTable.COLUMN_ID_DRIVE + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(PointsTable.TABLE_POINTS,
                            PointsTable.COLUMN_ID_DRIVE + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        String id;
        switch (uriType) {
            case DRIVING_LISTS:
                rowsUpdated = sqlDB.update(DrivingListTable.TABLE_DRIVING_LIST,
                        values,
                        selection,
                        selectionArgs);
                break;
            case DRIVING_LIST_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DrivingListTable.TABLE_DRIVING_LIST,
                            values,
                            DrivingListTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(DrivingListTable.TABLE_DRIVING_LIST,
                            values,
                            DrivingListTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case POINTS:
                rowsUpdated = sqlDB.update(PointsTable.TABLE_POINTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case POINT_ID_DRIVE:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PointsTable.TABLE_POINTS,
                            values,
                            PointsTable.COLUMN_ID_DRIVE + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(PointsTable.TABLE_POINTS,
                            values,
                            PointsTable.COLUMN_ID_DRIVE + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
