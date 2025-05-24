package nl.hva.capstone.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import nl.hva.capstone.data.model.Purchase
import nl.hva.capstone.repository.PurchaseRepository

class PurchaseViewModel(application: Application) : AndroidViewModel(application) {

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
        viewModelScope.launch {
            try {
                _purchaseList.value = repository.getPurchases()
            } catch (e: Exception) {
                _error.value = "Error fetching purchases: ${e.localizedMessage}"
            }
        }
    }

    fun savePurchaseWithImage(purchase: Purchase) {
        viewModelScope.launch {
            try {
                if (purchase.image == null) {
                    repository.savePurchase(purchase)
                } else {
                    repository.savePurchaseWithImage(purchase)
                }
                _purchaseSaved.value = true
                _error.value = null
            } catch (e: Exception) {
                _purchaseSaved.value = false
                _error.value = "Error saving purchase with image: ${e.localizedMessage}"
            }
        }
    }

    fun resetState() {
        _purchaseSaved.value = false
        _error.value = null
    }
}
