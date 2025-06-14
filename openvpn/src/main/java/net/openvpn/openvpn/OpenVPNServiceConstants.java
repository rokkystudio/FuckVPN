package net.openvpn.openvpn;

import android.app.PendingIntent;
import android.os.Handler;
import android.os.Messenger;

import androidx.core.app.NotificationCompat;

import net.openvpn.openvpn.control_channel.CCEventHandler;
import net.openvpn.openvpn.data.EventMsg;
import net.openvpn.openvpn.service.TrafficSpeedTracker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OpenVPNServiceConstants
{
    String TAG = "OpenVPNService";

    /*** Actions ***/
    String ACTION_BASE = "net.openvpn.openvpn.";
    String ACTION_BIND = ACTION_BASE + "BIND";
    String ACTION_CONNECT = ACTION_BASE + "CONNECT";
    String ACTION_CONNECT_RESUME = ACTION_BASE + "CONNECT_RESUME";
    String ACTION_DELETE_PROFILE = ACTION_BASE + "DELETE_PROFILE";
    String ACTION_DISCONNECT = ACTION_BASE + "DISCONNECT";
    String ACTION_EXTERNAL_DISCONNECT = ACTION_BASE + "EXTERNAL_DISCONNECT";
    String ACTION_IMPORT_PROFILE = ACTION_BASE + "IMPORT_PROFILE";
    String ACTION_IMPORT_PROFILE_VIA_PATH = ACTION_BASE + "ACTION_IMPORT_PROFILE_VIA_PATH";
    String ACTION_PAUSE = ACTION_BASE + "PAUSE";
    String ACTION_RENAME_PROFILE = ACTION_BASE + "RENAME_PROFILE";
    String ACTION_RESUME = ACTION_BASE + "RESUME";
    String ACTION_SUBMIT_PROXY_CREDS = ACTION_BASE + "ACTION_SUBMIT_PROXY_CREDS";
    String AON_CANCEL = ACTION_BASE + "AON_CANCEL";
    String AON_PROVIDE_CREDS = ACTION_BASE + "AON_PROVIDE_CREDS";

    /*** Broadcasts ***/
    String DEBUG_ACTION_BOOT_COMPLETED = "android.intent.action.ACTION_BOOT_COMPLETED";

    /*** IPC Events ***/
    String GET_PROFILE_MAX_SIZE_IPC_EVENT = "get_profile_max_size";
    String GET_PROFILE_MAX_SIZE_SUCCESS_IPC_EVENT = "get_profile_max_size_success";

    /*** Misc Keys ***/
    String INTENT_PREFIX = ACTION_BASE.substring(0, ACTION_BASE.length() - 1);
    String QUICK_TILE_CONNECT = "QUICK_TILE_CONNECT";
    String autoConnectKey = ACTION_BASE + "AUTOCONNECT";
    String autoStartKey = ACTION_BASE + "AUTOSTART";
    String profileIDKey = ACTION_BASE + "AUTOSTART_PROFILE_ID";

    /*** Event Priorities ***/
    int EV_PRIO_HIGH = 3;
    int EV_PRIO_MED = 2;
    int EV_PRIO_LOW = 1;
    int EV_PRIO_INVISIBLE = 0;

    /*** Message IDs ***/
    int MSG_CONNECTION_STATE_SUBSCRIBE = 1;
    int MSG_CONNECTION_STATE_UNSUBSCRIBE = 2;
    int MSG_TILE_STATE_SUBSCRIPTION_RESULT = 3;

    /*** Internal Codes ***/
    int GCI_REQ_ESTABLISH = 0;
    int GCI_REQ_NOTIFICATION = 1;
    int CALLER_ID = 20;
    int NOTIFICATION_ID = 1642;
}
