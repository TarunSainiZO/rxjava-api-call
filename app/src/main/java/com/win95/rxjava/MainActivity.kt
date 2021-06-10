package com.win95.rxjava

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.win95.rxjava.adapter.GithubAdapter
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    lateinit var githubAdapter: GithubAdapter
    lateinit var gitHubRepo: List<GitHubRepo>

    val recyclerView: RecyclerView
        get() = findViewById(R.id.recyclerView)

    val search: Button
        get() = findViewById(R.id.search)

    val username: EditText
        get() = findViewById(R.id.username)

    lateinit var subscription: Subscription


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gitHubRepo = arrayListOf()
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        githubAdapter = GithubAdapter(this, gitHubRepo as ArrayList<GitHubRepo>)
        recyclerView.adapter = githubAdapter

        search.setOnClickListener {
            val user = username.text
            if (user.isNotEmpty()) {
                getRepo(user.toString())
            }
        }

    }

    private fun getRepo(user: String) {

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
                }

                override fun onNext(gitHubRepos: List<GitHubRepo?>?) {
                    githubAdapter.setDataInRV(gitHubRepos as List<GitHubRepo>)
                    Log.d("output in rx", "task OnNext $gitHubRepos")
                }
            }) ?: return
    }

    override fun onDestroy() {
        if(this::subscription.isInitialized && !subscription.isUnsubscribed()){
            subscription.unsubscribe()
        }
        super.onDestroy()
    }
}
