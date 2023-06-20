package com.yashagrawal.weatherpro.splashScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.yashagrawal.weatherpro.MainActivity
import com.yashagrawal.weatherpro.R

class SplashScreen : AppCompatActivity() {
    lateinit var mfusedLocation: FusedLocationProviderClient
    private var REQUEST_CODE = 1010
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        mfusedLocation = LocationServices.getFusedLocationProviderClient(this)


        getLastLocation()

    }

    private fun locationEnable(): Boolean {

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getLastLocation() {
        if (checkForPermission()) {

            if (locationEnable()) {
                mfusedLocation.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        newLocation()
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("lat", location.latitude)
                            intent.putExtra("long", location.longitude)
                            startActivity(intent)
                            finish()
                        }, 2000)
                    }
                }
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
                Toast.makeText(this, "Please turn on GPS location \nHere is the last viewed data", Toast.LENGTH_SHORT).show()
            }

        } else {
            requestForPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun newLocation() {
        var locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.priority =
            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        mfusedLocation = LocationServices.getFusedLocationProviderClient(this)
        mfusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation: Location? = p0.lastLocation
        }
    }


    private fun requestForPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE
        )
    }

    private fun checkForPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

}