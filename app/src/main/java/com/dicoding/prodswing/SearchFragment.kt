package com.dicoding.prodswing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment

class SearchFragment : Fragment() {

    private var listener: OnSearchQueryListener? = null

    interface OnSearchQueryListener {
        fun onSearchQuery(query: String)
    }

    fun setOnSearchQueryListener(listener: OnSearchQueryListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragments_search, container, false)

        val searchView: SearchView = rootView.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    listener?.onSearchQuery(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    listener?.onSearchQuery(it)
                }
                return true
            }
        })

        return rootView
    }
}
