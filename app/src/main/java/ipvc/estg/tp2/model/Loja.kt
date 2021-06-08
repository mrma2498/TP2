package ipvc.estg.tp2.model

import com.google.firebase.firestore.GeoPoint

data class Loja(

    val id: Int,
    val nome: String,
    val localizacao: GeoPoint,
    val email: String,
    val password: String,
    val produtos: List<Produto>
    //val produto1: Boolean
   // val produto2: Map<String,Produto>,
    //val produto3: Map<String,Produto>

)

