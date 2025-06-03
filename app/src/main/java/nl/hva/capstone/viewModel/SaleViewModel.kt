package nl.hva.capstone.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import nl.hva.capstone.data.model.*
import nl.hva.capstone.repository.SaleRepository

class SaleViewModel(
    application: Application,
    private val loadingViewModel: LoadingViewModel
) : AndroidViewModel(application) {
    private val repository = SaleRepository()

    private val _sales = MutableLiveData<List<Sale>>()
    val sales: LiveData<List<Sale>> get() = _sales

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products
    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> get() = _services

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    suspend fun fetchSales(){
        loadingViewModel.enableLoading()
        try{
            _sales.value = repository.getSales()
        } catch (e: Exception){
            _error.value = "Error fetching sales: ${e.localizedMessage}"
        } finally {
            loadingViewModel.disableLoading()
        }
    }

    suspend fun fetchSaleInformation(sale: Sale){
        loadingViewModel.enableLoading()
        try{
            _products.value = repository.getSaleProductsForSale(sale.id)
            _services.value = repository.getSaleServicesForSale(sale.id)
        } catch (e: Exception){
            _error.value = "Error fetching sale information: ${e.localizedMessage}"
        } finally {
            loadingViewModel.disableLoading()
        }
    }

    suspend fun saveSale(sale: Sale, saleProducts: List<SaleProduct>, saleServices: List<SaleService>){
        loadingViewModel.enableLoading()
        try{
            repository.addSale(sale)
            repository.addSaleProducts(saleProducts)
            repository.addSaleServices(saleServices)
        } catch (e: Exception){
            _error.value = "Error saving sale: ${e.localizedMessage}"
        } finally {
            loadingViewModel.disableLoading()
        }
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
