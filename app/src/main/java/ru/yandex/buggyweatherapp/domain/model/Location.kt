package ru.yandex.buggyweatherapp.domain.model

data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null
) {
    
    override fun toString(): String {
        var result = ""
        result += "Latitude: $latitude, "
        result += "Longitude: $longitude"
        name?.let {
            result += ", Name: $it"
        }
        return result
    }
    


    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

    /*override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Location

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (name != other.name) return false

        return true
    }*/



    //TODO проверить для чего такой equals? так как не понятно чем онтличается от типового
    override fun equals(other: Any?): Boolean {
        if (other !is Location) return false
        return latitude == other.latitude && longitude == other.longitude
    }
}