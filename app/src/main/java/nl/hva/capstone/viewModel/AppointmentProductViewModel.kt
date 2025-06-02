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
import nl.hva.capstone.data.model.AppointmentProduct
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.repository.AppointmentProductRepository

class AppointmentProductViewModel(
    application: Application,
    private val loadingViewModel: LoadingViewModel
) : AndroidViewModel(application) {

    private val repository = AppointmentProductRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    fun addProductToAppointment(appointmentId: Long, productId: Long) {
        loadingViewModel.enableLoading()

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
            } finally {
                loadingViewModel.disableLoading()
            }
        }
    }

    fun fetchProductsForAppointment(appointmentId: Long) {
        loadingViewModel.enableLoading()

        viewModelScope.launch {
            try {
                val productList = repository.getProductsForAppointment(appointmentId)
                _products.value = productList
            } catch (e: Exception) {
                // Handle errors appropriately (e.g., log or show message)
            } finally {
                loadingViewModel.disableLoading()
            }
        }
    }

    fun deleteProductFromAppointment(appointmentId: Long, productId: Long) {
        loadingViewModel.enableLoading()

        viewModelScope.launch {
            try {
                val success = repository.deleteProductLinkFromAppointment(
                    appointmentId,
                    productId
                )

                if (success) {
                    fetchProductsForAppointment(appointmentId)
                }
            } catch (e: Exception) {
                // Handle errors appropriately (e.g., log or show message)
            } finally {
                loadingViewModel.disableLoading()
            }
        }
    }
}

class AppointmentProductViewModelFactory(
    private val application: Application,
    private val loadingViewModel: LoadingViewModel
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentProductViewModel::class.java)) {
            return AppointmentProductViewModel(application, loadingViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
