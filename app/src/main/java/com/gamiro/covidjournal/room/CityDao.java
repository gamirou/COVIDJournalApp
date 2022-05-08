package com.gamiro.covidjournal.room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CityDao {
    @Query("SELECT * FROM cities_table")
    LiveData<List<CityModel>> getAll();

    @Query("SELECT * FROM cities_table WHERE country = :country LIMIT 1")
    LiveData<CityModel> getCitiesByCountry(String country);

    @Insert
    void insertAll(CityModel... cityModels);

    @Delete
    void delete(CityModel cityModel);
}
