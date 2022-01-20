package com.example.jbapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Locale
import android.widget.ArrayAdapter
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.os.AsyncTask
import android.os.Build
import android.widget.*
import androidx.annotation.RequiresApi
import java.net.URL
import org.json.JSONObject
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.time.LocalTime
import android.icu.util.Calendar

import android.widget.TextView


class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        CoinTask().execute()

        val bButton: Button = findViewById(R.id.backButton)
        val rButton: Button = findViewById(R.id.zkouska)

        bButton.setOnClickListener(){
            val letsgoo = Intent(this, MainActivity::class.java)
            startActivity(letsgoo)
        }
        rButton.setOnClickListener(){
            refresh()
        }

    }

    private fun refresh() {
        val intent = Intent(applicationContext, SecondActivity::class.java)
        startActivity(intent)
        finish()
    }

    inner class CoinTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE


        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,cardano,solana,tether,xrp,terra,polkadot,dodgecoin,avalanche,polygon,litecoin,bnb,cosmos,uniswap,chainlink,stellar,tron,hedra,monero,helium,harmony,aave,zcash,kusama,nexo&vs_currencies=czk,usd").readText(Charsets.UTF_8)

            }catch (e: Exception){
                response = null
            }
            return response
        }


        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            try {
                val jsonObj = JSONObject(result)
                val poleNazev = intArrayOf(R.id.coinName1, R.id.coinName2, R.id.coinName3, R.id.coinName4, R.id.coinName5, R.id.coinName6, R.id.coinName7, R.id.coinName8, R.id.coinName9, R.id.coinName10, R.id.coinName11, R.id.coinName12, R.id.coinName13, R.id.coinName14, R.id.coinName15, R.id.coinName16, R.id.coinName17, R.id.coinName18, R.id.coinName19)
                val poleCZ = intArrayOf(R.id.czPrice1, R.id.czPrice2, R.id.czPrice3, R.id.czPrice4, R.id.czPrice5, R.id.czPrice6, R.id.czPrice7, R.id.czPrice8, R.id.czPrice9, R.id.czPrice10, R.id.czPrice11, R.id.czPrice12, R.id.czPrice13, R.id.czPrice14, R.id.czPrice15, R.id.czPrice16, R.id.czPrice17, R.id.czPrice18, R.id.czPrice19)
                val poleUS = intArrayOf(
                    R.id.usPrice1,
                    R.id.usPrice2,
                    R.id.usPrice3,
                    R.id.usPrice4,
                    R.id.usPrice5,
                    R.id.usPrice6,
                    R.id.usPrice7,
                    R.id.usPrice8,
                    R.id.usPrice9,
                    R.id.usPrice10,
                    R.id.usPrice11,
                    R.id.usPrice12,
                    R.id.usPrice13,
                    R.id.usPrice14,
                    R.id.usPrice15,
                    R.id.usPrice16,
                    R.id.usPrice17,
                    R.id.usPrice18,
                    R.id.usPrice19
                )
                val coiny = arrayOf("bitcoin","ethereum","cardano","helium","chainlink","tron","monero","cosmos","stellar","aave","harmony","solana","uniswap","nexo","tether","litecoin","polkadot","kusama","zcash")

                //NAMES
                for (i in 0..(poleNazev.size-1)){
                    val tv = findViewById(poleNazev.get(i)) as TextView
                    tv.setText((coiny.get(i)).toUpperCase())
                }

                //CZ
                val CZ = mutableListOf(" ")
                for (k in 1..19){
                    for (j in 0..(coiny.size-1)) {
                        CZ += jsonObj.getJSONObject(coiny.get(j)).getString("czk") + " Kč"
                    }
                }
                for (i in 0..(poleNazev.size-1)){
                    val tv = findViewById(poleCZ.get(i)) as TextView
                    tv.setText(CZ.get(i+1))
                }

                //US
                val US = mutableListOf(" ")
                for (k in 1..19){
                    for (j in 0..(coiny.size-1)) {
                        US += jsonObj.getJSONObject(coiny.get(j)).getString("usd") + " $"
                    }
                }
                for (i in 0..(poleNazev.size-1)){
                    val tv = findViewById(poleUS.get(i)) as TextView
                    tv.setText(US.get(i+1))
                }

                val simpleDateFormat = SimpleDateFormat("dd. MM.")
                val currentDateAndTime: String = simpleDateFormat.format(Date())

                findViewById<TextView>(R.id.date).text = currentDateAndTime
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}