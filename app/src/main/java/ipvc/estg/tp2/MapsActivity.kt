package ipvc.estg.tp2

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList



class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var location1:LatLng
    private lateinit var location2:LatLng
    private lateinit var location3:LatLng
    var waypoints = ArrayList<LatLng>()
    private var myLat = 0.0
    private var myLongi = 0.0
    private var minhaLocalizacao = LatLng(0.2,0.2)
    private lateinit var mMap: GoogleMap


    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        //inicialização fusedLocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                var loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                LocationChanged(lastLocation)

                myLat = lastLocation.latitude
                myLongi = lastLocation.longitude


                Log.d("mycoord", "$myLat, $myLongi")



            }
        }

        createLocationRequest()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()

        // Add a marker in Sydney and move the camera

        //mMap.isMyLocationEnabled = true

        location1 = LatLng(13.0356745, 77.5881522)
        mMap.addMarker(MarkerOptions().position(location1).title("My location"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1, 5f))

        location2 = LatLng(41.6987, -8.8214)
        mMap.addMarker(MarkerOptions().position(location2).title("Madurai"))

        location3 = LatLng(13.029727, 77.5933021)
        mMap.addMarker(MarkerOptions().position(location3).title("Bangalore"))

        //Exemplos
        mMap.addMarker(MarkerOptions().position(LatLng(41.6941,-8.8203)).title("Rio"))
        mMap.addMarker(MarkerOptions().position(LatLng(41.6955,-8.8380)).title("rua"))

        //exemplos waypoints
        waypoints.add(0, LatLng(41.6955,-8.8380))
        waypoints.add(1, LatLng(41.6941,-8.8203))




        Log.d("mycoordMap", "$minhaLocalizacao")


    }
// funcao para obter o url da API Directions
    fun getDirectionURL(origin:LatLng,dest:LatLng, waypoints: ArrayList<LatLng>) : String{

        var waypointsURL = ArrayList<String>()
        for(i in 0..waypoints.size-1){

            waypointsURL.add("via:${waypoints[i].latitude},${waypoints[i].longitude}")

            Log.d("CCCwaypoint", waypointsURL[i])

        }

        //obter url dos waypoints

        var waypointsUrlFinal: String

        if(waypointsURL.isNotEmpty()) {
            waypointsUrlFinal = waypointsURL.joinToString(
                separator = "|via:",
                prefix = "&waypoints="
            )
            Log.d("CCCwaypointsF", waypointsUrlFinal)
        } else {
            waypointsUrlFinal = "null"
        }


        Log.d("CCCorigin", " ${origin.latitude}, ${origin.longitude}")
        Log.d("CCCdestination", " ${dest.latitude}, ${dest.longitude}")

    //verificar se existem waypoints
        if(waypointsUrlFinal == "null"){
            return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=AIzaSyDs95Q8wVFlwO1pVFJ7gxR-XnpFLKpUGMQ"
        } else {
            return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}$waypointsUrlFinal&sensor=false&mode=driving&key=AIzaSyDs95Q8wVFlwO1pVFJ7gxR-XnpFLKpUGMQ"
        }

    }

    private inner class GetDirection(val url : String) : AsyncTask<Void,Void,List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.d("GoogleMap" , " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data,GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

    public fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }













    override fun onPause(){
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume(){
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    fun setUpMap(){
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

            return
        } else {
            mMap.isMyLocationEnabled = true

            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location->
                if(location != null) {
                    lastLocation = location

                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    minhaLocalizacao = currentLatLng
                    val URL = getDirectionURL(minhaLocalizacao, location2,waypoints)
                    GetDirection(URL).execute()
                    Log.d("URL", URL)
                    Log.d("CCCsetup", "$minhaLocalizacao")
                }
            }
        }
    }

    private fun createLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    public fun setAddress(adress: Address) {
        if(adress != null) {
            if(adress.getAddressLine(0) != null) {
                Log.d("LLL", ""+ adress.getAddressLine(0) +"")

            }
            if(adress.getAddressLine(1) != null) {
                Log.d("LLL", ""+ adress.getAddressLine(1) +"")

            }
        }
    }

    private fun LocationChanged(p0: Location) {
        Log.d("LLL", "changed!!")

        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress: List<Address>? = null

        try{
            Adress = geoCoder.getFromLocation(p0.latitude, p0.longitude,1)
        }catch (e: IOException) {
            e.printStackTrace()}
        setAddress(Adress!![0])
    }
}