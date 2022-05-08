package com.gamiro.covidjournal.viewmodels;

import com.gamiro.covidjournal.repositories.UserRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TokenViewModel extends ViewModel {

    private MutableLiveData<String> token;
    private UserRepository repository;

    public TokenViewModel() {
        repository = new UserRepository();
    }

    public LiveData<String> getToken() {
        if (token == null) {
            token = new MutableLiveData<>();
        }

        return token;
    }
    public void setToken(String newToken) {
        if (token == null) {
            token = new MutableLiveData<>();
        }
        token.setValue(newToken);
    }
    public void deleteToken() {
        repository.deleteTokenFromServerDatabase(token.getValue());
        token.setValue("");
    }
}
