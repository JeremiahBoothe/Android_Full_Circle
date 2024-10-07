package com.armstrongindustries.jbradio.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import com.armstrongindustries.jbradio.ActivityViewModel
import com.armstrongindustries.jbradio.databinding.FragmentHomeBinding
import com.armstrongindustries.jbradio.ui.service.AudioPlayerService

/**
 * @author Jeremiah Boothe
 * @version 1.0
 * @since 2023-09-21
 * @see HomeViewModel
 * @see AudioPlayerService
 * @see FragmentHomeBinding
 * @see HomeFragment
 * @see UnstableApi
 * @see Util
 * @see Intent
 * @see ComponentName
 */
@UnstableApi
class HomeFragment : Fragment() {
    private val _idLiveData = MutableLiveData<Int>()
    private val _artistLiveData = MutableLiveData<String>()
    private val _titleLiveData = MutableLiveData<String>()
    private val _albumTitleLiveData = MutableLiveData<String>()
    private val _artworkLiveData = MutableLiveData<String>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var intentService: Intent
    private lateinit var serviceBinder: AudioPlayerService.AudioPlayerServiceBinder

    /**
     * @see ServiceConnection
     */
    private val serviceConnection = object : ServiceConnection {
        /**
         * @param name ComponentName?
         * @param service IBinder?
         * @see ServiceConnection.onServiceConnected
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is AudioPlayerService.AudioPlayerServiceBinder) {
                serviceBinder = service
                binding.basicAudioPlayerWithNotificationPlayerView.player = serviceBinder.getExoPlayerInstance()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }
    private fun setupViewModelObservers() {
        val homeViewModel = ViewModelProvider(this@HomeFragment)[ActivityViewModel::class.java]

        homeViewModel.id.observe(viewLifecycleOwner) { _idLiveData.value = it }
        homeViewModel.artist.observe(viewLifecycleOwner) { _artistLiveData.value = it }
        homeViewModel.title.observe(viewLifecycleOwner) { _titleLiveData.value = it }
        homeViewModel.album.observe(viewLifecycleOwner) { _albumTitleLiveData.value = it }
        homeViewModel.artwork.observe(viewLifecycleOwner) { _artworkLiveData.value = it }

    }
    /**
     * @param inflater LayoutInflater
     * @param container ViewGroup?
     * @param savedInstanceState Bundle?
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
        setupViewModelObservers()

    }

    /**
     * @param view View
     * @param savedInstanceState Bundle?
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        intentService = Intent(requireContext(), AudioPlayerService::class.java)

        Util.startForegroundService(requireContext(), intentService)

        val textView: TextView = binding.basicAudioPlayerArtistNameView
        viewModel.artist.observe(viewLifecycleOwner) {
            textView.text = it
        }
    }

    override fun onStart() {
        super.onStart()
        requireContext()
            .bindService(
                intentService,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
    }

    override fun onStop() {
        requireContext()
            .unbindService(serviceConnection)
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
