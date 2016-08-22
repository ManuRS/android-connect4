/*
 * MapFragment.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import es.uam.eps.dadm.connect4.R;

public class MapFragment extends SupportMapFragment implements  GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                GoogleMap.OnMapLongClickListener,
                                                                GoogleMap.OnMapClickListener,
                                                                GoogleMap.OnMarkerClickListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    private final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE };

    public static String lat_ext = null;
    public static String lon_ext = null;

    /**
     * Metodo ejecutado tras crear el fragmento
     *
     * @param view view
     * @param savedInstanceState Bundle
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder( getActivity() )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();

        initListeners();
    }

    /**
     * Metodo que inicia los escuchadores
     */
    private void initListeners() {
        getMap().setOnMarkerClickListener(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnMapClickListener(this);
    }

    /**
     * Metodo que elimina los escuchadores
     */
    private void removeListeners() {
        if( getMap() != null ) {
            getMap().setOnMarkerClickListener( null );
            getMap().setOnMapLongClickListener(null);
            getMap().setOnInfoWindowClickListener(null);
            getMap().setOnMapClickListener(null);
        }
    }

    /**
     * Metodo del ciclo de vida
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListeners();
    }


    /**
     * Inicia la camara (zoom, tipo vista, etc)
     * @param location
     */
    private void initCamera( Location location ) {
        CameraPosition position = CameraPosition.builder()
                .target( new LatLng( location.getLatitude(), location.getLongitude() ) )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        getMap().animateCamera( CameraUpdateFactory.newCameraPosition( position ), null );
        String s = C4PreferencesActivity.getMapa(this.getContext());
        getMap().setMapType( MAP_TYPES[ Integer.parseInt(s) ] );
        getMap().setTrafficEnabled( true );
        getMap().setMyLocationEnabled( true );
        getMap().getUiSettings().setZoomControlsEnabled( true );
    }

    /**
     * Metodo del ciclo de vida
     */
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /**
     * Metodo del ciclo de vida
     */
    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Metodo para obtener permisos dinamicamente en M y adelante
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Para M y su sistema de permisos en tiempo real, nos avisa con el resultado
        if (requestCode == 124) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Rellamamos
                onConnected(null);
            } else {
                Toast.makeText(this.getActivity(), "No permission for actual location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Metodo para obtener permisos dinamicamente en M y adelante
     */
    @Override
    public void onConnected(Bundle bundle) {
        /*Permisos*/
        // Si la version es mayor que M debemos pedir permiso al usuario. Tambien se mira si ya se tiene porque ya se pidio
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 124);
            // El sistema respondera en la funcion onRequestPermissionsResult(int, String[], int[])
            // No se como irme de aqui sin hacer dialogo u esperar
            return;
        }
        // Version anterior que no necesita pedir permiso

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation( mGoogleApiClient );
        if(lat_ext!=null){
            mCurrentLocation = new Location( "" );
            mCurrentLocation.setLatitude( Double.parseDouble(lat_ext) );
            mCurrentLocation.setLongitude( Double.parseDouble(lon_ext) );
        }
        else if(mCurrentLocation==null){
            Toast.makeText(getContext(), getString(R.string.locerr), Toast.LENGTH_SHORT).show();
            mCurrentLocation = new Location( "" );
            mCurrentLocation.setLatitude( -1 );
            mCurrentLocation.setLongitude( -1 );
        }else{
            C4PreferencesActivity.setLat(getContext(), mCurrentLocation.getLatitude()+"");
            C4PreferencesActivity.setLon(getContext(), mCurrentLocation.getLongitude()+"");
            Toast.makeText(getContext(), "Latitud: "+mCurrentLocation.getLatitude()+"", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Longitud: "+mCurrentLocation.getLongitude()+"", Toast.LENGTH_SHORT).show();
        }
        initCamera( mCurrentLocation );
    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    /**
     * Localizacion si no hay conexion
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getContext(), getString(R.string.locerr), Toast.LENGTH_SHORT).show();
        mCurrentLocation = new Location( "" );
        mCurrentLocation.setLatitude( -1);
        mCurrentLocation.setLongitude( -1 );
        initCamera(mCurrentLocation);
    }

    /**
     * on click
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    /**
     * on click
     */
    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position( latLng );
        options.title( getAddressFromLatLng( latLng ) );
        options.icon( BitmapDescriptorFactory.defaultMarker( ) );
        getMap().addMarker( options );
    }

    /**
     * on click
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position( latLng );
        options.title( getAddressFromLatLng(latLng) );
        options.icon( BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher ) ) );
        getMap().addMarker(options);
    }

    /**
     * conversion
     */
    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( getActivity() );

        String address = "";
        try {
            address = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getAddressLine( 0 );
        } catch (Exception e ) {
        }

        return address;
    }
}
