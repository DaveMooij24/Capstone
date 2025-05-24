package nl.hva.capstone.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.Service
import nl.hva.capstone.repository.ServiceRepository

class ServiceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ServiceRepository()

    private val _serviceList = MutableLiveData<List<Service>>()
    val serviceList: LiveData<List<Service>> get() = _serviceList

    private val _serviceSaved = MutableLiveData<Boolean>()
    val serviceSaved: LiveData<Boolean> get() = _serviceSaved

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchService() {
        viewModelScope.launch {
            try {
                val services = repository.getServices()
                _serviceList.value = services
            } catch (e: Exception) {
                _error.value = "Error fetching services: ${e.localizedMessage}"
            }
        }
    }

    fun saveService(service: Service) {
        viewModelScope.launch {
            try {
                when {
                    service.name.isBlank() -> {
                        _error.value = "Behandeling naam moet ingevuld zijn."
                        _serviceSaved.value = false
                        return@launch
                    }
                    service.estimatedTimeMinutes == null || service.estimatedTimeMinutes <= 0 -> {
                        _error.value = "Er moet een geschatte duur ingevuld worden die groter dan nul is."
                        _serviceSaved.value = false
                        return@launch
                    }
                    service.price == null || service.price <= 0 -> {
                        _error.value = "Er moet een prijs ingevuld worden die groter dan nul is."
                        _serviceSaved.value = false
                        return@launch
                    }
                    service.taxes == null || service.taxes < 0 -> {
                        _error.value = "Er moet een belastingpercentage ingevuld worden dat nul of meer is."
                        _serviceSaved.value = false
                        return@launch
                    }
                }

                repository.saveService(service)
                _serviceSaved.value = true
                _error.value = null

            } catch (e: Exception) {
                _serviceSaved.value = false
                _error.value = "Error saving service: ${e.localizedMessage}"
            }
        }
    }

    fun resetState() {
        _serviceSaved.value = false
        _error.value = null
    }
}

