package eu.tuttivers.trackergps.smsUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

import org.greenrobot.eventbus.EventBus;

import eu.tuttivers.trackergps.R;


public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get("pdus");
            for (Object sm : sms) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sm);
                String phone = smsMessage.getOriginatingAddress();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String myPhone = prefs.getString(context.getString(R.string.key_number), "");
                if (!phone.equals(myPhone))
                    return;
                String message = smsMessage.getMessageBody();
                EventBus.getDefault().post(message);
            }
        }
    }


}
