package id.med.helpets.ui.maps

import MarkerInfoWindowAdapter
import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import id.med.helpets.R
import id.med.helpets.databinding.FragmentMapsBinding
import id.med.helpets.dataclass.Post
import id.med.helpets.helper.BitmapHelper

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var listPost: List<Post>

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.uiSettings.isZoomControlsEnabled = true

        googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext(), googleMap))
        getMyLocation(googleMap)
        getData(googleMap)
    }

    private val petsIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext() ,R.color.blue_dark)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.ic_pets, color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MapsFragment.requireActivity())

    }

    private fun getMyLocation(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                this@MapsFragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this@MapsFragment.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MapsFragment.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }
        val location = fusedLocationProviderClient.lastLocation
        googleMap.cameraPosition
        googleMap.isMyLocationEnabled = true
        location.addOnSuccessListener {
            if (it!=null){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15F))
            }
        }
    }

    private fun createMarkerStory(listPost: List<Post>, mMap: GoogleMap){
        for (story in listPost){
            val location = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("${story.name} Stories")
                    .icon(petsIcon)
            )
            marker?.tag = story
        }
    }

    private fun getData(mMap: GoogleMap){
        val db = Firebase.database.reference.child(CHILD)

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                listPost = snapshot.children.map { dataSnapshot ->
                    dataSnapshot.getValue(Post::class.java)!!
                }

                createMarkerStory(listPost, mMap)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val TAG = "Maps"
        const val CHILD = "post"
    }
}