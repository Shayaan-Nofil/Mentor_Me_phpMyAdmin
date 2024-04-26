package com.ShayaanNofil.i210450

import Chats
import Mentors
import Messages
import User
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.camera.video.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.util.Base64

class camera_video_mode : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private var imageCapture: ImageCapture? = null
    private var chat: Chats? = Chats()
    private var userid: String = ""
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var isrec: Boolean = false
    private var user : User? = null
    private var server_ip = "http://192.168.18.70//"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_video_mode)

        user = intent.getSerializableExtra("user") as User
        val bundle = intent.extras
        if (bundle != null){
            chat = bundle.getSerializable("chatdata", Chats::class.java)
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listener for take photo button
        val cameraCaptureButton: Button = findViewById(R.id.shutter_button)
        cameraCaptureButton.setOnClickListener {
            if (!isrec) {
                startRecording()
                cameraCaptureButton.setBackgroundResource(R.drawable.recording_icon_red)
            } else {
                cameraCaptureButton.setBackgroundResource(R.drawable.shutter_button_photo)
                stopRecording()
            }
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        var picbutton: Button = findViewById(R.id.picture_button)
        picbutton.setOnClickListener{
            val intent = Intent(this, camera_picture_mode::class.java)
            val bundle = Bundle()
            intent.putExtra("user", user)
            bundle.putSerializable("chatdata", chat)
            intent.putExtras(bundle)
            intent.putExtra("chatdata", chat)
            startActivity(intent)
            finish()
        }

    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(findViewById<PreviewView>(R.id.camera_preview).surfaceProvider)
                }

            // Initialize VideoCapture use case
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun startRecording() {
        val videoCapture = this.videoCapture ?: return

        isrec = true

        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@camera_video_mode,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)

                            uploadVideoToServer(recordEvent.outputResults.outputUri)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                    }
                }
            }
    }

    private fun stopRecording() {
        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            isrec = false
        }
    }

    private fun uploadVideoToServer(uri: Uri) {
        Thread(Runnable {
            var message: Messages = Messages()
            message.time = Calendar.getInstance().time.toString()
            message.senderid = user!!.id.toInt()
            message.senderpic = user!!.profilepic
            message.tag = "video"
            message.chatid = chat!!.id.toInt()

            val requestQueue = Volley.newRequestQueue(this)
            val serverUrl = server_ip + "createvideo_message.php"

            val stringRequest = object : StringRequest(
                com.android.volley.Request.Method.POST,
                serverUrl,
                com.android.volley.Response.Listener { response ->
                    sendNotiftoRecepients(chat!!, message)
                    finish()
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
                    params["video"] = encodeVideoUri(uri)
                    return params
                }
            }
            requestQueue.add(stringRequest)
        }).start()


    }

    private fun encodeVideoUri(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "camera_video_mode"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
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
}