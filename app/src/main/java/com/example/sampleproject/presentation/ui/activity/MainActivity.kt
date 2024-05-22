// Presentation Layer - MainActivity.kt
package com.example.sampleproject.presentation.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sampleproject.databinding.ActivityMainBinding
import com.example.sampleproject.presentation.ui.adapters.CountriesRecyclerAdapter
import com.example.sampleproject.presentation.ui.factories.MainViewModelFactory
import com.example.sampleproject.domain.usecase.GetCountriesUseCase
import com.example.sampleproject.data.repository.CountryRepositoryImpl
import com.example.sampleproject.data.networkmodule.RetrofitInstanceModule
import com.example.sampleproject.presentation.ui.viewmodels.MainViewModel

/**
 * Main activity responsible for displaying the list of countries.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var countryAdapter: CountriesRecyclerAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setUpRecyclerView()
        setUpViewModel()
        observeViewModel()
    }

    /**
     * Sets up the RecyclerView to display the list of countries.
     */
    private fun setUpRecyclerView() {
        countryAdapter = CountriesRecyclerAdapter()
        activityMainBinding.countryRecyclerView.apply {
            adapter = countryAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    /**
     * Sets up the ViewModel using ViewModelFactory and initializes MainViewModel.
     */
    private fun setUpViewModel() {
        val apiService = RetrofitInstanceModule().theRetrofitInstance()
        val repository = CountryRepositoryImpl(apiService)
        val getCountriesUseCase = GetCountriesUseCase(repository)
        val viewModelFactory = MainViewModelFactory(getCountriesUseCase)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        mainViewModel.getCountriesFromRepository()
    }

    /**
     * Observes changes in the ViewModel and updates the UI accordingly.
     */
    private fun observeViewModel() {
        mainViewModel.responseContainer.observe(this, Observer { countries ->
            countries?.let {
                countryAdapter.setData(it)
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        })

        mainViewModel.isShowProgress.observe(this, Observer { showProgress ->
            activityMainBinding.mainProgressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
        })

        mainViewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }
}
