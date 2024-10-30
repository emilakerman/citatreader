package com.emilakerman.citatreader

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emilakerman.citatreader.databinding.ActivityMainBinding
import java.io.InputStream
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val quoteText by lazy { binding.textQuote; }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val nextButton = binding.nextButton;
        val prevButton = binding.prevButton;
        val exitButton = binding.exitButton;
        val deleteButton = binding.deleteButton;
        val lineList = mutableListOf<String>()
        var currentQuote = retrieveIndexFromSharedPreferences();

        // Reads the file and initializes the first text.
        segmentInputStream(lineList);
        changeQuoteText(currentQuote, lineList);

        if (currentQuote == 0) {
            prevButton.isEnabled = false;
        }
        // TODO: Delete from local storage instead maybe
        deleteButton.setOnClickListener {
            lineList.removeAt(currentQuote);
            changeQuoteText(currentQuote, lineList);
        }
        exitButton.setOnClickListener { exitAndSave(currentQuote, lineList) }
        nextButton.setOnClickListener {
            if (currentQuote == lineList.lastIndex - 1) {
                nextButton.isEnabled = false;
            }
            prevButton.isEnabled = true;
            currentQuote++;
            changeQuoteText(currentQuote, lineList);
        }
        prevButton.setOnClickListener {
            if (currentQuote == lineList.lastIndex - 1) {
                nextButton.isEnabled = true;
            }
            if (currentQuote == 1) {
                prevButton.isEnabled = false;
            }
            if (currentQuote == 0) {
                currentQuote = 0;
            } else {
                currentQuote--;
                changeQuoteText(currentQuote, lineList);
            }
        }
    }
    private fun saveListToSharedPreferences(lineList: MutableList<String>) {
        val sharedPreference = getSharedPreferences("SAVED_QUOTES", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        val json = Gson().toJson(lineList)
        editor.putString("savedQuotes", json)
        editor.apply()
    }

    private fun changeQuoteText(currentQuote: Int, lineList: MutableList<String>) {
        quoteText.text = lineList[currentQuote];
    }
    private fun exitAndSave(currentQuote: Int, lineList: MutableList<String>) {
        saveCurrentIndexToSharedPreferences(currentQuote);
        saveListToSharedPreferences(lineList)
        this.finishAffinity();
    }
    private fun saveCurrentIndexToSharedPreferences(currentQuote: Int) {
        val sharedPreference = getSharedPreferences("SAVED_INDEX",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt("currentQuote",currentQuote)
        editor.apply();
    }
    private fun retrieveIndexFromSharedPreferences(): Int {
        val sharedPreference = getSharedPreferences("SAVED_INDEX", Context.MODE_PRIVATE)
        return sharedPreference.getInt("currentQuote", 0)
    }
    private fun retrieveQuotesFromSharedPreferences(): MutableList<String> {
        val sharedPreference = getSharedPreferences("SAVED_QUOTES", Context.MODE_PRIVATE)
        val json = sharedPreference.getString("savedQuotes", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
    private fun segmentInputStream(lineList: MutableList<String>) {
        val quotes = retrieveQuotesFromSharedPreferences()
        if (quotes.isNotEmpty()) {
            lineList.addAll(quotes);
        } else {
            val inputStream: InputStream = assets.open("cites.txt")
            inputStream.bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    lineList.add(line)
                }
            }
        }
    }
}