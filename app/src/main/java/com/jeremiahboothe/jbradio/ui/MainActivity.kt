package com.jeremiahboothe.jbradio.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.jeremiahboothe.jbradio.databinding.ActivityMainBinding
import com.jeremiahboothe.jbradio.metadata.APIService
import com.jeremiahboothe.jbradio.ui.util.AudioPlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@UnstableApi class MainActivity : AppCompatActivity() {

    private lateinit var intentService: Intent
    private lateinit var serviceBinder: AudioPlayerService.AudioPlayerServiceBinder
    private lateinit var binding: ActivityMainBinding
    /*
    //var parseJson = parseJSON()

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        intentService = Intent(this, AudioPlayerService::class.java)
        Util.startForegroundService(this, intentService)

        /*
        //binding.basicAudioPlayerWithNotificationJsonView.text = ""
        //binding.albumTextView.text = ""
        //binding.skuTextView.text = ""
        //binding.thumbTextView.text = ""
        //binding.artistTextView.text = ""
        */
        parseJSON()

    }
    /*
   // Object expressions create objects of anonymous classes, that is, classes that aren't explicitly
   // declared with the class declaration. Such classes are useful for one-time use.
   // You can define them from scratch, inherit from existing classes, or implement interfaces.
   // Instances of anonymous classes are also called anonymous objects because they are defined by an expression, not a name.
   */
    private val  serviceConnector =
        object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (service is AudioPlayerService.AudioPlayerServiceBinder) {
                    serviceBinder = service

                    binding.basicAudioPlayerWithNotificationPlayerView.player =
                        serviceBinder.getSimpleExoPlayerInstance()

                    serviceBinder.getTitleLiveData()
                        .observe(this@MainActivity) {
                            binding.basicAudioPlayerWithNotificationTextView.text = it
                        }

                    /*serviceBinder.getPrettyJsonTextViewMetaData()
                    //    .observe(this@BasicAudioPlayerWithNotification) {
                    //        binding.basicAudioPlayerWithNotificationJsonView.text = it
                    //    }
                    //serviceBinder.getDescriptionLiveData()
                        //.observe(this@BasicAudioPlayerWithNotification, Observer {
                           // binding.basicAudioPlayerWithNotificationDescriptionView.text = it
                        //})*/
                    //JB working in the JSON text data binding

                    serviceBinder.getDescriptionLiveData()
                        .observe(this@MainActivity) {
                            binding.basicAudioPlayerWithNotificationDescriptionView.text = it
                        }

                    serviceBinder.getArtistMetaData()
                        .observe(this@MainActivity) {
                            binding.artistTextView.text = it
                        }

                    serviceBinder.getAlbumTitleMetaData()
                        .observe(this@MainActivity) {
                            binding.albumTextView.text = it
                        }

                    serviceBinder.getSkuTextViewMetaData()
                        .observe(this@MainActivity) {
                            binding.skuTextView.text = it
                        }

                    serviceBinder.getThumbTextViewMetaData()
                        .observe(this@MainActivity) {
                            binding.thumbTextView.text = it
                        }


                    serviceBinder.getThumbTextViewMetaData()
                        .observe(this@MainActivity) {
                            val link = AudioPlayerService()
                                .AudioPlayerServiceBinder()
                                .getThumbTextViewMetaData()
                                .toString()
                            binding.thumbImageView.load(link) {
                                listener(
                                    onSuccess = { _, _ ->
                                        Snackbar.make(ImageView(this@MainActivity), "Success", Snackbar.LENGTH_SHORT).show()
                                        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()

                                    },
                                    onError = { request: ImageRequest, errorResult: ErrorResult ->
                                        request.error
                                        Toast.makeText(this@MainActivity, "$errorResult", Toast.LENGTH_SHORT).show()
                                        Snackbar.make(ImageView(this@MainActivity), "$errorResult", Snackbar.LENGTH_SHORT).show()

                                    })
                                // setup error image
                               // error(R.drawable.sle_radio)
                                //placeholder(R.drawable.sle_radio)
                                transformations(
                                    CircleCropTransformation()
                                )
                                build()
                            }
                        }



                    //binding coil breaks it JB needs to fix
                    /*serviceBinder.getThumbTextViewMetaData()
                        .observe(this@MainActivity){
                            binding.basicAudioPlayerWithNotificationTextViewImageView.load("https://radiojar-lib.appspot.com/get_media_image?size=orig&guid=f8eef4b4-c480-11eb-acff-fa163eb018f7")
                        }*/

                   /* //not coil
                    serviceBinder.getIconLiveData()
                        .observe(this@MainActivity) {
                            binding.thumbImageView.setImageDrawable(
                                ContextCompat.getDrawable(this@MainActivity, it)
                            )
                        }*/
                }
            }
        }

    override fun onStart() {
        super.onStart()
        bindService( intentService, serviceConnector, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        unbindService(serviceConnector)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun parseJSON() {
        /*// .addConverterFactory(GsonConverterFactory.create()) for Gson converter
        // .addConverterFactory(MoshiConverterFactory.create()) for Moshi converter
        // .addConverterFactory(Json.asConverterFactory("application/json".toMediaType())) for Kotlinx Serialization converter
        // .addConverterFactory(JacksonConverterFactory.create()) for Jackson converter
        */
        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.radiojar.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            // Do the GET request and get response
            val response = service.getMetaData()

            withContext(Dispatchers.IO) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(response.body())
                    Log.d("Pretty Printed JSON :", prettyJson)
                    //binding.basicAudioPlayerWithNotificationJsonView.text = prettyJson

                    // Album
                    val album = response.body()?.album ?: "N/A"
                    Log.d("Album: ", album)
                    binding.albumTextView.text = album

                    // SKU
                    val sku = response.body()?.sku ?: "N/A"
                    Log.d("SKU: ", sku)
                    binding.skuTextView.text = sku

                    // Thumb
                    val thumb = response.body()?.thumb ?: "N/A"
                    Log.d("Thumb: ", thumb)
                    binding.thumbTextView.text = thumb

                    // Employee Age
                    val artist = response.body()?.artist ?: "N/A"
                    Log.d("Artist: ", artist)
                    binding.artistTextView.text = artist

                    //JB Loop causes issues
                    delay(5000)
                    parseJSON()
                } else {
                    Log.e("RETROFIT_ERROR", response.code().toString())
                }
            }
        }
    }
}


