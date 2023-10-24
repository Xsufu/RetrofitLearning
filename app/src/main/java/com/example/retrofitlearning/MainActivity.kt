package com.example.retrofitlearning

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofitlearning.databinding.ActivityMainBinding
import com.example.retrofitlearning.retrofit.MainAPI
import com.example.retrofitlearning.retrofit.OutputData
import com.example.retrofitlearning.retrofit.ProductAPI
import com.squareup.picasso.Picasso
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
        val userAPI = retrofit.create(MainAPI::class.java)

        binding.sendRequest.setOnClickListener {
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
        }
    }
}