package com.geekbrains.tests.repository

import com.geekbrains.tests.model.SearchResponse
import retrofit2.Response

interface RepositoryCallback {
    fun handleGithubResponse(response: Response<SearchResponse?>?)
    fun handleGithubError()
}