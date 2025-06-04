package nl.hva.capstone.viewModel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.Product
import nl.hva.capstone.repository.ProductRepository

class ProductViewModel(
    application: Application,
    private val loadingViewModel: LoadingViewModel
) : AndroidViewModel(application) {

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
        loadingViewModel.enableLoading()

        viewModelScope.launch {
            try {
                _productList.value = repository.getProducts()
            } catch (e: Exception) {
                _error.value = "Error fetching products: ${e.localizedMessage}"
            } finally {
                loadingViewModel.disableLoading()
            }
        }
    }

    fun saveProductWithImage(newProductData: Product) {
        loadingViewModel.enableLoading()

        viewModelScope.launch {
            try {
                val imageUriFromDialog = newProductData.image

                val originalProductSnapshot = _product.value
                val originalFirebaseImageUri = originalProductSnapshot?.image

                val isNewImagePicked = imageUriFromDialog != null &&
                        imageUriFromDialog != originalFirebaseImageUri &&
                        (imageUriFromDialog.scheme == "content" || imageUriFromDialog.scheme == "file")

                if (isNewImagePicked) {
                    repository.saveProductWithImage(newProductData)
                    if (originalFirebaseImageUri != null && originalFirebaseImageUri.toString()
                            .isNotEmpty()
                    ) {
                        repository.deleteImage(originalFirebaseImageUri.toString())
                    }
                } else if (imageUriFromDialog == null && originalFirebaseImageUri != null) {
                    repository.saveProduct(newProductData)
                    if (originalFirebaseImageUri.toString().isNotEmpty()) {
                        repository.deleteImage(originalFirebaseImageUri.toString())
                    }
                } else {
                    repository.saveProduct(newProductData)
                }

                _productSaved.value = true
                _error.value = null
            } catch (e: Exception) {
                _productSaved.value = false
                _error.value = "Error saving product with image: ${e.localizedMessage}"
            } finally {
                loadingViewModel.disableLoading()
            }
        }
    }

    fun setProduct(product: Product) {
        _product.value = product
    }

    fun resetState() {
        _productSaved.value = false
        _error.value = null
    }
}

class ProductViewModelFactory(
    private val application: Application,
    private val loadingViewModel: LoadingViewModel
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(application, loadingViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
