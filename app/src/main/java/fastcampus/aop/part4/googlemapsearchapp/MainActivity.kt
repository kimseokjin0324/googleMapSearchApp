package fastcampus.aop.part4.googlemapsearchapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import fastcampus.aop.part4.googlemapsearchapp.databinding.ActivityMainBinding
import fastcampus.aop.part4.googlemapsearchapp.model.LocationLatLngEntity
import fastcampus.aop.part4.googlemapsearchapp.model.SearchResultEntity
import fastcampus.aop.part4.googlemapsearchapp.response.search.Poi
import fastcampus.aop.part4.googlemapsearchapp.response.search.Pois
import fastcampus.aop.part4.googlemapsearchapp.utility.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    // 코루틴 비동기 프로그래밍
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()
        intAdapter()
        initViews()
        bindViews()
        initData()
    }

    private fun intAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    //with() scopeFunction을 이용해서 binding에 쉽게 접근
    private fun initViews() = with(binding) {
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter
    }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    private fun setData(pois: Pois) {
        val dataList = pois.poi.map {
            SearchResultEntity(
                name = it.name ?: "빌딩명 없음",
                fullAddress = makeMainAdress(it),
                locationLatLng = LocationLatLngEntity(
                    it.noorLat,
                    it.noorLon
                )
            )
        }
        adapter.setSearchResultList(dataList) {
            Toast.makeText(
                this,
                "빌딩이름 : ${it.name}, 주소 : ${it.fullAddress} ,위도/경도 : ${it.locationLatLng}",
                Toast.LENGTH_SHORT
            )
                .show()
            startActivity(Intent(this, MapActivity::class.java))
        }
    }

    private fun searchKeyword(KeywordString: String) {
        launch(coroutineContext) {
            try { //IO Thread로 변경해서 데이터 받아오고 비동기 프로그래밍으로 Main으로 바꿔준다
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = KeywordString
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.d("검색 결과 response", body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MainActivity,
                    "검색하는 과정에서 에러가 발생했습니다. : ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }
}