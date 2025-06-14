package fuck.system.vpn.openvpn;

import android.app.Notification;
import android.app.Service;
import android.util.Log;

public class NotificationControl
{
    private static final String LOG_TAG = "OpenvpnNotificationControl";
    private final Service service;

    public NotificationControl(Service service) {
        this.service = service;
    }

    public void startForeground(int id, Notification notification) {
        service.startForeground(id, notification);
        Log.d(LOG_TAG, "Foreground service started: " + id);
    }

    public void stopForeground(boolean removeNotification) {
        service.stopForeground(removeNotification
                ? Service.STOP_FOREGROUND_REMOVE
                : Service.STOP_FOREGROUND_DETACH);
        Log.d(LOG_TAG, "Foreground service stopped");
    }
}
