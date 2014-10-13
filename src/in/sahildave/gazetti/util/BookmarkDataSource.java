package in.sahildave.gazetti.util;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by sahil on 13/10/14.
 */
public class BookmarkDataSource {

    public SQLiteDatabase database;
    public SQLiteHelper dbhelper;

    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_TITLE, SQLiteHelper.COLUMN_LINK };

    private String useTable;

    public BookmarkDataSource(Context context) {
        Log.d("TABLE", "in DataSource Context");

        dbhelper = SQLiteHelper.getInstance(context);

    }

    public void open() throws SQLException {
        database = dbhelper.getWritableDatabase();
    }



    public void close() {
        dbhelper.close();
    }
}
