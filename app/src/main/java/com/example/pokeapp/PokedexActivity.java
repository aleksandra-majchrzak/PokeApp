package com.example.pokeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PokedexActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button getPokemonsButton;

    public static final String TAG = "POKEAPP";
    private boolean isListShown = false;
    Parcelable savedRecyclerLayoutState;
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

        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable("BUNDLE_RECYCLER_LAYOUT");
            isListShown = savedInstanceState.getBoolean("isListShown", false);

            if (isListShown)
                prepare();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isListShown", isListShown);
        // to save current list visible position
        outState.putParcelable("BUNDLE_RECYCLER_LAYOUT", recyclerView.getLayoutManager().onSaveInstanceState());
    }

    private void prepare() {

        List<Pokemon> pokemons = new ArrayList();

        isListShown = true;

        prepareRecyclerView(pokemons);

        LoadPokemonsAsyncTask loadTask = new LoadPokemonsAsyncTask();
        loadTask.execute();
    }

    private void prepareRecyclerView(List<Pokemon> pokemonList) {
        PokemonAdapter adapter = new PokemonAdapter(pokemonList);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);


        // optional - to dynamically change number of columns due to screen size
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewWidth = recyclerView.getMeasuredWidth();
                        float cardViewWidth = getResources().getDimension(R.dimen.cell_width);

                        if (cardViewWidth > 0) {  // -2 == wrap_content for big screens
                            cardViewWidth += getResources().getDimension(R.dimen.cell_margin);

                            int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                            mLayoutManager.setSpanCount(newSpanCount);
                        }

                        mLayoutManager.requestLayout();
                    }
                });
    }

    class LoadPokemonsAsyncTask extends AsyncTask<Void, Pokemon, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Gson gson = new Gson();
            List<Pokemon> pokemons = null;

            try {
                InputStreamReader reader = new InputStreamReader(getAssets().open("pokemons.json"));
                pokemons = gson.fromJson(reader, new TypeToken<List<Pokemon>>() {
                }.getType());

                reader.close();

                for (Pokemon pokemon : pokemons) {
                    Bitmap bitmap = null;

                    try {
                        InputStream ioStream = getAssets().open("sprites/" + pokemon.id + ".png");
                        bitmap = BitmapFactory.decodeStream(ioStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pokemon.image = bitmap;

                    publishProgress(pokemon);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Pokemon... values) {
            super.onProgressUpdate(values);

            ((PokemonAdapter) recyclerView.getAdapter()).getPokemonList().add(values[0]);
            recyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (savedRecyclerLayoutState != null)
                recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }
}
