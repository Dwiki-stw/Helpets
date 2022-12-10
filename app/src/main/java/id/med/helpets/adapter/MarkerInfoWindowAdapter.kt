import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import id.med.helpets.R
import id.med.helpets.dataclass.Post
import id.med.helpets.ui.detail.DetailActivity

class MarkerInfoWindowAdapter(
    private val context: Context,
    private val mMap: GoogleMap
) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {
        val pets = marker.tag as? Post ?: return null


        val view = LayoutInflater.from(context).inflate(
            R.layout.marker_info_contents, null
        )

        view.findViewById<TextView>(R.id.tvTitle).text = pets.category
        view.findViewById<TextView>(R.id.textViewLocation).text = pets.address

        mMap.setOnInfoWindowClickListener {
            val intent = Intent (context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_POST, pets)
            context.startActivity(intent)
        }

       return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

}