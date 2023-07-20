package com.example.mysterio.news

import android.content.Context
import android.graphics.Paint
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.mysterio.R
import com.example.mysterio.news.newsmodel.ArticlesItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewsAdapter(val context: Context, val articles: ArrayList<ArticlesItem?>,val newsClicked:NewsClicked) : RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = articles[position]
        holder.author.text = String.format("%s %s",context.getString(R.string.auther),item?.author?:context.getString(R.string.self))
        holder.date.text = String.format("%s %s",context.getString(R.string.publish_date),convertDateFormat(item?.publishedAt.toString()))
        holder.desc.text = item?.description
        holder.title.text = item?.title
        val htmlText = item?.content
        holder.content.text = Html.fromHtml(htmlText)
        holder.mUrl.text = String.format("%s %s","Read Full Article on ",item?.url)
        holder.mUrl.paintFlags = holder.mUrl.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.mUrl.setOnClickListener {
            newsClicked.getnewsData(item?.url)
        }

        Glide.with(context)
            .load(item?.urlToImage)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.image)
        holder.more.setOnClickListener {
            if (holder.content.isVisible) {
                holder.content.visibility = View.GONE
                holder.mUrl.visibility = View.GONE
                holder.more.text = context.getText(R.string.readmore)
            }else{
                holder.content.visibility = View.VISIBLE
                holder.mUrl.visibility = View.VISIBLE
                holder.more.text = context.getText(R.string.readless)
            }
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun convertDateFormat(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date!!)
    }
}

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val desc = itemView.findViewById(R.id.tv_desc) as AppCompatTextView
    val date = itemView.findViewById(R.id.tv_publish) as AppCompatTextView
    val author = itemView.findViewById(R.id.tv_Auther) as AppCompatTextView
    val title = itemView.findViewById(R.id.tv_title) as AppCompatTextView
    val more = itemView.findViewById(R.id.tv_more) as AppCompatTextView
    val content = itemView.findViewById(R.id.tv_content) as AppCompatTextView
    val image = itemView.findViewById(R.id.iv_image) as AppCompatImageView
    val mUrl = itemView.findViewById(R.id.tv_url) as AppCompatTextView
}

