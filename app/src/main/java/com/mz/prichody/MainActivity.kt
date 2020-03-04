package com.mz.prichody

import android.content.Context
import android.content.Intent
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

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            // update TextView here!
                            //val aktualniCas = LocalDateTime.now(ZoneId.of("Europe/Prague"))
                            aktualizujCas()
                            zobrazSaldo()
                            ulozSaldoCelkem()
                            nactiSaldoCelkem(saldoCelkem)
                            aktualizujSaldoCelkem()

                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }
        Prichod(prichodCas)
        thread.start()
    }

    val delkaSmeny: Float = 8.0f
    val delkaPrestavky: Float = 0.5f
    var saldo = 0.0f
    var casPrichodu = LocalDateTime.now(ZoneId.of("Europe/Prague")) //LocalDateTime.of(2020,2,28,10,0,0,0)
    var casOdchodu = LocalDateTime.now(ZoneId.of("Europe/Prague"))

    val SHARED_PREFS: String  = "f"
    val TEXT: String  = "s"
    private var sal: Float = 0.0f

    fun Prichod(view: View)
    {
        var t = Toast.makeText(this@MainActivity, "Příchod zaznamenán", Toast.LENGTH_SHORT)
        t.show()
        casPrichodu = LocalDateTime.now(ZoneId.of("Europe/Prague"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val aktualniCasFormatovany = casPrichodu.format(formatter)
        prichodCas.text = aktualniCasFormatovany
    }

    //TODO: Nastavení příchozího / odchozího času a korekce salda při uložení!

    fun Odchod(view: View)
    {
        var t = Toast.makeText(this@MainActivity, "Odchod zaznamenán", Toast.LENGTH_SHORT)
        t.show()
        casOdchodu = LocalDateTime.now(ZoneId.of("Europe/Prague"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val aktualniCasFormatovany = casOdchodu.format(formatter)
        odchodCas.text = aktualniCasFormatovany
        vypocitejSaldo()
        zobrazSaldo()

        aktualizujSaldoCelkem()
        ulozSaldoCelkem()
    }

    fun vypocitejSaldo()
    {
        val doba = Duration.between(casPrichodu, casOdchodu)
        val smenaHodiny = (doba.getSeconds().toFloat())-delkaSmeny-delkaPrestavky
        saldo = "%.1f".format(smenaHodiny).toFloat()
    }

    fun zobrazSaldo()
    {
        val textView: TextView = findViewById(R.id.saldoDnes) as TextView
        textView.text = saldo.toString()
        nactiSaldoCelkem(saldoCelkem)
    }

    fun ulozSaldoCelkem()
    {
        val zaznam = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val zapis = zaznam.edit()
        zapis.putFloat(TEXT, sal)
        zapis.apply()
    }

    fun nactiSaldoCelkem(view: View)
    {
        val s = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        sal = s.getFloat(TEXT, 0.0f)
    }

    fun aktualizujSaldoCelkem()
    {
        val textView: TextView = findViewById(R.id.saldoCelkem) as TextView
        var a = saldo + sal
        textView.text = a.toString()
    }

    fun aktualizujCas()
    {
        val cas = LocalDateTime.now(ZoneId.of("Europe/Prague"))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val aktualniCasFormatovany = cas.format(formatter)
        val textView: TextView = findViewById(R.id.casMain) as TextView
        textView.text = aktualniCasFormatovany
    }

    fun jdiDoNastaveni(view: View)
    {
        val intent = Intent(this, NastaveniActivity::class.java)
        startActivity(intent)
    }
}