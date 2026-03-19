package com.example.facefit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.facefit.model.AuthState
import com.example.facefit.repositry.AuthRepository

//Buisness logic
class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    fun checkUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    fun login(email: String, password: String) {
        repository.loginUser(email, password) { state ->
            _authState.postValue(state)
        }
    }
    fun signup(name: String, email: String, password: String) {
        repository.registerUser(name, email, password) { state ->
            _authState.postValue(state)
        }
    }
    fun logout() {
        repository.logout()
    }
}