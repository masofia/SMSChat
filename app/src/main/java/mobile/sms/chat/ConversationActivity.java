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
        // test long
        mssg = "# Chat App\n" +
                "\n" +
                "## Open Items / Improvements:\n" +
                "\n" +
                "- SMS messages sent and received showing up in other chat? Is it possible to mute it from the other app?\n" +
                "If not it should be fine since the messages displayed there will be encrypted anyway. \n" +
                "\n" +
                "\"If the user has multiple SMS messaging apps installed on the Android phone, the App chooser will appear with a list of these apps, and the user can choose which one to use. (Android smartphones will have at least one, such as Messenger.)\"\n" +
                "(https://google-developer-training.github.io/android-developer-phone-sms-course/Lesson%202/2_p_sending_sms_messages.html)\n" +
                "\n" +
                "\"You can manage SMS operations such as dividing a message into fragments, sending a multipart message, get carrier-dependent configuration values, and so on.\" -- will need to break up encrypted messages as they may be too long\n" +
                "(https://google-developer-training.github.io/android-developer-phone-sms-course/Lesson%202/2_p_sending_sms_messages.html)\n" +
                "\n" +
                "- The App does not use any DB. When the app is closed, all message history is lost. A ConversationActivity contains message history, but when you exit the specific activity (and go back to main), it gets lost. For a message to be received, one must be on the ConversationActivity screen. \n" +
                "\n" +
                "- Use shared ViewModel? Use fragments?\n" +
                "https://stackoverflow.com/questions/44641121/share-viewmodel-between-fragments-that-are-in-different-activity\n" +
                "https://stackoverflow.com/questions/27466397/trying-create-a-chat-with-fragment\n" +
                "https://blog.mindorks.com/shared-viewmodel-in-android-shared-between-fragments\n" +
                "\n" +
                "\n" +
                "## Resources:\n" +
                "\n" +
                "- https://google-developer-training.github.io/android-developer-phone-sms-course\n" +
                "- https://medium.com/@anitaa_1990/how-to-update-an-activity-from-background-service-or-a-broadcastreceiver-6dabdb5cef74 \n" +
                "- https://medium.com/@peterekeneeze/passing-data-between-activities-2d0ef122f19d";
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
