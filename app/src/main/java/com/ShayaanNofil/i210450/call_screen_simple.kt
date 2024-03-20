package com.ShayaanNofil.i210450

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class call_screen_simple : AppCompatActivity() {
   // private lateinit var binding : ActivityCallScreenSimpleBinding

    private val appID = "def8448e122f467e86b87e4491d50c64"
    private  val channelName = "Video call Mentorme"
    private val token = "007eJxTYHjnb5Lv4h/+78iKH1ovM8P6syZ83/797yI2cXuzP4vztz1TYEhJTbMwMbFINTQySjMxM0+1MEuyME81MbE0TDE1SDYzmSn/PbUhkJFBdnMbCyMDBIL4wgxhmSmp+QrJiTk5Cr6peSX5RbmpDAwALP8nBA=="
    private val uid = 0
    private var isJoined = false
    private var agoraEngine : RtcEngine? = null
    private var localSurfaceView : SurfaceView? = null
    private var remoteSurfaceView : SurfaceView? = null


    private val permission_id = 22
    private val requestedpermession = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA)


    private fun checkSelfPermission() : Boolean{
        return !(ContextCompat.checkSelfPermission(this, requestedpermession[0]) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this, requestedpermession[1]
        ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun showMessage(message : String){
        runOnUiThread{
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    protected open fun setupAgoraEngine(): Boolean {
        try {
            // Set the engine configuration
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appID

            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)

            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            showMessage(e.toString())
            return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_screen_simple)

        if (!checkSelfPermission()){
            ActivityCompat.requestPermissions(
                this, requestedpermession, permission_id
            )
        }
        setupAgoraEngine()

        if (checkSelfPermission()){
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurfaceView!!.visibility = VISIBLE
            agoraEngine!!.startPreview()
            agoraEngine!!.joinChannel(token, channelName, uid, option)
        }
        else{
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        }


        val backbutton=findViewById<View>(R.id.end_button)
        backbutton.setOnClickListener(View.OnClickListener {
            leavecall()
            finish()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread{
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun leavecall() {
        if (!isJoined){
            showMessage("Please join a channel first")
        }
        else{
            agoraEngine!!.leaveChannel()
            showMessage("You left the channel")
            if (remoteSurfaceView != null){
                remoteSurfaceView!!.visibility = GONE
            }
            if (localSurfaceView != null){
                localSurfaceView!!.visibility = GONE
            }
        }
    }

    private val mRtcEventHandler : IRtcEngineEventHandler =
        object : IRtcEngineEventHandler(){
            override fun onUserJoined(uid: Int, elapsed: Int) {
                //super.onUserJoined(uid, elapsed)
                showMessage("Remote User joined $uid")
                runOnUiThread { (setupRemoteVideo(uid)) }
            }

            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                //super.onJoinChannelSuccess(channel, uid, elapsed)
                isJoined = true
                showMessage("Joined Channel $channel")
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                //super.onUserOffline(uid, reason)
                showMessage("User offline")
                runOnUiThread{
                    remoteSurfaceView!!.visibility = GONE
                }
            }
        }

    private fun setupRemoteVideo(uid : Int){
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        //binding.remoteUser.addView(remoteSurfaceView)

        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
    }

    private fun setupLocalVideo(){
        localSurfaceView = SurfaceView(baseContext)
        //binding.remoteUser.addView(localSurfaceView)

        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                0
            )
        )
    }
}