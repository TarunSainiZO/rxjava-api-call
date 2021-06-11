package com.win95.rxjava

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.win95.rxjava.adapter.GithubAdapter


class MainActivity : AppCompatActivity() {

    lateinit var githubAdapter: GithubAdapter
    lateinit var viewModel: MyViewModel
    private val recyclerView: RecyclerView
        get() = findViewById(R.id.recyclerView)

    private val search: Button
        get() = findViewById(R.id.search)

    private val username: EditText
        get() = findViewById(R.id.username)

    private val progressBar: ProgressBar
        get() = findViewById(R.id.progressBar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        githubAdapter = GithubAdapter(this, viewModel.getDataRepo() as MutableList<GitHubRepo>)
        recyclerView.adapter = githubAdapter

        viewModel.list.observe(this,{
            username.setTextColor(Color.BLACK)
            githubAdapter.setDataInRV(it as List<GitHubRepo>)
            hideProgressBar()
        })

        viewModel.invalid.observe(this,{
            if(it == false){
                hideProgressBar()
                githubAdapter.setDataInRV(emptyList())
                username.setTextColor(Color.RED)
                Toast.makeText(this@MainActivity,"Invalid username",Toast.LENGTH_SHORT).show()
            }
        })

        search.setOnClickListener {
            val user = username.text
            if (user.isNotEmpty()) {
                showProgoressBar()
                username.onEditorAction(EditorInfo.IME_ACTION_DONE);
                viewModel.getRepo(user.toString())
            }
        }

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
