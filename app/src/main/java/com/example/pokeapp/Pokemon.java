package com.example.pokeapp;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohru on 16.08.2016.
 */
public class Pokemon {
    int id;
    String name;
    String url;
    ArrayList<Object> types = new ArrayList<Object>();
    int height;
    int weight;
    Map<String , Object> sprites = new HashMap<String , Object>();
    Bitmap image;
    String type;

    void clone(Pokemon pokemon){
        this.id = pokemon.id;
        this.name = pokemon.name;
        if(this.url == null)
            this.url = pokemon.url;
        this.types = pokemon.types;
        this.height = pokemon.height;
        this.weight = pokemon.weight;
        this.sprites = pokemon.sprites;
        this.image = pokemon.image;
    }

    String getDefaultImsgeUrl(){
        return this.sprites.get("front_default").toString();
    }

    String getPokemonTypes(){
        String[] types = new String[this.types.size()];

        for(int i = 0; i< types.length; ++i){
            types[i] = ((Map<String , Map<String , String>>)this.types.get(i)).get("type").get("name");
        }

        return TextUtils.join("/", types);
    }
}
