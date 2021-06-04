package ipvc.estg.tp2.model

import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng

data class Parque (
    val id: Int,
    val nome: String,
    val localizacao: GeoPoint,
    val livre: Boolean
)