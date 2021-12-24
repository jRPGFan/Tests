package com.geekbrains.tests.presenter.search

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.repository.RepositoryCallback
import com.geekbrains.tests.presenter.RepositoryContract
import com.geekbrains.tests.presenter.rx.SchedulerProvider
import com.geekbrains.tests.presenter.rx.SearchSchedulerProvider
import com.geekbrains.tests.view.search.ViewSearchContract
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import retrofit2.Response

internal class SearchPresenter internal constructor(
    var viewContract: ViewSearchContract?,
    private val repository: RepositoryContract,
    private val appSchedulerProvider: SchedulerProvider = SearchSchedulerProvider()
) : PresenterSearchContract, RepositoryCallback {

    override fun searchGitHub(searchQuery: String) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.searchGithub(searchQuery)
                .subscribeOn(appSchedulerProvider.io())
                .observeOn(appSchedulerProvider.ui())
                .doOnSubscribe { viewContract?.displayLoading(true) }
                .doOnTerminate { viewContract?.displayLoading(false) }
                .subscribeWith(object : DisposableObserver<SearchResponse>() {
                    override fun onNext(t: SearchResponse) {
                        val searchResults = t.searchResults
                        val totalCount = t.totalCount
                        if (searchResults != null && totalCount != null) {
                            viewContract?.displaySearchResults(searchResults, totalCount)
                        } else {
                            viewContract?.displayError("Search results or total count are null")
                        }
                    }

                    override fun onError(e: Throwable) {
                        viewContract?.displayError(e.message ?: "Response is null or unsuccessful")
                    }

                    override fun onComplete() {}
                })
        )
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

    override fun onAttach() {}
    override fun onDetach() {
        viewContract = null
    }
}
