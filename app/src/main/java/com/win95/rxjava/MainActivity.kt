package com.win95.rxjava

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
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

    val progressBar: ProgressBar
        get() = findViewById(R.id.progressBar)

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
                showProgoressBar()
                username.onEditorAction(EditorInfo.IME_ACTION_DONE);
                getRepo(user.toString())
            }
        }

    }

    private fun getRepo(user: String) {

        subscription = GitHubClient.getInstance()
            .getStarredRepos(user)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnNext { showProgoressBar() }
            ?.subscribe(object : Observer<List<GitHubRepo?>?> {
                override fun onCompleted() {
                    Log.d("output in rx", "task completed")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    e.message?.toString()?.let { Log.d("output in rx", it) }
                    hideProgressBar()
                    githubAdapter.setDataInRV(emptyList())
                    Toast.makeText(this@MainActivity,"Invalid username",Toast.LENGTH_SHORT).show()
                }

                override fun onNext(gitHubRepos: List<GitHubRepo?>?) {
                    githubAdapter.setDataInRV(gitHubRepos as List<GitHubRepo>)
                    Log.d("output in rx", "task OnNext $gitHubRepos")
                    hideProgressBar()
                }
            }) ?: return
    }

    override fun onDestroy() {
        if (this::subscription.isInitialized && !subscription.isUnsubscribed()) {
            subscription.unsubscribe()
        }
        super.onDestroy()
    }

    fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun showProgoressBar() {
        progressBar.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideProgressBar()
    }
}
