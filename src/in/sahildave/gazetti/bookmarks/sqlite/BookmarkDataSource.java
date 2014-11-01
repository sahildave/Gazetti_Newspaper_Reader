package in.sahildave.gazetti.bookmarks.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil on 13/10/14.
 */
public class BookmarkDataSource {
    private final static String TAG = BookmarkDataSource.class.getName();

    public SQLiteDatabase database;
    public SQLiteHelper dbhelper;
    private String[] allColumns = {
            SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_NEWSPAPER_NAME,
            SQLiteHelper.COLUMN_CATEGORY_NAME,
            SQLiteHelper.COLUMN_NEWS_HEADLINE,
            SQLiteHelper.COLUMN_NEWS_BODY,
            SQLiteHelper.COLUMN_NEWS_ARTICLE_URL,
            SQLiteHelper.COLUMN_NEWS_PUB_DATE,
            SQLiteHelper.COLUMN_NEWS_IMAGE_URL};

    public BookmarkDataSource(Context context) {
        dbhelper = SQLiteHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbhelper.getWritableDatabase();
    }

    public void close() {
        dbhelper.close();
    }

    public void createBookmarkModelEntry(BookmarkModel bookmarkModel) {
        ContentValues values = new ContentValues();
        try {
            values.put(SQLiteHelper.COLUMN_NEWSPAPER_NAME, bookmarkModel.getNewspaperName());
            values.put(SQLiteHelper.COLUMN_CATEGORY_NAME, bookmarkModel.getCategoryName());
            values.put(SQLiteHelper.COLUMN_NEWS_HEADLINE, bookmarkModel.getmArticleHeadline());
            values.put(SQLiteHelper.COLUMN_NEWS_BODY, bookmarkModel.getmArticleBody());
            values.put(SQLiteHelper.COLUMN_NEWS_ARTICLE_URL, bookmarkModel.getmArticleURL());
            values.put(SQLiteHelper.COLUMN_NEWS_PUB_DATE, bookmarkModel.getmArticlePubDate());
            values.put(SQLiteHelper.COLUMN_NEWS_IMAGE_URL, bookmarkModel.getmArticleImageURL());
        } catch (SQLiteConstraintException sqe){
            Log.e(TAG, "Exception while creating sqlite entry - "+ sqe.getMessage(), sqe);
        }
        long insertId = database.insert(SQLiteHelper.TABLE_READ_IT_LATER, null,
                values);
        //Log.d(TAG, "Created entry "+insertId);

        Cursor cursor = database.query(SQLiteHelper.TABLE_READ_IT_LATER,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public void deleteBookmarkModelEntry(String mArticleURL) {
        //Log.d(TAG, "Deleting "+mArticleURL);

        database.delete(
                SQLiteHelper.TABLE_READ_IT_LATER,
                SQLiteHelper.COLUMN_NEWS_ARTICLE_URL + "=?",
                new String[]{mArticleURL});
    }

    public boolean isBookmarked(String mArticleURL) {
        Cursor cursor = null;
        try {
            cursor = database.query(
                    SQLiteHelper.TABLE_READ_IT_LATER,
                    allColumns,
                    SQLiteHelper.COLUMN_NEWS_ARTICLE_URL+ "=?",
                    new String[]{mArticleURL},
                    null, null, null);
        } catch (SQLiteException sqe){
            Log.e(BookmarkDataSource.class.getName(), "SQLITE ERROR "+sqe.getMessage(), sqe);
        } catch (Exception e) {
            Log.e(BookmarkDataSource.class.getName(), "Exception ERROR " + e.getMessage(), e);
        }
        boolean returnBoolean  = cursor.getCount() > 0;
        cursor.close();
        //Log.d(TAG, "Is bookmarked for "+mArticleURL+" -- "+returnBoolean);
        return returnBoolean;
    }

    public List<BookmarkModel> getAllBookmarkModels() {
        //Log.d(TAG, "Getting all Bookmarks");
        List<BookmarkModel> bookmarkModelList = new ArrayList<BookmarkModel>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_READ_IT_LATER,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BookmarkModel bookmarkObject = cursorToBookmarkModel(cursor);
            bookmarkModelList.add(bookmarkObject);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return bookmarkModelList;
    }

    private BookmarkModel cursorToBookmarkModel(Cursor cursor) {
        BookmarkModel BookmarkModel = new BookmarkModel();
        BookmarkModel.setNewspaperName(cursor.getString(1));
        BookmarkModel.setCategoryName(cursor.getString(2));
        BookmarkModel.setmArticleHeadline(cursor.getString(3));
        BookmarkModel.setmArticleBody(cursor.getString(4));
        BookmarkModel.setmArticleURL(cursor.getString(5));
        BookmarkModel.setmArticlePubDate(cursor.getString(6));
        BookmarkModel.setmArticleImageURL(cursor.getString(7));
        return BookmarkModel;
    }
}
