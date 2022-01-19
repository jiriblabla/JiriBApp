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






class MainActivity : AppCompatActivity() {

    val CITY: String = "frenštát pod radhoštěm,cz"
    val API: String = "06c921750b9a82d8f5d1294e1586276f" // Use API key
    val NAME: String = "Jiří"
    lateinit var spinner:Spinner
    lateinit var adapter: ArrayAdapter<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var data = arrayListOf<String>("Praha","Brno","Ostrava","Olomouc","Opava")
        adapter = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_dropdown_item,data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner = findViewById(R.id.spinner) as Spinner
        spinner.adapter=adapter

        weatherTask().execute()


        val showMore: Button = findViewById (R.id.showMore)

        showMore.setOnClickListener(){
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }




    }




    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE


        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            var responseCrypto:String = (URL("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum,cardano,solana,tether,xrp,terra,polkadot,dodgecoin&vs_currencies=czk,usd").readText(Charsets.UTF_8)).drop(1)


            try{
                response = (URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(Charsets.UTF_8)).dropLast(1)+ "," +responseCrypto

            }catch (e: Exception){
                response = null
            }
            return response
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun getGreetingMessage():String{
            val c = Calendar.getInstance()
            val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

            return when (timeOfDay) {
                in 0..11 -> "Dobré ráno, $NAME"
                in 12..17 -> "Dobré odpoledne, $NAME"
                in 18..24 -> "Dobrý večer, $NAME"
                else -> "Dobrý den, $NAME"
            }
        }
        private fun getSvatek():String{
            //var svatekData:String = URL("https://svatky.adresa.info/json").readText(Charsets.UTF_8)
            return "svatekData"
        }




        @RequiresApi(Build.VERSION_CODES.N)
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)




                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = SimpleDateFormat("dd. MM. yyyy").format(Date(updatedAt*1000))


                val temp = (main.getString("temp"))+"°C"
                val tempStr = (main.getString("temp"))
                val tempDble = ((tempStr.toDouble()).toBigDecimal().setScale(0, RoundingMode.HALF_UP)).toPlainString()+" °C"
                val tempM = (((main.getString("temp_min")).toDouble()).toBigDecimal().setScale(0, RoundingMode.HALF_UP)).toPlainString()
                val tempX = (((main.getString("temp_max")).toDouble()).toBigDecimal().setScale(0, RoundingMode.HALF_UP)).toPlainString()
                val tempMin = tempM +"°C"
                val tempMax = tempX +"°C"

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + " km/h"



                val priceBTCcz = jsonObj.getJSONObject("bitcoin").getString("czk")+" Kč"
                val priceBTCus = jsonObj.getJSONObject("bitcoin").getString("usd")+" $"
                val priceETHcz = jsonObj.getJSONObject("ethereum").getString("czk")+" Kč"
                val priceETHus = jsonObj.getJSONObject("ethereum").getString("usd")+" $"
                val priceADAcz = jsonObj.getJSONObject("cardano").getString("czk")+" Kč"
                val priceADAus = jsonObj.getJSONObject("cardano").getString("usd")+" $"


                findViewById<TextView>(R.id.address).text = getGreetingMessage()
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.svatek).text = "ahoj"

                findViewById<TextView>(R.id.tempRight).text = tempDble
                findViewById<TextView>(R.id.priceBTCcz).text = priceBTCcz
                findViewById<TextView>(R.id.priceBTCus).text = priceBTCus
                findViewById<TextView>(R.id.priceETHcz).text = priceETHcz
                findViewById<TextView>(R.id.priceETHus).text = priceETHus
                findViewById<TextView>(R.id.priceADAcz).text = priceADAcz
                findViewById<TextView>(R.id.priceADAus).text = priceADAus


                findViewById<TextView>(R.id.testCisla).text = result

                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("HH:mm").format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("HH:mm").format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                //findViewById<TextView>(R.id.pressure).text = pressure
                //findViewById<TextView>(R.id.humidity).text = humidity

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}