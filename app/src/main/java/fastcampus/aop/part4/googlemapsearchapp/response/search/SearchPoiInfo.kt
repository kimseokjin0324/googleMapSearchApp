package fastcampus.aop.part4.googlemapsearchapp.response.search

data class SearchPoiInfo(
    val totalCount: String,
    val count: String,
    val page: String,
    val pois: Pois
)