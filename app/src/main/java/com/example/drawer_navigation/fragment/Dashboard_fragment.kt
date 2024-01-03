package com.example.drawer_navigation.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.drawer_navigation.R
import com.example.drawer_navigation.adapter.AdapterView
import com.example.drawer_navigation.model.Book
import com.example.drawer_navigation.util.ConnectionManager
import org.json.JSONException
import java.util.Collections


class dashboard_fragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: AdapterView
    lateinit var progressLayout :RelativeLayout
    lateinit var progressBar :ProgressBar
   val bookInfoList = arrayListOf<Book>()

    var ratingComparator = Comparator<Book>{book, book2 ->
       if (book.bookRating.compareTo(book2.bookRating,true)==0){
           book.bookRating.compareTo(book2.bookName,true)
       }else{
           book.bookRating.compareTo(book2.bookRating,true)
       }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)  // its only using add menu in fragment because in activity compiler autometically doing this.
        val view = inflater.inflate(R.layout.fragment_dashboard_fragment, container, false)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        progressLayout= view.findViewById(R.id.progressLayout)
        progressBar= view.findViewById(R.id.progressBar)

        progressLayout.visibility=View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"
        if (ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object : JsonObjectRequest(
             Method.GET,
                url,
                null,
                Response.Listener {
                    // here we handle Response
                    try{
                        progressLayout.visibility= View.GONE
                        val success = it.getBoolean("success")
                        if(success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()){
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)
                                recyclerAdapter = AdapterView(activity as Context, bookInfoList)

                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager


                            }
                        }else{
                            Toast.makeText(activity as Context,"some error occur here !!",Toast.LENGTH_SHORT).show()
                        }
                    }catch(e:JSONException){
                        Toast.makeText(activity as Context,"Some Unexpected Error Occurred !!",Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    // here we handle Error
                    if(activity != null){
                        Toast.makeText(activity as Context, "Volley Error occurred!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"]= "8c04f01ae21973"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }else {
            // if Internet is Not Available
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("ERROR")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Setting") { text, listener ->
               val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if (id == R.id.action_sort){
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

}
