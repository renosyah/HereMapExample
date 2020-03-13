package com.syahputrareno975.heremapexample.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.here.sdk.core.Angle;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.GestureState;
import com.here.sdk.gestures.GestureType;
import com.here.sdk.gestures.PanListener;
import com.here.sdk.gestures.PinchRotateListener;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.gestures.TwoFingerPanListener;
import com.here.sdk.mapviewlite.CameraObserver;
import com.here.sdk.mapviewlite.CameraUpdate;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapPolyline;
import com.here.sdk.mapviewlite.MapPolylineStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PickMapItemsCallback;
import com.here.sdk.mapviewlite.PickMapItemsResult;
import com.here.sdk.mapviewlite.PixelFormat;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;
import com.syahputrareno975.heremapexample.R;
import com.syahputrareno975.heremapexample.di.component.ActivityComponent;
import com.syahputrareno975.heremapexample.di.component.DaggerActivityComponent;
import com.syahputrareno975.heremapexample.di.module.ActivityModule;
import com.syahputrareno975.heremapexample.util.Unit;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.syahputrareno975.heremapexample.util.StaticVariabel.MY_PERMISSIONS_REQUEST_LOCATION;

public class MapActivity extends AppCompatActivity implements MapActivityContract.View {

    @Inject
    public MapActivityContract.Presenter presenter;

    private Context context;
    private MapViewLite mapView;

    private LinearLayout bottomSheet;
    private BottomSheetBehavior sheetBehavior;
    private Button addMarker;

    // current coordinate
    GeoCoordinates userCoordinate = new GeoCoordinates(-7.792810, 110.408499);

    // for routing
    private RoutingEngine routingEngine;
    private MapPolyline routeMapPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initWidget(savedInstanceState);
    }

    private void initWidget(Bundle savedInstanceState){
        context = this;

        injectDependency();
        presenter.attach(this);
        presenter.subscribe();

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        bottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        sheetBehavior.setHideable(false);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        addMarker = findViewById(R.id.add_marker_button);
        addMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarker(mapView.getCamera().getTarget(),"Marker","Empty Marker");
                showRoutingExample(new Waypoint(userCoordinate),new Waypoint(mapView.getCamera().getTarget()));
            }
        });

        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException ignore) {

        }

        requestLocationPermission(new Unit<Boolean>() {
            @SuppressLint("MissingPermission")
            @Override
            public void invoke(Boolean o) {
                loadMapScene();
            }
        });
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {
                    mapView.getCamera().setTarget(userCoordinate);
                    mapView.getCamera().setZoomLevel(14);
                } else {
                    Log.e("map error",errorCode.toString());
                }

                setMarker(userCoordinate,"User","Current user position");

                setTapGestureHandler();
            }
        });
    }

    private void removeCurrentRoute(){
        if (mapView != null && routeMapPolyline != null){
            mapView.getMapScene().removeMapPolyline(routeMapPolyline);
            routeMapPolyline = null;
        }
    }

    private void showRoutingExample(Waypoint startWaypoint, Waypoint destinationWaypoint){

        if (routingEngine == null) {
            return;
        }

        List<Waypoint> waypoints = new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));
        routingEngine.calculateRoute(
                waypoints,
                new RoutingEngine.CarOptions(),
                new CalculateRouteCallback() {
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {

                        removeCurrentRoute();

                        if (routingError == null && routes != null && routes.get(0) != null) {

                            try {
                                    GeoPolyline routeGeoPolyline = new GeoPolyline(routes.get(0).getPolyline());
                                    MapPolylineStyle mapPolylineStyle = new MapPolylineStyle();
                                    mapPolylineStyle.setColor(ContextCompat.getColor(context,R.color.colorPrimary), PixelFormat.ARGB_8888);
                                    mapPolylineStyle.setWidth(10);
                                    routeMapPolyline = new MapPolyline(routeGeoPolyline, mapPolylineStyle);
                                    mapView.getMapScene().addMapPolyline(routeMapPolyline);

                            } catch (InstantiationErrorException ignore) { }

                        }
                    }
                });
    }

    private void setMarker(GeoCoordinates coordinates,String name,String message){

        MapMarker defaultMarker = new MapMarker(coordinates);

        MapImage mapImage = MapImageFactory.fromResource(context.getResources(),R.drawable.marker);
        MapMarkerImageStyle style = new MapMarkerImageStyle();
        style.setScale(1.0f);
        defaultMarker.addImage(mapImage, style);

        Metadata metadata = new Metadata();
        metadata.setString("name", name);
        metadata.setString("message", message);
        defaultMarker.setMetadata(metadata);

        mapView.getMapScene().addMapMarker(defaultMarker);
    }

    private void setTapGestureHandler() {

        // on map tap
        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(@NotNull Point2D touchPoint) {
                pickMapMarker(touchPoint);
            }
        });

        // disable gesture
        mapView.getGestures().disableDefaultAction(GestureType.DOUBLE_TAP);
        mapView.getGestures().disableDefaultAction(GestureType.TWO_FINGER_TAP);
        mapView.getGestures().disableDefaultAction(GestureType.TWO_FINGER_PAN);

        // disable rotation
        mapView.getCamera().addObserver(new CameraObserver() {
            @Override
            public void onCameraUpdated(@NonNull CameraUpdate cameraUpdate) {
                if (cameraUpdate.bearing != 0) {
                    mapView.getCamera().setBearing(0);
                }
            }
        });
    }

    private void pickMapMarker(final Point2D touchPoint) {
        float radiusInPixel = 2;
        mapView.pickMapItems(touchPoint, radiusInPixel, new PickMapItemsCallback() {
            @Override
            public void onMapItemsPicked(@Nullable PickMapItemsResult pickMapItemsResult) {
                if (pickMapItemsResult == null) {
                    return;
                }

                MapMarker mapMarker = pickMapItemsResult.getTopmostMarker();
                if (mapMarker == null) {
                    return;
                }

                Metadata metadata = mapMarker.getMetadata();
                if ( metadata == null) {
                    return;
                }

                new AlertDialog.Builder(context)
                        .setTitle(metadata.getString("name"))
                        .setMessage(metadata.getString("message"))
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeCurrentRoute();
                                mapView.getMapScene().removeMapMarker(mapMarker);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Close",null)
                        .create()
                        .show();

            }
        });
    }


    @Override
    public void showProgress(Boolean show) {

    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(context,MapActivity.class));
            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        presenter.unsubscribe();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    private void requestLocationPermission(Unit<Boolean> doIt){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            doIt.invoke(true);
        }
    }

    private void injectDependency(){
        ActivityComponent listcomponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .build();

        listcomponent.inject(this);
    }
}
