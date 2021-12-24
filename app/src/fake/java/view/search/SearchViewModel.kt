package view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.RepositoryContract
import com.geekbrains.tests.presenter.rx.SchedulerProvider
import com.geekbrains.tests.presenter.rx.SearchSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import repository.FakeGithubRepository

class SearchViewModel(
    private val repository: RepositoryContract = FakeGithubRepository(),
    private val appSchedulerProvider: SchedulerProvider = SearchSchedulerProvider()
) : ViewModel() {
    private val _liveData = MutableLiveData<ScreenState>()
    private val liveData: LiveData<ScreenState> = _liveData

    fun subscribeToLiveData() = liveData
    fun searchGithub(searchQuery: String) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.searchGithub(searchQuery)
                .subscribeOn(appSchedulerProvider.io())
                .observeOn(appSchedulerProvider.ui())
                .doOnSubscribe { _liveData.value = ScreenState.Loading }
                .subscribeWith(object : DisposableObserver<SearchResponse>() {
                    override fun onNext(t: SearchResponse) {
                        val searchResults = t.searchResults
                        val totalCount = t.totalCount
                        if (searchResults != null && totalCount != null)
                            _liveData.value = ScreenState.Working(searchResponse = t)
                        else
                            _liveData.value =
                                ScreenState.Error(Throwable("Search results or total count are null"))
                    }

                    override fun onError(e: Throwable) {
                        _liveData.value =
                            ScreenState.Error(Throwable(e.message ?: "Response is null or unsuccessful"))
                    }

                    override fun onComplete() {}
                })
        )
    }
}

sealed class ScreenState {
    object Loading : ScreenState()
    data class Working(val searchResponse: SearchResponse) : ScreenState()
    data class Error(val error: Throwable) : ScreenState()
}