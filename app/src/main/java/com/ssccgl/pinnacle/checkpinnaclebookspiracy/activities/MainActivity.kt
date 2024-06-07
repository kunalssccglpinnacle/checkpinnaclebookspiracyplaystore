//package com.ssccgl.pinnacle.checkpinnaclebookspiracy.activities
package com.ssccgl.pinnacle.checkpinnaclebookspiracy.ui.activities
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.databinding.ActivityMainBinding
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.network.RetrofitInstance
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.repository.Repository
import com.ssccgl.pinnacle.checkpinnaclebookspiracy.ui.activities.BarcodeScanActivity

import com.ssccgl.pinnacle.checkpinnaclebookspiracy.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ViewModel by viewModels {
        ViewModelFactory(Repository(RetrofitInstance.api))
    }

    private var requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val intent = Intent(this, BarcodeScanActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBc.setOnClickListener {
            requestCamera.launch(Manifest.permission.CAMERA)
        }
    }
}
