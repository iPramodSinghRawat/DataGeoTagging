package com.example.pramodsinghrawat.alphageotagging;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by PramodSinghRawat on 16-04-2016.
 */
public class DBhandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "alphaGeoTagging.db";
    private static final String PACKAGE = "com.example.pramodsinghrawat.alphageotagging";


    private Context mCtx; //<-- declare a Context reference
    public DBhandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        mCtx = context; //<-- fill it with the Context you are passed
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String recordTable = "CREATE TABLE recordTable (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " fileName VARCHAR(100) NOT NULL UNIQUE," +
                " remark VARCHAR(200)," +
                " latLng VARCHAR(30)," +
                " addedOn VARCHAR(64))";
        db.execSQL(recordTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
            db.execSQL("DROP TABLE IF EXISTS otherExpenses");
            onCreate(db);
        */
    }

    /* Function: put Data */
    public String putRecord(Record object) {
        /*Check here For Previous Similar data */
        SQLiteDatabase db = this.getReadableDatabase();
        String insert_qry = "INSERT INTO recordTable (fileName,remark,latLng,addedOn) " +
                "VALUES('" + object.getFileName() + "','" + object.getRemark() + "','"
                + object.getLatLng() + "','"+ object.getAddedOn()+ "')";
        try {
            db.execSQL(insert_qry);
            return "1";//+insert_qry;
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    public Record[] getRecordArray(){
        int count=0,i=0;
        String query = "Select * FROM recordTable ORDER BY _id DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        count=cursor.getCount();
        Record[] objectAry=new Record[count];
        if (cursor.moveToFirst() || count!=0){
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    objectAry[i]=new Record(cursor.getString(cursor.getColumnIndex("_id")),
                            cursor.getString(cursor.getColumnIndex("fileName")),
                            cursor.getString(cursor.getColumnIndex("remark")),
                            cursor.getString(cursor.getColumnIndex("latLng")),
                            cursor.getString(cursor.getColumnIndex("addedOn"))
                    );
                    i++;
                    cursor.moveToNext();
                }
            } else {
                objectAry[i]=new Record("id","fileName","remark","latLng","addedOn");
            }
        }
        else {
            objectAry[i]=new Record("id","fileName","remark","latLng","addedOn");
        }
        cursor.close();
        return objectAry;
    }

    public void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }


}
