package com.absys.test.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.Random;

public class User {
    private String id;
    private String firstname;
    private String lastname;
    private Date birthday;
    private String earthCountry;
    private String earthJob;
    private UserState state = UserState.CREATED;

    public User(String id, String firstname, String lastname, Date birthday, String earthCountry, String earthJob) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.earthCountry = earthCountry;
        this.earthJob = earthJob;
    }

    public User() {
    }

    /**
     * MARS-51*2 Standard : 4 Letters uppercase + 2 numbers (such as SFES45)
     * @return
     */
    public static String generateKey() {
        String generatedKey = RandomStringUtils.random(4, true, false);
        return generatedKey.toUpperCase() + String.format("%02d", new Random().nextInt(99));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public String getEarthCountry() {
        return earthCountry;
    }

    public void setEarthCountry(String earthCountry) {
        this.earthCountry = earthCountry;
    }

    public String getEarthJob() {
        return earthJob;
    }

    public void setEarthJob(String earthJob) {
        this.earthJob = earthJob;
    }
}
