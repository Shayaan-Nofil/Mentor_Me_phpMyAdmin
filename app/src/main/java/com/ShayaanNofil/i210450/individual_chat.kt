package com.ShayaanNofil.i210450

import Chats
import Mentors
import Messages
import User
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chat_recycle_adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import org.json.JSONObject
import android.content.pm.PackageManager
import android.util.Base64
import android.widget.ImageView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.ByteArrayOutputStream

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
private lateinit var username: String
class individual_chat : AppCompatActivity() {
    private var messages: Messages? = null
    private var messageimg: Uri? = null
    private var uploadimgbt : ImageButton? = null
    private var chat : Chats? = null
    private var user : User? = null
    private var screenshot: Boolean = false
    private var recording : Boolean = false
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var server_ip = "http://192.168.18.70//"
    var img: Bitmap?=null
    private var typeofuser : String = "user"

    @SuppressLint("SimpleDateFormat", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_chat)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        chat = intent.getSerializableExtra("object") as Chats
        user = intent.getSerializableExtra("user") as User
        typeofuser = intent.getStringExtra("typeofuser").toString()

        //Screenshots
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        contentResolver.registerContentObserver(uri, true, screenshotObserver)
        askNotificationPermission()

        var chattername : TextView = findViewById(R.id.chatter_name)
        mAuth = Firebase.auth
        //Get the appropriate name of the chatter
        if (chat!!.username == user!!.name){
            chattername.text = chat!!.mentorname
        }
        else{
            chattername.text = chat!!.username
        }

        displaymessages()

        val btsend: Button = findViewById(R.id.btsend)
        btsend.setOnClickListener(View.OnClickListener {
            uploadmessage()
            screenshot = false
        })

        val backbutton: Button = findViewById(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val voicebutton=findViewById<View>(R.id.voicecall_button)
        voicebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, voice_call::class.java )
            val bundle = Bundle()
            bundle.putSerializable("chatdata", chat)
            bundle.putString("recieverid", chat!!.id)
            temp.putExtras(bundle)
            startActivity(temp)
        })

        val videobutton=findViewById<View>(R.id.videocall_button)
        videobutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, call_screen_simple::class.java )
            temp.putExtra("user", user)
            startActivity(temp)
        })

        //Camera picture
        val uppic=findViewById<View>(R.id.btcamera)
        uppic.setOnClickListener(View.OnClickListener {
            screenshot = true
            val temp = Intent(this, camera_picture_mode::class.java )
            val bundle = Bundle()
            bundle.putSerializable("chatdata", chat)
            temp.putExtra("user", user)
            temp.putExtras(bundle)
            startActivity(temp)
            screenshot = false
        })

        //Voice Recording
        val vcrecbutton: ImageButton = findViewById(R.id.btvcrec)
        vcrecbutton.setOnClickListener() {
            if (!recording){
                recording = true
                vcrecbutton.setBackgroundResource(R.drawable.voice_icon_red)
                startRecording()
            }
            else {
                recording = false
                vcrecbutton.setBackgroundResource(R.drawable.mic_icon_white)
                stopRecording()
                uploadAudio()
                screenshot = false
            }
        }

        val task_homebutton=findViewById<View>(R.id.bthome)
        task_homebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, home_page::class.java )
            temp.putExtra("user", user)
            startActivity(temp)
        })

        val task_searchbutton=findViewById<View>(R.id.btsearch)
        task_searchbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, Search::class.java )
            temp.putExtra("user", user)
            startActivity(temp)
        })

        val task_chatbutton=findViewById<View>(R.id.btchat)
        task_chatbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, chats_page::class.java )
            temp.putExtra("user", user)
            startActivity(temp)
        })

        val task_profilebutton=findViewById<View>(R.id.btprofile)
        task_profilebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, profile_page::class.java )
            temp.putExtra("user", user)
            startActivity(temp)
        })

        uploadimgbt=findViewById(R.id.btattcontent)
        uploadimgbt!!.setOnClickListener(View.OnClickListener {
            messages = Messages()
            openGalleryForImage()
        })
    }

    private val galleryprofileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null){
            messages!!.tag = "image"
            messageimg = uri
            img = MediaStore.Images.Media.getBitmap(contentResolver,uri)

            Glide.with(this)
                .asBitmap()
                .load(messageimg)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val bitmapDrawable = BitmapDrawable(this@individual_chat.resources, resource)
                        uploadimgbt!!.background = bitmapDrawable
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle cleanup here
                    }
                })
        }
    }
    private fun openGalleryForImage() {
        galleryprofileLauncher.launch("image/*")
    }
    private fun uploadmessage(){
        var message: Messages = Messages()
        mAuth = Firebase.auth
        if (messages == null){ //Text Messages
            if (screenshot == true){
                message.content = "**User took a screenshot of chat**"
            }
            else{
                val messagecontent : EditText = findViewById(R.id.message_text)
                message.content = messagecontent.text.toString()
                messagecontent.setText("")
            }
            message.time = Calendar.getInstance().time.toString()
            message.senderid = user!!.id.toInt()
            message.senderpic = user!!.profilepic
            message.tag = "text"
            message.chatid = chat!!.id.toInt()

            val serverUrl = server_ip + "create_message.php"
            val requestQueue = Volley.newRequestQueue(this)

            val stringRequest = object : StringRequest(
                Method.POST, serverUrl,
                com.android.volley.Response.Listener<String> { response ->
                    Log.w("TAG", response)
                    displaymessages()
                    sendNotiftoRecepients(chat!!, message)
                },
                com.android.volley.Response.ErrorListener { error ->
                    // Handle error
                    Log.w("TAG", "message send failure")
                    val text = "Didn't work"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this, text, duration) // in Activity
                    toast.show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    val gson = Gson()
                    val userJson = gson.toJson(message)
                    params["message"] = userJson
                    return params
                }
            }
            requestQueue.add(stringRequest)
        }
        else{ //Other messages
            screenshot = false
            messages!!.senderid = user!!.id.toInt()
            messages!!.time = Calendar.getInstance().time.toString()
            message = messages!!
            messages = null
            uploadimgbt!!.background = ContextCompat.getDrawable(this, R.drawable.gallary_icon_white)
            message.senderpic = user!!.profilepic
            message.chatid = chat!!.id.toInt()


            val requestQueue = Volley.newRequestQueue(this)
            val serverUrl = server_ip + "createimage_message.php"

            val stringRequest = object : StringRequest(
                com.android.volley.Request.Method.POST,
                serverUrl,
                com.android.volley.Response.Listener { response ->
                    displaymessages()
                    sendNotiftoRecepients(chat!!, message)
                },
                com.android.volley.Response.ErrorListener { error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                    Log.e("error", error.toString())
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    val gson = Gson()
                    val userJson = gson.toJson(message)
                    params["message"] = userJson
                    params.put("image",bitmapToBase64(img!!))
                    return params
                }
            }
            requestQueue.add(stringRequest)
        }
        Log.w("Message uRI", message.content)
    }

    fun bitmapToBase64(dp:Bitmap):String{
        var stream= ByteArrayOutputStream()
        dp.compress(Bitmap.CompressFormat.JPEG,100,stream)
        stream.toByteArray()
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT).toString()

    }

    private val screenshotObserver = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean, uri: Uri?, userId: Int) {
            super.onChange(selfChange, uri, userId)
            if (!screenshot) {
                screenshot = true
                Log.d("ContentObserver", "Calling uploadmessage()")
                uploadmessage()
            }
        }
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            audioFile = File.createTempFile("audio", ".mp3", externalCacheDir)
            setOutputFile(audioFile!!.absolutePath)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("TAG", "startRecording: ", e)
            }
        }
    }
    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }
    private fun uploadAudio() {
        var message: Messages = Messages()
        message.time = Calendar.getInstance().time.toString()
        message.senderid = user!!.id.toInt()
        message.senderpic = user!!.profilepic
        message.tag = "audio"
        message.chatid = chat!!.id.toInt()

        val requestQueue = Volley.newRequestQueue(this)
        val serverUrl = server_ip + "createaudio_message.php"

        val stringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST,
            serverUrl,
            com.android.volley.Response.Listener { response ->
                displaymessages()
                sendNotiftoRecepients(chat!!, message)
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("error", error.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                val gson = Gson()
                val userJson = gson.toJson(message)
                params["message"] = userJson
                params["audio"] = encodeAudioFile(audioFile!!)
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun encodeAudioFile(file: File): String {
        val bytes = file.readBytes()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun showOptionsDialog(message: Messages) {
        val options = arrayOf("Delete Message", "Edit Message")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Options")
        builder.setItems(options) { dialogInterface: DialogInterface, which: Int ->
            when (which) {
                0 -> {
                    val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
                    val messageTime: Date = originalFormat.parse(message.time)!!
                    val currentTime = Calendar.getInstance().time

                    val diff = currentTime.time - messageTime.time

                    if (diff < 300000) {
                        deletemessage(message)
                    } else {
                        Toast.makeText(this, "You can only delete messages sent within the last 5 minutes", Toast.LENGTH_SHORT).show()
                    }

                }
                1 -> {
                    if (message.tag == "text"){
                        val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
                        val messageTime: Date = originalFormat.parse(message.time)!!
                        val currentTime = Calendar.getInstance().time

                        val diff = currentTime.time - messageTime.time

                        if (diff < 300000) {
                            var btsend: Button = findViewById(R.id.btsend)
                            btsend.isEnabled = false

                            val dialogView = LayoutInflater.from(this).inflate(R.layout.editmessage_layout, null)
                            val builder = AlertDialog.Builder(this).setView(dialogView)
                            val alertDialog = builder.show()

                            val editText = dialogView.findViewById<EditText>(R.id.dialog_edit_text)
                            editText.setText(message.content)

                            dialogView.findViewById<Button>(R.id.dialog_cancel_button).setOnClickListener {
                                alertDialog.dismiss()
                                btsend.isEnabled = true
                            }

                            dialogView.findViewById<Button>(R.id.dialog_done_button).setOnClickListener {
                                message.content = editText.text.toString()
                                editmessage(message)
                                btsend.isEnabled = true
                                editText.setText("")
                                alertDialog.dismiss()
                            }
                        }
                        else{
                            Toast.makeText(this, "You can only edit messages sent within the last 5 minutes", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(this, "You can only edit text messages", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        builder.create().show()
    }

    private fun showOptionsDialogimage(message: Messages) {
        val options = arrayOf("Delete Message", "View Message")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Options")
        builder.setItems(options) { dialogInterface: DialogInterface, which: Int ->
            when (which) {
                0 -> {
                    if (user!!.id.toInt() == message.senderid){
                        val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
                        val messageTime: Date = originalFormat.parse(message.time)!!
                        val currentTime = Calendar.getInstance().time
                        val diff = currentTime.time - messageTime.time

                        if (diff < 300000) {
                            deletemessage(message)
                        } else {
                            Toast.makeText(this, "You can only delete messages sent within the last 5 minutes", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(this, "You can only delete your own messages", Toast.LENGTH_SHORT).show()
                    }
                }
                1 -> {
                    val dialogView = LayoutInflater.from(this).inflate(R.layout.image_fullscreen_viewer, null)
                    val builder = AlertDialog.Builder(this).setView(dialogView)
                    val alertDialog = builder.show()

                    val window = alertDialog.window

                    window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

                    val backbutton = dialogView.findViewById<Button>(R.id.back_button)
                    backbutton.setOnClickListener(View.OnClickListener {
                        alertDialog.dismiss()
                    })

                    val imageView = dialogView.findViewById<ImageView>(R.id.image_displayer)
                    Glide.with(this)
                        .load(message.content)
                        .into(imageView)
                }
            }
        }
        builder.create().show()
    }

    private fun deletemessage(message: Messages){
        val serverUrl = server_ip + "delete_message.php"
        val requestQueue = Volley.newRequestQueue(this)
        Log.w("TAG", "In message")
        val stringRequest = object : StringRequest(
            Method.POST, serverUrl,
            com.android.volley.Response.Listener<String> { response ->
                Log.w("Server said", response)
                displaymessages()
            },
            com.android.volley.Response.ErrorListener { error ->
                // Handle error
                Log.w("TAG", "couldnt delete message")
                val text = "Didn't work"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration) // in Activity
                toast.show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["messageid"] = message.id
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun editmessage(message: Messages){
        val serverUrl = server_ip + "edit_message.php"
        val requestQueue = Volley.newRequestQueue(this)
        Log.w("TAG", "In message")
        val stringRequest = object : StringRequest(
            Method.POST, serverUrl,
            com.android.volley.Response.Listener<String> { response ->
                displaymessages()
            },
            com.android.volley.Response.ErrorListener { error ->
                // Handle error
                Log.w("TAG", "couldnt edit message")
                val text = "Didn't work"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration) // in Activity
                toast.show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["messageid"] = message.id
                params["content"] = message.content
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),)
    { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            askNotificationPermission()
        }
    }
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    fun sendPushNotification(token: String, title: String, subtitle: String, body: String, data: Map<String, String> = emptyMap()) {
        val url = "https://fcm.googleapis.com/fcm/send"
        val bodyJson = JSONObject()
        bodyJson.put("to", token)
        bodyJson.put("notification",
            JSONObject().also {
                it.put("title", title)
                it.put("subtitle", subtitle)
                it.put("body", body)
                it.put("sound", "social_notification_sound.wav")
            }
        )
        Log.d("TAG", "sendPushNotification: ${JSONObject(data)}")
        if (data.isNotEmpty()) {
            bodyJson.put("data", JSONObject(data))
        }

        var key="AAAAD3l6odE:APA91bFv-1TOpuvIoNLlqZxmR6v5-BfI4klzjVRUyak0fXtyIh7KBG76LokhbXDHOgGj8ufINA7kvc1VZGKI1EmGpPjI6N6z5QPPvm2u1K95bBNH37IgR6aHEF0qpSGhe6KxOD_zOEAD"
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "key=$key")
            .post(
                bodyJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            )
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    println("Received data: ${response.body?.string()}")
                    Log.d("TAG", "onResponse: ${response}   ")
                    Log.d("TAG", "onResponse Message: ${response.message}   ")
                }

                override fun onFailure(call: Call, e: IOException) {
                    println(e.message.toString())
                    Log.d("TAG", "onFailure: ${e.message.toString()}")
                }
            }
        )
    }
    fun sendNotiftoRecepients(chats: Chats, message: Messages){

        if (user!!.id.toInt() == chat!!.userid){
            val serverUrl = server_ip + "getmentor_id.php"
            val requestQueue = Volley.newRequestQueue(this)

            val stringRequest = object : StringRequest(
                Method.POST, serverUrl,
                com.android.volley.Response.Listener<String> { response ->
                    // Parse the response from the server
                    if (response != "bad") {
                        // Parse the JSON response into a User object
                        val userJson = JSONObject(response)
                        val mentor = Mentors()
                        mentor.token = userJson.getString("token")

                        sendPushNotification(mentor.token, "New Message " + user!!.name, "New Message from ", message.content, mapOf("chatid" to chat!!.id))
                    }

                },
                com.android.volley.Response.ErrorListener { error ->
                    // Handle error
                    Log.w("TAG", "getting mentor failure", error)
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["mentorid"] = chat!!.mentorid.toString()
                    return params
                }
            }
            requestQueue.add(stringRequest)
        }
        else if (user!!.id.toInt() == chat!!.mentorid){
            val serverUrl = server_ip + "getuserdata.php"
            val requestQueue = Volley.newRequestQueue(this)

            val stringRequest = object : StringRequest(
                Method.POST, serverUrl,
                com.android.volley.Response.Listener<String> { response ->
                    // Parse the response from the server
                    if (response != "bad") {
                        // Parse the JSON response into a User object
                        val userJson = JSONObject(response)
                        val mentor = User()
                        mentor.token = userJson.getString("token")

                        sendPushNotification(mentor.token, "New Message " + user!!.name, "New Message from ", message.content, mapOf("chatid" to chat!!.id))
                    }

                },
                com.android.volley.Response.ErrorListener { error ->
                    // Handle error
                    Log.w("TAG", "getting mentor failure", error)
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["userId"] = chat!!.userid.toString()
                    return params
                }
            }
            requestQueue.add(stringRequest)
        }
    }
    private fun displaymessages(){
        val recycle_topmentor: RecyclerView = findViewById(R.id.messages_recycler)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        recycle_topmentor.layoutManager = layoutManager

        val messagearray: MutableList<Messages> = mutableListOf()

        val serverUrl = server_ip + "get_messages.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST, serverUrl,
            { response ->
                // Parse the JSON response
                val jsonArray = JSONArray(response)

                // Clear the messagerarray
                messagearray.clear()
                // Loop through the JSON array
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    // Create a new Mentor object
                    val mentor = Messages()
                    mentor.id = jsonObject.getInt("id").toString()
                    mentor.senderid = jsonObject.getInt("senderid")
                    mentor.chatid = jsonObject.getInt("chatid")
                    mentor.senderpic = jsonObject.getString("senderpic")
                    mentor.time = jsonObject.getString("time")
                    mentor.content = jsonObject.getString("content")
                    mentor.tag = jsonObject.getString("tag")

                    messagearray.add(mentor)
                }

                val adapter = chat_recycle_adapter(messagearray, user!!, typeofuser)
                recycle_topmentor.adapter = adapter

                adapter.setOnClickListener(object :
                    chat_recycle_adapter.OnClickListener {
                    @SuppressLint("ServiceCast")
                    override fun onClick(position: Int, model: Messages) {
                        if (model.senderid == user!!.id.toInt() && model.tag != "image") {
                            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // For newer APIs
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        100,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                // For older APIs
                                vibrator.vibrate(100)
                            }
                            showOptionsDialog(model)
                        } else if (model.tag == "image") {
                            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // For newer APIs
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        100,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                // For older APIs
                                vibrator.vibrate(100)
                            }
                            showOptionsDialogimage(model)
                        }
                    }
                })
            },
            { error ->
                Log.e("TAG", "Error: ${error.message}", error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["chatid"] = chat!!.id
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}