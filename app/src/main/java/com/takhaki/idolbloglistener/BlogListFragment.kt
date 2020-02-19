package com.takhaki.idolbloglistener

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.takhaki.idolbloglistener.backgroundTasks.BlogHtmlParser
import com.takhaki.idolbloglistener.databinding.FragmentBlogListBinding
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.fragment_blog_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class BlogListFragment : Fragment() {

    companion object {
        fun newInstance() = BlogListFragment()
    }

    private lateinit var viewModel: BlogListViewModel
    private val adapter: BlogListAdapter by lazy {
        BlogListAdapter(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentBlogListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_blog_list, container, false
        )
        viewModel = ViewModelProviders.of(this).get(BlogListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.blogListViewModel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.adapter = adapter

        viewModel.articleList.observe(viewLifecycleOwner, Observer { articles ->
            if (articles != null) {
                adapter.itemList = articles
            }
        })

        // ローディングステートを監視する
        viewModel.loadingState.observe(viewLifecycleOwner, Observer { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        })

        BlogHtmlParser.getImgUrl("https://ameblo.jp/beyooooonds-rfro/").subscribe(object : DisposableSingleObserver<String>() {
            override fun onSuccess(data: String) {
                Log.d("画像URL", data)
            }

            override fun onError(e: Throwable) {
                Log.d("画像取得失敗", e.localizedMessage!!)
            }

        })

        viewModel.startXmlWorker()
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        adapter.setOnItemClickListener(object : BlogListAdapter.OnItemClickListener {
            override fun onClick(view: View, link: Uri) {
                context?.let { context ->
                    GlobalScope.launch {
                        ArticleDatabase.getInstance(context).articleDao()
                            .getArticles(link.toString())?.let { article ->
                                article.isRead = true
                                ArticleDatabase.getInstance(context).articleDao()
                                    .update(article)

                            }
                    }.start()
                    adapter.notifyDataSetChanged()
                    viewModel.goToBlog(context, link)
                }
            }
        })

        swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                context!!,
                R.color.colorPrimary
            )
        )

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.startXmlWorker()
        }
    }


}
