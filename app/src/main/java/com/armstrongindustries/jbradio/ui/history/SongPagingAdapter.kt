import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.armstrongindustries.jbradio.R
import com.armstrongindustries.jbradio.data.Song

class SongPagingAdapter : PagingDataAdapter<Song, SongPagingAdapter.SongViewHolder>(SONG_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        if (song != null) {
            holder.bind(song)
            holder.itemView
                .setOnClickListener {
                // Navigate to SongDetailFragment using NavController
                val bundle = bundleOf("song" to song)
                holder.itemView.findNavController().navigate(R.id.songDetailFragment, bundle)
            }
            holder.itemView.post {
                val width = holder.itemView.width
                val padding = (holder.itemView.paddingLeft + holder.itemView.paddingRight)
                val squareHeight = width - padding // Adjust for padding
                holder.itemView.layoutParams.height = squareHeight // Set height to match width
                holder.itemView.requestLayout()
            }
        }
    }

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.song_title)
        private val artistTextView: TextView = view.findViewById(R.id.song_artist)
        private val artworkImageView: ImageView = view.findViewById(R.id.song_artwork)

        fun bind(song: Song) {
            titleTextView.text = song.title
            artistTextView.text = song.artist
            artworkImageView.load(song.artworkUrl) {
                crossfade(true)
                placeholder(R.drawable.sle_radio)
                error(R.drawable.sle_radio)
            }
        }
    }

    companion object {
        private val SONG_COMPARATOR = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem == newItem
        }
    }
}
