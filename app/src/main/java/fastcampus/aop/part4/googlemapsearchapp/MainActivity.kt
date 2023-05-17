package fastcampus.aop.part4.googlemapsearchapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import fastcampus.aop.part4.googlemapsearchapp.databinding.ActivityMainBinding
import fastcampus.aop.part4.googlemapsearchapp.model.LocationLatLngEntity
import fastcampus.aop.part4.googlemapsearchapp.model.SearchResultEntity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intAdapter()
        initViews()
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
}