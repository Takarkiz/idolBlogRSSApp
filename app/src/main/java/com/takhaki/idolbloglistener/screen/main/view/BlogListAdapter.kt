package com.takhaki.idolbloglistener.screen.main

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.takhaki.idolbloglistener.Model.Article
import com.takhaki.idolbloglistener.R
import java.text.SimpleDateFormat
import java.util.*

class BlogListAdapter(val context: Context) :
    RecyclerView.Adapter<BlogListAdapter.BlogListViewHolder>() {

    var itemList = listOf<Article>()
        set(value) {
            field = value.sortedByDescending { it.date }
            notifyDataSetChanged()
        }

    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.view_article_item, parent, false)
        return BlogListViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BlogListViewHolder, position: Int) {
        val item = itemList[position]

        holder.titleTextView.text = item.title
        holder.contentTextView.text = item.content

        val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.JAPAN)
        holder.dateTextView.text = dateFormat.format(item.date)

        holder.itemView.setOnClickListener {
            listener.onClick(it, Uri.parse(item.url))
        }

        Glide.with(context)
            .load("https://lh3.googleusercontent.com/YO5C66xqdgTeiM_E1-7gVt2VzAKmPrEh09yItK0jA0G3XuSt6dCg6YhJLM-N6BLTuQ=s360-rw")
            .thumbnail(
                Glide
                    .with(context)
                    .load(R.raw.loading)
            )
            .into(holder.thumbnailImageView)

        if (item.isRead) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.readCellColor
                )
            )
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.baseWhiteColor
                )
            )
        }
    }

    interface OnItemClickListener {
        fun onClick(view: View, link: Uri)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class BlogListViewHolder(blogItemView: View) : RecyclerView.ViewHolder(blogItemView) {
        val titleTextView: TextView = blogItemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = blogItemView.findViewById(R.id.contentTextView)
        val dateTextView: TextView = blogItemView.findViewById(R.id.dateTextView)
        val thumbnailImageView: ImageView = blogItemView.findViewById(R.id.pageImageView)
    }

}

