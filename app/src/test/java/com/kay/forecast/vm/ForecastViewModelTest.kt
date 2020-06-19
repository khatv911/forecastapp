package com.kay.forecast.vm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kay.forecast.CoroutineTestRule
import com.kay.forecast.fakeForeCastWrapper
import com.kay.forecast.repository.CityNotFound
import com.kay.forecast.repository.InsufficientSearch
import com.kay.forecast.repository.WeatherRepo
import com.kay.forecast.ui.ForecastViewModel
import com.kay.forecast.ui.UiState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ForecastViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var mockRepo: WeatherRepo

    @MockK
    private lateinit var mockObserver: Observer<UiState<Any>>

    private lateinit var viewModel: ForecastViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        viewModel = ForecastViewModel(
            mockRepo,
            coroutineTestRule.testCoroutineDispatcher
        )
        viewModel.uiStateLiveData.observeForever(mockObserver)
    }

    @After
    fun tearDown() {
        viewModel.uiStateLiveData.removeObserver(mockObserver)
    }

    @Test
    fun `should dispatch Success when repo successfully fetch data`() =
        coroutineTestRule.runBlockingTest {
            val data = fakeForeCastWrapper()
            coEvery { mockRepo.getWeather(allAny()) } coAnswers {
                data
            }
            viewModel.queryCity("saigon")

            verify { mockObserver.onChanged(UiState.Loading) }
            verify { mockObserver.onChanged(UiState.Success(data)) }

        }

    @Test
    fun `should dispatch Insufficient Search when search keyword has less than 3 characters`() {
        viewModel.queryCity("cn")

        val errorSlot = slot<UiState<Any>>()
        verify { mockObserver.onChanged(capture(errorSlot)) }
        val captured = errorSlot.captured
        assertTrue(captured is UiState.Error && captured.exception is InsufficientSearch)

    }

    @Test
    fun `should dispatch Exception  when repo fail to fetch`() = coroutineTestRule.runBlockingTest {
        coEvery { mockRepo.getWeather(allAny()) } throws CityNotFound()

        viewModel.queryCity("hcm")

        val errorSlot = slot<UiState<Any>>()

        verify { mockObserver.onChanged(UiState.Loading) }
        verify { mockObserver.onChanged(capture(errorSlot)) }

        val captured = errorSlot.captured
        assertTrue(captured is UiState.Error && captured.exception is CityNotFound)
    }

}