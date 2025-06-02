package nl.hva.capstone.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.capstone.utils.*
import nl.hva.capstone.repository.LoginRepository
import kotlin.math.log

class LoginViewModel(
    application: Application,
    private val loadingViewModel: LoadingViewModel
) : AndroidViewModel(application) {

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

        loadingViewModel.enableLoading()

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
        loadingViewModel.enableLoading()

        try {
            var isValid = false
            if(dataStoreManager.getUsername() != null && dataStoreManager.getPassword() != null) {
                isValid = _loginRepository.checkCredentials(dataStoreManager.getUsername()!!, dataStoreManager.getPassword()!!)
            }
            login(isValid)
            _errorMessage.value = null
        } catch (e: Exception) {
            // Handle errors appropriately (e.g., log or show message)
        }
    }
}

class LoginViewModelFactory(
    private val application: Application,
    private val loadingViewModel: LoadingViewModel
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(application, loadingViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}