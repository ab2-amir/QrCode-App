package com.example.qrcodeapp;

import static java.util.Currency.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.ar.core.ArCoreApk;
import com.google.zxing.Result;

public class MainActivity extends AppCompatActivity {

    public void verifyPermissions() {
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            String[] tabPermission = {Manifest.permission.CAMERA};
            //Fenetre de permission -- travail avec fct onRequestPermissionsResult()
            requestPermissions(tabPermission, 100);
        }
    }
    private CodeScanner mCodeScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        Log.i("QR", "Link: "+ result.getText());
                        //open QrCode in Browser
                        /*
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(result.getText()));
                        startActivity(intent);
                         */
                        //Scanne QRCode then Open .gltf model with AR
                        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                        Uri intentUri =
                                Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                                        .appendQueryParameter("file", result.getText())
                                        .appendQueryParameter("mode", "ar_preferred")
                                        .appendQueryParameter("title", "Model3D")
                                        .build();
                        sceneViewerIntent.setData(intentUri);
                        sceneViewerIntent.setPackage("com.google.ar.core");
                        startActivity(sceneViewerIntent);
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPermissions();
                mCodeScanner.startPreview();
            }
        });

        Button getQrCode_btn = findViewById(R.id.button_qrcode);

        getQrCode_btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                verifyPermissions();
                mCodeScanner.startPreview();
            }
        });

        Button ar_btn = findViewById(R.id.button_ar);

        ar_btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(getApplicationContext());
                if(availability.isSupported())
                {
                    Toast.makeText(MainActivity.this, "AR IS WORKING !", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(MainActivity.this, "AR IS NOT WORKING !", Toast.LENGTH_SHORT).show();
                }

                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                Uri intentUri =
                        Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                                .appendQueryParameter("file", "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Box/glTF/Box.gltf")
                                .appendQueryParameter("mode", "ar_preferred")
                                .appendQueryParameter("title", "Model3D")
                                .build();
                sceneViewerIntent.setData(intentUri);
                sceneViewerIntent.setPackage("com.google.ar.core");
                startActivity(sceneViewerIntent);

            }
        });

    }


    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }



}