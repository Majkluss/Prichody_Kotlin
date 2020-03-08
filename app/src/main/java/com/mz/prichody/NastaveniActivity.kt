package com.mz.prichody

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button


class NastaveniActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_nastaveni)
	}

	// TODO: Přidat funkci pro nulování celkového salda
	fun jdiDoHome(@Suppress("UNUSED_PARAMETER")view: View)
	{
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
	}
}
