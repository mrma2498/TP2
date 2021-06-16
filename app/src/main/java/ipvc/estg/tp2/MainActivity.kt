package ipvc.estg.tp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bntMapa = findViewById<Button>(R.id.pagMap)
        bntMapa.setOnClickListener {
            val intent = Intent(this@MainActivity, RotasActivity::class.java)
            startActivity(intent)
        }

        val bntProduto = findViewById<Button>(R.id.pagProdutos)
        bntProduto.setOnClickListener {
            val intent = Intent(this@MainActivity, ListaProdutos::class.java)
            startActivity(intent)
        }

    }

}