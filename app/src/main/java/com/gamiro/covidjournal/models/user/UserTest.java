package com.gamiro.covidjournal.models.user;

import java.io.Serializable;

public class UserTest implements Serializable {

    public String dateMade;
    public String result; // PENDING, NEGATIVE, POSITIVE
    public String dateResult;

    public UserTest() {}

    public UserTest(String dateMade, String result, String dateResult) {
        this.dateMade = dateMade;
        this.result = result;
        this.dateResult = dateResult;
    }

    public String getDateMade() {
        return dateMade;
    }

    public void setDateMade(String dateMade) {
        this.dateMade = dateMade;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDateResult() {
        return dateResult;
    }

    public void setDateResult(String dateResult) {
        this.dateResult = dateResult;
    }
}
