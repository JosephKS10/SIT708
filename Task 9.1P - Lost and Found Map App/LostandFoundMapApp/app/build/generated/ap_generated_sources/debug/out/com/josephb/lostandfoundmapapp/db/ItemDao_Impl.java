package com.josephb.lostandfoundmapapp.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ItemDao_Impl implements ItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Item> __insertionAdapterOfItem;

  public ItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfItem = new EntityInsertionAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `items` (`id`,`type`,`name`,`phone`,`description`,`date`,`locationName`,`latitude`,`longitude`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Item entity) {
        statement.bindLong(1, entity.id);
        if (entity.type == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.type);
        }
        if (entity.name == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.name);
        }
        if (entity.phone == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.phone);
        }
        if (entity.description == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.description);
        }
        if (entity.date == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.date);
        }
        if (entity.locationName == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.locationName);
        }
        statement.bindDouble(8, entity.latitude);
        statement.bindDouble(9, entity.longitude);
      }
    };
  }

  @Override
  public long insert(final Item item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfItem.insertAndReturnId(item);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Item> getAll() {
    final String _sql = "SELECT * FROM items ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfLocationName = CursorUtil.getColumnIndexOrThrow(_cursor, "locationName");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final List<Item> _result = new ArrayList<Item>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Item _item;
        _item = new Item();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item.name = null;
        } else {
          _item.name = _cursor.getString(_cursorIndexOfName);
        }
        if (_cursor.isNull(_cursorIndexOfPhone)) {
          _item.phone = null;
        } else {
          _item.phone = _cursor.getString(_cursorIndexOfPhone);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _item.date = null;
        } else {
          _item.date = _cursor.getString(_cursorIndexOfDate);
        }
        if (_cursor.isNull(_cursorIndexOfLocationName)) {
          _item.locationName = null;
        } else {
          _item.locationName = _cursor.getString(_cursorIndexOfLocationName);
        }
        _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
