package com.example.mysterio.news

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mysterio.news.newsmodel.ArticlesItem
import com.example.mysterio.news.newsmodel.NewsResponse
import com.example.mysterio.databinding.FragmentNewsBinding

class NewsFragment : Fragment(),NewsClicked {
    private lateinit var viewModel: NewsViewModel
    lateinit var newsResponse :NewsResponse
    private lateinit var mBinding: FragmentNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentNewsBinding.inflate(inflater, container, false)
        initview()
        subscribeNewsData()
        return mBinding.root
    }

    private fun subscribeNewsData() {
        viewModel.myResponse.observe(viewLifecycleOwner) {
             newsResponse = it
             val articles = newsResponse.articles
            if(articles?.size!! > 0){
                setupView(articles)
            }
        }
    }

    private fun setupView(articles: ArrayList<ArticlesItem?>) {
        val recyclerView = mBinding.rvNews
        recyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        val adapter = NewsAdapter(requireActivity().applicationContext,articles,this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initview() {
        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        viewModel.getNews("")
        mBinding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i("TAG", "onQueryTextSubmit: $query")
                val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(mBinding.search.windowToken, 0)
                mBinding.search.clearFocus()
                viewModel.getNews(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    override fun getnewsData(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)

    }
}