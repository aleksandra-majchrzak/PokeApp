package com.example.pokeapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button getPokemonsButton;

    public static final String TAG = "POKEAPP";
    public static final String URL = "http://pokeapi.co/api/v2/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        getPokemonsButton = (Button) findViewById(R.id.get_pokemons_button);

        getPokemonsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepare();
            }
        });
    }

    private void prepare(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()) // ogarnij za co odpowiadaja konkretne metody i czy sa niezbiedne
                .build();

        final PokeService pokeService = retrofit.create(PokeService.class);

        pokeService.getPokemons().enqueue(new Callback<PokeResponse>() {

            @Override
            public void onResponse(Call<PokeResponse> call, final Response<PokeResponse> response) {

                if(response.body().results != null && !response.body().results.isEmpty()){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prepareRecyclerView(response.body().results);
                        }
                    });
                }
                else{
                    Log.e(TAG, "Response null or empty.");
                }
            }

            @Override
            public void onFailure(Call<PokeResponse> call, Throwable t) {
                Log.e(TAG, "Service error.");
                Toast.makeText(MainActivity.this, "Error connecting with network.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void prepareRecyclerView(List<Pokemon> pokemonList){
        PokemonAdapter adapter = new PokemonAdapter(pokemonList);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
    }
}
