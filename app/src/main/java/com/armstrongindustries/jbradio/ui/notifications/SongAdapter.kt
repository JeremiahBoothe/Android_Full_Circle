package com.armstrongindustries.jbradio.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armstrongindustries.jbradio.R
import com.armstrongindustries.jbradio.data.Song

class SongAdapter(private val songs: List<Song>, private val onClick: (Song) -> Unit) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    // Implement the ViewHolder and required methods...

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song) {
            // Bind your song data to views
            itemView.setOnClickListener { onClick(song) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size
}
