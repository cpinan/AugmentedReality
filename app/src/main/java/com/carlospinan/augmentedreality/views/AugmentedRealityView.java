package com.carlospinan.augmentedreality.views;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.location.Location;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.carlospinan.augmentedreality.entities.PokemonEntity;
import com.carlospinan.augmentedreality.objects.PokemonObject;
import com.carlospinan.augmentedreality.objects.RadarObject;
import com.carlospinan.augmentedreality.utils.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Piñan
 */
public class AugmentedRealityView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private boolean isInPreview;

    private RadarObject radarObject;
    private List<PokemonObject> pokemonObjectList;

    public AugmentedRealityView(Context context) {
        this(context, null);
    }

    public AugmentedRealityView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AugmentedRealityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            getHolder().addCallback(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Create objects
        createObjects(canvas);

        // Draw elements
        if (radarObject != null) {
            radarObject.onDraw(canvas);
        }

        if (pokemonObjectList != null && !pokemonObjectList.isEmpty()) {
            for (PokemonObject pokemonObject : pokemonObjectList) {
                pokemonObject.onDraw(canvas);
            }
        }
    }

    private void createObjects(Canvas canvas) {
        if (radarObject == null) {
            float width = canvas.getWidth();
            float height = canvas.getHeight();

            radarObject = new RadarObject(width, height);

            pokemonObjectList = new ArrayList<>();
            List<PokemonEntity> pokemonEntityList = Helper.getPokemonEntities(getContext());
            for (PokemonEntity pokemonEntity : pokemonEntityList) {
                PokemonObject pokemonObject = new PokemonObject(
                        pokemonEntity,
                        width,
                        height
                );
                pokemonObjectList.add(pokemonObject);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);
        startCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { /* UNUSED */ }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCamera();
    }

    private void startCamera() {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(getHolder());
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopCamera() {
        if (isInPreview && camera != null) {
            isInPreview = false;
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void setData(
            Location location,
            float[] orientation
    ) {
        if (pokemonObjectList != null && !pokemonObjectList.isEmpty()) {
            for (PokemonObject pokemonObject : pokemonObjectList) {
                pokemonObject.setCurrentOrientation(orientation);
                pokemonObject.setCurrentLocation(location);
            }
        }
        if (radarObject != null) {
            radarObject.setCurrentLocation(location);
            radarObject.setCurrentOrientation(orientation);
        }
    }
}