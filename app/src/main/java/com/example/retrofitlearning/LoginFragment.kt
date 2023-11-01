package com.example.retrofitlearning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.retrofitlearning.databinding.FragmentLoginBinding
import com.example.retrofitlearning.retrofit.MainAPI
import com.example.retrofitlearning.retrofit.OutputData
import com.example.retrofitlearning.viewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var mainAPI: MainAPI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRetrofit()

        binding.buttonLogin.setOnClickListener {
            auth(
                OutputData(
                    binding.ptLogin.text.toString(),
                    binding.ptPassword.text.toString()
                )
            )
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

    private fun auth(authRequest: OutputData) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainAPI.auth(authRequest)
            val errorMessage =
                response.errorBody()?.string()?.let { JSONObject(it).getString("message") }

            if (errorMessage != null) {
                requireActivity().runOnUiThread {
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                requireActivity().runOnUiThread {
                    viewModel.token.value = response.body()?.token
                    findNavController().navigate(R.id.action_loginFragment_to_productsFragment)
                }
            }
        }
    }
}