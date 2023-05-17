package fastcampus.aop.part4.googlemapsearchapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import fastcampus.aop.part4.googlemapsearchapp.databinding.ActivityMainBinding
import fastcampus.aop.part4.googlemapsearchapp.model.LocationLatLngEntity
import fastcampus.aop.part4.googlemapsearchapp.model.SearchResultEntity
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
        setData()
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

    private fun setData() {
        val dataList = (0..10).map {
            SearchResultEntity(
                name = "빌딩 $it",
                fullAddress = "주소 $it",
                locationLatLng = LocationLatLngEntity(
                    it.toFloat(), it.toFloat()
                )
            )
        }
        adapter.setSearchResultList(dataList) {
            Toast.makeText(this, "빌딩이름 : ${it.name}, 주소 : ${it.fullAddress}", Toast.LENGTH_SHORT)
                .show()
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
                        }
                    }
                }

            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }
}