package com.carlospinan.augmentedreality.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.carlospinan.augmentedreality.entities.PokemonEntity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Piñan
 */
public class Helper {

    public static float[] distanceAndBearing(Location origin, Location destination) {
        float[] results = new float[3];
        Location.distanceBetween(
                origin.getLatitude(),
                origin.getLongitude(),
                destination.getLatitude(),
                destination.getLongitude(),
                results
        );
        return results;
    }

    public static List<PokemonEntity> getPokemonEntities(Context context) {
        List<PokemonEntity> pokemonEntityList = new ArrayList<>();
        String response = getJsonFromFile(context, "pokemon.json");
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");
                final PokemonEntity pokemonEntity = new PokemonEntity();
                pokemonEntity.setName(jsonObject.getString("name"));
                pokemonEntity.setType(jsonObject.getString("type"));
                pokemonEntity.setPath(jsonObject.getString("path"));
                pokemonEntity.setLocation(latitude, longitude);
                Picasso.with(context)
                        .load(pokemonEntity.getPath())
                        .resize(250, 250)
                        .centerInside()
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                pokemonEntity.setBitmap(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                pokemonEntityList.add(pokemonEntity);
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return pokemonEntityList;
    }

    public static String getJsonFromFile(Context context, String path) {
        String json;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
