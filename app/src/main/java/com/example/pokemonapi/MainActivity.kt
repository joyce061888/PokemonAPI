package com.example.pokemonapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    // list of pokemon names to get data and images for
    val pokemonNames = listOf("chimchar", "piplup", "turtwig")
    var currentPokemonIndex = 0

    // views
    lateinit var button: Button
    lateinit var imageView: ImageView
    lateinit var pokemonTypeTextView: TextView
    lateinit var pokemonNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize views
        button = findViewById<Button>(R.id.pokemonButton)
        imageView = findViewById<ImageView>(R.id.pokemonImage)
        pokemonTypeTextView = findViewById<TextView>(R.id.pokemonType)
        pokemonNameTextView = findViewById<TextView>(R.id.pokemonName)

        // set a click listener for the button
        button.setOnClickListener{
            currentPokemonIndex = (currentPokemonIndex + 1) % pokemonNames.size
            getPokemonDataAndImage(pokemonNames[currentPokemonIndex])
        }

        // init data and image display
        getPokemonDataAndImage(pokemonNames[currentPokemonIndex])
    }

    private fun getPokemonDataAndImage(pokemonName: String) {
        val client = AsyncHttpClient()
        client["https://pokeapi.co/api/v2/pokemon/$pokemonName/", object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                Log.d("Pokemon", "response successful$json")
                if (json != null) {
                    // get name and type
                   // val type = json.jsonObject.getJSONObject("types").getJSONObject("0").getJSONObject("type").getString("name")
                    val name = json.jsonObject.getString("name")
                    val typesArray = json.jsonObject.getJSONArray("types")
                    // Iterate through each type object and extract the "name" attribute
                    val typeNames = ArrayList<String>()
                    for (i in 0 until typesArray.length()) {
                        val typeObject = typesArray.getJSONObject(i).getJSONObject("type")
                        val typeName = typeObject.getString("name")
                        typeNames.add(typeName)
                    }
                    // get pokemon image
                    val sprites = json.jsonObject.getJSONObject("sprites") // endpoint in PokeAPI for images
                    val frontDefaultImageURL = sprites.getString("front_default")
                    Log.d("pokemonImageURL", "Front Default Image URL: $frontDefaultImageURL")
                    Log.d("pokemonType", "Pokemon Type: $typeNames")
                    Log.d("pokemonName", "Pokemon Name: $name")
                    // load image and update text views
                    loadImage(frontDefaultImageURL)
                    pokemonNameTextView.text = "Pokemon Name: $name"
                    var type = typeNames.joinToString(", ")
                    pokemonTypeTextView.text = "Pokemon Type: $type"
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Pokemon Error", errorResponse)
            }
        }]
    }

    private fun loadImage(imageURL: String) {
        Glide.with(this)
            .load(imageURL)
            .fitCenter()
            .into(imageView)
    }

}