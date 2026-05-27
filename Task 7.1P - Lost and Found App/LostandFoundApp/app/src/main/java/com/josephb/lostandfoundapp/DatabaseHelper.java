package com.josephb.lostandfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "lostandfound.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE = "items";
    private static final String COL_ID = "id";
    private static final String COL_POST_TYPE = "post_type";
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phone";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_INCIDENT_DATE = "incident_date";
    private static final String COL_LOCATION = "location";
    private static final String COL_CATEGORY = "category";
    private static final String COL_IMAGE_PATH = "image_path";
    private static final String COL_CREATED_AT = "created_at";

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE " + TABLE + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_POST_TYPE + " TEXT NOT NULL, " +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_PHONE + " TEXT, " +
                    COL_DESCRIPTION + " TEXT, " +
                    COL_INCIDENT_DATE + " TEXT, " +
                    COL_LOCATION + " TEXT, " +
                    COL_CATEGORY + " TEXT NOT NULL, " +
                    COL_IMAGE_PATH + " TEXT, " +
                    COL_CREATED_AT + " INTEGER NOT NULL" +
                    ")";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insertItem(@NonNull Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_POST_TYPE, item.getPostType());
        cv.put(COL_NAME, item.getName());
        cv.put(COL_PHONE, item.getPhone());
        cv.put(COL_DESCRIPTION, item.getDescription());
        cv.put(COL_INCIDENT_DATE, item.getIncidentDate());
        cv.put(COL_LOCATION, item.getLocation());
        cv.put(COL_CATEGORY, item.getCategory());
        cv.put(COL_IMAGE_PATH, item.getImagePath());
        cv.put(COL_CREATED_AT, item.getCreatedAt());
        long id = db.insert(TABLE, null, cv);
        db.close();
        return id;
    }

    public int deleteItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    @Nullable
    public Item getItemById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Item item = null;
        if (c.moveToFirst()) {
            item = cursorToItem(c);
        }
        c.close();
        db.close();
        return item;
    }

    public List<Item> getAllItems() {
        return getItemsFiltered(null, null);
    }

    /**
     * @param category     null or "All" for no category filter, otherwise one of Item.CATEGORIES
     * @param searchQuery  null/empty for no text filter; matches name OR description (case-insensitive)
     */
    public List<Item> getItemsFiltered(@Nullable String category, @Nullable String searchQuery) {
        List<Item> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder where = new StringBuilder();
        List<String> args = new ArrayList<>();

        if (category != null && !category.isEmpty() && !"All".equalsIgnoreCase(category)) {
            where.append(COL_CATEGORY).append(" = ?");
            args.add(category);
        }

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            if (where.length() > 0) where.append(" AND ");
            where.append("(LOWER(").append(COL_NAME).append(") LIKE ? OR LOWER(")
                    .append(COL_DESCRIPTION).append(") LIKE ?)");
            String like = "%" + searchQuery.toLowerCase().trim() + "%";
            args.add(like);
            args.add(like);
        }

        String whereClause = where.length() > 0 ? where.toString() : null;
        String[] whereArgs = args.isEmpty() ? null : args.toArray(new String[0]);

        Cursor c = db.query(TABLE, null, whereClause, whereArgs,
                null, null, COL_CREATED_AT + " DESC");

        while (c.moveToNext()) {
            result.add(cursorToItem(c));
        }
        c.close();
        db.close();
        return result;
    }

    private Item cursorToItem(Cursor c) {
        Item item = new Item();
        item.setId(c.getLong(c.getColumnIndexOrThrow(COL_ID)));
        item.setPostType(c.getString(c.getColumnIndexOrThrow(COL_POST_TYPE)));
        item.setName(c.getString(c.getColumnIndexOrThrow(COL_NAME)));
        item.setPhone(c.getString(c.getColumnIndexOrThrow(COL_PHONE)));
        item.setDescription(c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION)));
        item.setIncidentDate(c.getString(c.getColumnIndexOrThrow(COL_INCIDENT_DATE)));
        item.setLocation(c.getString(c.getColumnIndexOrThrow(COL_LOCATION)));
        item.setCategory(c.getString(c.getColumnIndexOrThrow(COL_CATEGORY)));
        item.setImagePath(c.getString(c.getColumnIndexOrThrow(COL_IMAGE_PATH)));
        item.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(COL_CREATED_AT)));
        return item;
    }
}