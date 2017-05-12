package com.neology.parking_neo.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.neology.parking_neo.BottomSheetDataNFC;
import com.neology.parking_neo.R;
import com.neology.parking_neo.Services.FetchAddressIntentService;
import com.neology.parking_neo.VolleyApp;
import com.neology.parking_neo.dialogs.PreciosPicker;
import com.neology.parking_neo.interfaces.ParkingAsynResponse;
import com.neology.parking_neo.interfaces.RouteMapsApiResponse;
import com.neology.parking_neo.model.Estacionamientos;
import com.neology.parking_neo.rest.Api_Parking;
import com.neology.parking_neo.rest.Api_RouteMaps;
import com.neology.parking_neo.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Cesar Segura on 24/02/2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    protected static final String TAG = "main-activity";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    private static final int LOCATION_PERMISSION_CODE = 123;
    private static final int REQUEST_EXTERNAL_PERMISSION_CODE = 666;
    public static TextView saldoTxt;
    public static TextView contador;
    public static RelativeLayout cajaContador;
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    /**
     * Displays the location address.
     */
    protected TextView mLocationAddressTextView;
    private GoogleMap mMap;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    /**
     * Visible while the address is being fetched.
     */
    private ProgressBar mProgressBar;
    /**
     * Kicks off the request to fetch an address when pressed.
     */
    private ImageView mFetchAddressButton;
    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    View.OnClickListener fetchAddressButtonHandler1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // We only start the service to fetch the address if GoogleApiClient is connected.
            if (mGoogleApiClient.isConnected() && mLastLocation != null) {
                startIntentService();
            }
            // If GoogleApiClient isn't connected, we process the user's request by setting
            // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
            // fetch the address. As far as the user is concerned, pressing the Fetch Address button
            // immediately kicks off the process of getting the address.
            mAddressRequested = true;
            updateUIWidgets();
        }
    };
    private BottomSheetBehavior mBottomSheetBehavior;
    private View bottomSheet;
    private FloatingActionButton pargarParquiBtn;
    private Marker[] markersParking;
    private ArrayList<Estacionamientos> estacionamientosArrayList;
    private SeekBar seekBar;
    private int radius = 100;
    private int REQUEST_ACCESS_LOCATION = 0;
    public static final String[] PERMISSIONS_EXTERNAL_STORAGE = {
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };
    public static final String[] PERMISSION_ACCESS_LOCATION = {
            ACCESS_FINE_LOCATION
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResultReceiver = new AddressResultReceiver(new Handler());
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";

        buildGoogleApiClient();
        checkExternalStoragePermission();
    }

    public boolean checkExternalStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }

        int readStoragePermissionState = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
        int writeStoragePermissionState = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);

        boolean externalStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        if (!externalStoragePermissionGranted) {
            requestPermissions(PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_PERMISSION_CODE);
        }

        return externalStoragePermissionGranted;
    }

    public boolean checkLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }

        int locationPermissionState = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);

        boolean locationPermissionGranted = locationPermissionState == PackageManager.PERMISSION_GRANTED;
        if (!locationPermissionGranted) {
            requestPermissions(PERMISSION_ACCESS_LOCATION, REQUEST_ACCESS_LOCATION);
        }

        return locationPermissionGranted;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        saldoTxt = (TextView) v.findViewById(R.id.saldoID);
        contador = (TextView) v.findViewById(R.id.contadorID);
        cajaContador = (RelativeLayout) v.findViewById(R.id.cajaContadorID);
        cajaContador.setVisibility(View.GONE);
        mLocationAddressTextView = (TextView) v.findViewById(R.id.location_address_view1);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar1);
        mFetchAddressButton = (ImageView) v.findViewById(R.id.fetch_address_button1);
        mFetchAddressButton.setOnClickListener(fetchAddressButtonHandler1);
        setupBottomSheet(v);
        //showBottomSheet();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        updateValuesFromBundle(savedInstanceState);

        updateUIWidgets();
        initPagoParqui(v);
        implementSeekBar(v);
        return v;
    }

    private void implementSeekBar(View v) {
        seekBar = (SeekBar) v.findViewById(R.id.seekBarId);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radius = radius + i;
                mMap.clear();
                configCamera();
                callApiParking(i);
                drawCircle(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void showBottomSheet() {
        BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetDataNFC();
        bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    private void setupBottomSheet(View v) {
        bottomSheet = v.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(190);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        initElements(v);
    }

    private void initPagoParqui(View view) {
        pargarParquiBtn = (FloatingActionButton) view.findViewById(R.id.pagarParquiID);
        pargarParquiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(2);
            }
        });
    }

    private void initElements(View v) {
        RelativeLayout recargarBtnId = (RelativeLayout) v.findViewById(R.id.recargarBtnID);
        recargarBtnId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);
            }
        });
    }

    void showDialog(int tipoMovimiento) {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = PreciosPicker.newInstance(tipoMovimiento);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_map));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                drawCircle(2000);
                createMapMarkers(estacionamientosArrayList);
                createRoute(latLng);
                pargarParquiBtn.setVisibility(View.VISIBLE);
            }
        });

    }

    private void createRoute(LatLng latLng) {
        final String url_route = Constants.getRequestUrl(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), latLng);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.getRequestUrl(
                        new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                        latLng),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, url_route);
                        Log.d(TAG, response.toString());
                        new Api_RouteMaps(new RouteMapsApiResponse() {
                            @Override
                            public void processFinish(Boolean output, PolylineOptions polylineOptions) {
                                if (output) {
                                    mMap.addPolyline(polylineOptions);
                                }
                            }
                        }).execute(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Adding request to request queue
        VolleyApp.getmInstance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if (!checkLocationPermission()) {
            Toast.makeText(getContext(), "Debe permitir el uso de la ubicación para interactuar con el mapa", Toast.LENGTH_SHORT).show();
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            getLocation();
        }
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Urra", Toast.LENGTH_SHORT).show();
                    getLocation();
                } else {
                    Toast.makeText(getContext(), "permission denied, boo!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getLocation() {
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(getContext(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            startIntentService();
        }
    }


    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        mLocationAddressTextView.setText(mAddressOutput);
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressRequested) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            mFetchAddressButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);
            mFetchAddressButton.setEnabled(true);
        }
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void configCamera() {
        CameraPosition cameraPosition = new CameraPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16, 20, 40);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Marker"));
    }

    private void callApiParking(int radius) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.URL_API_PARKING(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new Api_Parking(new ParkingAsynResponse() {
                            @Override
                            public void processFinish(Boolean output, ArrayList<Estacionamientos> mEstacionamientosArrayList) {
                                if (output) {
                                    estacionamientosArrayList = mEstacionamientosArrayList;
                                    createMapMarkers(estacionamientosArrayList);
                                }
                            }
                        }).execute(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Adding request to request queue
        VolleyApp.getmInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void drawCircle(int radius) {
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .radius(radius)
                .strokeColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .fillColor(ContextCompat.getColor(getContext(), R.color.circle)));
    }

    private void createMapMarkers(ArrayList<Estacionamientos> estacionamientosArrayList) {
        if (estacionamientosArrayList.size() > 0) {
            markersParking = new Marker[estacionamientosArrayList.size()];
            for (int i = 0; i < markersParking.length; i++) {
                markersParking[i] = mMap.addMarker(new MarkerOptions()
                        .position(estacionamientosArrayList.get(i).getUbicacion())
                        .title(estacionamientosArrayList.get(i).getNombre())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_parking)));
            }
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                mMap.clear();
                showToast(getString(R.string.address_found));
                configCamera();
                callApiParking(radius);
                drawCircle(radius);

            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }
}
