package nl.hva.capstone.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import nl.hva.capstone.data.model.*
import nl.hva.capstone.repository.SaleRepository
import java.util.Date

class SaleViewModel(
    application: Application,
    private val loadingViewModel: LoadingViewModel
) : AndroidViewModel(application) {
    private val repository = SaleRepository()

    private val _sales = MutableLiveData<List<Sale>>()
    val sales: LiveData<List<Sale>> get() = _sales

    private val _saleInformation = MutableLiveData<List<SaleInformation>>()
    val saleInformation: LiveData<List<SaleInformation>> get() = _saleInformation

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    suspend fun fetchSales() {
        loadingViewModel.enableLoading()
        try {
            _sales.value = repository.getSales()
        } catch (e: Exception) {
            _error.value = "Error fetching sales: ${e.localizedMessage}"
        } finally {
            loadingViewModel.disableLoading()
        }
    }

    suspend fun fetchSaleInformation(sale: Sale) {
        loadingViewModel.enableLoading()
        try {
            _saleInformation.value = repository.getSaleInformationForSale(sale.id)
        } catch (e: Exception) {
            _error.value = "Error fetching sale information: ${e.localizedMessage}"
        } finally {
            loadingViewModel.disableLoading()
        }
    }

    suspend fun saveSale(sale: Sale, saleInformation: List<SaleInformation>) {
        loadingViewModel.enableLoading()
        try {
            repository.addSale(sale)
            repository.addSaleInformation(saleInformation)
        } catch (e: Exception) {
            _error.value = "Error saving sale: ${e.localizedMessage}"
        } finally {
            loadingViewModel.disableLoading()
        }
    }

    suspend fun processAndSaveSale(
        service: Service?,
        products: List<Product>,
        clientName: String,
        nextAppointment: Timestamp?,
    ) {
        loadingViewModel.enableLoading()
        try {
            val saleId = service?.id ?: System.currentTimeMillis()

            val sale = Sale(
                id = saleId,
                dateTime = Timestamp.now(),
                clientName = clientName,
                nextAppointmentDate = nextAppointment
            )

            val saleProductInfo = products.mapNotNull {
                val price = it.salePrice
                val tax = it.taxes
                if (price != null && tax != null) {
                    SaleInformation(
                        saleId = saleId,
                        name = it.name,
                        price = price,
                        tax = tax
                    )
                } else null
            }

            val saleServiceInfo = service?.let {
                val price = it.price
                val tax = it.taxes
                if (price != null && tax != null) {
                    SaleInformation(
                        saleId = saleId,
                        name = it.name,
                        price = price,
                        tax = tax
                    )
                } else null
            }

            // Combine both into a single list
            val saleInformationList = saleServiceInfo?.let {
                saleProductInfo + it
            } ?: saleProductInfo

            saveSale(sale, saleInformationList)
        } catch (e: Exception) {
            _error.value = "Error processing sale: ${e.localizedMessage}"
        } finally {
            loadingViewModel.disableLoading()
        }
    }

    fun resetState() {
        _error.value = null
    }
}




class SaleViewModelFactory(
    private val application: Application,
    private val loadingViewModel: LoadingViewModel
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaleViewModel::class.java)) {
            return SaleViewModel(application, loadingViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
