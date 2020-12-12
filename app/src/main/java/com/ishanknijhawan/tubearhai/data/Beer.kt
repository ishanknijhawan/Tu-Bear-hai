package com.ishanknijhawan.tubearhai.data

import com.google.gson.annotations.SerializedName

data class Beer(
    val abv: Double,
    @SerializedName("attenuation_level")
    val attenuationLevel: Double,
    @SerializedName("boil_volume")
    val boilVolume: BoilVolume,
    @SerializedName("brewers_tips")
    val brewersTips: String,
    @SerializedName("contributed_by")
    val contributedBy: String,
    val description: String,
    val ebc: Double,
    @SerializedName("first_brewed")
    val firstBrewed: String,
    @SerializedName("food_pairing")
    val foodPairing: List<String>,
    val ibu: Double,
    val id: Double,
    @SerializedName("image_url")
    val imageUrl: String,
    val ingredients: Ingredients,
    val method: Method,
    val name: String,
    val ph: Double,
    val srm: Double,
    val tagline: String,
    @SerializedName("target_fg")
    val targetFg: Double,
    @SerializedName("target_og")
    val targetOg: Double,
    val volume: Volume
)

data class Amount(
    val unit: String,
    val value: Double
)

data class Hop(
    val add: String,
    val amount: Amount,
    val attribute: String,
    val name: String
)

data class AmountX(
    val unit: String,
    val value: Double
)

data class BoilVolume(
    val unit: String,
    val value: Double
)

data class Fermentation(
    val temp: Temp
)

data class Ingredients(
    val hops: List<Hop>,
    val malt: List<Malt>,
    val yeast: String
)

data class Malt(
    val amount: AmountX,
    val name: String
)

data class MashTemp(
    val duration: Double,
    val temp: TempX
)

data class Method(
    val fermentation: Fermentation,
    @SerializedName("mash_temp")
    val mashTemp: List<MashTemp>,
    val twist: Any
)

data class Temp(
    val unit: String,
    val value: Double
)

data class TempX(
    val unit: String,
    val value: Double
)

data class Volume(
    val unit: String,
    val value: Double
)
