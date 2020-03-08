package com.mz.prichody
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class NastaveniActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_nastaveni)
		nacti()
	}

	var shS: String = "sharedSaldo"
	var name = 0.0f

	// TODO: Přidat funkci pro nulování celkového salda
	fun jdiDoHome(@Suppress("UNUSED_PARAMETER") view: View) {
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
	}
//TODO: https://developer.android.com/guide/topics/ui/settings/use-saved-values
	// Uloží hodnotu Saldo celkem do repozitáře
	fun smazSaldo(@Suppress("UNUSED_PARAMETER") view: View) {
		var prefsSaldo = PreferenceManager.getDefaultSharedPreferences(this)
		val editor = prefsSaldo.edit()
		editor.remove(shS).apply()
	}

	fun nacti() {
		var prefsSaldo = PreferenceManager.getDefaultSharedPreferences(this)
		name = prefsSaldo.getFloat(shS, 0.0f)
	}
}
