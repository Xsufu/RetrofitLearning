package com.example.retrofitlearning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitlearning.adapter.ProductAdapter
import com.example.retrofitlearning.databinding.FragmentProductsBinding
import com.example.retrofitlearning.retrofit.MainAPI
import com.example.retrofitlearning.viewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var adapter: ProductAdapter
    private lateinit var mainAPI: MainAPI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRetrofit()

        adapter = ProductAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter

        viewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                val list = mainAPI.getAllProducts(token)
                requireActivity().runOnUiThread {
                    adapter.submitList(list.products)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRetrofit() {
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

        mainAPI = retrofit.create(MainAPI::class.java)
    }
}