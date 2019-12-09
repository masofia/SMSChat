package mobile.sms.chat;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MessageReceiver extends BroadcastReceiver {

    public static final String pdu_type = "pdus";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the SMS message
        Bundle bundle = intent.getExtras();
        String strMessage = "";
        String format = bundle.getString("format");

        // Retrieve the SMS message received (PDU = protocol data unit)
        Object[] pdus = (Object[]) bundle.get(pdu_type);

        if (pdus != null) {
            // Check the Android version
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);

            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                // Check Android version and use appropriate createFromPdu
                if (isVersionM) {
                    // If Android version M or newer:
                    messages[i] =
                            SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                // Build the message to show
                String n = messages[i].getOriginatingAddress();
                strMessage += "SMS from " + n;
                strMessage += ": " + messages[i].getMessageBody() + "\n";


                Intent in = new Intent("mobile.sms.chat");
                Bundle extras = new Bundle();
                extras.putString("contact", n.substring(n.length() - 4));   // put only the last 4 for emulator testing
                extras.putString("message", strMessage);
                in.putExtras(extras);
                context.sendBroadcast(in);
            }

        }
    }
}