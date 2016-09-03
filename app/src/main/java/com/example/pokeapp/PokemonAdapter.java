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

        if(pokemon.sprites == null || pokemon.sprites.isEmpty() || pokemon.image == null)
            holder.pokemonImage.setImageResource(R.drawable.ic_launcher);
        else {

            holder.pokemonImage.setImageBitmap(pokemon.image);
            holder.pokemonType.setText(pokemon.getPokemonTypes());
            holder.pokemonHeight.setText(String.valueOf(pokemon.height));
            holder.pokemonWeight.setText(String.valueOf(pokemon.weight));
        }

        holder.pokemonName.setText(pokemonList.get(position).name.toUpperCase());
    }

    @Override
    public int getItemCount() {
        return this.pokemonList.size();
    }

    public Pokemon getItem(int position){
        return pokemonList.get(position);
    }

    public class PokemonViewHolder extends RecyclerView.ViewHolder {

        ImageView pokemonImage;
        TextView pokemonName;
        TextView pokemonType;
        TextView pokemonHeight;
        TextView pokemonWeight;

        public PokemonViewHolder(View itemView) {
            super(itemView);

            pokemonImage = ((ImageView)itemView.findViewById(R.id.pokemon_imageView));
            pokemonName = (TextView) itemView.findViewById(R.id.pokemon_name_textView);
            pokemonType = (TextView) itemView.findViewById(R.id.poke_type_textView);
            pokemonHeight = (TextView) itemView.findViewById(R.id.poke_height_textView);
            pokemonWeight = (TextView) itemView.findViewById(R.id.poke_weight_textView);
        }
    }

    public List<Pokemon> getPokemons(){
        return pokemonList;
    }

}
