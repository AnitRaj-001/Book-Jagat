package com.example.drawer_navigation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.drawer_navigation.R
import com.example.drawer_navigation.activity.DescriptionActivity
import com.example.drawer_navigation.model.Book
import com.squareup.picasso.Picasso

class AdapterView(val context: Context, val itemList: ArrayList<Book>) :
    RecyclerView.Adapter<AdapterView.DashboardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard, parent, false)
        return DashboardViewHolder(view)
    }
    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder( holder: DashboardViewHolder, position:

    Int) {
        val book = itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtAuthorName.text = book.bookAuthor
        holder.txtBookPrice.text = book.bookPrice
        holder.txtBookRating.text = book.bookRating
       // holder.imgBookImage.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImage)

        holder.llView.setOnClickListener {
           val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id", book.bookId)
            context.startActivity(intent)
        }
    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBookName: TextView = view.findViewById(R.id.bookname)
        val txtAuthorName: TextView = view.findViewById(R.id.Authorname)
        val txtBookPrice: TextView = view.findViewById(R.id.price)
        val txtBookRating: TextView = view.findViewById(R.id.bookRating)
        val imgBookImage: ImageView = view.findViewById(R.id.imgPic)
        val llView: LinearLayout = view.findViewById(R.id.llView)

    }
}