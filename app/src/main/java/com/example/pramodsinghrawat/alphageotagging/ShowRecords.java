package com.example.pramodsinghrawat.alphageotagging;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowRecords extends SuperClass implements SimpleGestureFilter.SimpleGestureListener {

    TextView titleTV,latLngTV,dateTimeTV,remarkTV;
    ImageView imageView;
    Record[] object;
    private SimpleGestureFilter detector;
    int dtLen=0,i=0;
    DBhandler dbHandler = new DBhandler(this, null, null, 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_records);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleTV = (TextView) findViewById(R.id.titleTV);
        latLngTV = (TextView) findViewById(R.id.latLngTV);
        dateTimeTV = (TextView) findViewById(R.id.dateTimeTV);
        remarkTV = (TextView) findViewById(R.id.remarkTV);
        imageView = (ImageView) findViewById(R.id.imageView);

        object=dbHandler.getRecordArray();
        dtLen=object.length;

        showData(i);//here i=0;
        detector = new SimpleGestureFilter(this,this);
    }

    public void showData(int index){

        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date date = null;
        try {
            date = originalFormat.parse(object[index].getAddedOn());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = targetFormat.format(date);  // 20120821

        //vehicleDetailTv.setText("Vehicle: " + object[index].getVehicleId());/* Put Vehicle Dat a Here */
        latLngTV.setText("LatLong: "+ object[index].getLatLng().replace('-',','));
        dateTimeTV.setText("Date Time: "+formattedDate);
        //dateTimeTV.setText("Date Time: "+formattedDate+" "+ object[index].getAddedOn());
        remarkTV.setText("Remark: "+ object[index].getRemark());

        if (object[index].getFileName().equals("0")) {
            //receiptTv.setText("Receipt: N/A ");
            imageView.setImageDrawable(null);
        }
        else {
            //receiptTv.setText("Receipt: Yes");
            String file_name = object[index].getFileName();
            //vehicleFuelTypeTv.setText("FuelType: "+object.getFuelType());
            String filename=file_name+".jpg";
            String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+appDataFolder+"/"+filename;
            File destination = new File(destinationPath);
            //show_image_file(receiptView, destination);

            try {
                FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inSampleSize = 10;

                String imagePath = destination.getAbsolutePath();
                //resultTv.setText(imagePath);
                Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
                imageView.setImageBitmap(bmp);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                remarkTV.setText("FileNotFoundException: "+ e.getMessage());
                //resultTv.setText(e.getMessage());
            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //View v = getCurrentFocus();
        //Toast.makeText(MainActivity.this, "I am Touched", Toast.LENGTH_LONG).show();
        this.detector.onTouchEvent(event);
        boolean ret = super.dispatchTouchEvent(event);
        return ret;
    }

    @Override
    public void onSwipe(int direction) {
        // String str = "";
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT :
                showPre();
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                showNext();
                break;
            /*
            case SimpleGestureFilter.SWIPE_DOWN :
                //str = "Swipe Down";
                //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                break;
            case SimpleGestureFilter.SWIPE_UP :
                //str = "Swipe Up";
                //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                break;
            */
        }
    }
    public void showPre(){
        int j=i-1;
        if(j<0){
            Toast.makeText(this, "Start of List", Toast.LENGTH_SHORT).show();
            i=0;
        }
        else{
            showData(j);
            i--;
        }
    }
    public void showNext(){
        int j=i+1;
        if(j>= dtLen){
            Toast.makeText(this, "End of List", Toast.LENGTH_SHORT).show();
            i= dtLen-1;
        }
        else{
            showData(j);
            i++;
        }
    }
    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }
}
