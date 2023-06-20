package com.yashagrawal.weatherpro
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var cityTextView : TextView
    lateinit var countryTextView: TextView
    lateinit var cityTextView2 : TextView
    lateinit var countryTextView2: TextView
     var country : String = ""
     var cityName : String = ""
    lateinit var temp : TextView
    lateinit var humidity : TextView
    lateinit var speed : TextView
    lateinit var desp : TextView

    lateinit var preferences : SharedPreferences
    lateinit var  receivedCity : String
    lateinit var  receivedCountry : String
    lateinit var  receivedTemp : String
    lateinit var  receivedHumid : String
    lateinit var  receivedSpeed : String
    lateinit var  receivedDesp : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Defining hooks
        cityTextView = findViewById(R.id.cityName)
        countryTextView = findViewById(R.id.countryName)
        cityTextView2 = findViewById(R.id.cityName2)
        countryTextView2 = findViewById(R.id.countryName2)
        temp = findViewById(R.id.temp)
        humidity = findViewById(R.id.humidity)
        speed = findViewById(R.id.speed)
        desp = findViewById(R.id.desp)

        val lat: Double = intent.getDoubleExtra("lat",0.0)
        val long: Double = intent.getDoubleExtra("long",0.0)
        if (lat != 0.0 && long != 0.0){
            getCityName(lat,long)
            Toast.makeText(this,"City Name : $cityName",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Failed , while getting city name",Toast.LENGTH_SHORT).show()
        }
        // For setting city name and country name to the layout
        cityTextView.text = cityName
        countryTextView.text = country
        cityTextView2.text = cityName
        countryTextView2.text = country

        getJsonData(cityName)

        // For storing previous response in the local storage while location and internet is off
        preferences = getSharedPreferences("mypref", MODE_PRIVATE)
        receivedCity = preferences.getString("cityName", null).toString()
        receivedCountry = preferences.getString("countryName", null).toString()
        receivedTemp = preferences.getString("temp", null).toString()
        receivedHumid = preferences.getString("humid", null).toString()
        receivedSpeed = preferences.getString("ws", null).toString()
        receivedDesp = preferences.getString("desp", null).toString()

        cityTextView.text = receivedCity
        cityTextView2.text = receivedCity
        countryTextView.text = receivedCountry
        countryTextView2.text = receivedCountry
        temp.text = receivedTemp
        humidity.text = receivedHumid
        speed.text = receivedSpeed
        desp.text = receivedDesp

    }

    private fun getJsonData(cityName: String) {
        val queue = Volley.newRequestQueue(this)
        val API_KEY = "5b9db5a78f026925a908412c759a8a47"
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${cityName}&appid=${API_KEY}"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null, { response ->
            setValues(response)
        },
            {
                Toast.makeText(this,"Some error occured while getting current data ",Toast.LENGTH_SHORT).show()
            })
        queue.add(jsonObjectRequest)
    }

    private fun setValues(response: JSONObject){
        var temperature = response.getJSONObject("main").getString("temp")
        temperature = (((temperature).toFloat() - 273.15).toInt()).toString()
        temp.text = "Temperature : "+temperature+" °C"

        var humidi = response.getJSONObject("main").getString("humidity")
        humidity.text = "Humidity : "+humidi+" %"

        var ws = response.getJSONObject("wind").getString("speed")
        ws = ((ws).toDouble() *3.6).toString()
        var numSpeed : Double = ((ws).toDouble())
        val decimalFormat = DecimalFormat("#.###")
        numSpeed = decimalFormat.format(numSpeed).toDouble()

        speed.text = "Wind Speed : "+numSpeed + " km/hr"

        var description = response.getJSONArray("weather").getJSONObject(0).getString("description")
        desp.text = "Description : "+description


        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("cityName", cityName)
        editor.putString("countryName", country)
        editor.putString("temp", "Temperature : " +temperature+" °C")
        editor.putString("humid", "Humidity : $humidi %")
        editor.putString("ws", "Wind Speed : $numSpeed Km/H")
        editor.putString("desp", "Weather Description : $description")
        editor.apply()

    }

    private fun getCityName(lat: Double, long: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val address = geocoder.getFromLocation(lat, long, 1)
            if (!address.isNullOrEmpty()) {
                country = address[0].countryName
                cityName = address[0].locality
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error getting city", Toast.LENGTH_SHORT).show()
        }
    }
}