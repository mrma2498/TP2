package ipvc.estg.tp2.model



import com.google.firebase.firestore.GeoPoint
import java.io.Serializable


data class Loja (

    val id: Int,
    val nome: String,
    val localizacao: GeoPoint,
    val email: String,
    val password: String,
    val produtos: List<Produto>

):Serializable