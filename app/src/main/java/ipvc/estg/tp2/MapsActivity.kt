package ipvc.estg.tp2

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipvc.estg.tp2.model.Parque

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    /**Variável que contém a lista dos parques de estacionamento*/
    val parques = mutableListOf<Parque>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getListaParques()


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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
            }
        // Add a marker in Sydney and move the camera
       // val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }


    /**Obtém a lista de parques de estacionamento.
     * NOTA: Para aceder às coordenadas -> localizacao.latitude ou localizacao.longitude
     *
     * */
    private fun getListaParques() {

        val docs = Firebase.firestore.collection("parques")

        docs.addSnapshotListener { snapshot, e ->

            for (documento in snapshot!!.documents) {
                val parque = Parque(
                    documento.id.toInt(),
                    "${documento.data?.get("nome")}",
                    documento.data?.getValue("localizacao") as GeoPoint,
                    documento.data?.getValue("livre") as Boolean

                )
                parques += parque
            }
            Log.d("maria", parques.toString())

            /**Insere os markers no mapa aqui, como fizemos no primeiro trabalho.
             *  Se for fora desta função a lista está vazia.
             *  */

            for ( i in parques){
            val zone = LatLng(41.69569, -8.82771)
            val zoomLevel = 1000f
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zone, zoomLevel))
            Log.d("marcos", "aqui"+ i.toString())
            var posicao = LatLng(i.localizacao.latitude, i.localizacao.longitude)
            if (i.livre != true ) {
                mMap.addMarker(
                    MarkerOptions()
                        .position(posicao)
                        .title(i.nome)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                )
            } else {
                mMap.addMarker(
                    MarkerOptions()
                        .position(posicao)
                        .title(i.nome)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                )
            }
        }
        }
    }
}
