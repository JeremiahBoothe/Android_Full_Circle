package com.jeremiahboothe.jbradio.metadata

import com.google.gson.annotations.SerializedName

//interface for no good reason
interface RadioMetaData{
        var artist: String
}

data class SimpleJSONModel(
        /*
        // @SerializedName(" ") for the Gson converter
        // @field:Json(name = " ") for the Moshi converter
        // @SerialName(" ") for the Kotlinx Serialization converter
        // @JsonProperty(" ") for the Jackson converter
        // @JSONField(name = " ") for the Fastjson converter
        */

        @SerializedName("album")
        var album: String,

        @SerializedName("sku")
        var sku: String,

        @SerializedName("thumb")
        var thumb: String?,

        @SerializedName("artist")
        override var artist: String

):RadioMetaData

/*
@Composable
fun Greeting(album: String) {
        Text("Hello $album")
}*/