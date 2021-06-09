package ipvc.estg.tp2

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val bntMapa = findViewById<Button>(R.id.pagMap)
        bntMapa.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }




    }





}

