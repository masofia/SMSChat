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

import org.json.JSONObject;

import mobile.sms.model.Contact;
import mobile.sms.model.Message;

public class ConversationActivity extends AppCompatActivity {
    private ConversationViewModel conversationViewModel;
    private BroadcastReceiver broadcastReceiver;
    private int ZXING_CAMERA_PERMISSION = 1;
    private static final int QR_ACTIVITY_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        setContentView(R.layout.activity_chat);
        conversationViewModel = ViewModelProviders.of(this).get(ConversationViewModel.class);

        EditText text = findViewById(R.id.textToSend);

        String contactNum = getIntent().getStringExtra("contact");
        Contact receiver = new Contact(contactNum);
        conversationViewModel.setContact(receiver);

        ImageButton sendButton = findViewById(R.id.sendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgString = text.getText().toString();
                Message message = new Message(msgString);

                message.setReceiver(receiver);
                message.encryptText();
                conversationViewModel.addMessage(message.getText());
                sendMessage(message, receiver);
            }
        });

        //add QR code reader
        ImageButton setKeyButton = findViewById(R.id.setKey);
        setKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qreader = new Intent(getApplicationContext(), QrScannerActivity.class);
                startActivityForResult(qreader, QR_ACTIVITY_REQUEST_CODE);
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

    public void sendMessage(Message mssg, Contact contact) {
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
                smgr.sendTextMessage(contact.getNumber(), null, mssg.getEncryptedText(), null, null);
                Toast.makeText(ConversationActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            Toast.makeText(ConversationActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String qr = data.getStringExtra("QR");

        try {
            JSONObject obj = new JSONObject(qr);
            String privateKey = obj.getString("key_hex");
            String iv = obj.getString("iv_hex");
            setEncryptionValues(privateKey, iv);
        } catch (Exception e) {
            Toast.makeText(ConversationActivity.this, "Failed to scan QR code. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setEncryptionValues(String key, String iv) {
        conversationViewModel.setContactPrivateKey(key);
        conversationViewModel.setContactIv(iv);
    }
}
