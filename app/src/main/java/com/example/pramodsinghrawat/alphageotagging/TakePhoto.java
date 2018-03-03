package com.example.pramodsinghrawat.alphageotagging;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

public class TakePhoto extends SuperClass implements LocationListener {

    int REQUEST_CAMERA = 0;
    ImageView imageView;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1; // 30 Sec
    double userlatitude = 0;
    double userLongitude = 0;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private LocationManager locationManager;

    SharedPreferences sharedpreferences;
    DBhandler dbHandler = new DBhandler(this, null, null, 1);
    Button takePhotosBTN,savePhotosBTN;
    EditText remarkET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView = (ImageView) findViewById(R.id.imageView);
        remarkET = (EditText) findViewById(R.id.remarkET);
        takePhotosBTN = (Button) findViewById(R.id.takePhotosBTN);
        savePhotosBTN = (Button) findViewById(R.id.savePhotosBTN);
        takePhotosBTN.setEnabled(false);
        savePhotosBTN.setEnabled(false);
        sharedpreferences = getSharedPreferences("AlphaGeoTagging", Context.MODE_PRIVATE);

        /********** get Gps location service LocationManager object ***********/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, this);

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Location location = null;
        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled

            AlertDialog.Builder builder =new AlertDialog.Builder(this);
            builder.setMessage("Enable GPS to Proceed")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    Intent gpsOptionsIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(gpsOptionsIntent);
                                    d.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                }
                            });
            builder.create().show();

        } else {
            this.canGetLocation = true;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            // First get location from Network Provider
            if (isNetworkEnabled){
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        userlatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            userlatitude = location.getLatitude();
                            userLongitude = location.getLongitude();
                        }
                    }
                }
            }
            takePhotosBTN.setEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        userlatitude = location.getLatitude();
        userLongitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        takePhotosBTN.setEnabled(false);
        /******** Called when User off Gps *********/
        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        takePhotosBTN.setEnabled(true);
        /******** Called when User on Gps  *********/
        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    public void takePhoto(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            onCaptureImageResult(data);
        }
    }

    //private static Bitmap thumbnail=null;
    // Function: on Camera Click
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //Uri uri=new URI(Environment.getExternalStorageDirectory(),"DCIM/"+System.currentTimeMillis() + ".jpg");
        File destination = new File(Environment.getExternalStorageDirectory(),"DCIM/AlphaGeoTagging_"+System.currentTimeMillis() + ".jpg");

        Uri uri=Uri.fromFile(destination);
        String file_path=uri.toString();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(thumbnail);

        //resultTv.setText(file_path);
        //Toast.makeText(getBaseContext(), "file_path: " + file_path, Toast.LENGTH_LONG).show();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("file_path", file_path);
        editor.commit();
        savePhotosBTN.setEnabled(true);
    }

    public void submitData(View view){

        String addedOn = new Timestamp(System.currentTimeMillis()).toString();
        String remarks="";
        if(remarkET.getText().toString().equals("") || remarkET.getText().toString()==null){
            remarks="N/A";
        }else{remarks=remarkET.getText().toString();}

        String userLatLng=userlatitude+"-"+userLongitude;
        //String file_name = "alphaGeoTagging_"+userLatLng+"_"+addedOn;
        String file_name = "AlphaGeoTagging_"+userLatLng+"_"+System.currentTimeMillis();
        Record object=new Record(file_name,remarks,userLatLng,addedOn);

        String rtn=dbHandler.putRecord(object);

        if(rtn.equals("1")){
            savePhotosBTN.setEnabled(false);
            moveImageFile(file_name);
            Toast.makeText(getBaseContext(), "Records Added", Toast.LENGTH_LONG).show();
            finish();
            Intent intent = new Intent(this, ShowRecords.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(getBaseContext(), "Error Try Again: " + rtn, Toast.LENGTH_LONG).show();
        }
    }

    private void moveImageFile(String file_name){
        //Toast.makeText(getBaseContext(), "moveImageFile: file_name " + file_name, Toast.LENGTH_LONG).show();
        String file_uri_s = sharedpreferences.getString("file_path", null);//Captured File/

        File img_file=new File(Uri.parse(file_uri_s).getPath());//Captured File/

        String filename=file_name+".jpg";

        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+appDataFolder+"/"+filename;
        File destination = new File(destinationPath);

        String rsltRtn=moveFileToProjectFolder(img_file, destination);
        if(rsltRtn.equals("1")){
            //show_image_file(receiptView, destination);
            //Toast.makeText(getBaseContext(), "moved ImageFile", Toast.LENGTH_LONG).show();
        }
        else{
            //resultTv.setText(rsltRtn+" "+destinationPath);
            //Toast.makeText(getBaseContext(), "Error in: "+rsltRtn, Toast.LENGTH_LONG).show();
        }
    }

}
