package cat.dev.fitness;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fitness.db";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static Cursor getAllEntries(SQLiteDatabase db, String tableName) {
        return db.query(tableName, null, null, null, null, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(User.SQL_CREATE_ENTRIES);
        db.execSQL(Workout.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(User.SQL_DELETE_ENTRIES);
        db.execSQL(Workout.SQL_DELETE_ENTRIES);

        onCreate(db);
    }

    static class User implements BaseColumns {
        static final String TABLE_NAME = "User";

        static final String COLUMN_NAME_BIRTHDAY = "birthday";
        static final String COLUMN_NAME_EMAIL = "email";
        static final String COLUMN_NAME_FULL_NAME = "full_name";
        static final String COLUMN_NAME_SEX = "sex";
        static final String COLUMN_NAME_WEIGHT = "weight";

        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME_BIRTHDAY + " DATE, " +
                        COLUMN_NAME_EMAIL + " TEXT, " +
                        COLUMN_NAME_FULL_NAME + " TEXT NOT NULL, " +
                        COLUMN_NAME_SEX + " INTEGER, " +
                        COLUMN_NAME_WEIGHT + " REAL)";

        static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static class Workout implements BaseColumns {
        static final String TABLE_NAME = "Workout";

        static final String COLUMN_NAME_ACTIVE_TIME = "active_time";
        static final String COLUMN_NAME_BURNT_CALORIES = "burnt_calories";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_DISTANCE = "distance";
        static final String COLUMN_NAME_START_TIME = "start_time";
        static final String COLUMN_NAME_STEPS = "steps";
        static final String COLUMN_NAME_TOTAL_TIME = "total_time";

        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME_ACTIVE_TIME + " INTEGER, " +
                        COLUMN_NAME_BURNT_CALORIES + " INTEGER, " +
                        COLUMN_NAME_DATE + " TEXT, " +
                        COLUMN_NAME_DISTANCE + " REAL, " +
                        COLUMN_NAME_START_TIME + " INTEGER, " +
                        COLUMN_NAME_STEPS + " INTEGER DEFAULT 0, " +
                        COLUMN_NAME_TOTAL_TIME + " INTEGER)";

        static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
