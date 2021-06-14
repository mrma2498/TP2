package ipvc.estg.tp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast


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

        var listaProds = ArrayList <String>()

        comprar.setOnClickListener {


            if (batatas.isChecked) {
                listaProds.add("batatas")
            }
            if (alface.isChecked) {
                listaProds.add("alface")
            }
            if (tomates.isChecked) {
                listaProds.add("tomates")
            }
            if(carne.isChecked){
                listaProds.add("carne")
            }
            if(peixe.isChecked){
                listaProds.add("peixe")
            }
            if(ovos.isChecked){
                listaProds.add("ovos")
            }
            if(cenouras.isChecked){
                listaProds.add("cenouras")
            }
            if(ervilhas.isChecked){
                listaProds.add("ervilhas")
            }
            if(laranjas.isChecked){
                listaProds.add("laranjas")
            }
            if(limoes.isChecked){
                listaProds.add("limoes")
            }
            if(macas.isChecked){
                listaProds.add("macas")
            }
            Log.d("Jose", listaProds.toString())
        }


    }


}