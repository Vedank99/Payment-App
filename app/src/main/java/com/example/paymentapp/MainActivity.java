package com.example.paymentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    //int PERMISSION_READ_STATE = 0;
    HashMap map = new HashMap<>();
    EditText editText;
    Button button;
    String phoneNumber = "1234";


    private static final int PERMISSION_READ_STATE = 69; //This is integer used for requesting read state
    private static final int  PERMISSION_CALL_STATE = 420; //This is integer used for requesting call state

    boolean readPerm = false; //This is bool if read permission is granted
    boolean callPerm = false; //This is bool if call permission is granted

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Making the HashMap for some reason I do not know
        map.put("KEY_LOGIN",new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
        map.put("KEY_ERROR",new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));

        USSDApi ussdApi = USSDController.getInstance(this);

        editText = findViewById(R.id.editText);

        button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            phoneNumber = editText.getText().toString();

            //Both functions are called to make readPerm and callPerm true
            readPermission();
            callPermission();

            //Both are true, we will call our function
            if (readPerm && callPerm) {

                ussdApi.callUSSDInvoke(phoneNumber, map, new USSDController.CallbackInvoke() {
                    @Override
                    public void responseInvoke(String message) {
                        // message has the response string data
                        String dataToSend = "data";
                        //if (phoneNumber.length() > 0)
                          //  dataToSend = phoneNumber;// <- send "data" into USSD's input text
                        ussdApi.send(dataToSend, message1 -> {
                            // message has the response string data from USSD
                        });
                    }

                    @Override
                    public void over(String message) {
                        // message has the response string data from USSD or error
                        // response no have input text, NOT SEND ANY DATA
                    }
                });

            } else {
                //We did not get one or both permissions for some bullshit reason
            }
        });

    }

    //This method requests for read permission
    //If request is successful readPerm becomes true
    public  void readPermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Build more 23 Read Permission is granted");
                readPerm = true;
            } else {
                Log.v("TAG","Build more 23 Read Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Build less 23 Read Permission is granted");
            readPerm = true;
        }
    }

    //This method requests for call permission
    //If request is successful callPerm becomes true
    public void callPermission() {
        try
        {
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    Log.v("TAG","CALL Permission is revoked");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL_STATE);

                }else{
                    Log.v("TAG","CALL Permission is granted");
                    callPerm = true;
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //This method is called when any of the above permissions are not granted
    //The inner switch case requests whatever permission we need based on PERMISSION_[CALL/READ]_STATE
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_READ_STATE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TAG","Read Permission Requested is granted");
                    readPerm = true;

                    //do your specific task after read phone state granted
                } else {
                    readPerm = false;
                    Log.v("TAG","Read Permission Requested is denied");
                }
                return;
            } case PERMISSION_CALL_STATE :{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TAG","Call Permission Requested is granted");
                    callPerm = true;
                }
                else {
                    Log.v("TAG","Call Permission Requested is denied");
                    callPerm = false;
                }
            }

        }
    }


}