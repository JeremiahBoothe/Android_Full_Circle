package com.armstrongindustries.jbradio.ui.history

import SongPagingAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armstrongindustries.jbradio.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SongFragment : Fragment() {

    private lateinit var viewModel: SongViewModel
    private lateinit var adapter: SongPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song_list, container, false)

        // Set up the RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = GridLayoutManager(requireContext(), 3) // 3 items per row
        recyclerView.layoutManager = layoutManager

        adapter = SongPagingAdapter()
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[SongViewModel::class.java]

        // Observe and submit data to the adapter
        lifecycleScope.launch {
            viewModel.fetchSongs().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        return view
    }
}
