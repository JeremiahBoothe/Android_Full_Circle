package com.armstrongindustries.jbradio

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerControlView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.armstrongindustries.jbradio.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * MainActivity serves as the entry point for the application,
 * initializing necessary components and managing audio playback functionality.
 *
 * @author Jeremiah Boothe
 * @date 10/06/2024
 */
@UnstableApi
class MainActivity : AppCompatActivity() {

    private lateinit var playerControlView: PlayerControlView
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var workManagerUtil: WorkManagerUtil
    private lateinit var exoPlayerManager: ExoPlayerLifecycleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePlayerControlView()
        initializeComponents()
        setupViewModelObservers()
        requestNotificationPermission()
        setupNavigation()
    }

    private fun initializePlayerControlView() {
        playerControlView = findViewById(R.id.player_control_view)
        playerControlView.showTimeoutMs = 0 // Keep control view visible
    }

    private fun initializeComponents() {
        permissionHandler = PermissionHandler(this) {
            workManagerUtil.scheduleMetadataFetching()
        }

        workManagerUtil = WorkManagerUtil(this)
        exoPlayerManager = ExoPlayerLifecycleManager(this, playerControlView)
    }

    private fun requestNotificationPermission() {
        permissionHandler.requestNotificationPermission()
        permissionHandler.onPermissionResult = { granted ->
            if (!granted) {
                showNotificationPermissionToast()
            }
        }
    }

    private fun showNotificationPermissionToast() {
        Toast.makeText(this, "Notification permission is required for full functionality.", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        exoPlayerManager.start() // Start the ExoPlayer
    }

    override fun onStop() {
        exoPlayerManager.stop() // Stop the ExoPlayer
        super.onStop()
    }

    private fun setupViewModelObservers() {
        val activityViewModel = ViewModelProvider(this)[ActivityViewModel::class.java]
        activityViewModel.id.observe(this) { id ->
            exoPlayerManager.updateId(id)
        }
        activityViewModel.artist.observe(this) { artist ->
            exoPlayerManager.updateArtist(artist)
        }
        activityViewModel.title.observe(this) { title ->
            exoPlayerManager.updateTitle(title)
        }
        activityViewModel.album.observe(this) { album ->
            exoPlayerManager.updateAlbum(album)
        }
        activityViewModel.artwork.observe(this) { artwork ->
            exoPlayerManager.updateArtwork(artwork)
        }
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_song)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
