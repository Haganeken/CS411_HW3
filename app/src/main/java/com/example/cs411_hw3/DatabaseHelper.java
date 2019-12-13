package com.example.cs411_hw3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "user_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USER = "users";
    private static final String TABLE_USER_COURSE = "users_course";
    private static final String KEY_ID = "id";
    private static final String KEY_FIRSTNAME = "name";
    private static final String KEY_COURSE = "course";


    //Student table with name + id

    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE "
            + TABLE_USER + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FIRSTNAME + " TEXT );";

    //Course table with course + id
    private static final String CREATE_TABLE_USER_COURSE = "CREATE TABLE "
            + TABLE_USER_COURSE + "(" + KEY_ID + " INTEGER,"+ KEY_COURSE + " TEXT );";


    //DatabaseHelper
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.d("table", CREATE_TABLE_STUDENTS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STUDENTS);
        db.execSQL(CREATE_TABLE_USER_COURSE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_USER + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_USER_COURSE + "'");

        onCreate(db);
    }

    //add user
    public void addUser(String name, String course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, name);
        long id = db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        //adding user course in users_course table
        ContentValues valuesCourse = new ContentValues();
        valuesCourse.put(KEY_ID, id);
        valuesCourse.put(KEY_COURSE, course);
        db.insert(TABLE_USER_COURSE, null, valuesCourse);


    }

    //getalluser
    public ArrayList<UserModel> getAllUsers() {
        ArrayList<UserModel> userModelArrayList = new ArrayList<UserModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                UserModel userModel = new UserModel();
                userModel.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                userModel.setName(c.getString(c.getColumnIndex(KEY_FIRSTNAME)));

                String selectCourseQuery = "SELECT  * FROM " + TABLE_USER_COURSE +" WHERE "+KEY_ID+" = "+ userModel.getId();
                Log.d("test",selectCourseQuery);

                Cursor cCourse = db.rawQuery(selectCourseQuery, null);

                if (cCourse.moveToFirst()) {
                    do {
                        userModel.setCourse(cCourse.getString(cCourse.getColumnIndex(KEY_COURSE)));
                    } while (cCourse.moveToNext());
                }



                userModelArrayList.add(userModel);
            } while (c.moveToNext());
        }
        return userModelArrayList;
    }

    public void updateUser(int id, String name, String course) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, name);
        db.update(TABLE_USER, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});


        ContentValues valuesCourse = new ContentValues();
        valuesCourse.put(KEY_COURSE, course);
        db.update(TABLE_USER_COURSE, valuesCourse, KEY_ID + " = ?", new String[]{String.valueOf(id)});


    }

    //delete from tables and db
    public void deleteUSer(int id) {


        SQLiteDatabase db = this.getWritableDatabase();


        db.delete(TABLE_USER, KEY_ID + " = ?",new String[]{String.valueOf(id)});


        db.delete(TABLE_USER_COURSE, KEY_ID + " = ?", new String[]{String.valueOf(id)});


    }

}