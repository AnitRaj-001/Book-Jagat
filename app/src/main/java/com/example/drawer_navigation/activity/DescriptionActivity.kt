package com.example.drawer_navigation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.drawer_navigation.R
import com.example.drawer_navigation.database.BookDatabase
import com.example.drawer_navigation.database.BookEntities
import com.example.drawer_navigation.util.ConnectionManager
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtAuthorName: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookPic: ImageView
    lateinit var txtBookDescription: TextView
    lateinit var bookProgressBar: ProgressBar
    lateinit var btnAddFavourite: Button
    lateinit var progressLayoutBookDetails: RelativeLayout
    lateinit var toolbar: Toolbar

    var bookId: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtAuthorName = findViewById(R.id.txtAuthorName)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookPic = findViewById(R.id.bookPic)
        txtBookDescription = findViewById(R.id.txtBookDetails)
        bookProgressBar = findViewById(R.id.bookProgressBar)
        bookProgressBar.visibility = View.VISIBLE
        btnAddFavourite = findViewById(R.id.btnAddFavourite)
        progressLayoutBookDetails = findViewById(R.id.progressLayoutBookDetails)
        progressLayoutBookDetails.visibility = View.VISIBLE

        toolbar = findViewById(R.id.booktoolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occurred ",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occurred ",
                Toast.LENGTH_SHORT
            ).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val urlLink = "http://13.235.250.119/v1/book/get_book/"

        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)) {

            val jsonParams = JSONObject()
            jsonParams.put("book_id", bookId)

            val jsonRequest =
                @SuppressLint("SetTextI18n")
                object : JsonObjectRequest(
                   com.android.volley.Request.Method.POST,
                    urlLink,
                    jsonParams,
                    Listener {
                        try {
                            val success = it.getBoolean("success")
                            if (success) {
                                val bookJsonObject = it.getJSONObject("book_data")
                                progressLayoutBookDetails.visibility = View.GONE

                                val bookImageURl = bookJsonObject.getString("image")
                                Picasso.get().load(bookJsonObject.getString("image"))
                                    .error(R.drawable.default_book_cover).into(imgBookPic)
                                txtBookName.text = bookJsonObject.getString("name")
                                txtAuthorName.text = bookJsonObject.getString("author")
                                txtBookRating.text = bookJsonObject.getString("rating")
                                txtBookPrice.text = bookJsonObject.getString("price")
                                txtBookDescription.text = bookJsonObject.getString("description")

                                val bookEntities = BookEntities(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtAuthorName.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDescription.text.toString(),
                                txtBookName.text.toString(),
                                bookImageURl
                                )

                                val checkFav = DBASyncTask(applicationContext, bookEntities ,1).execute()
                                val isFav = checkFav.get()

                                if(isFav){
                                   btnAddFavourite.text="Remove From Favourites"
                                    val favColor = ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                                    btnAddFavourite.setBackgroundColor(favColor)
                                } else{
                                    btnAddFavourite.text="Add to Favourites"
                                    val nofavColor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                    btnAddFavourite.setBackgroundColor(nofavColor)
                                }
                                btnAddFavourite.setOnClickListener{
                                    if (!DBASyncTask(applicationContext,bookEntities,1).execute().get()){
                                        val async = DBASyncTask(applicationContext,bookEntities,2).execute()
                                        val result = async.get()
                                        if (result){
                                            Toast.makeText(this@DescriptionActivity,"Book Add to Favourite",Toast.LENGTH_SHORT).show()

                                            btnAddFavourite.text="Remove From Favourite"
                                            val favColor = ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                                            btnAddFavourite.setBackgroundColor(favColor)
                                        }
                                        else{
                                            Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        val async = DBASyncTask(applicationContext,bookEntities,3).execute()
                                        val result = async.get()
                                        if (result){
                                            Toast.makeText(this@DescriptionActivity,"Book Removed from Favourite",Toast.LENGTH_SHORT).show()

                                            btnAddFavourite.text="Add to Favourite"
                                            val nofavColor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                            btnAddFavourite.setBackgroundColor(nofavColor)
                                        }
                                        else{
                                            Toast.makeText(this@DescriptionActivity,"Some Error Occurred",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                            }
                            else {
                                Toast.makeText(
                                    this@DescriptionActivity,
                                    "some error occur here !!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "some exception occur here !!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    ErrorListener {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Volley Error $it",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "8c04f01ae21973"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        }else{
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("ERROR")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Setting") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }
    class DBASyncTask (val context: Context,val bookEntities: BookEntities,val mode:Int): AsyncTask<Void,Void,Boolean>(){
        /*
        Mode 1 -> Check DB id Book is favourite or not
        Mode 2 -> Insert the book in DB as Favourite
        Mode 3->  Delete Book from DB
         */
        val db = Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode) {
                1 ->{
                    val book: BookEntities? = db.bookDao().getBookById(bookEntities.book_id.toString())
                    db.close()
                    return book != null
                }
                2 ->{
                    db.bookDao().insertBook(bookEntities)
                    db.close()
                    return true
                }
                3 ->{
                    db.bookDao().deleteBook(bookEntities)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}