package com.gamiro.covidjournal.models.user;

public class UserSymptoms {
    public boolean ache;
    public boolean chills;
    public boolean cough;
    public boolean fatigue;
    public boolean fever;
    public boolean headache;
    public boolean lackOfTaste;
    public String temperature;
    public String temperatureMode;
    public boolean soreThroat;
    public boolean vomit;

    // Severe symptoms
    public boolean breathing;
    public boolean chestPain;
    public boolean awake;
    public boolean lips;

    // Where are you
    public String whereAreYou;

    public UserSymptoms() {}

    public UserSymptoms(boolean ache, boolean chills, boolean cough, boolean fatigue,
                        boolean fever, boolean headache, boolean lackOfTaste, String temperature,
                        String temperatureMode, boolean soreThroat, boolean vomit) {
        this.ache = ache;
        this.chills = chills;
        this.cough = cough;
        this.fatigue = fatigue;
        this.fever = fever;
        this.headache = headache;
        this.lackOfTaste = lackOfTaste;
        this.temperature = temperature;
        this.temperatureMode = temperatureMode;
        this.soreThroat = soreThroat;
        this.vomit = vomit;
    }

    public boolean isAche() {
        return ache;
    }

    public void setAche(boolean ache) {
        this.ache = ache;
    }

    public boolean isChills() {
        return chills;
    }

    public void setChills(boolean chills) {
        this.chills = chills;
    }

    public boolean isCough() {
        return cough;
    }

    public void setCough(boolean cough) {
        this.cough = cough;
    }

    public boolean isFatigue() {
        return fatigue;
    }

    public void setFatigue(boolean fatigue) {
        this.fatigue = fatigue;
    }

    public boolean isFever() {
        return fever;
    }

    public void setFever(boolean fever) {
        this.fever = fever;
    }

    public boolean isHeadache() {
        return headache;
    }

    public void setHeadache(boolean headache) {
        this.headache = headache;
    }

    public boolean isLackOfTaste() {
        return lackOfTaste;
    }

    public void setLackOfTaste(boolean lackOfTaste) {
        this.lackOfTaste = lackOfTaste;
    }

    public String getTemperatureMode() {
        return temperatureMode;
    }

    public void setTemperatureMode(String temperatureMode) {
        this.temperatureMode = temperatureMode;
    }

    public boolean isSoreThroat() {
        return soreThroat;
    }

    public void setSoreThroat(boolean soreThroat) {
        this.soreThroat = soreThroat;
    }

    public boolean isVomit() {
        return vomit;
    }

    public void setVomit(boolean vomit) {
        this.vomit = vomit;
    }

    public boolean isBreathing() {
        return breathing;
    }

    public void setBreathing(boolean breathing) {
        this.breathing = breathing;
    }

    public boolean isChestPain() {
        return chestPain;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setChestPain(boolean chestPain) {
        this.chestPain = chestPain;
    }

    public boolean isAwake() {
        return awake;
    }

    public void setAwake(boolean awake) {
        this.awake = awake;
    }

    public boolean isLips() {
        return lips;
    }

    public void setLips(boolean lips) {
        this.lips = lips;
    }

    public String getWhereAreYou() {
        return whereAreYou;
    }

    public void setWhereAreYou(String whereAreYou) {
        this.whereAreYou = whereAreYou;
    }
}
