package ipvc.estg.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng
import ipvc.estg.tp2.model.Parque
import ipvc.estg.tp2.model.Produto

class MainActivity : AppCompatActivity() {

    val produtos = mutableListOf<Produto>()
    val parques = mutableListOf<Parque>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bntMapa = findViewById<Button>(R.id.pagMap)
        bntMapa.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }



        //getListaProdutos();
        getListaParques()
    }


    /**Obtém lista de produtos existentes no sistema e a sua disponibilidade.*/
    private fun getListaProdutos(){

        val docs = Firebase.firestore.collection("produtos")

        docs.addSnapshotListener{ snapshot, e ->

            for (documento in snapshot!!.documents){
                val produto = Produto(
                        documento.id,
                        "${documento.data?.get("nome")}",
                        documento.data?.getValue("disponivel") as Boolean
                )
                produtos += produto
            }
        }

    }


    /**Obtém a lista de parques de estacionamento.
     * NOTA: Para aceder às coordenadas -> localizacao.latitude ou localizacao.longitude
     *
     * */
    private fun getListaParques(){

        val docs = Firebase.firestore.collection("parques")

        docs.addSnapshotListener{ snapshot, e ->

            for (documento in snapshot!!.documents){
                val parque = Parque(
                        documento.id.toInt(),
                        "${documento.data?.get("nome")}",
                        documento.data?.getValue("localizacao") as GeoPoint,
                        documento.data?.getValue("livre") as Boolean

                )
                parques += parque
            }

        }
    }
}