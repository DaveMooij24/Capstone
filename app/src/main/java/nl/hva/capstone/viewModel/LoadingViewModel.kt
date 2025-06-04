package nl.hva.capstone.viewModel;

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LoadingViewModel(application: Application) : AndroidViewModel(application) {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun enableLoading() {
        _loading.postValue(true)
    }

    fun disableLoading() {
        _loading.postValue(false)

    }
}
