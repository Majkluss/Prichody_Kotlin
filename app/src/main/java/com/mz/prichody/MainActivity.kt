package com.mz.prichody

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.parseAsHtml
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Time
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.system.measureTimeMillis

class Time(internal var hours: Int, internal var minues: Int, internal var seconds: Int)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NactiSaldoCelkem()
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            // update TextView here!
                            AktualizujCas()
                            ZobrazSaldo()
                            UlozSaldoCelkem()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }
        NactiCasPrichodu()
        ZobrazCasPrichodu()
        thread.start()
    }

    // TODO: https://www.programiz.com/kotlin-programming/examples/difference-time

    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val delkaSmeny: Float = 8.0f
    val delkaPrestavky: Float = 0.5f
    var saldo = 0.0f
    var casPrichoduNeform = LocalTime.now(ZoneId.of("Europe/Prague")) //LocalDateTime.of(2020,2,28,10,0,0,0)
    var casPrichodu = casPrichoduNeform.format(formatter)
    var casOdchoduNeform = LocalTime.now(ZoneId.of("Europe/Prague"))
    var casOdchodu = casOdchoduNeform.format(formatter)
    var vypocet = 0.0f
    var casString: String? = ""
    var saldoCelkem: Float = 0.0f
    var shS: String = "sharedSaldo"
    var shP: String = "sharedPrichod"

    val prichod = Time()
    
    fun VypocetIntervalu(start: casPrichodu, stop: casOdchodu): com.mz.prichody.Time{

    }

    val interval: Time = VypocetIntervalu(casPrichodu, casOdchodu)

    // TODO: Vytvořit správný formát zobrazeného salda HH:mm
    /*fun NactiCelek()
    {
        val textView: TextView = findViewById(R.id.textViewCelek) as TextView
        var celek = casPrichodu.toFloat() - casOdchodu.toFloat()
        textView.text = celek.toString()
    }*/

    fun ZobrazCasPrichodu()
    {
        prichodCas.text = casString
    }

    fun Prichod(@Suppress("UNUSED_PARAMETER")view: View)
    {
        var t = Toast.makeText(this@MainActivity, "Příchod zaznamenán", Toast.LENGTH_SHORT)
        t.show()
        casPrichoduNeform = LocalTime.now(ZoneId.of("Europe/Prague"))
        casPrichodu = casPrichoduNeform.format(formatter)
        prichodCas.text = casPrichodu
        casString = casPrichodu
        UlozCasPrichodu()
    }

    fun Odchod(@Suppress("UNUSED_PARAMETER")view: View)
    {
        var t = Toast.makeText(this@MainActivity, "Odchod zaznamenán", Toast.LENGTH_SHORT)
        t.show()
        casOdchoduNeform = LocalTime.now(ZoneId.of("Europe/Prague"))
        casOdchodu = casOdchoduNeform.format(formatter)
        odchodCas.text = casOdchodu
        VypocitejSaldo()
        UlozSaldoCelkem()
    }

    fun VypocitejSaldo()
    {

        val doba = Duration.between(casPrichoduNeform, casOdchoduNeform).toMillis()
        val smenaHodiny = ((doba.toFloat())/1000)-delkaSmeny-delkaPrestavky //60/60/1000
        saldo = "%.1f".format(smenaHodiny).toFloat()
    }

    // Zobrazí Saldo
    fun ZobrazSaldo()
    {
        val textView: TextView = findViewById(R.id.saldoDnes) as TextView
        val textViewc: TextView = findViewById(R.id.saldoCelkem) as TextView
        vypocet = saldo + saldoCelkem
        textView.text = saldo.toString()
        textViewc.text = vypocet.toString()
    }

    fun UlozCasPrichodu()
    {
        val sharedPrefCas =  getSharedPreferences(shP,Context.MODE_PRIVATE)
        val sharedPrefCasN = getSharedPreferences("neformatovany", Context.MODE_PRIVATE)
        val editor = sharedPrefCas.edit()
        val editorN = sharedPrefCasN.edit()
        editor.putString(shP, casPrichodu.toString())
        //editor.putLong("neformatovany", casPrichoduNeform.toLong())
        editor.commit()
    }

    fun NactiCasPrichodu()
    {
        val c: SharedPreferences = getSharedPreferences(shP, Context.MODE_PRIVATE)
        casString = c.getString(shP,"casPrichodu")
        //casPrichodu = LocalDateTime.parse(casString, formatter)
        casPrichodu = casString
    }

    // Uloží hodnotu Saldo celkem do repozitáře
    fun UlozSaldoCelkem()
    {
        //val sharedPrefSaldo = getSharedPreferences(shS, Context.MODE_PRIVATE)
        var prefsSaldo: SharedPreferences = getDefaultSharedPreferences(getApplicationContext());
        val editor = prefsSaldo.edit()
        editor.putFloat(shS, vypocet)
        //editor.apply()
        editor.commit()
    }

    // Načte hodnotu Saldo celkem z repozitáře
    fun NactiSaldoCelkem()
    {
        var prefsSaldo: SharedPreferences = getDefaultSharedPreferences(getApplicationContext());
        //val s = getSharedPreferences(shS, Context.MODE_PRIVATE)
        saldoCelkem = prefsSaldo.getFloat(shS, 0.0f)
    }

    // Aktualizuje čas a uloží jej do formátu HH:mm:ss
    fun AktualizujCas()
    {
        val cas = LocalTime.now(ZoneId.of("Europe/Prague"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val aktualniCasFormatovany = cas.format(formatter)
        val textView: TextView = findViewById(R.id.casMain) as TextView
        textView.text = aktualniCasFormatovany
    }

    // Přechod do aktivity Nastavení
    fun JdiDoNastaveni(@Suppress("UNUSED_PARAMETER")view: View)
    {
        val intent = Intent(this, NastaveniActivity::class.java)
        startActivity(intent)
    }

    /*val intent = Intent(this@MainActivity,NastaveniActivity::class.java)
    intent.putExtra("Username","John Doe")
    startActivity(intent)*/
}

/*Send value from HomeActivity
val intent = Intent(this@HomeActivity,ProfileActivity::class.java)
intent.putExtra("Username","John Doe")
startActivity(intent)
Get values in ProfileActivity
val profileName=intent.getStringExtra("Username")*/

/*We can set values on our SharedPreference instance in Kotlin in the following way:
val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
var editor = sharedPreference.edit()
editor.putString("username","Anupam")
editor.putLong("l",100L)
editor.commit()
To retrieve values we do:
sharedPreference.getString("username","defaultName")
sharedPreference.getLong("l",1L)*/