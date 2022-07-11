package ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model

import com.google.gson.annotations.SerializedName

data class GetTrailerResponse(
    @SerializedName("results") val trailer: List<Trailer>
)
