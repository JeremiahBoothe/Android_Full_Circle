package com.armstrongindustries.jbradio.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armstrongindustries.jbradio.R

class SongFragment : Fragment() {
    private lateinit var viewModel: SongViewModel
    private lateinit var adapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_song, container, false)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.song_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get ViewModel
        viewModel = ViewModelProvider(this).get(SongViewModel::class.java)

        // Observe the list of songs and set up adapter
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            adapter = SongAdapter(songs) { song ->
                // Handle item click if needed
            }
            recyclerView.adapter = adapter
        }

        return view
    }
}
