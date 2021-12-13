package com.geekbrains.tests.presenter.search

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.repository.RepositoryCallback
import com.geekbrains.tests.presenter.RepositoryContract
import com.geekbrains.tests.view.search.ViewSearchContract
import retrofit2.Response

internal class SearchPresenter internal constructor(
    var viewContract: ViewSearchContract?,
    private val repository: RepositoryContract
) : PresenterSearchContract, RepositoryCallback {

    override fun searchGitHub(searchQuery: String) {
        viewContract?.displayLoading(true)
        repository.searchGithub(searchQuery, this)
    }

    override fun handleGithubResponse(response: Response<SearchResponse?>?) {
        viewContract?.displayLoading(false)
        if (response != null && response.isSuccessful) {
            val searchResponse = response.body()
            val searchResults = searchResponse?.searchResults
            val totalCount = searchResponse?.totalCount
            if (searchResults != null && totalCount != null) {
                viewContract?.displaySearchResults(
                    searchResults,
                    totalCount
                )
            } else {
                viewContract?.displayError("Search results or total count are null")
            }
        } else {
            viewContract?.displayError("Response is null or unsuccessful")
        }
    }

    override fun handleGithubError() {
        viewContract?.displayLoading(false)
        viewContract?.displayError()
    }

    override fun onAttach() { }
    override fun onDetach() { viewContract = null }
}
