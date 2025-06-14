package net.openvpn.openvpn.service;

import android.app.PendingIntent;
import net.openvpn.openvpn.data.EventMsg;
import net.openvpn.openvpn.data.LogMsg;

public interface EventReceiver {
    void event(EventMsg eventMsg);

    PendingIntent get_configure_intent(int i);

    void log(LogMsg logMsg);
}
