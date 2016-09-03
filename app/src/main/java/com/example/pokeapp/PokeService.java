package com.example.pokeapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mohru on 17.08.2016.
 */
public interface PokeService {

    @GET("pokemon/")
    Call<PokeResponse> getPokemons();

    @GET("pokemon/{id}/")
    //Call<Pokemon> getPokemon(@Path("id") int pokemonId);
    Observable<Pokemon> getPokemon(@Path("id") int pokemonId);
}
