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
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nactiSaldoCelkem()
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            // update TextView here!
                            aktualizujCas()
                            zobrazSaldo()
                            ulozSaldoCelkem()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }
        nactiCasPrichodu()
        zobrazCasPrichodu()
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


    // TODO: Vytvořit správný formát zobrazeného salda HH:mm

    fun zobrazCasPrichodu()
    {
        prichodCas.text = casString
    }

    fun prichod(@Suppress("UNUSED_PARAMETER")view: View)
    {
        var t = Toast.makeText(this@MainActivity, "Příchod zaznamenán", Toast.LENGTH_SHORT)
        t.show()
        casPrichoduNeform = LocalTime.now(ZoneId.of("Europe/Prague"))
        casPrichodu = casPrichoduNeform.format(formatter)
        prichodCas.text = casPrichodu
        casString = casPrichodu
        ulozCasPrichodu()
    }

    fun odchod(@Suppress("UNUSED_PARAMETER")view: View)
    {
        var t = Toast.makeText(this@MainActivity, "Odchod zaznamenán", Toast.LENGTH_SHORT)
        t.show()
        casOdchoduNeform = LocalTime.now(ZoneId.of("Europe/Prague"))
        casOdchodu = casOdchoduNeform.format(formatter)
        odchodCas.text = casOdchodu
        vypocitejSaldo()
        ulozSaldoCelkem()
    }

    fun vypocitejSaldo()
    {
        val doba = Duration.between(casPrichoduNeform, casOdchoduNeform).toMillis()
        val smenaHodiny = ((doba.toFloat())/1000)-delkaSmeny-delkaPrestavky //60/60/1000
        saldo = "%.1f".format(smenaHodiny).toFloat()
    }

    // Zobrazí Saldo
    fun zobrazSaldo()
    {
        val textView: TextView = findViewById(R.id.saldoDnes) as TextView
        val textViewc: TextView = findViewById(R.id.saldoCelkem) as TextView
        vypocet = "%.1f".format(saldo + saldoCelkem).toFloat()
        textView.text = saldo.toString()
        textViewc.text = vypocet.toString()
    }

    fun ulozCasPrichodu()
    {
        var sharedPrefCas = getDefaultSharedPreferences(this)
        //var sharedPrefCas =  getSharedPreferences(shP,Context.MODE_PRIVATE)
        var editor = sharedPrefCas.edit()
        editor.putString(shP, casPrichodu.toString()).apply()
    }

    fun nactiCasPrichodu()
    {
        var sharedPrefCas = getDefaultSharedPreferences(this)
        //al c: SharedPreferences = getSharedPreferences(shP, Context.MODE_PRIVATE)
        casString = sharedPrefCas.getString(shP,"casPrichodu")
        casPrichodu = casString
    }

    // Uloží hodnotu Saldo celkem do repozitáře
    fun ulozSaldoCelkem()
    {
        var prefsSaldo = getDefaultSharedPreferences(this)
        //var prefsSaldo: SharedPreferences = getDefaultSharedPreferences(applicationContext)
        val editor = prefsSaldo.edit()
        editor.putFloat(shS, vypocet).apply()
    }

    // Načte hodnotu Saldo celkem z repozitáře
    fun nactiSaldoCelkem()
    {
        //var prefsSaldo: SharedPreferences = getDefaultSharedPreferences(applicationContext)
        var prefsSaldo = getDefaultSharedPreferences(this)
        saldoCelkem = prefsSaldo.getFloat(shS, 0.0f)
    }

    // Aktualizuje čas a uloží jej do formátu HH:mm:ss
    fun aktualizujCas()
    {
        val cas = LocalTime.now(ZoneId.of("Europe/Prague"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val aktualniCasFormatovany = cas.format(formatter)
        val textView: TextView = findViewById(R.id.casMain) as TextView
        textView.text = aktualniCasFormatovany
    }

    // Přechod do aktivity Nastavení
    fun jdiDoNastaveni(@Suppress("UNUSED_PARAMETER")view: View)
    {
        val intent = Intent(this, NastaveniActivity::class.java)
        //intent.putExtra(sP)
        startActivity(intent)
    }

}
