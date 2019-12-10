package mobile.sms.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import java.util.logging.Logger;

import me.dm7.barcodescanner.zxing.ZXingScannerView;



public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    // allows camera to turn on
    private int ZXING_CAMERA_PERMISSION = 1;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // shamelessly copied design from Maria
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            ZXING_CAMERA_PERMISSION);
                }
            } else {
                // Programmatically initialize the scanner view
                mScannerView = new ZXingScannerView(this);
                // Set the scanner view as the content view
                setContentView(mScannerView);
            }
        } catch (Exception e) {
            Toast.makeText(QrScannerActivity.this, "Failed to open QR Scanner, Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // if resumed, register as handler and start camera
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        // if paused, stop camera
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // get result and log
        Logger.getAnonymousLogger().info(rawResult.getText());
        Toast.makeText(QrScannerActivity.this, "Key successfully received.", Toast.LENGTH_SHORT).show();
        // send data back to conversation activity
        Intent intent = new Intent();
        intent.putExtra("QR", rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();
    }
}
