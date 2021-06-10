package com.win95.rxjava

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable


class GitHubClient() {
    private val GITHUB_BASE_URL = "https://api.github.com/"

    companion object {
        lateinit var globalInstance: GitHubClient
        fun getInstance(): GitHubClient {
            if (!::globalInstance.isInitialized) {
                globalInstance = GitHubClient()
            }
            return globalInstance
        }

    }

    private var gitHubService: GitHubService? = null

    init {
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        val retrofit = Retrofit.Builder().baseUrl(GITHUB_BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        gitHubService = retrofit.create(GitHubService::class.java)
    }


    fun getStarredRepos(userName: String): Observable<List<GitHubRepo?>?>? {
        return gitHubService!!.getStarredRepositories(userName)
    }

}