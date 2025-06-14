package fuck.system.vpn.openvpn;

import android.app.Activity;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationPermissions
{
    public static void requestIfNeeded(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= 33) {
            NotificationManagerCompat manager = NotificationManagerCompat.from(activity);
            if (!manager.areNotificationsEnabled()) {
                ActivityCompat.requestPermissions(activity, new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
            }
        }
    }
} 