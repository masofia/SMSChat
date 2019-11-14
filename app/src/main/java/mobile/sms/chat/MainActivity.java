package mobile.sms.chat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

import mobile.sms.model.Contact;
import mobile.sms.model.Conversation;

public class MainActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private MainViewModel mainViewModel;
    private BroadcastReceiver broadcastReceiver;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        setContentView(R.layout.activity_main);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        EditText phoneNumber = findViewById(R.id.phoneNumber);

        ll = findViewById(R.id.layout);

        ImageButton newConvo = findViewById(R.id.newConversation);
        newConvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = phoneNumber.getText().toString();
                Intent chat = new Intent(getApplicationContext(), ConversationActivity.class);
                chat.putExtra("contact", num);

                mainViewModel.addNewConversation(new Conversation(new Contact(num), new ArrayList<>()));
                startActivity(chat);
            }
        });

    }

    @TargetApi(24)
    private void registerReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String mssg = intent.getStringExtra("message");
                String contact = intent.getStringExtra("contact");

                Predicate<Conversation> f = (convo) ->
                        (convo).getContact().getNumber().equals(contact);

                Optional<Conversation> conversation = mainViewModel.getAllConversations().stream()
                        .filter(f)
                        .findFirst();

                conversation.ifPresent(conversation1 -> conversation1.addMessage(mssg));

                if (!conversation.isPresent()) {
                    Conversation newConvo = new Conversation(new Contact(contact));
                    newConvo.addMessage(mssg);
                    mainViewModel.addNewConversation(newConvo);

                    TextView convoView = new TextView(context);
                    convoView.setText(contact);
                    ll.addView(convoView);
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("mobile.sms.chat"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }


    // This is for settings bar :

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
