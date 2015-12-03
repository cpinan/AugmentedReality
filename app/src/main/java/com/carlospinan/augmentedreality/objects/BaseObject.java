package com.carlospinan.augmentedreality.objects;

import android.graphics.Canvas;
import android.location.Location;

/**
 * @author Carlos Piñan
 */
public abstract class BaseObject {

    protected float width;
    protected float height;

    private Location currentLocation;
    private float[] currentOrientation;

    public BaseObject(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public abstract void onDraw(Canvas canvas);

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public float[] getCurrentOrientation() {
        return currentOrientation;
    }

    public void setCurrentOrientation(float[] currentOrientation) {
        this.currentOrientation = currentOrientation;
    }
}
