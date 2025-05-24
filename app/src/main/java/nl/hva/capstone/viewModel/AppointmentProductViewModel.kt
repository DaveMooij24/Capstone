package nl.hva.capstone.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.AppointmentProduct
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.repository.AppointmentProductRepository

class AppointmentProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppointmentProductRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    fun addProductToAppointment(appointmentId: Long, productId: Long) {
        viewModelScope.launch {
            try {
                val appointmentProduct = AppointmentProduct(
                    appointmentId = appointmentId,
                    productId = productId
                )
                repository.addAppointmentProduct(appointmentProduct)
                fetchProductsForAppointment(appointmentId) // Refresh list
            } catch (e: Exception) {
                // Handle errors appropriately (e.g., log or show message)
            }
        }
    }

    fun fetchProductsForAppointment(appointmentId: Long) {
        viewModelScope.launch {
            val productList = repository.getProductsForAppointment(appointmentId)
            _products.value = productList
        }
    }

    fun deleteProductFromAppointment(appointmentId: Long, productId: Long) {
        viewModelScope.launch {
            val success = repository.deleteProductLinkFromAppointment(
                appointmentId,
                productId
            )

            if (success) {
                fetchProductsForAppointment(appointmentId)
            }
        }
    }
}
