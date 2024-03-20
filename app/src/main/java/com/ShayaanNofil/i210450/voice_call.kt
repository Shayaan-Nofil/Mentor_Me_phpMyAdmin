package com.ShayaanNofil.i210450

import Chats
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import java.util.*
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

private lateinit var chat: Chats
private lateinit var recieverid: String
private lateinit var revieverimg: String
private lateinit var recievername: String
private lateinit var mAuth: FirebaseAuth
private lateinit var mRtcEngine: RtcEngine
private var callStartedTimeMillis = 0L


class voice_call : AppCompatActivity() {
    private val appID = "def8448e122f467e86b87e4491d50c64"
    private val token = "007eJxTYFgc4/xE/0fo0ffX63cdPJlmedmOqWz2hLWMh77r1x/5YHhKgSElNc3CxMQi1dDIKM3EzDzVwizJwjzVxMTSMMXUINnMRFT5d2pDICODpcknJkYGCATxuRnC8jOTUxWSE3NyihkYAPNDI5I="
    private var calltime: TextView? = null
    protected var agoraEngine: RtcEngine? = null // The RTCEngine instance

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_call)
        calltime = findViewById(R.id.calltime)

        val bundle = intent.extras
        if (bundle != null){
            chat = bundle.getSerializable("chatdata", Chats::class.java)!!
            //revieverimg = bundle.getString("recieverimg")!!
            //recievername = bundle.getString("recievername")!!
        }



        // Initialize the Agora engine
        initializeAgoraEngine()

        // Join the channel with the same id as chat.id
        joinChannel(chat.id)

        val endbutton=findViewById<View>(R.id.end_button)
        endbutton.setOnClickListener(View.OnClickListener {
            leaveChannel()
            finish()
        })
    }

    private fun initializeAgoraEngine() {
        val rtcEventHandler = object : IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                runOnUiThread {
                    Log.w("TAG", "User joined")
                    calltime!!.text = "00:00:00"
                    callStartedTimeMillis = System.currentTimeMillis()
                    updateCallTime()
                }
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                runOnUiThread {
                    calltime!!.text = "Disconnected"
                }
            }
        }

        mRtcEngine = RtcEngine.create(baseContext, appID, rtcEventHandler)
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
    }

    private fun joinChannel(channelid: String) {
        mRtcEngine.joinChannel(token, channelid, "", 0)
        calltime!!.text = "Ringing"
    }

    private fun leaveChannel() {
        mRtcEngine.leaveChannel()
    }

    private fun updateCallTime() {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val callDuration = System.currentTimeMillis() - callStartedTimeMillis
                    val seconds = (callDuration / 1000) % 60
                    val minutes = (callDuration / (1000 * 60)) % 60
                    val hours = (callDuration / (1000 * 60 * 60)) % 24

                    calltime!!.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                }
            }
        }, 0, 1000)
    }
}