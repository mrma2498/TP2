package ipvc.estg.tp2

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.protobuf.Parser
import ipvc.estg.tp2.model.Loja
import ipvc.estg.tp2.model.Parque
import ipvc.estg.tp2.model.Produto
import java.net.URL
import org.jetbrains.anko.uiThread
import com.beust.klaxon.*
import com.google.android.gms.maps.model.*
import org.jetbrains.anko.async

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationRequest: LocationRequest
    private lateinit var currenteLatLng: LatLng

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
     * we just add a marker near inicio, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // declare bounds object to fit whole route in screen
        //-----------------------------------------------------------------------------
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
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
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
                currenteLatLng = LatLng(location.latitude, location.longitude)
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
                    documento.data?.getValue("livre") as Long

                )
                parques += parque
            }
            Log.d("maria", parques.toString())

            /**Insere os markers no mapa aqui, como fizemos no primeiro trabalho.
             *  Se for fora desta função a lista está vazia.
             *  */

            for (i in parques) {
                var teste = 0
                var posicao = LatLng(i.localizacao.latitude, i.localizacao.longitude)
                Log.d(
                    "marcos",
                    " Nome: " + i.nome + "latitude: " + posicao.latitude + " longitude: " + posicao.longitude
                )
                if (i.livre == teste.toLong()) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicao, 10f))
                    mMap.addMarker(
                        MarkerOptions()
                            .position(posicao)
                            .title(i.nome)
                            .snippet("Estado do parque:" + i.livre)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )


                } else if (i.livre != teste.toLong()) {
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
            for (i in parques) {
                mMap.setOnMapLongClickListener { latLng ->
                    if (Math.abs(i.localizacao.latitude - latLng.latitude) < 0.05 && Math.abs(
                            i.localizacao.longitude - latLng.longitude
                        ) < 0.05
                    ) {
                            Log.d(
                                "marcos",
                                "Long Click latitude: " + latLng.latitude + " longitude: " + latLng.longitude
                            )
                            var posicao = LatLng(i.localizacao.latitude, i.localizacao.longitude) //---> só da a da ESE porque é o ultimo elemento do for
                            val inicio = currenteLatLng
                            val destino = latLng
                            rota(inicio, destino)
                    }
                }
            }

        }
    }

    private fun rota(inicioInicial: LatLng, destinoFinal: LatLng) {
        val LatLongB = LatLngBounds.Builder()
        val inicio = LatLng(inicioInicial.latitude, inicioInicial.longitude)
        val destino = LatLng(destinoFinal.latitude, destinoFinal.longitude)
        mMap!!.addMarker(MarkerOptions().position(inicio).title("Saida"))
        // Declare polyline object and set up color and width
        val options = PolylineOptions()
        options.color(Color.BLUE)
        options.width(5f)

        // build URL to call API
        val url = getURL(inicio, destino)

        async {
            // Connect to URL, download content and convert into string asynchronously
            val result = URL(url).readText()
            uiThread {
                // When API call is done, create parser and convert into JsonObjec
                val parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // get to the correct element in JsonObject
                val routes = json.array<JsonObject>("routes")
                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                // Add  points to polyline and bounds
                options.add(inicio)
                LatLongB.include(inicio)
                for (point in polypts) {
                    options.add(point)
                    LatLongB.include(point)
                }
                options.add(destino)
                LatLongB.include(destino)
                // build bounds
                val bounds = LatLongB.build()
                // add polyline to the map
                mMap!!.addPolyline(options)
                // show map with route centered
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }

    private fun getURL(from: LatLng, to: LatLng): String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val key = "key=AIzaSyBGspzB93rDQl3lvtRms23XjrFsXNSf0AY"
        val params = "$origin&$dest&$sensor&$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    private fun decodePoly(encoded: String): List<LatLng> {
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

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()

        locationRequest.interval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    /**Obtém lista de lojas existentes no sistema.
     * NOTA: Para aceder às coordenadas -> localizacao.latitude ou localizacao.longitude
     * */
    private fun getListaLojas() {


        val docs = Firebase.firestore.collection("lojas")


        docs.addSnapshotListener { snapshot, e ->

            for (documento in snapshot!!.documents) {


                val docMap: Map<String, *>? = documento.data?.get("produto1") as? Map<String, *>

                val nome: String? = docMap?.get("nome") as? String
                val disponivel: Boolean? = docMap?.get("disponivel") as? Boolean


                val docMap2: Map<String, *>? = documento.data?.get("produto2") as? Map<String, *>

                val nome2: String? = docMap2?.get("nome") as? String
                val disponivel2: Boolean? = docMap?.get("disponivel") as? Boolean


                val docMap3: Map<String, *>? = documento.data?.get("produto3") as? Map<String, *>

                val nome3: String? = docMap3?.get("nome") as? String
                val disponivel3: Boolean? = docMap?.get("disponivel") as? Boolean



                if (disponivel != null && nome != null && disponivel2 != null && nome2 != null && disponivel3 != null && nome3 != null) {

                    val p1 = Produto(nome, disponivel)
                    val p2 = Produto(nome2, disponivel2)
                    val p3 = Produto(nome3, disponivel3)

                    val produtos: List<Produto> = listOf(p1, p2, p3)

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
            for (l in lojas) {
                Log.d("TAG", l.toString() + "\n")
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
