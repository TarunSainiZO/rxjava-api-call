package com.win95.rxjava

import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable


interface GitHubService {
    @GET("users/{user}/repos")
    fun getStarredRepositories(@Path("user") userName: String?): Observable<List<GitHubRepo?>?>?
}