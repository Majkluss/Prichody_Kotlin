package com.mz.prichody

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Duration
import java.time.LocalDateTime

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

    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val delkaSmeny: Float = 8.0f
    val delkaPrestavky: Float = 0.5f
    var saldo = 0.0f
    var casPrichoduNeform = LocalDateTime.now(ZoneId.of("Europe/Prague")) //LocalDateTime.of(2020,2,28,10,0,0,0)
    var casPrichodu = casPrichoduNeform.format(formatter)
    var casOdchoduNeform = LocalDateTime.now(ZoneId.of("Europe/Prague"))
    var casOdchodu = casOdchoduNeform.format(formatter)
    var vypocet = 0.0f
    var casString: String? = ""
    private var saldoCelkem: Float = 0.0f
    var shS: String = "sharedSaldo"
    var shP: String = "sharedPrichod"

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
        casPrichoduNeform = LocalDateTime.now(ZoneId.of("Europe/Prague"))
        casPrichodu = casPrichoduNeform.format(formatter)
        prichodCas.text = casPrichodu
        UlozCasPrichodu()
    }

    fun Odchod(@Suppress("UNUSED_PARAMETER")view: View)
    {
        var t = Toast.makeText(this@MainActivity, "Odchod zaznamenán", Toast.LENGTH_SHORT)
        t.show()
        casOdchoduNeform = LocalDateTime.now(ZoneId.of("Europe/Prague"))
        casOdchodu = casOdchoduNeform.format(formatter)
        odchodCas.text = casOdchodu
        VypocitejSaldo()
        UlozSaldoCelkem()
    }

    fun VypocitejSaldo()
    {
        val doba = Duration.between(casPrichoduNeform, casOdchoduNeform)
        val smenaHodiny = (doba.getSeconds().toFloat())-delkaSmeny-delkaPrestavky
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
        val editor = sharedPrefCas.edit()
        editor.putString(shP, casPrichodu.toString())
        editor.commit()
    }

    fun NactiCasPrichodu()
    {
        val c: SharedPreferences = getSharedPreferences(shP, Context.MODE_PRIVATE)
        casString = c.getString(shP,"casPrichodu")
    }

    // Uloží hodnotu Saldo celkem do repozitáře
    fun UlozSaldoCelkem()
    {
        val sharedPrefSaldo = getSharedPreferences(shS, Context.MODE_PRIVATE)
        val editor = sharedPrefSaldo.edit()
        editor.putFloat(shS, vypocet)
        //editor.apply()
        editor.commit()
    }

    // Načte hodnotu Saldo celkem z repozitáře
    fun NactiSaldoCelkem()
    {
        val s = getSharedPreferences(shS, Context.MODE_PRIVATE)
        saldoCelkem = s.getFloat(shS, 0.0f)
    }

    // Aktualizuje čas a uloží jej do formátu HH:mm:ss
    fun AktualizujCas()
    {
        val cas = LocalDateTime.now(ZoneId.of("Europe/Prague"))
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
}

/*We can set values on our SharedPreference instance in Kotlin in the following way:

val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
var editor = sharedPreference.edit()
editor.putString("username","Anupam")
editor.putLong("l",100L)
editor.commit()
To retrieve values we do:

sharedPreference.getString("username","defaultName")
sharedPreference.getLong("l",1L)*/