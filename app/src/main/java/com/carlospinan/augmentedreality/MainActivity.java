package com.carlospinan.augmentedreality;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carlospinan.augmentedreality.utils.Constants;
import com.carlospinan.augmentedreality.views.AugmentedRealityView;

/**
 * @author Carlos Piñan
 * @source http://stackoverflow.com/questions/13353185/android-get-angle-of-device-in-the-yz-plane
 * @source http://capycoding.blogspot.in/2012/10/get-angle-from-sensor-in-android.html
 * @source http://stackoverflow.com/questions/8905000/android-axes-vectors-from-currentOrientation-rotational-angles
 * @source http://stackoverflow.com/questions/9454948/android-pitch-and-roll-issue
 * @source http://stackoverflow.com/questions/7411114/android-how-to-use-sensormanager-getaltitudefloat-p0-float-p
 * @source http://stackoverflow.com/questions/13305397/using-android-accelerometer-to-detect-force-peak-value
 * @source http://developer.android.com/guide/topics/sensors/sensors_position.html
 */
public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private static final long MIN_TIME = 1000L;
    private static final float MIN_DISTANCE = 2.0f;

    private LocationManager locationManager;
    private SensorManager sensorManager;

    private Location location;
    private float[] accelerometerValues;
    private float[] geomagneticValues;

    private TextView dataTextView;
    private AugmentedRealityView augmentedRealityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

        dataTextView = (TextView) findViewById(R.id.dataTextView);
        dataTextView.setVisibility(Constants.SHOW_DATA_ON_SCREEN ? View.VISIBLE : View.GONE);
        augmentedRealityView = (AugmentedRealityView) findViewById(R.id.augmentedRealityView);

        registerListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerListeners() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { /* UNUSED */ }

    @Override
    public void onProviderEnabled(String provider) { /* UNUSED */ }

    @Override
    public void onProviderDisabled(String provider) { /* UNUSED */ }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagneticValues = event.values.clone();
        }
        if (location != null &&
                accelerometerValues != null && accelerometerValues.length > 0 &&
                geomagneticValues != null && geomagneticValues.length > 0) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, accelerometerValues, geomagneticValues);
            if (success) {
                // Remap to camera's point-of-view
                float[] outR = new float[9];
                SensorManager.remapCoordinateSystem(R,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z, outR);

                float[] orientation = new float[3];
                /*
                 * <li>currentOrientation[0]: <i>azimuth</i>, rotation around the -Z axis,
                 *                i.e. the opposite direction of Z axis.</li>
                 * <li>currentOrientation[1]: <i>pitch</i>, rotation around the -X axis,
                 *                i.e the opposite direction of X axis.</li>
                 * <li>currentOrientation[2]: <i>roll</i>, rotation around the Y axis.</li>
                 */
                SensorManager.getOrientation(outR, orientation);

                float azimuth = transform(orientation[0]);
                float pitch = transform(orientation[1]);
                float roll = transform(orientation[2]);

                if (Constants.SHOW_DATA_ON_SCREEN) {
                    String result = String.format
                            (
                                    "AccelX: %f\n" +
                                            "AccelY: %f\n" +
                                            "AccelZ: %f\n" +
                                            "geomagneticValues X: %f\n" +
                                            "geomagneticValues Y: %f\n" +
                                            "geomagneticValues Z: %f\n" +
                                            "azimuth: %f\n" +
                                            "pitch: %f\n" +
                                            "roll: %f\n" +
                                            "latitude: %f\n" +
                                            "longitude: %f",
                                    accelerometerValues[0],
                                    accelerometerValues[1],
                                    accelerometerValues[2],
                                    geomagneticValues[0],
                                    geomagneticValues[1],
                                    geomagneticValues[2],
                                    azimuth,
                                    pitch,
                                    roll,
                                    location.getLatitude(),
                                    location.getLongitude()

                            );
                    dataTextView.setText(result);
                }
                augmentedRealityView.postInvalidate();
                augmentedRealityView.setData
                        (
                                location,
                                new float[]{azimuth, pitch, roll}
                        );
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* UNUSED */ }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager != null) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME,
                    MIN_DISTANCE,
                    this
            );
        }
        if (sensorManager != null) {
            sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
            );
            sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private float transform(float value) {
        return (float) ((Math.toDegrees(value) + 360) % 360);
    }

}
