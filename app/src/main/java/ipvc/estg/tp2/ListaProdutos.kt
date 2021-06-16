package ipvc.estg.tp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipvc.estg.tp2.model.Loja
import ipvc.estg.tp2.model.Produto


class ListaProdutos : AppCompatActivity() {

    lateinit var batatas: CheckBox
    lateinit var alface: CheckBox
    lateinit var tomates: CheckBox
    lateinit var carne: CheckBox
    lateinit var peixe: CheckBox
    lateinit var ovos: CheckBox
    lateinit var cenouras: CheckBox
    lateinit var ervilhas: CheckBox
    lateinit var laranjas: CheckBox
    lateinit var limoes: CheckBox
    lateinit var macas: CheckBox
    lateinit var comprar: Button

    val lojas = mutableListOf<Loja>()
    var finalLojas = ArrayList<Loja>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_produtos)

        batatas = findViewById(R.id.batatas)
        alface = findViewById(R.id.alface)
        tomates = findViewById(R.id.tomates)
        carne = findViewById(R.id.carne)
        peixe = findViewById(R.id.peixe)
        ovos = findViewById(R.id.ovos)
        cenouras = findViewById(R.id.cenouras)
        ervilhas = findViewById(R.id.ervilhas)
        laranjas = findViewById(R.id.laranjas)
        limoes = findViewById(R.id.limoes)
        macas = findViewById(R.id.macas)
        comprar = findViewById(R.id.compras)

        var listaProds = ArrayList<String>()

        comprar.setOnClickListener {


            if (batatas.isChecked) {
                listaProds.add("Batatas")
            }
            if (alface.isChecked) {
                listaProds.add("Alface")
            }
            if (tomates.isChecked) {
                listaProds.add("Tomates")
            }
            if (carne.isChecked) {
                listaProds.add("Carne")
            }
            if (peixe.isChecked) {
                listaProds.add("Peixe")
            }
            if (ovos.isChecked) {
                listaProds.add("Ovos")
            }
            if (cenouras.isChecked) {
                listaProds.add("Cenouras")
            }
            if (ervilhas.isChecked) {
                listaProds.add("Ervilhas")
            }
            if (laranjas.isChecked) {
                listaProds.add("Laranjas")
            }
            if (limoes.isChecked) {
                listaProds.add("Limões")
            }
            if (macas.isChecked) {
                listaProds.add("Maçãs")
            }


            if (listaProds.isEmpty()) {
                Toast.makeText(this, "Nenhum produto selecionado", Toast.LENGTH_SHORT).show()
            } else {


                getListaLojas(listaProds)

            }
        }

    }



    private fun getListaLojas(Lista: ArrayList<String>) {

        var produtosEncontrados = ArrayList<String>()
        var cordenadasLojas = ArrayList<Float>()

        val docs = Firebase.firestore.collection("lojas")


        docs.addSnapshotListener { snapshot, e ->

            for (documento in snapshot!!.documents) {


                val docMap: Map<String, *>? = documento.data?.get("produto1") as? Map<String, *>

                val nome: String? = docMap?.get("nome") as? String
                val disponivel: Boolean? = docMap?.get("disponivel") as? Boolean


                val docMap2: Map<String, *>? = documento.data?.get("produto2") as? Map<String, *>

                val nome2: String? = docMap2?.get("nome") as? String
                val disponivel2: Boolean? = docMap2?.get("disponivel") as? Boolean


                val docMap3: Map<String, *>? = documento.data?.get("produto3") as? Map<String, *>

                val nome3: String? = docMap3?.get("nome") as? String
                val disponivel3: Boolean? = docMap3?.get("disponivel") as? Boolean



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
          for (lojas in lojas) {
               Log.d("TAG", lojas.toString() + "\n")

                for(i in 0 .. lojas.produtos.size-1) {

             //       Log.d("TAG", lojas.produtos[i].nome + "\n")
                    for(produto in Lista){
                        Log.d("TOG", produto + lojas.produtos[i].nome)
                        if(produto == lojas.produtos[i].nome ){
                            if(produtosEncontrados.contains(produto)) {
                               Log.d("TAG", "Ja foi encontrada uma loja para esse produto")
                            }else{
                                Log.d("OLE", produto)
                                produtosEncontrados.add(produto)
                                if(finalLojas.contains(lojas)){
                                    Log.d("OLE", "loja ja adicionada" + lojas.id.toString())
                                }else{
                                    finalLojas.add(lojas)
                                    Log.d("OLE", lojas.id.toString() + "\n")
                                }

                            }
                        }

                    }
                }
            }

            for(j in finalLojas){

                cordenadasLojas.add(j.localizacao.latitude.toFloat())
                cordenadasLojas.add(j.localizacao.longitude.toFloat())


            }
            Log.d("Teste", cordenadasLojas.toString())



            val intent = Intent(this@ListaProdutos, MapsActivity::class.java)
            intent.putExtra("key", cordenadasLojas)
            startActivity(intent)

        }



    }
}
