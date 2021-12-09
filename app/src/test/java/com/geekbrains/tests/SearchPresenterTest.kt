package com.geekbrains.tests

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.model.SearchResult
import com.geekbrains.tests.presenter.search.SearchPresenter
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.view.search.ViewSearchContract
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

class SearchPresenterTest {

    private lateinit var presenter: SearchPresenter

    @Mock
    private lateinit var repository: GitHubRepository

    @Mock
    private lateinit var viewContract: ViewSearchContract

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = SearchPresenter(viewContract, repository)
    }

    @Test
    fun searchGitHub_Test() {
        val searchQuery = "some query"
        presenter.searchGitHub(searchQuery)
        verify(repository, times(1)).searchGithub(searchQuery, presenter)
    }

    @Test
    fun handleGitHubError_Test() {
        presenter.handleGitHubError()
        verify(viewContract, times(1)).displayError()
    }

    @Test
    fun handleGitHubResponse_ResponseUnsuccessful() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.isSuccessful).thenReturn(false)
        assertFalse(response.isSuccessful)
    }

    @Test
    fun handleGitHubResponse_Failure() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.isSuccessful).thenReturn(false)
        presenter.handleGitHubResponse(response)
        verify(viewContract, times(1))
            .displayError("Response is null or unsuccessful")
    }

    @Test
    fun handleGitHubResponse_ResponseFailure_ViewContractMethodOrder() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.isSuccessful).thenReturn(false)
        presenter.handleGitHubResponse(response)

        val inOrder = inOrder(viewContract)
        inOrder.verify(viewContract).displayLoading(false)
        inOrder.verify(viewContract).displayError("Response is null or unsuccessful")
    }

    @Test
    fun handleGitHubResponse_ResponseIsEmpty() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.body()).thenReturn(null)
        presenter.handleGitHubResponse(response)
        assertNull(response.body())
    }

    @Test
    fun handleGitHubResponse_ResponseIsNotEmpty() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.body()).thenReturn(mock(SearchResponse::class.java))
        presenter.handleGitHubResponse(response)
        assertNotNull(response.body())
    }

    @Test
    fun handleGitHubResponse_EmptyResponse() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.isSuccessful).thenReturn(true)
        `when`(response.body()).thenReturn(null)
        presenter.handleGitHubResponse(response)
        verify(viewContract, times(1))
            .displayError("Search results or total count are null")
    }

    @Test
    fun handleGitHubResponse_Success() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        val searchResponse = mock(SearchResponse::class.java)
        val searchResults = listOf(mock(SearchResult::class.java))
        `when`(response.isSuccessful).thenReturn(true)
        `when`(response.body()).thenReturn(searchResponse)
        `when`(searchResponse.searchResults).thenReturn(searchResults)
        `when`(searchResponse.totalCount).thenReturn(101)
        presenter.handleGitHubResponse(response)
        verify(viewContract, times(1))
            .displaySearchResults(searchResults, 101)
    }
}
