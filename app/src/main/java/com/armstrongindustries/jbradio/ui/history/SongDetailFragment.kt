package com.armstrongindustries.jbradio.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import com.armstrongindustries.jbradio.R
import com.armstrongindustries.jbradio.data.Song

class SongDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song_detail, container, false)

        val titleTextView: TextView = view.findViewById(R.id.detail_song_title)
        val artistTextView: TextView = view.findViewById(R.id.detail_song_artist)
        val albumTextView: TextView = view.findViewById(R.id.detail_song_album)
        val artworkImageView: ImageView = view.findViewById(R.id.detail_song_artwork)
        val descriptionTextView: TextView = view.findViewById(R.id.detail_song_description)

        // Get arguments passed from the adapter
        val song = arguments?.getParcelable("song", Song::class.java)

        song?.let {
            titleTextView.text = it.title
            artistTextView.text = it.artist
            albumTextView.text = it.album
            artworkImageView.load(it.artworkUrl) {
                crossfade(true)
                placeholder(R.drawable.sle_radio)
                error(R.drawable.sle_radio)
            }
            descriptionTextView.text = "This is a detailed description of ${it.title}."
        }

        return view
    }
}
