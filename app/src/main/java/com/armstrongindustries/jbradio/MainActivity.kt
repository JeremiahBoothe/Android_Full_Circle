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
import com.armstrongindustries.jbradio.service.AudioPlayerService

/**
 * Entry point for the application, managing UI interactions and service binding.
 * @author Jeremiah Boothe
 * @date 10/06/2024
 */
@UnstableApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var intentService: Intent
    private var serviceBinder: AudioPlayerService.AudioPlayerServiceBinder? = null
    private var isServiceBound = false

    private val requestNotificationPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startServiceAndBind()
            } else {
                showToast("Notification permission is required for full functionality")
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

        setupPlayerControlView()
        setupViewModelObservers()
        requestNotificationPermission()
    }

    private fun setupPlayerControlView() {
        findViewById<PlayerControlView>(R.id.player_control_view).showTimeoutMs = 0
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
        val activityViewModel = ViewModelProvider(this)[ActivityViewModel::class.java]
        observeLiveData(activityViewModel.id, _idLiveData)
        observeLiveData(activityViewModel.artist, _artistLiveData)
        observeLiveData(activityViewModel.title, _titleLiveData)
        observeLiveData(activityViewModel.album, _albumTitleLiveData)
        observeLiveData(activityViewModel.artwork, _artworkLiveData)
    }

    private fun <T> observeLiveData(source: LiveData<T>, target: MutableLiveData<T>) {
        source.observe(this) { value -> target.postValue(value) }
    }

    private val serviceConnector = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is AudioPlayerService.AudioPlayerServiceBinder) {
                serviceBinder = service
                bindServiceLiveData()
                setupNavigation()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBinder = null
            showToast("Audio player service disconnected")
        }
    }

    private fun bindServiceLiveData() {
        serviceBinder?.let { binder ->
            observeLiveData(binder.getArtistMetaData(), _artistLiveData)
            observeLiveData(binder.getTitleLiveData(), _titleLiveData)
            observeLiveData(binder.getAlbumTitleMetaData(), _albumTitleLiveData)
            observeLiveData(binder.getArtworkMetaData(), _artworkLiveData)
        }
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        if (isServiceBound) {
            unbindService(serviceConnector)
            isServiceBound = false
        }
        stopService(intentService)
    }
}
