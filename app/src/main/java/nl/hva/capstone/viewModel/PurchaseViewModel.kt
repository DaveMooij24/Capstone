package nl.hva.capstone.viewModel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.Purchase
import nl.hva.capstone.repository.PurchaseRepository

class PurchaseViewModel(
    application: Application,
    private val loadingViewModel: LoadingViewModel
) : AndroidViewModel(application) {

    private val repository = PurchaseRepository()

    private val _purchaseList = MutableLiveData<List<Purchase>>()
    val purchaseList: LiveData<List<Purchase>> get() = _purchaseList

    private val _purchase = MutableLiveData<Purchase?>()
    val purchase: LiveData<Purchase?> get() = _purchase

    private val _purchaseSaved = MutableLiveData<Boolean>()
    val purchaseSaved: LiveData<Boolean> get() = _purchaseSaved

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchPurchases() {
        loadingViewModel.enableLoading()

        viewModelScope.launch {
            try {
                _purchaseList.value = repository.getPurchases()
            } catch (e: Exception) {
                _error.value = "Error fetching purchases: ${e.localizedMessage}"
            } finally {
                loadingViewModel.disableLoading()
            }
        }
    }

    fun savePurchaseWithImage(newPurchase: Purchase) {
        loadingViewModel.enableLoading()

        viewModelScope.launch {
            try {
                val imageUriFromDialog = newPurchase.image

                val originalPurchaseSnapshot = _purchase.value
                val originalFirebaseImageUri = originalPurchaseSnapshot?.image

                val isNewImagePicked = imageUriFromDialog != null &&
                        imageUriFromDialog != originalFirebaseImageUri &&
                        (imageUriFromDialog.scheme == "content" || imageUriFromDialog.scheme == "file")

                if (isNewImagePicked) {
                    repository.savePurchaseWithImage(newPurchase)
                    if (originalFirebaseImageUri != null && originalFirebaseImageUri.toString()
                            .isNotEmpty()
                    ) {
                        repository.deleteImage(originalFirebaseImageUri.toString())
                    }
                } else if (imageUriFromDialog == null && originalFirebaseImageUri != null) {
                    repository.savePurchase(newPurchase)
                    if (originalFirebaseImageUri.toString().isNotEmpty()) {
                        repository.deleteImage(originalFirebaseImageUri.toString())
                    }
                } else {
                    repository.savePurchase(newPurchase)
                }

                _purchaseSaved.value = true
                _error.value = null
            } catch (e: Exception) {
                _purchaseSaved.value = false
                _error.value = "Error saving purchase with image: ${e.localizedMessage}"
            } finally {
                loadingViewModel.disableLoading()
            }
        }
    }

    fun setPurchase(purchase: Purchase){
        _purchase.value = purchase
    }

    fun resetState() {
        _purchaseSaved.value = false
        _error.value = null
    }
}

class PurchaseViewModelFactory(
    private val application: Application,
    private val loadingViewModel: LoadingViewModel
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PurchaseViewModel::class.java)) {
            return PurchaseViewModel(application, loadingViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
