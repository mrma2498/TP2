package ipvc.estg.tp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipvc.estg.tp2.model.Produto

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //loadProdutos()
    }

    //teste
    private fun loadProdutos(){
        val docs = Firebase.firestore.collection("produtos")

        Log.d("MARIA",docs.toString())


        docs.addSnapshotListener{ snapshot, e ->

            val produtos = mutableListOf<Produto>()
            for (documento in snapshot!!.documents){
                val produto = Produto(
                documento.id,
                "${documento.data?.get("nome")}",
                ""
                )
                produtos += produto
            }
            Log.d("MARIA",produtos.toString())

        }

    }
}