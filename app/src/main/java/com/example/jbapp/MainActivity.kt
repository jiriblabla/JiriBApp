package com.example.jbapp


import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    val API: String = "06c921750b9a82d8f5d1294e1586276f" // Use API key
    val NAME: String = "Jiří"
    lateinit var spinner:Spinner
    lateinit var adapter: ArrayAdapter<String>
    val SHARED_PREFS = "sharedPrefs"
    val TEXT = "text"

    private lateinit var text: String
    private lateinit var textView: TextView
    private lateinit var editText: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var data = arrayListOf<String>("Praha","Brno","Ostrava","Olomouc","Opava","Frenštát p.R.")
        val showMore: Button = findViewById (R.id.showMore)

        adapter = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_dropdown_item,data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner = findViewById(R.id.spinner) as Spinner
        spinner.adapter=adapter

        editText = findViewById<EditText>(R.id.noteContent)
        textView = findViewById<TextView>(R.id.testView)
        saveButton = findViewById<Button>(R.id.save)
        deleteButton = findViewById<Button>(R.id.delete)

        saveButton.setOnClickListener() {
            saveData()
            textView.setText(editText.getText().toString())

        }
        deleteButton.setOnClickListener() {
            clearData()
            saveData()

        }
        showMore.setOnClickListener(){
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        loadData()
        updateViews()


        spinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> reloadData("praha,cz")
                    1 -> reloadData("brno,cz")
                    2 -> reloadData("ostrava,cz")
                    3 -> reloadData("olomouc,cz")
                    4 -> reloadData("opava,cz")
                    5 -> reloadData("frenštát pod radhoštěm,cz")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    fun saveData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TEXT, textView.getText().toString())
        editor.apply()
        Toast.makeText(this, "Uloženo", Toast.LENGTH_SHORT).show()
    }


    fun loadData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        text = sharedPreferences.getString(TEXT, "")!!
    }

    fun updateViews() {
        textView.text = text
    }

    fun clearData(){
        textView.text = ""
    }

    fun reloadData(test:String){
        weatherTask(test).execute()
    }

    inner class weatherTask(val cities: String) : AsyncTask<String, Void, String>() {

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
                response = (URL("https://api.openweathermap.org/data/2.5/weather?q=$cities&units=metric&appid=$API").readText(Charsets.UTF_8)).dropLast(1)+ "," +responseCrypto

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

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = SimpleDateFormat("dd. MM. yyyy").format(Date(updatedAt*1000))

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
                findViewById<TextView>(R.id.tempRight).text = tempDble
                findViewById<TextView>(R.id.priceBTCcz).text = priceBTCcz
                findViewById<TextView>(R.id.priceBTCus).text = priceBTCus
                findViewById<TextView>(R.id.priceETHcz).text = priceETHcz
                findViewById<TextView>(R.id.priceETHus).text = priceETHus
                findViewById<TextView>(R.id.priceADAcz).text = priceADAcz
                findViewById<TextView>(R.id.priceADAus).text = priceADAus
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("HH:mm").format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("HH:mm").format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }

    }
}