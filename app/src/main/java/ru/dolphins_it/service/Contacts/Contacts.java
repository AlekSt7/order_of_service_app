package ru.dolphins_it.service.Contacts;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.dolphins_it.service.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Contacts extends android.support.v4.app.Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;
    private static final double TARGET_LATITUDE = 55.7209234; //Координаты офиса организации
    private static final double TARGET_LONGITUDE = 37.6509284;

    public static Contacts newInstance() {
        Contacts fragment = new Contacts();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        double lat = TARGET_LATITUDE;
        double lng = TARGET_LONGITUDE;
        //устанавливаем позицию и масштаб отображения карты
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(17)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);

        if (null != googleMap) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title("Долфинс-Айти")
                    .draggable(false)
            );
        }
    }

}
