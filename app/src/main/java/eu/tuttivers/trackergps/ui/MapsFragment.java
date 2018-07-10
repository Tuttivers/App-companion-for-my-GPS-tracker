package eu.tuttivers.trackergps.ui;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.tuttivers.trackergps.R;
import eu.tuttivers.trackergps.smsUtils.SmsSender;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_layout, container, false);
        MapView mMapView = view.findViewById(R.id.map_dashBoard);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            sendRequestToTracker();
            view.findViewById(R.id.fab).setEnabled(false);
        });
        return view;
    }

    protected void sendRequestToTracker() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, 0);
        } else {
            SmsSender.sendSMS(getActivity());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void showLocation(String message) {
        LatLng coords = parseLocation(message);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
        markerOptions.position(coords);
        if (marker != null) {
            marker.remove();
            marker = null;
        }
        marker = mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 13));
        view.findViewById(R.id.fab).setClickable(true);
    }


    private LatLng parseLocation(String message) {
        int indexStartCut = message.lastIndexOf("f=q&q=") + 6;
        int indexEndCut = message.lastIndexOf("&z=");
        String rawCoords = message.substring(indexStartCut, indexEndCut);
        String[] coords = rawCoords.split(",");
        double latitude = Double.parseDouble(coords[0]);
        double longitude = Double.parseDouble(coords[1]);
        return new LatLng(latitude, longitude);
    }

}
