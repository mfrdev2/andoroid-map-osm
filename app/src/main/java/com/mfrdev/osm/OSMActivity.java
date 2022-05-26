package com.mfrdev.osm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.Toast;

import com.mfrdev.osm.databinding.ActivityOsmactivityBinding;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OSMActivity extends AppCompatActivity {
    private ActivityOsmactivityBinding binding;
    private IMapController controller;
    private MapView map;
    private IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOsmactivityBinding.inflate(getLayoutInflater());
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(binding.getRoot());
        map = binding.mapView;
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        mapController = map.getController();
        mapController.setZoom(11.0);
        //  GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);


        // map.getOverlays().add(myLocationNewOverlay);
        //  populateMapMarker(startPoint);


/*        ArrayList<OverlayItem> items = new ArrayList<>();
        OverlayItem item1 = new OverlayItem("Rallo's office",
                "my office",
                new GeoPoint(43.45020,
                        2.00517));

        Drawable marker = item1.getMarker(0);
        items.add(item1);


        ItemizedOverlayWithFocus<OverlayItem> itemizedOverlayWithFocus = new ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(), items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                Toast.makeText(getApplicationContext(),"onItemSingleTapUp",Toast.LENGTH_SHORT);
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                Toast.makeText(getApplicationContext(),"onItemLongPress",Toast.LENGTH_SHORT);
                return false;
            }
        });

        itemizedOverlayWithFocus.setFocusItemsOnTap(true);
        binding.mapView.getOverlays().add(itemizedOverlayWithFocus);*/

        mPermissionResult.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
    }


    private final ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean grantedAllPermission = true;

                for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                    if (!entry.getValue()) {
                        grantedAllPermission = false;
                    }
                }
                if (grantedAllPermission) {
                    initviews();
                } else {

                }
            });

    private void initviews() {

   /*     MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        myLocationNewOverlay.enableMyLocation();
        map.getOverlays().add(myLocationNewOverlay);
        map.getOverlays().add(myLocationNewOverlay);
*/

        CompassOverlay compassOverlay = new CompassOverlay(getApplicationContext(), new InternalCompassOrientationProvider(getApplicationContext()), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        //Enable Map Rotation
        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(getApplicationContext(), map);
        rotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(rotationGestureOverlay);

        ArrayList<GeoPoint> geoList = new ArrayList<>();
        geoList.add(new GeoPoint(23.7277, 90.4106));
          geoList.add(new GeoPoint(24.7277, 90.4106));


        initMarker(geoList);

        fastOverlay();

        polylines();

    }

    private void polylines() {
        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint(23.7277, 90.4106));
        geoPoints.add(new GeoPoint(24.7277, 91.4106));
        geoPoints.add(new GeoPoint(24.7277, 90.4106));
//add your points here
        Polyline line = new Polyline();   //see note below!
        line.setPoints(geoPoints);
        line.setColor(Color.GREEN);
        line.setWidth(20);
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Toast.makeText(mapView.getContext(), "polyline with " + polyline.getPoints().size() + "pts was tapped", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        map.getOverlayManager().add(line);
    }

    private void fastOverlay() {
        // create 10k labelled points
        // in most cases, there will be no problems of displaying >100k points, feel free to try
        List<IGeoPoint> points = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            points.add(new LabelledGeoPoint(37 + Math.random() * 5, -8 + Math.random() * 5
                    , "Point #" + i));
        }

        // wrap them in a theme
        SimplePointTheme pt = new SimplePointTheme(points, true);

        // create label style
        Paint textStyle = new Paint();
        textStyle.setStyle(Paint.Style.FILL);
        textStyle.setColor(Color.parseColor("#0000ff"));
        textStyle.setTextAlign(Paint.Align.CENTER);
        textStyle.setTextSize(24);

        // set some visual options for the overlay
        // we use here MAXIMUM_OPTIMIZATION algorithm, which works well with >100k points
        SimpleFastPointOverlayOptions opt = SimpleFastPointOverlayOptions.getDefaultStyle()
                .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                .setRadius(7).setIsClickable(true).setCellSize(15).setTextStyle(textStyle);

        // create the overlay with the theme
        final SimpleFastPointOverlay sfpo = new SimpleFastPointOverlay(pt, opt);

        // onClick callback
        sfpo.setOnClickListener(new SimpleFastPointOverlay.OnClickListener() {
            @Override
            public void onClick(SimpleFastPointOverlay.PointAdapter points, Integer point) {
                Toast.makeText(map.getContext()
                        , "You clicked " + ((LabelledGeoPoint) points.get(point)).getLabel()
                        , Toast.LENGTH_SHORT).show();
            }
        });

        // add overlay
        map.getOverlays().add(sfpo);
    }

    private void initMarker(ArrayList<GeoPoint> geoList) {
        //your items
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for (GeoPoint geoPoint : geoList) {
            OverlayItem item = new OverlayItem("Title", "Desc", geoPoint);
            populateMapMarker(geoPoint);
            // item.setMarker(getResources().getDrawable(R.drawable.ic_launcher_background));
            items.add(item);
        }
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        IGeoPoint point = item.getPoint();
                        double latitude = point.getLatitude();
                        double longitude = point.getLongitude();
                        Toast.makeText(getApplicationContext(), "Lat::" + latitude + " long::" + longitude, Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Toast.makeText(getApplicationContext(), "long", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }, getApplicationContext());
        //      mOverlay.setFocusItemsOnTap(true);


      /*  Marker startMarker = new Marker(map);
        startMarker.setPosition(new GeoPoint(24.7277, 90.4106));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher_background));
        startMarker.setTitle("Start point");*/


        map.getOverlays().add(mOverlay);
    }


    private void populateMapMarker(GeoPoint geoPoint) {
        mapController.setCenter(geoPoint);
    }

    @Override
    protected void onPause() {
        super.onPause();

        binding.mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }
}