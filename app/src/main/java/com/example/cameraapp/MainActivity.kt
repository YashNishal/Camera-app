package com.example.cameraapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var camera: Camera
    lateinit var preview: Preview
    lateinit var imageCapture : ImageCapture
    lateinit var cameraSelector: CameraSelector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),0)
        }
        captureBtn.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val photofile = File(externalMediaDirs.firstOrNull(),"CameraApp -${System.currentTimeMillis()}.jpg")
        val output = ImageCapture.OutputFileOptions.Builder(photofile).build()

        imageCapture.takePicture(output,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(applicationContext,"Image Saved",Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(this,"Please accept the permission",Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()
            preview.setSurfaceProvider(cameraView.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()
            cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            cameraProvider.unbindAll()
            camera=cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
        },ContextCompat.getMainExecutor(this))
    }
}