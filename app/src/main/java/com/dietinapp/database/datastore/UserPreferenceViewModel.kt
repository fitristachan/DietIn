package com.dietinapp.database.datastore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserPreferenceViewModel (private val pref: UserPreference) : ViewModel() {
    fun getSession(): LiveData<Boolean?> {
        return pref.getSession().asLiveData()
    }

    fun getUsername(): LiveData<String?> {
        return pref.getUsername().asLiveData()
    }

    fun getEmail(): LiveData<String?> {
        return pref.getEmail().asLiveData()
    }

    fun getPhoto(): LiveData<String?> {
        return pref.getPhoto().asLiveData()
    }

    fun getToken(): LiveData<String?>{
        val token = MutableLiveData("")
        viewModelScope.launch {
            token.value = pref.getToken()
        }
        return token
    }

    fun saveToken(token: String, session: Boolean, username: String, email: String, photo: String) {
        viewModelScope.launch {
            pref.saveUser(token, session, username, email, photo)
        }
    }

    fun savePhoto(photo: String) {
        viewModelScope.launch {
            pref.saveOnlyPhoto(photo)
        }
    }

    fun saveUsername(username: String) {
        viewModelScope.launch {
            pref.saveOnlyUsername(username)
        }
    }


    fun reloadToken(token: String){
        viewModelScope.launch {
            pref.deleteOnlyToken()
            pref.saveOnlyToken(token)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            pref.deleteAll()
        }
    }
}

class UserPreferenceViewModelFactory (private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPreferenceViewModel::class.java)) {
            return UserPreferenceViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}