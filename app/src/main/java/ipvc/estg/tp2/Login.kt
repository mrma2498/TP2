package ipvc.estg.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipvc.estg.tp2.model.CredenciaisLoja
import ipvc.estg.tp2.model.Loja
import ipvc.estg.tp2.model.Produto

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val button = findViewById<Button>(R.id.loginAccount)


        button.setOnClickListener {

            val email = findViewById<EditText>(R.id.emailEdit)
            val password = findViewById<EditText>(R.id.passwordEdit)

            Log.d("MARIA", email.text.toString() + password.text.toString())


            //val docs = Firebase.firestore.collection("lojas").whereEqualTo("email",email.text.toString()).whereEqualTo("password",password.text.toString())

            val docs = Firebase.firestore.collection("lojas").whereEqualTo("email","srantonio@gmail.com").whereEqualTo("password","antonio661")

            val lojas = mutableListOf<CredenciaisLoja>()



            docs.addSnapshotListener { snapshot, e ->

                for (documento in snapshot!!.documents) {


                        val loja = CredenciaisLoja(
                            documento.id.toInt(),
                            "${documento.data?.get("email")}",
                            "${documento.data?.get("password")}"


                        )
                        lojas+=loja
                    Log.d("MARIA", lojas.toString())


                    if (lojas.isEmpty()){
                        Toast.makeText(this, "Loja não registada!",Toast.LENGTH_SHORT).show()
                    } else {

                        Log.d("MARIA", "Login com sucesso")

                        val intent = Intent(this@Login, AlteracaoEstadoProd::class.java)

                        intent.putExtra("idLoja",lojas[0].id)


                        startActivity(intent)


                    }

                }




            }


        }

    }
}