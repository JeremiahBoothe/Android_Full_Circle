package com.armstrongindustries.jbradio

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerControlView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.armstrongindustries.jbradio.databinding.ActivityMainBinding
import com.armstrongindustries.jbradio.ui.service.AudioPlayerService
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Entry point for the application, initializing the database to support app functionality.
 * @author Jeremiah Boothe
 * @date 10/06/2024
 * @param
 */
@UnstableApi
class MainActivity : AppCompatActivity() {

    private lateinit var playerControlView: PlayerControlView
    private lateinit var binding: ActivityMainBinding
    private lateinit var intentService: Intent
    private var serviceBinder: AudioPlayerService.AudioPlayerServiceBinder? = null

    private var isServiceBound = false
    private val requestNotificationPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startServiceAndBind()
            } else {
                Toast.makeText(this, "Notification permission is required for full functionality", Toast.LENGTH_SHORT).show()
            }
        }

    private val _idLiveData = MutableLiveData<Int>()
    private val _artistLiveData = MutableLiveData<String>()
    private val _titleLiveData = MutableLiveData<String>()
    private val _albumTitleLiveData = MutableLiveData<String>()
    private val _artworkLiveData = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intentService = Intent(this, AudioPlayerService::class.java)

        setupViewModelObservers()
        playerControlView = findViewById(R.id.player_control_view)
        playerControlView.showTimeoutMs = 0


        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startServiceAndBind()
        }
    }

    private fun startServiceAndBind() {
        if (!isServiceBound) {
            startForegroundService(intentService)
            bindService(intentService, serviceConnector, BIND_AUTO_CREATE)
        }
    }

    private fun setupViewModelObservers() {
        val activityViewModel = ViewModelProvider(this@MainActivity)[ActivityViewModel::class.java]

        activityViewModel.id.observe(this@MainActivity) { _idLiveData.value = it }
        activityViewModel.artist.observe(this@MainActivity) { _artistLiveData.value = it }
        activityViewModel.title.observe(this@MainActivity) { _titleLiveData.value = it }
        activityViewModel.album.observe(this@MainActivity) { _albumTitleLiveData.value = it }
        activityViewModel.artwork.observe(this@MainActivity) { _artworkLiveData.value = it }
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        )

        serviceBinder?.getExoPlayerInstance()?.let {
            playerControlView.player = it
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun observeServiceLiveData(liveData: LiveData<String>, targetLiveData: MutableLiveData<String>) {
        liveData.observe(this) { value -> targetLiveData.postValue(value) }
    }

    private val serviceConnector = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBinder = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is AudioPlayerService.AudioPlayerServiceBinder) {
                serviceBinder = service

                serviceBinder?.apply {
                    observeServiceLiveData(getArtistMetaData(), _artistLiveData)
                    observeServiceLiveData(getTitleLiveData(), _titleLiveData)
                    observeServiceLiveData(getAlbumTitleMetaData(), _albumTitleLiveData)
                    observeServiceLiveData(getArtworkMetaData(), _artworkLiveData)
                }

                setupNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isServiceBound) {
            bindService(intentService, serviceConnector, BIND_AUTO_CREATE)
            isServiceBound = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnector)
            isServiceBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(intentService)
    }
}
