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
        deleteButton.setOnClickListener {
            // Only removes the quote from sharedPrefs if the list has been saved.
            if (checkIfQuotesAreSaved()) {
                val savedQuotes = retrieveQuotesFromSharedPreferences()
                savedQuotes.removeAt(currentQuote);
                saveListToSharedPreferences(savedQuotes);
                lineList.removeAt(currentQuote);
            } else {
                lineList.removeAt(currentQuote);
            }
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
    // Saves the entire list of quotes to shared preferences.
    private fun saveListToSharedPreferences(lineList: MutableList<String>) {
        val sharedPreference = getSharedPreferences("SAVED_QUOTES", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        val json = Gson().toJson(lineList)
        editor.putString("savedQuotes", json)
        editor.apply()
    }
    // Reusable function to update the quote text on the screen.
    private fun changeQuoteText(currentQuote: Int, lineList: MutableList<String>) {
        quoteText.text = lineList[currentQuote];
    }
    // Exits the app and saves the current quote and the whole list of quotes.
    private fun exitAndSave(currentQuote: Int, lineList: MutableList<String>) {
        saveCurrentIndexToSharedPreferences(currentQuote);
        saveListToSharedPreferences(lineList)
        this.finishAffinity();
    }
    // Saves the position of the current quote.
    private fun saveCurrentIndexToSharedPreferences(currentQuote: Int) {
        val sharedPreference = getSharedPreferences("SAVED_INDEX",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt("currentQuote",currentQuote)
        editor.apply();
    }
    // Retrieves the position of the quote from when the user exited the app.
    private fun retrieveIndexFromSharedPreferences(): Int {
        val sharedPreference = getSharedPreferences("SAVED_INDEX", Context.MODE_PRIVATE)
        return sharedPreference.getInt("currentQuote", 0)
    }
    // Checks if the quotes have been saved or not.
    private fun checkIfQuotesAreSaved(): Boolean {
        val sharedPreference = getSharedPreferences("SAVED_QUOTES", Context.MODE_PRIVATE)
        val json = sharedPreference.getString("savedQuotes", null)
        return json != null
    }
    // Retrieves the saved quotes from shared preferences.
    private fun retrieveQuotesFromSharedPreferences(): MutableList<String> {
        val sharedPreference = getSharedPreferences("SAVED_QUOTES", Context.MODE_PRIVATE)
        // Working with json instead since it was easier to sort the list this way.
        val json = sharedPreference.getString("savedQuotes", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
    // Deals with the cites.txt and segments the data into individual lines.
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