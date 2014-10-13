package in.sahildave.gazetti.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sahil on 13/10/14.
 */
public class SQLiteHelper extends SQLiteOpenHelper{

    private static SQLiteHelper sInstance;
    public SQLiteDatabase database;

    private static final String DATABASE_NAME = "gazetti_ril.db";
    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";

    public static final String COLUMN_NEWSPAPER_ID = "newspaper_id_column";
    public static final String COLUMN_CATEGORY_ID = "category_id_column";
    public static final String COLUMN_NEWS_TITLE = "news_title";
    public static final String COLUMN_NEWS_IMAGE_URL = "news_image_url";
    public static final String COLUMN_NEWS_BODY = "news_body";

    public static final String TABLE_READ_IT_LATER = "read_it_later";

    //new
    private static final String CREATE_TABLE_RIL =
            "CREATE TABLE " + TABLE_READ_IT_LATER + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NEWSPAPER_ID + " TEXT NOT NULL, "
                    + COLUMN_CATEGORY_ID + " TEXT NOT NULL, "
                    + COLUMN_NEWS_TITLE + " TEXT NOT NULL, "
                    + COLUMN_NEWS_IMAGE_URL + " TEXT NOT NULL, "
                    + COLUMN_NEWS_BODY + " TEXT NOT NULL);";

    public static SQLiteHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLiteHelper(context);
        }
        return sInstance;
    }

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);

    }
}
