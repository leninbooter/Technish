package com.alenin.technish;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

public class gettechnician extends AppCompatActivity implements OnConnectionFailedListener, ConnectionCallbacks
{

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            Log.i("TAG",resultData.getString(Constants.RESULT_DATA_KEY) );
            TextView mLatitudeText = (TextView)findViewById(R.id.mLatitudeText);

            mLatitudeText.setText(resultData.getString(Constants.RESULT_DATA_KEY));

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText( getApplicationContext(), "DIR ENCONTRADA", Toast.LENGTH_LONG).show();
            }

        }
    }

    private GoogleApiClient mGoogleApiClient;
    private AddressResultReceiver mResultReceiver;
    protected Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gettechnician);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            Log.i( "TAG", "Location: mGoogleApiClient got ");
        }else
        {
            Log.i( "TAG", "Location: mGoogleApiClient != null");
        }
    }



    public void onConnected(Bundle connectionHint)
    {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED )
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null)
            {
                Log.i( "TAG", "Location : " + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());

                TextView mLatitudeText = (TextView)findViewById(R.id.mLatitudeText);
                TextView mLongitudeText = (TextView)findViewById(R.id.mLongitudeText);

                mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                if (mGoogleApiClient.isConnected() && mLastLocation != null) {
                    startIntentService();
                }
            }else
            {
                Log.i( "TAG", "Location : null");
            }
        }else
        {
            Log.i( "TAG", "Location : denied");
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.i( "TAG", "Location: onconnectionfailed");
        // ...
    }

    @Override
    public void onConnectionSuspended (int cause)
    {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        Log.i( "TAG", "Location: mGoogleApiClient .connect");
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }
}
