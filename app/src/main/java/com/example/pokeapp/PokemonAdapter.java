package com.example.pokeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mohru on 17.08.2016.
 */
public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private List<Pokemon> pokemonList;
//    private PokemonViewHolder holder;

    public static final String TAG = "PokemonAdapter";

    public PokemonAdapter(List<Pokemon> pokemonList){
        this.pokemonList = pokemonList;
    }

    @Override
    public PokemonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_cell, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PokemonViewHolder holder, final int position) {
    //    this.holder = holder;

        final Pokemon pokemon = pokemonList.get(position);
        String url = pokemon.url;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create()) // ogarnij za co odpowiadaja konkretne metody i czy sa niezbiedne
                .build();

        final PokeService pokeService = retrofit.create(PokeService.class);

        String[] parts = url.split("/");

        if(pokemon.sprites == null || pokemon.sprites.isEmpty()){

            pokeService.getPokemon(Integer.valueOf(parts[parts.length -1])).enqueue(new Callback<Pokemon>() {
                @Override
                public void onResponse(Call<Pokemon> call, final Response<Pokemon> response) {

                    if (response.body() != null) {

                        pokemon.clone(response.body());

                        holder.pokemonType.setText(pokemon.getPokemonTypes());
                        new LoadPokemonData(holder.pokemonImage, pokemon).execute(response.body().getDefaultImsgeUrl());
                    } else {
                        Log.e(TAG, "Response null or empty.");
                    }
                }


                @Override
                public void onFailure(Call<Pokemon> call, Throwable t) {
                    Log.e(TAG, "Service error.");
                }
            });

            holder.pokemonImage.setImageResource(R.drawable.ic_launcher);
        }
        else{
            holder.pokemonType.setText(pokemon.getPokemonTypes());
            holder.pokemonImage.setImageBitmap(pokemon.image);
        }

        holder.pokemonName.setText(pokemonList.get(position).name.toUpperCase());
    }

    @Override
    public int getItemCount() {
        return this.pokemonList.size();
    }

    public class PokemonViewHolder extends RecyclerView.ViewHolder {

        ImageView pokemonImage;
        TextView pokemonName;
        TextView pokemonType;

        public PokemonViewHolder(View itemView) {
            super(itemView);

            pokemonImage = ((ImageView)itemView.findViewById(R.id.pokemon_imageView));
            pokemonName = (TextView) itemView.findViewById(R.id.pokemon_name_textView);
            pokemonType = (TextView) itemView.findViewById(R.id.poke_type_textView);
        }
    }

    private class LoadPokemonData extends AsyncTask<String,String, Bitmap>{

        ImageView pokemonImage;
        Pokemon pokemon;

        public LoadPokemonData(ImageView pokemonImage, Pokemon pokemon){
            this.pokemonImage = pokemonImage;
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

            if(pokemonImage != null)
                pokemonImage.setImageBitmap(bitmap);

            if(pokemon != null)
                pokemon.image = bitmap;
        }
    }
}
