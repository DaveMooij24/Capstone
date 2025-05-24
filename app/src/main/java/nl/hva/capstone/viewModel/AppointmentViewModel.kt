package nl.hva.capstone.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.Appointment
import nl.hva.capstone.repository.AppointmentRepository
import java.util.*

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppointmentRepository()

    private val _appointmentList = MutableLiveData<List<Appointment>>()
    val appointmentList: LiveData<List<Appointment>> get() = _appointmentList

    private val _appointment = MutableLiveData<Appointment?>()
    val appointment: MutableLiveData<Appointment?> get() = _appointment

    private val _appointmentSaved = MutableLiveData<Boolean>()
    val appointmentSaved: LiveData<Boolean> get() = _appointmentSaved

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchAppointmentsById(id: Long) {
        viewModelScope.launch {
            try {
                val appointment = repository.getAppointmentById(id)
                _appointment.value = appointment
            } catch (e: Exception) {
                _error.value = "Error fetching appointment: ${e.localizedMessage}"
            }
        }
    }

    fun fetchMostRecentAppointment() {
        viewModelScope.launch {
            try {
                val latest = repository.getMostRecentAppointment()
                _appointment.value = latest
            } catch (e: Exception) {
                _error.value = "Error fetching most recent appointment: ${e.localizedMessage}"
            }
        }
    }

    fun saveAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                when {
                    appointment.clientId <= 0 -> {
                        _error.value = "Klant ID is ongeldig."
                        _appointmentSaved.value = false
                        return@launch
                    }
                    appointment.serviceId <= 0 -> {
                        _error.value = "Service ID is ongeldig."
                        _appointmentSaved.value = false
                        return@launch
                    }
                }

                repository.saveAppointment(appointment)
                _appointmentSaved.value = true
                _error.value = null
            } catch (e: Exception) {
                _appointmentSaved.value = false
                _error.value = "Error saving appointment: ${e.localizedMessage}"
            }
        }
    }

    fun fetchAppointmentsBetween(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            try {
                val appointments = repository.getAppointmentsBetween(startDate, endDate)
                _appointmentList.value = appointments
            } catch (e: Exception) {
                _error.value = "Error fetching appointments: ${e.localizedMessage}"
            }
        }
    }

    fun updateAppointmentCheckoutStatus(appointmentId: Long, checkedOut: Boolean) {
        viewModelScope.launch {
            repository.updateAppointmentCheckoutStatus(appointmentId, checkedOut)
        }
    }


    fun resetState() {
        _appointmentSaved.value = false
        _error.value = null
    }
}
