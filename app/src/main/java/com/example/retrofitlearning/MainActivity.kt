package com.example.retrofitlearning

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitlearning.adapter.ProductAdapter
import com.example.retrofitlearning.databinding.ActivityMainBinding
import com.example.retrofitlearning.retrofit.MainAPI
import com.example.retrofitlearning.retrofit.ProductAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ProductAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter

        // Создаём перехватчик и указываем его уровень
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // Создаём клиент с нашим перехватчиком
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        // Создаём билдер
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // Указываем интерфейс для работы
        val productAPI = retrofit.create(ProductAPI::class.java)
        val userAPI = retrofit.create(MainAPI::class.java)

        /*binding.sendRequest.setOnClickListener {
            // Выносим в отдельный поток
            CoroutineScope(Dispatchers.IO).launch {
                // Делаем запрос
                //val product = productAPI.getProductById(3)
                val user = userAPI.auth(
                    OutputData(
                        binding.username.text.toString(),
                        binding.password.text.toString()
                    )
                )

                // Запускаем на основном потоке
                runOnUiThread {
                    //binding.surname.text = product.title
                    binding.apply {
                        Picasso.get().load(user.image).into(avatarIV)
                        firstNameHolder.text = user.firstName
                        surenameHolder.text = user.lastName
                    }
                }
            }
        }*/

        // Слушатель для поиска
        binding.sv.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch {
                    // val list = userAPI.getAllProducts()
                    val list = query?.let { userAPI.getProductBySearch(it) }
                    runOnUiThread {
                        adapter.submitList(list?.products)
                    }
                }

                return true
            }
        })
    }
}