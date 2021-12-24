package com.geekbrains.tests

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.stubs.SchedulerProviderStub
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config
import repository.FakeGithubRepository
import view.search.ScreenState
import view.search.SearchViewModel

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SearchViewModelTest {
    private lateinit var searchViewModel: SearchViewModel
    @Mock
    private lateinit var repository: FakeGithubRepository
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchViewModel = SearchViewModel(repository, SchedulerProviderStub())
    }

    @Test
    fun search_Test() {
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(SearchResponse(1, listOf()))
        )
        searchViewModel.searchGithub(SEARCH_QUERY)
        verify(repository, times(1)).searchGithub(SEARCH_QUERY)
    }

    @Test
    fun liveData_TestReturnValueIsNotNull() {
        val observer = Observer<ScreenState> {}
        val liveData = searchViewModel.subscribeToLiveData()
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(SearchResponse(1, listOf()))
        )
        try {
            liveData.observeForever(observer)
            searchViewModel.searchGithub(SEARCH_QUERY)
            Assert.assertNotNull(liveData.value)
        } finally { liveData.removeObserver(observer) }
    }

    @Test
    fun liveData_TestReturnValueIsError() {
        val observer = Observer<ScreenState> {}
        val liveData = searchViewModel.subscribeToLiveData()
        val error = Throwable(ERROR_TEXT)
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(Observable.error(error))
        try {
            liveData.observeForever(observer)
            searchViewModel.searchGithub(SEARCH_QUERY)
            val value: ScreenState.Error = liveData.value as ScreenState.Error
            Assert.assertEquals(value.error.message, error.message)
        } finally { liveData.removeObserver(observer) }
    }

    @Test
    fun liveData_TestTotalCountIsNull_ReturnsError() {
        val observer = Observer<ScreenState> {}
        val liveData = searchViewModel.subscribeToLiveData()
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(SearchResponse(null, listOf()))
        )
        try {
            liveData.observeForever(observer)
            searchViewModel.searchGithub(SEARCH_QUERY)
            val value: ScreenState.Error = liveData.value as ScreenState.Error
            Assert.assertEquals(value.error.message, RESULT_ERROR)
        } finally { liveData.removeObserver(observer) }
    }

    @Test
    fun liveData_SearchResultIsNull_ReturnsError() {
        val observer = Observer<ScreenState> {}
        val liveData = searchViewModel.subscribeToLiveData()
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(SearchResponse(55, null))
        )
        try {
            liveData.observeForever(observer)
            searchViewModel.searchGithub(SEARCH_QUERY)
            val value: ScreenState.Error = liveData.value as ScreenState.Error
            Assert.assertEquals(value.error.message, RESULT_ERROR)
        } finally { liveData.removeObserver(observer) }
    }

    @Test
    fun liveData_TotalCountCorrect_ReturnsData() {
        val observer = Observer<ScreenState> {}
        val liveData = searchViewModel.subscribeToLiveData()
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(SearchResponse(55, listOf()))
        )
        try {
            liveData.observeForever(observer)
            searchViewModel.searchGithub(SEARCH_QUERY)
            val value: ScreenState.Working = liveData.value as ScreenState.Working
            Assert.assertEquals(value.searchResponse.totalCount, 55)
            Assert.assertNotNull(value.searchResponse.searchResults)
        } finally { liveData.removeObserver(observer) }
    }

    companion object {
        private const val SEARCH_QUERY = "some query"
        private const val ERROR_TEXT = "error"
        private const val RESULT_ERROR = "Search results or total count are null"
    }
}