package nl.hva.capstone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.Login
import nl.hva.capstone.repository.LoginRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginRepository = LoginRepository()

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
//                val newLogin = Login(
//                    id = System.currentTimeMillis(),
//                    username = username,
//                    password = hashedPassword
//                )
//                _loginRepository.saveLogin(newLogin)

                val isValid = _loginRepository.checkCredentials(username, hashedPassword)

                if (isValid) {
                    _loginSuccess.value = true
                    _errorMessage.value = null
                } else {
                    _loginSuccess.value = false
                    _errorMessage.value = "Gebruikersnaam en/of wachtwoord is incorrect."
                }
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
}