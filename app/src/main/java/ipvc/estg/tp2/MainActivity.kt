package ipvc.estg.tp2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipvc.estg.tp2.model.Loja
import ipvc.estg.tp2.model.Parque
import ipvc.estg.tp2.model.Produto


class MainActivity : AppCompatActivity() {

    val lojas = mutableListOf<Loja>()
    val parques = mutableListOf<Parque>()
    val produtos = mutableListOf<Produto>()

    var selecao = mutableListOf<String>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //getListaLojas();
        //getListaParques()
        //getProdutosPorLoja(2)
        //testeIntersect()
        getProdutosPorLojaDisponiveis(1)
    }


    /**Obtém lista de lojas existentes no sistema.
     * NOTA: Para aceder às coordenadas -> localizacao.latitude ou localizacao.longitude
     * */
    private fun getListaLojas(){

        val docs = Firebase.firestore.collection("lojas")

        docs.addSnapshotListener{ snapshot, e ->

            for (documento in snapshot!!.documents){
                val loja = Loja(
                        documento.id.toInt(),
                        "${documento.data?.get("nome")}",
                        documento.data?.getValue("localizacao") as GeoPoint,
                        "${documento.data?.get("email")}",
                        "${documento.data?.get("password")}"
                )
                lojas += loja
                Log.d("maria",lojas.toString())

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


    //Adicionar return lista e fazer
    private fun getProdutosPorLoja(idloja: Int) {

        val docs = Firebase.firestore.collection("lojas").document(idloja.toString()).collection("produtos")


        docs.addSnapshotListener{ snapshot, e ->

            for (documento in snapshot!!.documents){
                val produto = Produto(
                    documento.id.toInt(),
                    "${documento.data?.get("nome")}",
                    documento.data?.getValue("disponivel") as Boolean

                )
                produtos += produto

            }
            Log.d("maria",produtos.toString())

        }

    }

    private fun getProdutosPorLojaDisponiveis(idloja: Int) {

        val docs = Firebase.firestore.collection("lojas").document(idloja.toString()).collection("produtos").whereEqualTo("disponivel",true)


        docs.addSnapshotListener{ snapshot, e ->

            for (documento in snapshot!!.documents){
                val produto = Produto(
                    documento.id.toInt(),
                    "${documento.data?.get("nome")}",
                    documento.data?.getValue("disponivel") as Boolean

                )
                produtos += produto

            }
            Log.d("maria",produtos.toString())

        }

    }




    //Ideia de como poderá ser feita a seleção de lojas com os produtos disponiveis
    fun testeIntersect(){
        selecao.add("Batatas")
        selecao.add("Alface")

        Log.d("maria",selecao.toString())

        var produtosLoja = mutableListOf<String>()

        produtosLoja.add("Batatas")
        produtosLoja.add("Alface")
        produtosLoja.add("Tomate")

        Log.d("maria",produtosLoja.toString())


        var existe: Boolean = false

        var comum: Set<String> = selecao.intersect(produtosLoja)

        Log.d("maria",comum.toString())

        if (comum.isNotEmpty()){
            existe = true
            Log.d("maria",comum.toString())
            //Verificar se os produtos estão disponiveis
        }



    }



}