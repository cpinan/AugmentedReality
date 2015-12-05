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
    private boolean isVisible;

    private float dX;
    private Paint paintPokemonData;
    private double mAzimuthTeoretical;

    public PokemonObject(PokemonEntity pokemonEntity, float width, float height) {
        super(width, height);
        this.distance = Float.MIN_VALUE;
        this.pokemonEntity = pokemonEntity;
        this.dX = 0;

        paintPokemonData = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPokemonData.setARGB(255, 255, 0, 0);
        paintPokemonData.setFakeBoldText(true);
        paintPokemonData.setTextSize(30.0f);
        paintPokemonData.setTextAlign(Paint.Align.CENTER);

        this.isVisible = false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isVisible && pokemonEntity != null && pokemonEntity.getBitmap() != null) {
            float bitmapWidth = pokemonEntity.getBitmap().getWidth();
            float bitmapHeight = pokemonEntity.getBitmap().getHeight();

            float posX = (width * 0.5f - bitmapWidth * 0.5f);
            float posY = height * 0.5f - bitmapHeight * 0.5f;

            canvas.save();
            canvas.drawBitmap(pokemonEntity.getBitmap(), posX, posY, null);
            if (distance != Float.MIN_VALUE) {
                float pokemonDXWidth = bitmapWidth * 0.5f;
                canvas.drawText(pokemonEntity.getName(), posX + pokemonDXWidth, posY + bitmapHeight * 0.2f, paintPokemonData);
                canvas.drawText(pokemonEntity.getType(), posX + pokemonDXWidth, posY + bitmapHeight * 0.4f, paintPokemonData);
                canvas.drawText(String.format("Distancia: %.2f KM", distance), posX + pokemonDXWidth, posY + bitmapHeight * 0.6f, paintPokemonData);
                canvas.drawText(String.format("Angle: %.2f", mAzimuthTeoretical), posX + pokemonDXWidth, posY + bitmapHeight * 0.8f, paintPokemonData);
            }
            canvas.restore();
        }
    }

    @Override
    public void setCurrentLocation(Location currentLocation) {
        super.setCurrentLocation(currentLocation);
        this.distance = Helper.distanceAndBearing(currentLocation, pokemonEntity.getLocation())[0] / 1000; // Kilometers
    }

    @Override
    public void setCurrentOrientation(float[] currentOrientation) {
        super.setCurrentOrientation(currentOrientation);
        if (getCurrentLocation() != null) {
            double mAzimuthReal = currentOrientation[0];
            mAzimuthTeoretical = Helper.calculateAngle(pokemonEntity.getLocation(), getCurrentLocation());
            double minAngle = Helper.calculateAzimuthAccuracy(mAzimuthTeoretical).get(0);
            double maxAngle = Helper.calculateAzimuthAccuracy(mAzimuthTeoretical).get(1);
            isVisible = Helper.isBetween(minAngle, maxAngle, mAzimuthReal);
            dX = (float) (mAzimuthTeoretical - mAzimuthReal);
        }
    }

}
