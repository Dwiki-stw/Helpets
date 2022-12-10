package id.med.helpets.ui.createpost

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import id.med.helpets.R
import id.med.helpets.databinding.ActivityCreatePostBinding
import id.med.helpets.dataclass.Post
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreatePostActivity : AppCompatActivity() {

    private var _binding: ActivityCreatePostBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseDatabase
    private lateinit var image: Uri

    private lateinit var loading : Dialog

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        db = Firebase.database

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getMyLocation()

        setupLoading()

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.tvLocation.setOnClickListener {
            if (binding.tvLocation.text.length == ADDRESS.length){
                binding.tvLocation.text = shortAddress(ADDRESS)
            }else{
                binding.tvLocation.text = ADDRESS
            }
        }

        binding.floatingButtonGetLocation.setOnClickListener {
            binding.tvLocation.text = ""
            getMyLocation()
        }


        binding.imgAddPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked){
                when(checkedId){
                    binding.btnCat.id -> CATEGORY = "CAT"
                    binding.btnDog.id -> CATEGORY = "DOG"
                }
            }else{
                CATEGORY = "category"
            }
        }

        binding.buttonSend.setOnClickListener {
            if(checkData()){
                sendPost(image)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun sendPost(image: Uri){

        loading.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)

        //instance firebaseStorage
        val storageReference = FirebaseStorage.getInstance().reference.child("images/$fileName")

        //upload image
        val uploadTask = storageReference.putFile(image)

        //Handling
        uploadTask.continueWithTask {task ->
            if (!task.isSuccessful){
                loading.cancel()
                Log.d(TAG, "Upload Gagal")
                task.exception?.let {
                    throw it
                }
            }
            storageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful){
                val url = task.result.toString()

                //push to database
                insertToDatabase(url)

            }else{
                loading.cancel()
                Log.d(TAG, "Upload Gagal")
            }
        }
    }

    private fun insertToDatabase(url: String){
        val databaseRef = db.reference.child(POST)
        val description = binding.tvDecription.text.toString()

        val push = databaseRef.push()
        val id = push.key

        //create Post
        val post = Post(
            id,
            "test",
            description,
            url,
            CATEGORY,
            ADDRESS,
            LAT,
            LON,
            Date().time
        )


        push.setValue(post) { error, _ ->
            if (error != null) {
                loading.cancel()
                Toast.makeText(this, "Upload Gagal" + error.message, Toast.LENGTH_SHORT).show()
            } else {
                loading.cancel()
                Toast.makeText(this, "Terikirm", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if (uri != null){
            binding.imgAddPhoto.setImageURI(uri)
            image = uri
        }else{
            Toast.makeText(this, "Gagal Memuat Foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it!=null){
                showLoading(true)
                LAT = it.latitude
                LON = it.longitude
                Handler(Looper.getMainLooper()).postDelayed({
                    ADDRESS = getAddress(LAT, LON)
                    binding.tvLocation.text = shortAddress(ADDRESS)
                }, 1500)
            }
        }
        location.addOnFailureListener {
            Toast.makeText(this, "Gagal menentukan lokasi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAddress(lat: Double, lon: Double): String{
        var address = ""
        val geocoder = Geocoder(this@CreatePostActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if( list != null && list.size != 0){
                showLoading(false)
                address = list[0].getAddressLine(0)
            }
        }catch (e: IOException){
            showLoading(false)
            e.printStackTrace()
        }
        return address
    }

    private fun shortAddress(address: String): String{
        return address.substring(0..25) + "…"
    }

    private fun checkData(): Boolean{
        val description = binding.tvDecription.text.toString()

        if (description.isNotEmpty()){
            if (CATEGORY != "category"){
                if (ADDRESS != "address"){
                    try {
                        val image = image
                        return true
                    }catch (e: Exception){
                        Toast.makeText(this, "Photo Tidak Kosong", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this, "Aktifkan Lokasi Anda", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Pilih Kategori", Toast.LENGTH_SHORT).show()
            }
        }else{
            binding.tvDecription.error = "Decription tidak boleh kosong"
        }

        return false
    }

    private fun showLoading(isLoading : Boolean){
        binding.progressLocation.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupLoading(){

        loading = Dialog(this)

        val params = window.attributes
        params.gravity = Gravity.CENTER

        window.attributes = params
        loading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        loading.setTitle(null)
        loading.setCancelable(false)
        loading.setOnCancelListener(null)
        val view = layoutInflater.inflate(R.layout.loading_custome, null)

        loading.setContentView(view)

    }

    companion object{
        private const val TAG = "CreatePost"

        private var ADDRESS = "address"
        private var CATEGORY = "category"
        private var LAT = 0.0
        private var LON = 0.0

        const val POST = "post"


    }

}