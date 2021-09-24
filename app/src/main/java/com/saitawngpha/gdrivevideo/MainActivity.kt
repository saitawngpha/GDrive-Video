package com.saitawngpha.gdrivevideo

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val frameVideo = "<html><body>Video From GDrive<br><iframe width=\"100%\"height=\"40%\"  src=\"https://drive.google.com/file/d/1At2Cp-Z79iqnYCT7C1IcQulEsgTUWuLr/preview\"frameborder=\"0\" allowfullscreen></iframe></body></html>"
    private var mPlayer: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private val hlsUrl = "https://taidrive.000webhostapp.com/gdfetch.php?url=https://drive.google.com/file/d/1At2Cp-Z79iqnYCT7C1IcQulEsgTUWuLr/preview"
    //https://taidrive.000webhostapp.com/gdfetch.php?url=
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //no screenshot
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        //keep screen on while playing
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)

    }

    private fun initPlayer() {
        mPlayer = SimpleExoPlayer.Builder(this).build()
        // Bind the player to the view.
        playerView.player = mPlayer
        mPlayer!!.playWhenReady = true
        mPlayer!!.seekTo(playbackPosition)
        mPlayer!!.prepare(buildMediaSource(), false, false)

    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || mPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }


    private fun releasePlayer() {
        if (mPlayer == null) {
            return
        }
        playWhenReady = mPlayer!!.playWhenReady
        playbackPosition = mPlayer!!.currentPosition
        currentWindow = mPlayer!!.currentWindowIndex
        mPlayer!!.release()
        mPlayer = null
    }

    private fun buildMediaSource(): MediaSource {
        val userAgent =
                Util.getUserAgent(playerView.context, playerView.context.getString(R.string.app_name))

        //val dataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                userAgent,
                null /* listener */,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
        )

        val dataSourceFactory = DefaultDataSourceFactory(
                this,
                null /* listener */,
                httpDataSourceFactory
        )
        //val hlsMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(hlsUrl))
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(hlsUrl)))

        return mediaSource
    }



}