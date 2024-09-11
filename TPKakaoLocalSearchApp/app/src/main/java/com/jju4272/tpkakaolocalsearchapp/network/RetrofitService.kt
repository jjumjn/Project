package com.jju4272.tpkakaolocalsearchapp.network

import com.jju4272.tpkakaolocalsearchapp.data.KakaoSearchPlaceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitService {
    // 카카오 키워드 장소 검색 API를 GET방식으로 요청하는 작업 명세
    @Headers("Authorization: KakaoAK 3145f70b424e3fdd4dc5bc413ddc1d83")
    @GET("/v2/local/search/keyword.json?sort=distance")
    fun searchPlacesFromServer(@Query("query") query:String, @Query("x") longitude:String, @Query("y") latitude:String) : Call<String>

    @Headers("Authorization: KakaoAK 3145f70b424e3fdd4dc5bc413ddc1d83")
    @GET("/v2/local/search/keyword.json?sort=distance")
    fun searchPlacesFromServer2(@Query("query") query:String, @Query("x") longitude:String, @Query("y") latitude:String) : Call<KakaoSearchPlaceResponse>

}