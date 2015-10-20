package com.gdgvitvellore.volsbbonetouch.Database;

import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "accountsManager";

    private static final String TABLE_ACCOUNTS = "accounts";

    private static final String KEY_ID = "id";

    private static final String KEY_NAME = "uname";

    private static final String KEY_PASS = "password";

    public DatabaseHandler(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_ACCOUNTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_PASS + " TEXT" + ")";
        db.execSQL(CREATE_ACCOUNTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);

        onCreate(db);
    }

    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, account.getName()); // Contact Name
        values.put(KEY_PASS, account.getPassword()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_ACCOUNTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single account
    public Account getAccount(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACCOUNTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_PASS}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Account account = new Account(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return account
        return account;
    }

    // Getting All Contacts
    public List<Account> getAllContacts() {
        List<Account> accountList = new ArrayList<Account>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Account account = new Account();
                account.setID(Integer.parseInt(cursor.getString(0)));
                account.setName(cursor.getString(1));
                account.setPassword(cursor.getString(2));
                // Adding account to list
                accountList.add(account);
            } while (cursor.moveToNext());
        }

        // return account list
        return accountList;
    }
    public boolean checkContact(String uname){
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNTS+" WHERE "+KEY_NAME+"="+uname;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_PASS}, KEY_NAME + "=?",
                new String[]{uname}, null, null, null, null);

        if(cursor.moveToFirst()){
            return true;

        }
        else
            return false;
    }
    // Updating single account
    public int updateContact(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, account.getName());
        values.put(KEY_PASS, account.getPassword());

        // updating row
        return db.update(TABLE_ACCOUNTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(account.getID())});
    }

    // Deleting single account
    public void deleteContact(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNTS, KEY_ID + " = ?",
                new String[]{String.valueOf(account.getID())});
        db.close();
    }


    // Getting accounts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ACCOUNTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int n=cursor.getCount();

        cursor.close();
        // return count
        return n;

    }

}