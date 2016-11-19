package com.example.pokeapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mohru on 17.08.2016.
 */
public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private List<Pokemon> pokemonList;

    public PokemonAdapter(List<Pokemon> pokemonList) {
        this.pokemonList = pokemonList;
    }

    public List<Pokemon> getPokemonList() {
        return pokemonList;
    }

    @Override
    public PokemonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_cell, parent, false);

        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PokemonViewHolder holder, final int position) {

        final Pokemon pokemon = pokemonList.get(position);

        if (pokemon.sprites == null || pokemon.sprites.isEmpty() || pokemon.image == null)
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

    public Pokemon getItem(int position) {
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

            pokemonImage = ((ImageView) itemView.findViewById(R.id.pokemon_imageView));
            pokemonName = (TextView) itemView.findViewById(R.id.pokemon_name_textView);
            pokemonType = (TextView) itemView.findViewById(R.id.poke_type_textView);
            pokemonHeight = (TextView) itemView.findViewById(R.id.poke_height_textView);
            pokemonWeight = (TextView) itemView.findViewById(R.id.poke_weight_textView);
        }
    }

}
