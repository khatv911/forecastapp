package com.kay.forecast.ui

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import androidx.activity.viewModels
import androidx.core.content.getSystemService
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kay.forecast.App
import com.kay.forecast.DEBOUNCE_DURATION
import com.kay.forecast.MIN_QUERY_LENGTH
import com.kay.forecast.R
import com.kay.forecast.repository.ForecastsWrapper
import com.kay.forecast.repository.CityNotFound
import com.kay.forecast.repository.InsufficientSearch
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val vm: ForecastViewModel by viewModels { ForecastViewModelFactory(
        (application as App).dependenciesContainer
    ) }

    private lateinit var adapter: ForecastAdapter

    private val snackbar by lazy {
        Snackbar.make(recyclerView, "", Snackbar.LENGTH_LONG)
    }
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set up adapter and recyclerview
        adapter = ForecastAdapter()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )

        TooltipCompat.setTooltipText(clearQuery, clearQuery.contentDescription)
        clearQuery.setOnClickListener {
            query.setText("")
        }

        // debouncing query
        query.doAfterTextChanged {
            clearQuery.isVisible = it?.isNotEmpty() ?: false
            searchJob?.cancel()
            searchJob = lifecycle.coroutineScope.launch {
                it?.let {
                    delay(DEBOUNCE_DURATION)
                    vm.queryCity(it.trim().toString())
                    hideKeyboard()
                }
            }
        }
        vm.uiStateLiveData.observe(this, Observer { onUiStateChange(it) })

    }

    @VisibleForTesting
    fun onUiStateChange(uiState: UiState<ForecastsWrapper>) {
        when {
            uiState.isLoading -> showLoading()
            uiState.succeeded -> {
                hideLoading()
                adapter.submitList(uiState.successOr(
                    ForecastsWrapper(
                        emptyList()
                    )
                ).forecasts)

            }
            else -> onError((uiState as UiState.Error).exception)
        }
    }

    // not a great error handler, but it's informative
    private fun onError(e: Throwable) {
        snackbar.setText(
            when (e) {
                is InsufficientSearch -> resources.getString(
                    R.string.require_more_character,
                    MIN_QUERY_LENGTH
                )
                is CityNotFound -> resources.getString(R.string.error_city_not_found)
                else -> "Something went wrong"
            }
        ).setDuration(Snackbar.LENGTH_LONG).show()

        adapter.submitList(emptyList())
    }

    private fun showLoading() {
        snackbar.setText("Loading...").show()
    }

    private fun hideLoading() {
        snackbar.dismiss()
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService<InputMethodManager>()!!
        inputMethodManager.hideSoftInputFromWindow(query.windowToken, HIDE_NOT_ALWAYS)
    }

}
