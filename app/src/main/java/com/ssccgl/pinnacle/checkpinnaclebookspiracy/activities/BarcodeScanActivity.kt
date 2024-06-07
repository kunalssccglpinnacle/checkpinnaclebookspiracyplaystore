
package com.ssccgl.pinnacle.checkpinnaclebookspiracy.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.databinding.ActivityBarcodeScanBinding
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstance
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.BarcodeViewModel
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.ViewModelFactory

class BarcodeScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarcodeScanBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelFactory(Repository(RetrofitInstance.api))
    }

    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    private var isCameraStarted = false
    private var lastScannedBarcode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Barcode Scan"

        initBarcodeScanner()
        observeViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initBarcodeScanner() {
        try {
            barcodeDetector = BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.CODE_128)
                .build()

            cameraSource = CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (!isCameraStarted) {
                    try {
                        cameraSource.start(binding.surfaceView.holder)
                        isCameraStarted = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                try {
                    cameraSource.stop()
                    isCameraStarted = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() > 0) {
                    val scannedBarcode = barcodes.valueAt(0).displayValue
                    if (scannedBarcode == lastScannedBarcode) {
                        runOnUiThread {
                            Toast.makeText(this@BarcodeScanActivity, "Barcode already scanned, Please go back", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        lastScannedBarcode = scannedBarcode
                        val intent = Intent(this@BarcodeScanActivity, OtpAuthActivity::class.java)
                        intent.putExtra("scannedBarcode", scannedBarcode)
                        startActivity(intent)
                        vibrate()
                    }
                }
            }
        })
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    private fun observeViewModel() {
        viewModel.scannedBarcode.observe(this, Observer { barcode ->
            // Handle scanned barcode if needed
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            // Handle error message if needed
        })
    }

    override fun onResume() {
        super.onResume()
        if (!isCameraStarted) {
            initBarcodeScanner()
        }
    }

    override fun onPause() {
        super.onPause()
        cameraSource.stop()
        isCameraStarted = false
    }
}
