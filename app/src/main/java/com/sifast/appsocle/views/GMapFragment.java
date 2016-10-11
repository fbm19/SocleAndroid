package com.sifast.appsocle.views;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sifast.appsocle.R;
import com.sifast.appsocle.models.Feedback;
import com.sifast.appsocle.models.PointOfSale;
import com.sifast.appsocle.models.User;
import com.sifast.appsocle.tasks.MarkerInsertionTask;
import com.sifast.appsocle.tasks.FeedbackSendingTask;

import java.util.Date;


public class GMapFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmentgmap, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //TODO  handle this api version exception
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //puting the map in the fragment
            MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.myMap2);
            fragment.getMapAsync(this);


        }

        super.onViewCreated(view, savedInstanceState);
    }

    public void onLocationChanged(Location location) {
        //function called when the location is changed

        try {

            mMap.clear();
        } catch (Exception e) {

        }
        LatLng mypos = new LatLng(location.getLatitude(), location.getLongitude());

        //camera annimation
        CameraPosition camPos = new CameraPosition.Builder().target(mypos)
                .zoom(70)
                .bearing(45)
                .tilt(65)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(camUpd3);
        mMap.addMarker(new MarkerOptions().position(mypos).title( getActivity().getBaseContext().getResources().getString(R.string.maPosition)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mypos));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mypos));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    void getMyLocation() {
        //function called to get the current location
        // definition of the location manager
        LocationManager locManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        //definition of the listenner
        android.location.LocationListener locationListener = new android.location.LocationListener() {
            // Called when a new location is found by the network location provider.
            public void onLocationChanged(Location location) {
                // setting the camera of the map to insert the marker in the current position
                LatLng myLaLn = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition camPos = new CameraPosition.Builder().target(myLaLn)
                        .zoom(15)
                        .bearing(45)
                        .tilt(70)
                        .build();

                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.animateCamera(camUpd3);

                //  setting the marker in the current position
                String markerTitle = "Feedback";
                String snippet = "Population: 776733";
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title(markerTitle)
                        .snippet(snippet));

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
//        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,  locationListener);
        //set the frequency of updates
        //TODO check this permission
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        //check the permission
        Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMyLocation();
        final Date date = new Date();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                                          @Override
                                          public boolean onMarkerClick(final Marker marker) {
                                              //  Take some action here
                                              dialog = new Dialog(getActivity());
                                              dialog.setContentView(R.layout.fragment_feedback);
                                              dialog.setTitle(getActivity().getBaseContext().getResources().getString(R.string.feedback));
                                              final Button butCancel, butSendFeedBack;

                                              butCancel = (Button) dialog.findViewById(R.id.butCancelFeedback);
                                              butSendFeedBack = (Button) dialog.findViewById(R.id.butSendFeedback);

                                              butSendFeedBack.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {

                                                      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                      //TODO test this
                                                      String username = sharedPreferences.getString("username", null);
                                                      User user = new User(username, null, null, null, null);
                                                      EditText txtFeedback = (EditText) dialog.findViewById(R.id.txtFeedback);
                                                      String comment = txtFeedback.getText().toString();
                                                      PointOfSale pointOfSale = new PointOfSale(new com.sifast.appsocle.models.Location(marker.getPosition().longitude, marker.getPosition().latitude));

                                                      //decalre the feedback object and set it's attribute
                                                      Feedback feedback = new Feedback();
                                                      feedback.setDeclaredBy(username);
                                                      feedback.setDeclaredLat(String.valueOf(pointOfSale.getLocation().getLatitude()));
                                                      feedback.setDeclaredLong(String.valueOf(pointOfSale.getLocation().getLongitude()));
                                                      feedback.setDeclartionDate(date.toString());
                                                      feedback.setMessageDeclared(comment);
                                                      if(!comment.equals("")) {
                                                          FeedbackSendingTask sendFeedbackTask = new FeedbackSendingTask(feedback, getActivity());
                                                          sendFeedbackTask.execute();
                                                          dialog.dismiss();
                                                          Toast.makeText(getActivity().getApplicationContext(), getActivity().getBaseContext().getResources().getString(R.string.feedbackSentMsg), Toast.LENGTH_LONG).show();
                                                      }
                                                      else txtFeedback.setError(getActivity().getBaseContext().getResources().getString(R.string.nullFeedbackError));
                                                  }
                                              });
                                              butCancel.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      dialog.dismiss();
                                                  }
                                              });
                                              dialog.show();
                                              return true;
                                          }

                                      }
        );

        //loading all the markers
        MarkerInsertionTask insertMarkerTask = new MarkerInsertionTask(mMap,getActivity());
        insertMarkerTask.execute();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }
}
