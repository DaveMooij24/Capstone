package nl.hva.capstone.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.capstone.utils.*
import nl.hva.capstone.repository.LoginRepository
import kotlin.math.log

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginRepository = LoginRepository()
    private val dataStoreManager = DataStoreManager(application.applicationContext)

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun login(username: String, password: String) {
        if (username.isBlank()) {
            _loginSuccess.value = false
            _errorMessage.value = "Gebruikersnaam en/of wachtwoord is incorrect."
            return
        }

        if (password.isBlank()) {
            _loginSuccess.value = false
            _errorMessage.value = "Gebruikersnaam en/of wachtwoord is incorrect."
            return
        }
        val hashedPassword = _loginRepository.hashPassword(password)

        viewModelScope.launch {
            try {
                val isValid = _loginRepository.checkCredentials(username, hashedPassword)

                if (isValid) {
                    dataStoreManager.saveLogin(username, hashedPassword)
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Gebruikersnaam en/of wachtwoord is onjuist."
                }
                login(isValid)
            } catch (e: Exception) {
                _loginSuccess.value = false
                _errorMessage.value = "Failed to log in: ${e.message}"
            }

        }
    }

    fun resetLoginState() {
        _loginSuccess.value = false
        _errorMessage.value = null
    }

    fun login(isValid: Boolean){
        _loginSuccess.value = isValid
    }

    suspend fun biometricLogin() {
        var isValid = false
        if(dataStoreManager.getUsername() != null && dataStoreManager.getPassword() != null) {
            isValid = _loginRepository.checkCredentials(dataStoreManager.getUsername()!!, dataStoreManager.getPassword()!!)
        }
        login(isValid)
        _errorMessage.value = null
    }
}