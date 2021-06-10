package com.win95.rxjava.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.win95.rxjava.GitHubRepo
import com.win95.rxjava.R

class GithubAdapter(val context: Context, var data: MutableList<GitHubRepo>) :
    RecyclerView.Adapter<GithubAdapter.GitViewHolder>() {

    class GitViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val repoName : TextView
        get() = view.findViewById(R.id.repoName)

        val description : TextView
        get() = view.findViewById(R.id.description)

        val language : TextView
        get() = view.findViewById(R.id.language)

        val stars : TextView
        get() = view.findViewById(R.id.stars)


        fun setDataInUI(githubRepoData : GitHubRepo) {
            repoName.text = githubRepoData.name
            description.text = githubRepoData.description
            language.text = githubRepoData.language
            stars.text = "Stars "+githubRepoData.stargazersCount
        }
    }

    fun setDataInRV(data: List<GitHubRepo>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return GitViewHolder(view)
    }

    override fun onBindViewHolder(holder: GitViewHolder, position: Int) {
        holder.setDataInUI(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}