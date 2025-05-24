package nl.hva.capstone.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.Client
import nl.hva.capstone.repository.ClientRepository

class ClientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ClientRepository()

    private val _clientList = MutableLiveData<List<Client>>()
    val clientList: LiveData<List<Client>> get() = _clientList

    private val _client = MutableLiveData<Client?>()
    val client: MutableLiveData<Client?> get() = _client

    private val _clientSaved = MutableLiveData<Boolean>()
    val clientSaved: LiveData<Boolean> get() = _clientSaved

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchClients() {
        viewModelScope.launch {
            try {
                val clients = repository.getClients()
                _clientList.value = clients
            } catch (e: Exception) {
                _error.value = "Error fetching clients: ${e.localizedMessage}"
            }
        }
    }

    fun fetchClientById(id: Long) {
        viewModelScope.launch {
            try {
                val client = repository.getClientById(id)
                _client.value = client
            } catch (e: Exception) {
                _error.value = "Error fetching client: ${e.localizedMessage}"
            }
        }
    }

    fun saveClient(client: Client) {
        viewModelScope.launch {
            try {
                when {
                    client.name.isBlank() -> {
                        _error.value = "Klantnaam moet ingevuld zijn."
                        _clientSaved.value = false
                        return@launch
                    }
                    client.gender.isBlank() -> {
                        _error.value = "Geslacht moet ingevuld zijn."
                        _clientSaved.value = false
                        return@launch
                    }
                    client.phone.isBlank() -> {
                        _error.value = "Telefoonnummer moet ingevuld zijn."
                        _clientSaved.value = false
                        return@launch
                    }
                    client.email.isBlank() -> {
                        _error.value = "E-mailadres moet ingevuld zijn."
                        _clientSaved.value = false
                        return@launch
                    }
                }

                repository.saveClient(client)
                _clientSaved.value = true
                _error.value = null

            } catch (e: Exception) {
                _clientSaved.value = false
                _error.value = "Error saving client: ${e.localizedMessage}"
            }
        }
    }


    fun resetState() {
        _clientSaved.value = false
        _error.value = null
    }
}

