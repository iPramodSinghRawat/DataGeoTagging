package com.example.pramodsinghrawat.alphageotagging;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by PramodSinghRawat on 16-04-2016.
 */
public class SuperClass extends AppCompatActivity {
    public String appDataFolder=".AlphaGeoTagging";

    /* Function to Move Image Files */
    public String moveFileToProjectFolder(File sourceFile, File destinationFile){
        /* Create Project data folder if not exist */
        File dataFolder = new File(Environment.getExternalStorageDirectory() + "/"+appDataFolder);
        boolean success = true;
        boolean move = true;
        if (!dataFolder.exists()){
            success = dataFolder.mkdir();
            if (success) {
                // Do something on success
                move = true;
            } else {
                // Do something else on failure
                move = false;
            }
        }

        if(!move){
            return "Directory Does not Exist and not able to Create";
        }else{
            InputStream inStream = null;
            OutputStream outStream = null;
            try{
                inStream = new FileInputStream(sourceFile);
                outStream = new FileOutputStream(destinationFile);
                byte[] buffer = new byte[1024];
                int length;
                //copy the file content in bytes
                while ((length = inStream.read(buffer)) > 0){
                    outStream.write(buffer, 0, length);
                }
                inStream.close();
                outStream.close();
                return "1";
                //return "File is copied successful!";
            }catch(IOException e){
                e.printStackTrace();
                //tvPath.setText(e.getMessage());
                return e.getMessage();
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();

        String crrntActvty=this.getClass().getSimpleName();

        /*Close if Current and Launcher activity is same */
        if(crrntActvty.equals("MainActivity")){
            finish();
            AppExit();
        }else{
            finish();
            Intent intent = new Intent(getBaseContext(),MainActivity.class);
            startActivity(intent);
        }
    }

    public void AppExit(){
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
