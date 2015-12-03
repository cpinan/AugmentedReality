package com.carlospinan.augmentedreality.objects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;

import com.carlospinan.augmentedreality.entities.PokemonEntity;
import com.carlospinan.augmentedreality.utils.Helper;

/**
 * @author Carlos Piñan
 */
public class PokemonObject extends BaseObject {

    private PokemonEntity pokemonEntity;
    private float distance;
    private float angle;

    private Paint paintPokemonData;

    public PokemonObject(PokemonEntity pokemonEntity, float width, float height) {
        super(width, height);
        this.distance = Float.MIN_VALUE;
        this.pokemonEntity = pokemonEntity;

        paintPokemonData = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPokemonData.setARGB(255, 255, 0, 0);
        paintPokemonData.setFakeBoldText(true);
        paintPokemonData.setTextSize(30.0f);
        paintPokemonData.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (pokemonEntity != null && pokemonEntity.getBitmap() != null) {
            float bitmapWidth = pokemonEntity.getBitmap().getWidth();
            float bitmapHeight = pokemonEntity.getBitmap().getHeight();

            float posX = width * 0.5f - bitmapWidth * 0.5f;
            float posY = height * 0.5f - bitmapHeight * 0.5f;

            canvas.save();
            canvas.drawBitmap(pokemonEntity.getBitmap(), posX, posY, null);
            if (distance != Float.MIN_VALUE) {
                float pokemonDXWidth = bitmapWidth * 0.5f;
                canvas.drawText(pokemonEntity.getName(), posX + pokemonDXWidth, posY + bitmapHeight * 0.2f, paintPokemonData);
                canvas.drawText(pokemonEntity.getType(), posX + pokemonDXWidth, posY + bitmapHeight * 0.4f, paintPokemonData);
                canvas.drawText(String.format("Distancia: %.2f KM", distance), posX + pokemonDXWidth, posY + bitmapHeight * 0.6f, paintPokemonData);
                canvas.drawText(String.format("Angle: %.2f", angle), posX + pokemonDXWidth, posY + bitmapHeight * 0.8f, paintPokemonData);
            }
            canvas.restore();
        }
    }

    @Override
    public void setCurrentLocation(Location currentLocation) {
        super.setCurrentLocation(currentLocation);
        this.distance = Helper.distanceAndBearing(currentLocation, pokemonEntity.getLocation())[0] / 1000; // Kilometers
        this.angle = currentLocation.bearingTo(pokemonEntity.getLocation());
        if (this.angle < 0) {
            this.angle += 360;
        }
    }

    @Override
    public void setCurrentOrientation(float[] currentOrientation) {
        super.setCurrentOrientation(currentOrientation);
    }

}
