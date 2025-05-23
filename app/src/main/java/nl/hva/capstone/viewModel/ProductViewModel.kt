package nl.hva.capstone.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.repository.ProductRepository

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository()

    private val _productList = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> get() = _productList

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _productSaved = MutableLiveData<Boolean>()
    val productSaved: LiveData<Boolean> get() = _productSaved

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchProducts() {
        viewModelScope.launch {
            try {
                _productList.value = repository.getProducts()
            } catch (e: Exception) {
                _error.value = "Error fetching products: ${e.localizedMessage}"
            }
        }
    }

    fun saveProductWithImage(product: Product) {
        viewModelScope.launch {
            try {
                if (product.image == null) {
                    repository.saveProduct(product)
                } else {
                    repository.saveProductWithImage(product)
                }
                _productSaved.value = true
                _error.value = null
            } catch (e: Exception) {
                _productSaved.value = false
                _error.value = "Error saving product with image: ${e.localizedMessage}"
            }
        }
    }

    fun resetState() {
        _productSaved.value = false
        _error.value = null
    }
}
