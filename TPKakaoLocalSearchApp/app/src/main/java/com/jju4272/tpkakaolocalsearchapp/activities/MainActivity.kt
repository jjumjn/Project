package com.jju4272.tpkakaolocalsearchapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.jju4272.tpkakaolocalsearchapp.R
import com.jju4272.tpkakaolocalsearchapp.data.KakaoSearchPlaceResponse
import com.jju4272.tpkakaolocalsearchapp.data.Place
import com.jju4272.tpkakaolocalsearchapp.data.PlaceMeta
import com.jju4272.tpkakaolocalsearchapp.databinding.ActivityMainBinding
import com.jju4272.tpkakaolocalsearchapp.fragments.PlaceFavoriteFragment
import com.jju4272.tpkakaolocalsearchapp.fragments.PlaceListFragment
import com.jju4272.tpkakaolocalsearchapp.fragments.PlaceMapFragment
import com.jju4272.tpkakaolocalsearchapp.network.RetrofitHelper
import com.jju4272.tpkakaolocalsearchapp.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    //1.
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //5. 카카오 검색에 필요한 요청 데이터 : query(검색장소명), x(경도:longutude), y(위도:latitude), sort(고정값: distance)
    //5.1) 검색장소명
    var searchQuery:String = "화장실" // 앱 초기 검색어 - 내 주변 개방화장실
    //5.2) 현재 내위치 정보가진 객체(위도,경도 정보를 멤버로 보유한 객체)생성
    var mylocation:Location?= null

    //5.3) [ 구글 Fused Location API 사용 : com.google.*:play-services-location]
    val providerClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    // 카카오 검색결과 (json)를 분석한 객체 참조변수
    var searchPlaceResponse:KakaoSearchPlaceResponse?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //2.
        setContentView(binding.root)

        //4. 처음 보여질 Bottom 탭의 프래그먼트 붙이기
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, PlaceListFragment()).commit()


        //3. bottom 탭 선택에 따라 프래그먼트 변경하여 배치
        binding.bnv.setOnItemSelectedListener { menuItem ->
            // 선택된 탭에 따라 화면 반응하기
            when(menuItem.itemId){
                R.id.bnv_menu_list->supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PlaceListFragment()).commit()
                R.id.bnv_menu_map->supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PlaceMapFragment()).commit()
                R.id.bnv_menu_favorite->supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PlaceFavoriteFragment()).commit()
                R.id.bnv_menu_account-> Toast.makeText(this, "계정관리화면이동예정", Toast.LENGTH_SHORT).show()
            }
            // 리턴값을 true로 해야 선택변경  ui가 반영된다
            true
        }

        //6. 내 위치 정보 요청 - 동적 퍼미션 필요
        val permissionChecke = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionChecke == PackageManager.PERMISSION_DENIED)
            permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        else requestMyLocation()

        // 툴바에 있는 NavigationIcon 인 내 위치탐색 버튼 클릭
        binding.toolbar.setNavigationOnClickListener { requestMyLocation() }

        // 소프트 키보드에 검색버튼을 클릭했을때 새로운 키워드로 장소 검색 요청
        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            searchQuery = binding.etSearch.text.toString()
            searchPlaces()

            false
        }

        // 단축 검색 장소 버튼들 클릭에 반응하기
        setChoiceButtonListener()


    } // onCreate

    // 위치정보 퍼미션 요청 대행사 객체 동료
    private val permissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it) requestMyLocation()
            else Toast.makeText(this, "서비스 이용불가", Toast.LENGTH_SHORT).show()
    }

    // 내위치 정보를 요청 하는 기능 메소드 정의
    private fun requestMyLocation(){
        // 요청에 대한 기준을 정하는 객체
        val  request:LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        // 실시간 위치 정보 갱신 요청
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }
        providerClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    // 위치정보 갱신떄마다 반응하는 콜백 객체 생성
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            mylocation = p0.lastLocation

            // 내 위치를 얻었으니 위치정보 탐색을
            providerClient.removeLocationUpdates(this) // this: LocationCalback

            // 위치정보가 있으니 카카오 키워드 장소 검색 오픈API를 통해 정보 가져오기
            searchPlaces()
        }
    }

    // 카카오 '키워드' 장소 검색 장소정보들을 가져오는 작업 메소드
    private fun searchPlaces(){
        Toast.makeText(this, "${searchQuery} : ${mylocation?.longitude} : ${mylocation?.latitude}", Toast.LENGTH_SHORT).show()

        // 카카오 키워드 로컬 서치 API - Retrofit Library 추가
        val retrofit:Retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitService:RetrofitService = retrofit.create(RetrofitService::class.java)
        val call = retrofitService.searchPlacesFromServer2(searchQuery, mylocation?.longitude.toString(), mylocation?.latitude.toString())
        call.enqueue(object : Callback<KakaoSearchPlaceResponse>{
            override fun onResponse(
                p0: Call<KakaoSearchPlaceResponse>,
                p1: Response<KakaoSearchPlaceResponse>
            ) {
                // p1 파라미터로 전달 된 json을 파싱한 결과 받기 - 그 결과를 Fragment들에서 사용하기에 멤버 변수로 참조하기
                searchPlaceResponse = p1.body()

                // 데이터가 온전히 분석되었는지 확인해보기
                val meta:PlaceMeta?= searchPlaceResponse?.meta
                val documents:List<Place>? = searchPlaceResponse?.documents
                // AlertDialog.Builder(this@MainActivity).setMessage("${meta?.total_count} \n ${documents?.size}").create().show()

                // 새로운 검색이 완료되면 무조건 리스트 탭을 가장 먼저 보여주기
                binding.bnv.selectedItemId = R.id.bnv_menu_list
            }


            override fun onFailure(p0: Call<KakaoSearchPlaceResponse>, p1: Throwable) {
                Toast.makeText(this@MainActivity, "오류: ${p1.message}", Toast.LENGTH_SHORT).show()
            }

        })

    } // searchPlaces

    //  단축 검색 버튼에 리스너를 붙이는 작업 메소드
    private fun setChoiceButtonListener(){
        binding.layoutChoice.choiceWc.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceGas.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceEv.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceHospital.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceForest.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFood.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceParking.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceStore.setOnClickListener { clickChoice(it) }
    }

    // 멤버변수
    var choiceID= R.id.choice_wc

    private fun clickChoice(view: View){

        // 기존 선택되었던 버튼을 찾아 배경이미지를 선택되지 않은 하얀색 원그림으로 변경
        findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_choice)

        // 현재 클릭된 버튼 (파라미터 : view)의 배경을 선택된 회색 원그림으로 변경
        view.setBackgroundResource(R.drawable.bg_choice_select)

        // 클릭한 뷰의 id를 얻어오기
        choiceID = view.id

        when(choiceID){
            R.id.choice_wc-> searchQuery="화장실"
            R.id.choice_gas-> searchQuery="주유소"
            R.id.choice_ev-> searchQuery="충전소"
            R.id.choice_hospital-> searchQuery="병원"
            R.id.choice_forest-> searchQuery="공원"
            R.id.choice_food-> searchQuery="맛집"
            R.id.choice_parking-> searchQuery="주차장"
            R.id.choice_store-> searchQuery="편의점"

        } // when

        // 새로운 검색장소명으로 장소 요청
        searchPlaces()

        binding.etSearch.text.clear()

    }

} // MainActivity