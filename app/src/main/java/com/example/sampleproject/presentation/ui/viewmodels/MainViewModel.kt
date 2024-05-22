// Presentation Layer - MainViewModel.kt
package com.example.sampleproject.presentation.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampleproject.domain.network.model.Country
import com.example.sampleproject.domain.usecase.GetCountriesUseCase
import com.example.sampleproject.data.utils.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel class responsible for handling business logic and communication between the UI and the data layer.
 */
class MainViewModel(private val getCountriesUseCase: GetCountriesUseCase) : ViewModel() {
    // LiveData to hold the list of countries retrieved from the repository
    val responseContainer = MutableLiveData<List<Country>>()

    // LiveData to hold any error messages
    val errorMessage = MutableLiveData<String>()

    // LiveData to indicate whether progress is being shown
    val isShowProgress = MutableLiveData<Boolean>()

    // Job to manage coroutines
    private var job: Job? = null

    // Coroutine exception handler to handle exceptions
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled : ${throwable.localizedMessage}")
    }

    /**
     * Fetches the list of countries from the repository.
     */
    fun getCountriesFromRepository() {
        isShowProgress.value = true
        job = viewModelScope.launch(exceptionHandler) {
            val result = getCountriesUseCase.execute()
            if (result is Result.Success) {
                responseContainer.postValue(result.data)
            } else if (result is Result.Error) {
                onError("Error: ${result.message}")
            }
            isShowProgress.postValue(false)
        }
    }

    /**
     * Handles error messages.
     */
    private fun onError(message: String) {
        errorMessage.value = message
        isShowProgress.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
