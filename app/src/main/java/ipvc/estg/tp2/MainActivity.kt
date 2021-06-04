package ipvc.estg.tp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipvc.estg.tp2.model.Produto

class MainActivity : AppCompatActivity() {

    val produtos = mutableListOf<Produto>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getListaProdutos();
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
            for (p in produtos){
                Log.d("MARIA",p.nome + p.disponivel + "\n")
            }


        }

    }
}