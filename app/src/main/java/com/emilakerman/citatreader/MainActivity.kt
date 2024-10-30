package com.emilakerman.citatreader

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.emilakerman.citatreader.databinding.ActivityMainBinding
import java.io.File
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val quoteText = binding.textQuote;
        val nextButton = binding.nextButton;
        val prevButton = binding.prevButton;
        val exitButton = binding.exitButton;
        val deleteButton = binding.deleteButton;
        val lineList = mutableListOf<String>()
        var currentQuote = retrieveFromSharedPreferences();

        segmentInputStream(lineList);
        quoteText.text = lineList[currentQuote];

        if (currentQuote == 0) {
            prevButton.isEnabled = false;
        }
        // TODO: Delete from local storage instead maybe
        deleteButton.setOnClickListener {
            lineList.removeAt(currentQuote);
            quoteText.text = lineList[currentQuote];
        }
        exitButton.setOnClickListener {
            this.finishAffinity();
            saveCurrentIndexToSharedPreferences(currentQuote);
        }
        nextButton.setOnClickListener {
            if (currentQuote == lineList.lastIndex - 1) {
                nextButton.isEnabled = false;
            }
            prevButton.isEnabled = true;
            currentQuote++;
            quoteText.text = lineList[currentQuote];
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
                quoteText.text = lineList[currentQuote];
            }
        }
    }
    private fun saveCurrentIndexToSharedPreferences(currentQuote: Int) {
        val sharedPreference = getSharedPreferences("SAVED_INDEX",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt("currentQuote",currentQuote)
        editor.apply();
    }
    private fun retrieveFromSharedPreferences(): Int {
        val sharedPreference = getSharedPreferences("SAVED_INDEX", Context.MODE_PRIVATE)
        return sharedPreference.getInt("currentQuote", 0)
    }
    private fun segmentInputStream(lineList: MutableList<String>) {
        val inputStream: InputStream = applicationContext.assets.open("cites.txt")
        inputStream.bufferedReader().use { reader ->
            reader.forEachLine { line ->
                lineList.add(line)
            }
        }
    }
}