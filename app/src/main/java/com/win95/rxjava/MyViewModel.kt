package com.win95.rxjava

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MyViewModel : ViewModel() {
    val list = MutableLiveData<List<GitHubRepo>>()
    var invalid = MutableLiveData<Boolean>(true)
    lateinit var subscription: Subscription
    override fun onCleared() {
        super.onCleared()
        if (this::subscription.isInitialized && !subscription.isUnsubscribed()) {
            subscription.unsubscribe()
        }
    }

    fun getDataRepo(): List<GitHubRepo> {
        val mutableList = mutableListOf<GitHubRepo>()
        if (list.value != null) {
            for (i in list.value!!) mutableList.add(i)
        }
        return mutableList
    }

    fun getRepo(user: String) {

        subscription = GitHubClient.getInstance()
            .getStarredRepos(user)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<List<GitHubRepo?>?> {
                override fun onCompleted() {
                    Log.d("output in rx", "task completed")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    e.message?.toString()?.let { Log.d("output in rx", it) }
                    invalid.value = false
                    invalid.value = true
                }

                override fun onNext(gitHubRepos: List<GitHubRepo?>?) {
                    Log.d("output in rx", "task OnNext $gitHubRepos")
                    list.value = (gitHubRepos as List<GitHubRepo>?)
                }
            }) ?: return
    }
}