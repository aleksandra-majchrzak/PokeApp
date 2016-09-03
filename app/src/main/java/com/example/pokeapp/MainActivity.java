package com.example.pokeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button getPokemonsButton;

    public static final String TAG = "POKEAPP";
    public static final String mURL = "http://pokeapi.co/api/v2/";

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

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mURL)
                .addConverterFactory(GsonConverterFactory.create()) // ogarnij za co odpowiadaja konkretne metody i czy sa niezbiedne
                .build();

        final PokeService pokeService = retrofit.create(PokeService.class);

        pokeService.getPokemons().enqueue(new Callback<PokeResponse>() {

            @Override
            public void onResponse(Call<PokeResponse> call, final Response<PokeResponse> response) {

                if(response.body().results != null && !response.body().results.isEmpty()){

                    prepareRecyclerView(response.body().results);

                    // callback call fot pokemon
                    /*
                    for(Pokemon pokemon : response.body().results) {

                        if (pokemon.sprites == null || pokemon.sprites.isEmpty() || pokemon.image == null) {

                            String url = pokemon.url;
                            String[] parts = url.split("/");

                            pokeService.getPokemon(Integer.valueOf(parts[parts.length -1])).enqueue(new Callback<Pokemon>() {
                                @Override
                                public void onResponse(Call<Pokemon> call, final Response<Pokemon> innerResponse) {

                                    if (innerResponse.body() != null) {

                                        Pokemon old = ((PokemonAdapter)recyclerView.getAdapter()).getItem(innerResponse.body().id -1);
                                        old.clone(innerResponse.body());

                                        new LoadPokemonData(old).execute(old.getDefaultImsgeUrl());
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                    } else {
                                        Log.e(TAG, "Response null or empty.");
                                    }
                                }


                                @Override
                                public void onFailure(Call<Pokemon> call, Throwable t) {
                                    Log.e(TAG, "Service error. " + t.getLocalizedMessage());
                                }
                            });
                        }
                    }*/

                    Retrofit innerRetrofit = new Retrofit.Builder()
                            .baseUrl(mURL)
                            .addConverterFactory(GsonConverterFactory.create()) // ogarnij za co odpowiadaja konkretne metody i czy sa niezbiedne
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();

                    final PokeService pokemonService = innerRetrofit.create(PokeService.class);

                    for(Pokemon pokemon : response.body().results) {

                        String url = pokemon.url;
                        String[] parts = url.split("/");

                        pokemon.id = Integer.valueOf(parts[parts.length -1]);

                        pokemonService.getPokemon(pokemon.id)
                                .flatMap(new Func1<Pokemon, Observable<Pokemon>>() {
                                    @Override
                                    public Observable<Pokemon> call(Pokemon pokemon) {

                                        Log.d("pokeapp", "in first map");
                                        Pokemon old = ((PokemonAdapter)recyclerView.getAdapter()).getItem(pokemon.id -1);
                                        old.clone(pokemon);
                                        return Observable.just(pokemon);
                                    }
                                })
                           //     .subscribeOn(Schedulers.newThread())
                                .flatMap(new Func1<Pokemon, Observable<Pokemon>>() {
                                    @Override
                                    public Observable<Pokemon> call(Pokemon pokemon) {

                                        Log.d("pokeapp", "in second map");

                                        Bitmap bitmap = null;

                                        try {
                                            InputStream ioStream = (InputStream) new URL(pokemon.getDefaultImsgeUrl()).getContent();
                                            bitmap = BitmapFactory.decodeStream(ioStream);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        Pokemon old = ((PokemonAdapter)recyclerView.getAdapter()).getItem(pokemon.id -1);
                                        old.image = bitmap;

                                        return Observable.just(pokemon);
                                    }
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Pokemon>() {
                                    @Override
                                    public final void onCompleted() {
                                        // do nothing

                                    }

                                    @Override
                                    public final void onError(Throwable e) {
                                        try {
                                            if(e != null && e.getMessage() != null)
                                                Log.e("GithubDemo", e.getMessage());
                                            else
                                                Log.e("GithubDemo", "e or message is null");
                                        } catch (Exception e1) {
                                           // Log.e("", "Error  is null " + e1.getStackTrace());
                                            e1.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public final void onNext(Pokemon response) {
                                        //adapter.addData(response);
                                //        Pokemon old = ((PokemonAdapter)recyclerView.getAdapter()).getItem(response.id -1);
                                //        old.clone(response);
             //                           new LoadPokemonData(old).execute(old.getDefaultImsgeUrl());
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                });
                    }


                    Log.d("debug", "everything's fine");
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

    private class LoadPokemonData extends AsyncTask<String,String, Bitmap> {

        Pokemon pokemon;

        public LoadPokemonData(Pokemon pokemon){
            this.pokemon = pokemon;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if(pokemon != null)
                pokemon.image = bitmap;

            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
