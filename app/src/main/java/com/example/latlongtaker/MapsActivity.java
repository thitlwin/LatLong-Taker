package com.example.latlongtaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 110;
    private static final String TAG = "-- MapsActivity --";

    int zoom = ZoomLevel.Streets.value;

    private GoogleMap mMap;
    LatLng latLong;
    FusedLocationProviderClient mFusedLocationClient;
    TextInputEditText edLatitude, edLongitude;
    SupportMapFragment mapFragment;
    LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        edLatitude = findViewById(R.id.edit_text_latitude);
        edLongitude = findViewById(R.id.edit_text_longitude);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Toast.makeText(MapsActivity.this, "Updated location.", Toast.LENGTH_SHORT).show();
                showLocation(locationResult.getLastLocation());
            }
        };

        getLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(5000);
        return locationRequest;
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {

            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);

            /*mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        showLocation(location);
                    }
                }
            });*/
        }
    }

    private void showLocation(Location location) {

        latLong = new LatLng(location.getLatitude(), location.getLongitude());
        mapFragment.getMapAsync(this);

        edLatitude.setText(location.getLatitude() + "");
        edLongitude.setText(location.getLongitude() + "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
//                    getLocation();
                    enableMyLocation();
                }
                else {
                    Toast.makeText(this, "Location permission is denined.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.addMarker(new MarkerOptions().position(latLong).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(latLong));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, zoom));
        setMapLongClick(mMap);

        setPoiClick(mMap);

        enableMyLocation();
    }

    public void copyLatitude(View view) {

        if (TextUtils.isEmpty(edLatitude.getText())) {
            Toast.makeText(this, "Empty latitude", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", edLatitude.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Copied latitude.", Toast.LENGTH_SHORT).show();
    }

    public void copyLongitude(View view) {

        if (TextUtils.isEmpty(edLongitude.getText())) {
            Toast.makeText(this, "Empty latitude", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", edLongitude.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Copied longitude", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String snipped = String.format(Locale.getDefault(),
                        "Lat: %1$.5f, Long: %2$.5f", latLng.latitude, latLng.longitude);
                map.addMarker(new MarkerOptions().position(latLng)
                        .title("Dropped Pin").snippet(snipped));
            }
        });
    }

    public void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                Marker poiMarker = map.addMarker(new MarkerOptions()
                .position(pointOfInterest.latLng)
                .title(pointOfInterest.name));
                poiMarker.showInfoWindow();
            }
        });
    }

    public void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }
}
