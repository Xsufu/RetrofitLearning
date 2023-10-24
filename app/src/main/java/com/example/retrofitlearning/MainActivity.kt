package com.example.retrofitlearning

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofitlearning.databinding.ActivityMainBinding
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.sendRequest.setOnClickListener {
            // Выносим в отдельный поток
            CoroutineScope(Dispatchers.IO).launch {
                // Делаем запрос
                val product = productAPI.getProductById(3)
                // Запускаем на основном потоке
                runOnUiThread {
                    binding.productBrand.text = product.title
                }
            }
        }
    }
}