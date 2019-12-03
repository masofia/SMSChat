package mobile.sms.chat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

public class ConversationActivity extends AppCompatActivity {
    private ConversationViewModel conversationViewModel;
    private BroadcastReceiver broadcastReceiver;
    private int ZXING_CAMERA_PERMISSION = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        setContentView(R.layout.activity_chat);
        conversationViewModel = ViewModelProviders.of(this).get(ConversationViewModel.class);

        EditText text = findViewById(R.id.textToSend);

        String contact = getIntent().getStringExtra("contact");
        conversationViewModel.setContact(contact);  // just phone number for now...

        ImageButton sendButton = findViewById(R.id.sendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mssg = text.getText().toString();
                conversationViewModel.addMessage(mssg);
                sendMessage(mssg, contact);
            }
        });

        //add QR code reader
        ImageButton setKeyButton = findViewById(R.id.setKey);
        setKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qreader = new Intent(getApplicationContext(), QrScannerActivity.class);
                startActivity(qreader);
            }
        });
    }

    private void registerReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mssg = intent.getStringExtra("message");
                String contact = intent.getStringExtra("contact");

                if (conversationViewModel.getConversation().getContact().getNumber().equals(contact)) {
                    conversationViewModel.addMessage(mssg);

                    LinearLayout receivedText = findViewById(R.id.receivedMessage);
                    TextView convoView = new TextView(context);
                    convoView.setText(mssg);
                    receivedText.addView(convoView);
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("mobile.sms.chat"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*
         * Step 4: Ensure to unregister the receiver when the activity is destroyed so that
         * you don't face any memory leak issues in the app
         */
        if(broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    public void sendMessage(String mssg, String contact) {
        try {
            SmsManager smgr = SmsManager.getDefault();

            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            1);
//                            MY_PERMISSIONS_REQUEST_SEND_SMS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                smgr.sendTextMessage(contact, null, mssg, null, null);
                Toast.makeText(ConversationActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            Toast.makeText(ConversationActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
        }
    }

}
