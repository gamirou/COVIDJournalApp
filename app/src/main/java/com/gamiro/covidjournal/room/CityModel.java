package com.gamiro.covidjournal.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cities_table")
public class CityModel {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "country")
    public String country;

    @ColumnInfo(name = "cities_list")
    public String citiesList;

    public CityModel(String country, String citiesList) {
        this.country = country;
        this.citiesList = citiesList;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCitiesList() {
        return citiesList;
    }

    public void setCitiesList(String citiesList) {
        this.citiesList = citiesList;
    }
}
