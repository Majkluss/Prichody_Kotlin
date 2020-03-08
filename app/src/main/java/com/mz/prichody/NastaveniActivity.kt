package com.mz.prichody

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.TextView
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class NastaveniActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_nastaveni)
		val thread: Thread = object : Thread() {
			override fun run() {
				try {
					while (!this.isInterrupted) {
						sleep(1000)
						runOnUiThread {
							// update TextView here!
							AktualizujCas()
						}
					}
				} catch (e: InterruptedException) {
				}
			}
		}
		thread.start()
	}

	var shS: String = "sharedSaldo"

	// TODO: Přidat funkci pro nulování celkového salda
	fun jdiDoHome(@Suppress("UNUSED_PARAMETER")view: View)
	{
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
	}

	fun SmazSaldoa()
	{
		val intent = Intent(this@NastaveniActivity,MainActivity::class.java)
		intent.putExtra("smazat",0.0f)
		startActivity(intent)
	}

	// Uloží hodnotu Saldo celkem do repozitáře
	fun SmazSaldo(@Suppress("UNUSED_PARAMETER")view: View)
	{
		var prefsSaldo: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		val editor = prefsSaldo.edit()
		editor.putFloat(shS, 0.0f)
		editor.commit()
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
}
