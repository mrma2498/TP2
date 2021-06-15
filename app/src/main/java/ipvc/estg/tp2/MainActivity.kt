package ipvc.estg.tp2

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipvc.estg.tp2.model.Loja
import ipvc.estg.tp2.model.Produto

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val bntMapa = findViewById<Button>(R.id.pagMap)
        bntMapa.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        val bntAlteracao = findViewById<Button>(R.id.pagAlteracao)
        bntAlteracao.setOnClickListener {
            val intent = Intent(this@MainActivity, AlteracaoEstadoProd::class.java)
            startActivity(intent)
        }









    }




}

