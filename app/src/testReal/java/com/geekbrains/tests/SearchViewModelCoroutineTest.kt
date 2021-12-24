package com.geekbrains.tests

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.repository.GitHubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config
import view.search.ScreenState
import view.search.SearchViewModel

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
class SearchViewModelCoroutineTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var testCoroutineRule = TestCoroutineRule()
    private lateinit var searchViewModel: SearchViewModel
    @Mock
    private lateinit var repository: GitHubRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchViewModel = SearchViewModel(repository)
    }

    @Test
    fun coroutines_TestReturnValueIsNotNull() {
        testCoroutineRule.runBlockingTest {
            val observer = Observer<ScreenState> {  }
            val liveData = searchViewModel.subscribeToLiveData()
            `when`(repository.searchGithubAsync(SEARCH_QUERY)).thenReturn(SearchResponse(1, listOf()))

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGithub(SEARCH_QUERY)
                Assert.assertNotNull(liveData.value)
            } finally { liveData.removeObserver(observer) }
        }
    }

    @Test
    fun coroutines_TestTotalCountIsNull_ReturnsError() {
        testCoroutineRule.runBlockingTest {
            val observer = Observer<ScreenState> {  }
            val liveData = searchViewModel.subscribeToLiveData()
            `when`(repository.searchGithubAsync(SEARCH_QUERY)).thenReturn(SearchResponse(null, listOf()))

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGithub(SEARCH_QUERY)
                val value: ScreenState.Error = liveData.value as ScreenState.Error
                Assert.assertEquals(value.error.message, RESULTS_ERROR)
            } finally { liveData.removeObserver(observer) }
        }
    }

    @Test
    fun coroutines_TestException() {
        testCoroutineRule.runBlockingTest {
            val observer = Observer<ScreenState> {  }
            val liveData = searchViewModel.subscribeToLiveData()

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGithub(SEARCH_QUERY)
                val value: ScreenState.Error = liveData.value as ScreenState.Error
                Assert.assertEquals(value.error.message, EXCEPTION_TEXT)
            } finally { liveData.removeObserver(observer) }
        }
    }

    @Test
    fun coroutines_TestSearchResultIsNull_ReturnsError() {
        testCoroutineRule.runBlockingTest {
            val observer = Observer<ScreenState> {  }
            val liveData = searchViewModel.subscribeToLiveData()
            `when`(repository.searchGithubAsync(SEARCH_QUERY)).thenReturn(SearchResponse(55, null))

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGithub(SEARCH_QUERY)
                val value: ScreenState.Error = liveData.value as ScreenState.Error
                Assert.assertEquals(value.error.message, RESULTS_ERROR)
            } finally { liveData.removeObserver(observer) }
        }
    }

    @Test
    fun coroutines_TotalCountCorrect_ReturnsData() {
        testCoroutineRule.runBlockingTest {
            val observer = Observer<ScreenState> {  }
            val liveData = searchViewModel.subscribeToLiveData()
            `when`(repository.searchGithubAsync(SEARCH_QUERY)).thenReturn(SearchResponse(55, listOf()))

            try {
                liveData.observeForever(observer)
                searchViewModel.searchGithub(SEARCH_QUERY)
                val value: ScreenState.Working = liveData.value as ScreenState.Working
                Assert.assertEquals(value.searchResponse.totalCount, 55)
                Assert.assertNotNull(value.searchResponse.searchResults)
            } finally { liveData.removeObserver(observer) }
        }
    }

    companion object {
        private const val SEARCH_QUERY = "some query"
        private const val RESULTS_ERROR = "java.lang.Throwable: Search results or total count are null"
        private const val EXCEPTION_TEXT = "Response is null or unsuccessful"
    }
}