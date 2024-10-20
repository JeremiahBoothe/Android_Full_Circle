package com.armstrongindustries.jbradio.ui.notifications

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.request.ImageRequest
import com.armstrongindustries.jbradio.R
import com.armstrongindustries.jbradio.data.RadioMetaData
import com.armstrongindustries.jbradio.data.Song

class SongAdapter(
    private val songs: List<RadioMetaData>,
    private val onClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songId: TextView = view.findViewById(R.id.song_title)
        val artist: TextView = view.findViewById(R.id.artist_name)
        val title: TextView = view.findViewById(R.id.song_title)
        val album: TextView = view.findViewById(R.id.album_name)
        val artwork: ImageView = view.findViewById(R.id.artwork_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songId.text = song.id.toString()
        holder.artist.text = song.artist.artistName
        holder.title.text = song.title
        holder.album.text = song.album

        // Load image with Coil
        Coil.imageLoader(holder.artwork.context).enqueue(
            ImageRequest.Builder(holder.artwork.context)
                .data(song.getImageDrawable(holder.artwork.context))
                .target(holder.artwork)
                .build()
        )

        // Set click listener
        holder.itemView.setOnClickListener {
            onClick(song)
        }
    }

    private fun onClick(song: RadioMetaData) {
        return

    }

    override fun getItemCount(): Int {
        return songs.size
    }
}
