package ipvc.estg.tp2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipvc.estg.tp2.model.Loja
import ipvc.estg.tp2.model.Produto


class AlteracaoEstadoProd : AppCompatActivity() {


    //ID loja
    val idLoja = Int





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alteracao_estado_prod)

        getLoja(1)


    }

    private fun getLoja(idLoja: Int) {


        val docs = Firebase.firestore.collection("lojas").document(idLoja.toString())


        docs.addSnapshotListener { snapshot, e ->




                val docMap: Map<String, *>? = snapshot!!.data?.get("produto1") as? Map<String, *>

                val nome: String? = docMap?.get("nome") as? String
                val disponivel: Boolean? = docMap?.get("disponivel") as? Boolean


                val docMap2: Map<String, *>? = snapshot!!.data?.get("produto2") as? Map<String, *>

                val nome2: String? = docMap2?.get("nome") as? String
                val disponivel2: Boolean? = docMap2?.get("disponivel") as? Boolean


                val docMap3: Map<String, *>? = snapshot!!.data?.get("produto3") as? Map<String, *>

                val nome3: String? = docMap3?.get("nome") as? String
                val disponivel3: Boolean? = docMap3?.get("disponivel") as? Boolean



                if (disponivel != null && nome != null && disponivel2 != null && nome2 != null && disponivel3 != null && nome3 != null) {

                    val p1 = Produto(nome, disponivel)
                    val p2 = Produto(nome2, disponivel2)
                    val p3 = Produto(nome3, disponivel3)

                    val produtos: List<Produto> = listOf(p1, p2, p3)

                    val loja = Loja(
                        snapshot!!.id.toInt(),
                        "${snapshot!!.data?.get("nome")}",
                        snapshot!!.data?.getValue("localizacao") as GeoPoint,
                        "${snapshot!!.data?.get("email")}",
                        "${snapshot!!.data?.get("password")}",
                        produtos

                    )
                    Log.d("teste2",loja.toString())

                    val nomeLoja = findViewById<TextView>(R.id.nomeLoja)

                    val emailLoja = findViewById<TextView>(R.id.email)

                    val checkBox1 = findViewById<CheckBox>(R.id.checkBox1)
                    val checkBox2 = findViewById<CheckBox>(R.id.checkBox2)
                    val checkBox3 = findViewById<CheckBox>(R.id.checkBox3)
                    val button = findViewById<Button>(R.id.button_save)

                    nomeLoja.text = "Loja " + loja.nome
                    emailLoja.text = loja.email

                    checkBox1.text = loja.produtos[0].nome
                    checkBox2.text = loja.produtos[1].nome
                    checkBox3.text = loja.produtos[2].nome


                    checkBox1.isChecked = loja.produtos[0].disponivel
                    checkBox2.isChecked = loja.produtos[1].disponivel
                    checkBox3.isChecked = loja.produtos[2].disponivel


                    button.setOnClickListener {

                        var estado1: Boolean = checkBox1.isChecked
                        var estado2: Boolean = checkBox2.isChecked
                        var estado3: Boolean = checkBox3.isChecked

                        val docRef: DocumentReference = Firebase.firestore.collection("lojas").document(1.toString())
                        docRef.update(mapOf("produto1.disponivel" to estado1))
                        docRef.update(mapOf("produto2.disponivel" to estado2))
                        docRef.update(mapOf("produto3.disponivel" to estado3))
                    }


                }

            }



        }




}