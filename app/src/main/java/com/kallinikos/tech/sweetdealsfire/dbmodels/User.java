package com.kallinikos.tech.sweetdealsfire.dbmodels;

/**
 * Created by kallinikos on 20/01/17.
 */

public class User {

    public String displayName;
    public String phoneNumber;

    public User(){
        //Default Constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String displayName,String phoneNumber){
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
