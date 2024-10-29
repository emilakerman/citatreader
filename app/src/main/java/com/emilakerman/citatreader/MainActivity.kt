package com.emilakerman.citatreader

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
        val inputStream: InputStream = applicationContext.assets.open("cites.txt")

        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().use { reader ->
            reader.forEachLine { line ->
                lineList.add(line)
            }
        }

        var currentQuote = 0;

        quoteText.text = lineList[currentQuote];



        nextButton.setOnClickListener {
            if (currentQuote == lineList.lastIndex - 1) {
                nextButton.isEnabled = false;
            }
            currentQuote++;
            quoteText.text = lineList[currentQuote];
        }
        prevButton.setOnClickListener {
            if (currentQuote == lineList.lastIndex - 1) {
                nextButton.isEnabled = true;
            }
            if (currentQuote == 0) {
                currentQuote = 0;
            } else {
                currentQuote--;
                quoteText.text = lineList[currentQuote];
            }

        }

    }
}