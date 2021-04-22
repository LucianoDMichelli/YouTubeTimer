package com.example.youtubetimer

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SearchPage : AppCompatActivity() {


    private lateinit var svSearch: SearchView
    private lateinit var rvVideoList: RecyclerView
    private lateinit var ivLogo: ImageView

    private lateinit var videoListAdapter: VideoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_page)

        svSearch = findViewById(R.id.svSearch)
        rvVideoList = findViewById(R.id.rvVideoList)
        ivLogo = findViewById(R.id.ivLogo)

        var videoList: MutableList<VideoResult> = mutableListOf()

        // Setting up RecyclerView
        videoListAdapter = VideoListAdapter(videoList)
        val layoutManager = LinearLayoutManager(applicationContext)
        rvVideoList.layoutManager = layoutManager
        rvVideoList.itemAnimator = DefaultItemAnimator()
        rvVideoList.adapter = videoListAdapter

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                // Reset videoList for each search
                videoList.clear()

                // Avoids android.os.NetworkOnMainThreadException
                val thread = Thread {
                    try {
                        SearchAndCommentFunctions.generateSearch(query, videoList, context = this@SearchPage)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                thread.start()
                // Wait for thread to finish before continuing so that videoList is filled before notifyDataSetChanged()
                thread.join()
                // Can't be inside thread or we get android.view.ViewRootImpl$CalledFromWrongThreadException
                ivLogo.visibility = View.GONE
                videoListAdapter.notifyDataSetChanged()

                return false
            }
        })
    }
}