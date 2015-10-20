package com.gdgvitvellore.volsbbonetouch.Database;

public class Account {

    //private variables
    int _id;
    String _username;
    String _password;

    // Empty constructor
    public Account(){

    }
    // constructor
    public Account(int id, String uname, String pass){
        this._id = id;
        this._username = uname;
        this._password = pass;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._username;
    }

    // setting name
    public void setName(String name){
        this._username = name;
    }

    // getting phone number
    public String getPassword(){
        return this._password;
    }

    // setting phone number
    public void setPassword(String password){
        this._password = password;
    }
}