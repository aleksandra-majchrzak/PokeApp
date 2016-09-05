package com.example.pokeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PokedexActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button getPokemonsButton;

    public static final String TAG = "POKEAPP";
    //public static final String mURL = "http://pokeapi.co/api/v2/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

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

        List<Pokemon> pokemons = getPokemons();

        prepareRecyclerView(pokemons);

        for(Pokemon pokemon : pokemons) {

            Observable.just(pokemon)
                .flatMap(new Func1<Pokemon, Observable<Pokemon>>() {
                    @Override
                    public Observable<Pokemon> call(Pokemon pokemon) {

                        Bitmap bitmap = null;

                        try {
                            InputStream ioStream = getAssets().open("sprites/" + pokemon.id + ".png");
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
                                Log.e(TAG, e.getMessage());
                            else
                                Log.e(TAG, "e or message is null");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public final void onNext(Pokemon response) {

                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
        }
    }

    private void prepareRecyclerView(List<Pokemon> pokemonList){
        PokemonAdapter adapter = new PokemonAdapter(pokemonList);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);


        // optional
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewWidth = recyclerView.getMeasuredWidth();
                        float cardViewWidth = getResources().getDimension(R.dimen.cell_width);

                        if(cardViewWidth > 0){  // -2 == wrap_content for big screens
                            cardViewWidth += getResources().getDimension(R.dimen.cell_margin);

                            int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                            mLayoutManager.setSpanCount(newSpanCount);
                        }

                        mLayoutManager.requestLayout();
                    }
                });
    }

    private List<Pokemon> getPokemons(){

        Gson gson = new Gson();
        List<Pokemon> pokemons = null;

        try {
            pokemons = gson.fromJson(new InputStreamReader(getAssets().open("pokemons.json")),
                    new TypeToken<List<Pokemon>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pokemons;
    }
}
