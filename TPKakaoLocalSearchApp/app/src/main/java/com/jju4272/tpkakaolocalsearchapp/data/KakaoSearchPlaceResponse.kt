package com.jju4272.tpkakaolocalsearchapp.data

data class KakaoSearchPlaceResponse(var meta:PlaceMeta, var documents:List<Place>)

data class PlaceMeta(var total_count:Int, var pageable_coumt:Int, var is_end:Boolean)

data class Place(
    var id:String,
    var place_name:String,
    var category_name:String,
    var phone:String,
    var address_name:String,
    var road_address_name:String,
    var x:String, // 경도
    var y:String, // 위도
    var place_url:String, // 장소에 대한 검색사이트 링크
    var distance:String // 좌표 중심까지의 거리, 단 요청 파라미터로 x,y를 준 경우만 존재 [단위는 m]

)
