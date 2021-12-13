package repository

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.RepositoryContract
import com.geekbrains.tests.repository.RepositoryCallback
import retrofit2.Response

internal class FakeGithubRepository : RepositoryContract {
    override fun searchGithub(query: String, callback: RepositoryCallback) {
        callback.handleGithubResponse(Response.success(SearchResponse(55, listOf())))
    }
}