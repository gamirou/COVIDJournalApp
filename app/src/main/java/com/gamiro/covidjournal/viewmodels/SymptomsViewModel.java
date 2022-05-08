package com.gamiro.covidjournal.viewmodels;

import com.gamiro.covidjournal.models.user.UserSymptoms;
import com.gamiro.covidjournal.repositories.UserRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SymptomsViewModel extends ViewModel {

    // Repo
    private UserRepository userRepository;

    // User symptoms
    private MutableLiveData<UserSymptoms> userSymptoms;
    private MutableLiveData<UserSymptoms> localUserSymptoms;

    public SymptomsViewModel() {
        userRepository = new UserRepository();
    }

    /**
     * USER SYMPTOMS
     */
    public LiveData<UserSymptoms> getUserSymptoms() {
        if (userSymptoms == null) {
            userSymptoms = new MutableLiveData<>();
        }

        userSymptoms = userRepository.getUserSymptoms();
        return userSymptoms;
    }
    public LiveData<UserSymptoms> getLocalUserSymptoms() {
        if (localUserSymptoms == null) {
            localUserSymptoms = new MutableLiveData<>();
        }

        return localUserSymptoms;
    }
    // Update does not save to database
    public void updateLocalUserSymptoms(UserSymptoms symptoms) {
        localUserSymptoms.setValue(symptoms);
    }
    public void updateLocalUserSymptoms(boolean breathing, boolean chestPain, boolean awake, boolean lips) {
        UserSymptoms symptoms = localUserSymptoms.getValue();
        symptoms.setBreathing(breathing);
        symptoms.setChestPain(chestPain);
        symptoms.setAwake(awake);
        symptoms.setLips(lips);
        localUserSymptoms.setValue(symptoms);
    }
    public void updateLocalUserSymptoms(String whereAreYou) {
        UserSymptoms symptoms = localUserSymptoms.getValue();
        symptoms.setWhereAreYou(whereAreYou);
        localUserSymptoms.setValue(symptoms);
    }

    /**
     * Also returns if the user might / might not have coronavirus
     * @return - boolean
     */
    public void saveUserSymptoms() {
        UserSymptoms symptoms = localUserSymptoms.getValue();
        userRepository.editUserSymptoms(symptoms);
    }
    public void deleteLocalUserSymptoms() {
        localUserSymptoms = null;
    }
}
