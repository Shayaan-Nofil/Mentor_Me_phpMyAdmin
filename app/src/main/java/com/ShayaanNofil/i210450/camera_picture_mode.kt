package com.ShayaanNofil.i210450

import Chats
import Mentors
import Messages
import User
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class camera_picture_mode : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private var imageCapture: ImageCapture? = null
    private var chat: Chats? = Chats()
    private var user : User? = null
    private var server_ip = "http://192.168.68.184//"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_picture_mode)

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
            takePhoto()
        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        var videobutton: Button = findViewById(R.id.video_button)
        videobutton.setOnClickListener{
            val intent = Intent(this, camera_video_mode::class.java)
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

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {


                    // Get the orientation of the image
                    val exif = ExifInterface(photoFile.absolutePath)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )

                    // Decode the saved file into a Bitmap
                    var bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                    // Rotate the Bitmap according to the orientation
                    bitmap = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotate(90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotate(180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotate(270f)
                        else -> bitmap
                    }

                    // Assign the rotated Bitmap to the img variable
                    var savedUri = bitmap

                    // Upload the image to Firestore
                    uploadImageToServer(savedUri)
                }

                // Extension function to rotate a Bitmap
                fun Bitmap.rotate(degrees: Float): Bitmap {
                    val matrix = Matrix()
                    matrix.postRotate(degrees)
                    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
                }
            })
    }

    private fun uploadImageToServer(uri: Bitmap) {
        Thread(Runnable {
            var message: Messages = Messages()
            message.time = Calendar.getInstance().time.toString()
            message.senderid = user!!.id.toInt()
            message.senderpic = user!!.profilepic
            message.tag = "image"
            message.chatid = chat!!.id.toInt()

            val requestQueue = Volley.newRequestQueue(this)
            val serverUrl = server_ip + "createimage_message.php"

            val stringRequest = object : StringRequest(
                com.android.volley.Request.Method.POST,
                serverUrl,
                com.android.volley.Response.Listener { response ->
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
                    params.put("image",bitmapToBase64(uri))
                    return params
                }
            }
            requestQueue.add(stringRequest)

        }).start()

        finish()
    }

    fun bitmapToBase64(dp: Bitmap):String{
        var stream= ByteArrayOutputStream()
        dp.compress(Bitmap.CompressFormat.JPEG,100,stream)
        stream.toByteArray()
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT).toString()

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
        private const val TAG = "camera_picture_mode"
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