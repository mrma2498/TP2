package ipvc.estg.tp2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.grpc.InternalChannelz.id
import ipvc.estg.tp2.model.Loja
import ipvc.estg.tp2.model.Parque
import ipvc.estg.tp2.model.Produto

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationRequest: LocationRequest
    private val LOCATION_PERMISSION_REQUEST_CODE = 2

    /**Variável que contém a lista dos parques de estacionamento*/
    val parques = mutableListOf<Parque>()


    val lojas = mutableListOf<Loja>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val fabverParques = findViewById<FloatingActionButton>(R.id.btnVerParques)
        fabverParques.setOnClickListener {
            getListaParques()
        }


        getListaLojas()

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
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                return
            }
            return
        }
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currenteLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currenteLatLng, 10f))
            }
        }
        createLocationRequest()

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

            for (i in parques) {

                Log.d("marcos", "aqui" + i.toString())
                var posicao = LatLng(i.localizacao.latitude, i.localizacao.longitude)
                if (i.livre != true) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicao, 10f))
                    mMap.addMarker(
                        MarkerOptions()
                            .position(posicao)
                            .title(i.nome)
                            .snippet("Estado do parque:" + i.livre)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicao, 10f))
                    mMap.addMarker(
                        MarkerOptions()
                            .position(posicao)
                            .title(i.nome)
                            .snippet("Estado do parque:" + i.livre)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

                    )
                }

            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()

        locationRequest.interval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }




    /**Obtém lista de lojas existentes no sistema.
     * NOTA: Para aceder às coordenadas -> localizacao.latitude ou localizacao.longitude
     * */
    private fun getListaLojas(){


        val docs = Firebase.firestore.collection("lojas")


        docs.addSnapshotListener{ snapshot, e ->

            for (documento in snapshot!!.documents){


                val docMap: Map<String, *>? = documento.data?.get("produto1") as? Map<String, *>

                val nome : String? = docMap?.get("nome") as? String
                val disponivel: Boolean? = docMap?.get("disponivel") as? Boolean


                val docMap2: Map<String, *>? = documento.data?.get("produto2") as? Map<String, *>

                val nome2 : String? = docMap2?.get("nome") as? String
                val disponivel2: Boolean? = docMap?.get("disponivel") as? Boolean


                val docMap3: Map<String, *>? = documento.data?.get("produto3") as? Map<String, *>

                val nome3 : String? = docMap3?.get("nome") as? String
                val disponivel3: Boolean? = docMap?.get("disponivel") as? Boolean



                if (disponivel != null && nome != null && disponivel2 != null && nome2 != null && disponivel3 != null && nome3 != null) {

                    val p1 = Produto(nome,disponivel)
                    val p2 = Produto(nome2,disponivel2)
                    val p3 = Produto(nome3,disponivel3)

                    val produtos: List<Produto> = listOf(p1,p2,p3)

                    val loja = Loja(
                        documento.id.toInt(),
                        "${documento.data?.get("nome")}",
                        documento.data?.getValue("localizacao") as GeoPoint,
                        "${documento.data?.get("email")}",
                        "${documento.data?.get("password")}",
                        produtos

                    )
                    lojas += loja
                }
            }
            for (l in lojas){
                Log.d("TAG",l.toString() + "\n")
            }

            /**Inserir lojas no mapa a partir daqui
             *
             *
             * */


        }

    }


    //Ideia de como poderá ser feita a seleção de lojas com os produtos disponiveis
    fun testeIntersect() {


        var selecao = mutableListOf<String>()

        selecao.add("Batatas")
        selecao.add("Alface")

        Log.d("maria", selecao.toString())

        var produtosLoja = mutableListOf<String>()

        produtosLoja.add("Batatas")
        produtosLoja.add("Alface")
        produtosLoja.add("Tomate")

        Log.d("maria", produtosLoja.toString())


        var existe: Boolean = false

        var comum: Set<String> = selecao.intersect(produtosLoja)

        Log.d("maria", comum.toString())

        if (comum.isNotEmpty()) {
            existe = true
            Log.d("maria", comum.toString())
            //Verificar se os produtos estão disponiveis

        }



    }
}
