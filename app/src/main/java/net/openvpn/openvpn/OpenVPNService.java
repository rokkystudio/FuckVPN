package net.openvpn.openvpn;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.security.KeyChain;
import android.security.KeyChainException;
import android.system.OsConstants;
import android.util.Base64;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.openvpn.openvpn.DnsServer;
import net.openvpn.openvpn.IPC.IPCAction;
import net.openvpn.openvpn.IPC.IPCCallback;
import net.openvpn.openvpn.IPC.IPCConstants;
import net.openvpn.openvpn.IPC.IPCReceiver;
import net.openvpn.openvpn.IPC.IPCSender;
import net.openvpn.openvpn.IPC.IPCUtils;
import net.openvpn.openvpn.MessageQueue;
import net.openvpn.openvpn.NotificationIconMapping;
import net.openvpn.openvpn.NotificationService;
import net.openvpn.openvpn.OpenVPNClientThread;
import net.openvpn.openvpn.PrefUtil;
import net.openvpn.openvpn.Request;
import net.openvpn.openvpn.VpnProfile;
import net.openvpn.openvpn.connectivity.ConnectivityReceiver;
import net.openvpn.openvpn.control_channel.CCAccessControl;
import net.openvpn.openvpn.control_channel.CCEventHandler;
import net.openvpn.openvpn.control_channel.ICCSender;
import net.openvpn.openvpn.crypto.Signing;
import net.openvpn.openvpn.data.BandwidthInfo;
import net.openvpn.openvpn.data.ClientAPI_Array;
import net.openvpn.openvpn.data.Config;
import net.openvpn.openvpn.data.ConnectionInfo;
import net.openvpn.openvpn.data.ConnectionStats;
import net.openvpn.openvpn.data.ControlChannelMessageData;
import net.openvpn.openvpn.data.EditProxyInfo;
import net.openvpn.openvpn.data.EvalConfig;
import net.openvpn.openvpn.data.EventMsg;
import net.openvpn.openvpn.data.ImportException;
import net.openvpn.openvpn.data.ImportProfileResult;
import net.openvpn.openvpn.data.ImportResult;
import net.openvpn.openvpn.data.ImportViaReactInfo;
import net.openvpn.openvpn.data.InitialBindData;
import net.openvpn.openvpn.data.LogDeque;
import net.openvpn.openvpn.data.LogMsg;
import net.openvpn.openvpn.data.MergeConfig;
import net.openvpn.openvpn.data.NetworkEventInfo;
import net.openvpn.openvpn.data.ProfileData;
import net.openvpn.openvpn.data.ProfileList;
import net.openvpn.openvpn.data.ProxyContext;
import net.openvpn.openvpn.data.ProxyItem;
import net.openvpn.openvpn.data.ProxyList;
import net.openvpn.openvpn.data.RenameProfileInfo;
import net.openvpn.openvpn.data.SpeedStats;
import net.openvpn.openvpn.data.TunnelBytesInfo;
import net.openvpn.openvpn.service.EventInfo;
import net.openvpn.openvpn.service.InternalError;
import net.openvpn.openvpn.service.ProfileFN;
import net.openvpn.openvpn.service.TrafficSpeedTracker;
import net.openvpn.unified.LogsManager;
import net.openvpn.unified.MainActivity;
import net.openvpn.unified.MainApplication;
import net.openvpn.unified.R$drawable;
import net.openvpn.unified.R$string;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OpenVPNService extends VpnService implements OpenVPNClientThread.EventReceiver, ICCSender
{
    public static final String ACTION_BASE = "net.openvpn.openvpn.";
    public static final String ACTION_BIND = "net.openvpn.openvpn.BIND";
    public static final String ACTION_CONNECT = "net.openvpn.openvpn.CONNECT";
    public static final String ACTION_CONNECT_RESUME = "net.openvpn.openvpn.CONNECT_RESUME";
    public static final String ACTION_DELETE_PROFILE = "net.openvpn.openvpn.DELETE_PROFILE";
    public static final String ACTION_DISCONNECT = "net.openvpn.openvpn.DISCONNECT";
    public static final String ACTION_EXTERNAL_DISCONNECT = "net.openvpn.openvpn.EXTERNAL_DISCONNECT";
    public static final String ACTION_IMPORT_PROFILE = "net.openvpn.openvpn.IMPORT_PROFILE";
    public static final String ACTION_IMPORT_PROFILE_VIA_PATH = "net.openvpn.openvpn.ACTION_IMPORT_PROFILE_VIA_PATH";
    public static final String ACTION_PAUSE = "net.openvpn.openvpn.PAUSE";
    public static final String ACTION_RENAME_PROFILE = "net.openvpn.openvpn.RENAME_PROFILE";
    public static final String ACTION_RESUME = "net.openvpn.openvpn.RESUME";
    public static final String ACTION_SUBMIT_PROXY_CREDS = "net.openvpn.openvpn.ACTION_SUBMIT_PROXY_CREDS";
    public static final String AON_CANCEL = "net.openvpn.openvpn.AON_CANCEL";
    public static final String AON_PROVIDE_CREDS = "net.openvpn.openvpn.AON_PROVIDE_CREDS";
    private static final int CALLER_ID = 20;
    public static final String DEBUG_ACTION_BOOT_COMPLETED = "android.intent.action.ACTION_BOOT_COMPLETED";
    public static final int EV_PRIO_HIGH = 3;
    public static final int EV_PRIO_INVISIBLE = 0;
    public static final int EV_PRIO_LOW = 1;
    public static final int EV_PRIO_MED = 2;
    private static final int GCI_REQ_ESTABLISH = 0;
    private static final int GCI_REQ_NOTIFICATION = 1;
    public static String GET_PROFILE_MAX_SIZE_IPC_EVENT = "get_profile_max_size";
    public static String GET_PROFILE_MAX_SIZE_SUCCESS_IPC_EVENT = "get_profile_max_size_success";
    public static final String INTENT_PREFIX = "net.openvpn.openvpn";
    public static final int MSG_CONNECTION_STATE_SUBSCRIBE = 1;
    public static final int MSG_CONNECTION_STATE_UNSUBSCRIBE = 2;
    public static final int MSG_TILE_STATE_SUBSCRIPTION_RESULT = 3;
    private static final int NOTIFICATION_ID = 1642;
    public static final String QUICK_TILE_CONNECT = "QUICK_TILE_CONNECT";
    private static final String TAG = "OpenVPNService";
    private static final String autoConnectKey = "net.openvpn.openvpn.AUTOCONNECT";
    private static final String autoStartKey = "net.openvpn.openvpn.AUTOSTART";
    /* access modifiers changed from: private */
    public static final Map<String, Messenger> connectionStateObservers = new HashMap();
    public static PendingIntent disconnectPendingService = null;
    public static PendingIntent pausePendingService = null;
    private static final String profileIDKey = "net.openvpn.openvpn.AUTOSTART_PROFILE_ID";
    public static PendingIntent resumePendingService;
    /* access modifiers changed from: private */
    public boolean active = false;
    /* access modifiers changed from: private */
    public boolean auth_pending = false;
    private CCEventHandler cc_event_handler;
    public Handler connectDelayHandler;
    public Runnable connectDelayedTask;
    /* access modifiers changed from: private */
    public CPUUsage cpu_usage;
    /* access modifiers changed from: private */
    public ProfileData current_profile;
    /* access modifiers changed from: private */
    public boolean currently_connected = false;
    /* access modifiers changed from: private */
    public boolean enable_notifications;
    /* access modifiers changed from: private */
    public HashMap event_info;
    /* access modifiers changed from: private */
    public ExternalConnectionManager externalConnectionManager;
    /* access modifiers changed from: private */
    public boolean initial_connect = true;
    public IPCReceiver ipc_receiver = null;
    public IPCSender ipc_sender = null;
    public boolean isLogsPaused;
    /* access modifiers changed from: private */
    public ConnectionState lastKnownState = ConnectionState.DISCONNECTED;
    public EventMsg last_core_event = null;
    /* access modifiers changed from: private */
    public EventMsg last_event_prof_manage;
    /* access modifiers changed from: private */
    public LogsManager logsManager = null;
    /* access modifiers changed from: private */
    public ConnectivityReceiver mConnectivityReceiver = null;
    NotificationCompat.Builder mNotifyBuilder;
    private ScreenReceiver mScreenReceiver = null;
    private OpenVPNClientThread mThread;
    /* access modifiers changed from: private */
    public boolean manual_pause = false;
    /* access modifiers changed from: private */
    public MessageQueue message_queue;
    private NotificationService notificationService;
    /* access modifiers changed from: private */
    public boolean paused = false;
    /* access modifiers changed from: private */
    public boolean paused_before_timeout = false;
    private List<Integer> persistable_notification_events = Arrays.asList(new Integer[]{Integer.valueOf(R$string.auth_failed), Integer.valueOf(R$string.connection_timeout), Integer.valueOf(R$string.auth_pending), Integer.valueOf(R$string.auth_pending_failed), Integer.valueOf(R$string.dynamic_challenge), Integer.valueOf(R$string.aon_request_creds), Integer.valueOf(R$string.session_expired), Integer.valueOf(R$string.need_creds), Integer.valueOf(R$string.network_unavailable)});
    private List<Integer> ping_repeat_timeouts = Arrays.asList(new Integer[]{2, 2, 2, 2, 2});
    /* access modifiers changed from: private */
    public Request.Ping pinger;
    /* access modifiers changed from: private */
    public PrefUtil prefs;
    private PrefUtil.IPCProvider prefs_ipc;
    public String profile_display_name;
    /* access modifiers changed from: private */
    public ProfileList profile_list;
    public ProxyList proxy_list;
    /* access modifiers changed from: private */
    public PasswordUtil pwds;
    public boolean screen_on = true;
    private final Messenger serviceMessenger = new Messenger(new IncomingHandler(this));
    private boolean should_stop = false;
    /* access modifiers changed from: private */
    public boolean shutdown_pending = false;
    private long thread_started = 0;
    private TrafficSpeedTracker trafficSpeedTracker;

    /* renamed from: net.openvpn.openvpn.OpenVPNService$43  reason: invalid class name */
    static /* synthetic */ class AnonymousClass43 {
        static final /* synthetic */ int[] $SwitchMap$net$openvpn$openvpn$NotificationService$NotificationActionType;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                net.openvpn.openvpn.NotificationService$NotificationActionType[] r0 = net.openvpn.openvpn.NotificationService.NotificationActionType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$net$openvpn$openvpn$NotificationService$NotificationActionType = r0
                net.openvpn.openvpn.NotificationService$NotificationActionType r1 = net.openvpn.openvpn.NotificationService.NotificationActionType.PERSISTENT     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$net$openvpn$openvpn$NotificationService$NotificationActionType     // Catch:{ NoSuchFieldError -> 0x001d }
                net.openvpn.openvpn.NotificationService$NotificationActionType r1 = net.openvpn.openvpn.NotificationService.NotificationActionType.AUTH_PENDING     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$net$openvpn$openvpn$NotificationService$NotificationActionType     // Catch:{ NoSuchFieldError -> 0x0028 }
                net.openvpn.openvpn.NotificationService$NotificationActionType r1 = net.openvpn.openvpn.NotificationService.NotificationActionType.NORMAL     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: net.openvpn.openvpn.OpenVPNService.AnonymousClass43.<clinit>():void");
        }
    }

    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    public interface ConnectionStateChangeListener {
        void onChange(ConnectionState connectionState);
    }

    private class EnvVar {
        public String key;
        public String value;

        EnvVar(String str, String str2) {
            this.key = str;
            this.value = str2;
        }

        public String toString() {
            return this.key + ' ' + this.value;
        }
    }

    private class EventHandler implements MessageQueue.EventHandler {
        private EventHandler() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:117:0x0240, code lost:
            if (net.openvpn.openvpn.OpenVPNService.m321$$Nest$fgetexternalConnectionManager(r0).isExternalConnection() == false) goto L_0x0242;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x008a, code lost:
            if (r0 != net.openvpn.unified.R$string.dynamic_challenge) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a9, code lost:
            if (net.openvpn.openvpn.OpenVPNService.m318$$Nest$fgetcurrent_profile(r13.this$0) != null) goto L_0x00ab;
         */
        /* JADX WARNING: Removed duplicated region for block: B:125:0x026d  */
        /* JADX WARNING: Removed duplicated region for block: B:129:0x0285  */
        /* JADX WARNING: Removed duplicated region for block: B:138:0x02a7 A[ADDED_TO_REGION] */
        /* JADX WARNING: Removed duplicated region for block: B:150:0x02c3  */
        /* JADX WARNING: Removed duplicated region for block: B:151:0x02c9  */
        /* JADX WARNING: Removed duplicated region for block: B:158:0x02dc  */
        /* JADX WARNING: Removed duplicated region for block: B:160:0x02e2  */
        /* JADX WARNING: Removed duplicated region for block: B:163:0x02eb  */
        /* JADX WARNING: Removed duplicated region for block: B:165:0x02f8  */
        /* JADX WARNING: Removed duplicated region for block: B:168:0x0307  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean on_event(net.openvpn.openvpn.data.EventMsg r14) {
            /*
                r13 = this;
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r1 = 0
                r0.currently_connected = r1
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.EventMsg r2 = r0.last_core_event
                int r3 = r14.res_id
                int r4 = net.openvpn.unified.R$string.disconnected
                java.lang.String r5 = "OpenVPNService"
                java.lang.String r6 = "current_profile"
                r7 = 0
                r8 = 1
                if (r3 != r4) goto L_0x008d
                if (r2 == 0) goto L_0x0025
                java.util.HashMap r0 = r0.event_info
                java.lang.String r3 = r2.name
                java.lang.Object r0 = r0.get(r3)
                net.openvpn.openvpn.service.EventInfo r0 = (net.openvpn.openvpn.service.EventInfo) r0
                goto L_0x0026
            L_0x0025:
                r0 = r7
            L_0x0026:
                if (r0 == 0) goto L_0x002e
                net.openvpn.openvpn.NotificationService$NotificationActionType r0 = r0.notif_action_type
                net.openvpn.openvpn.NotificationService$NotificationActionType r3 = net.openvpn.openvpn.NotificationService.NotificationActionType.PERSISTENT
                if (r0 == r3) goto L_0x003a
            L_0x002e:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.ExternalConnectionManager r0 = r0.externalConnectionManager
                boolean r0 = r0.isNotificationShowing()
                if (r0 == 0) goto L_0x003c
            L_0x003a:
                r0 = r8
                goto L_0x003d
            L_0x003c:
                r0 = r1
            L_0x003d:
                if (r0 != 0) goto L_0x0044
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.stop_notification()
            L_0x0044:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.auth_pending = r1
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.initial_connect = r8
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.cancel_ping()
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.CPUUsage r0 = r0.cpu_usage
                if (r0 == 0) goto L_0x0064
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.CPUUsage r0 = r0.cpu_usage
                r0.stop()
            L_0x0064:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                boolean r0 = r0.shutdown_pending
                if (r0 != 0) goto L_0x0071
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.set_autostart_profile_id(r7)
            L_0x0071:
                if (r2 == 0) goto L_0x0247
                int r0 = r2.flags
                r0 = r0 & r8
                if (r0 == 0) goto L_0x007a
                r14.priority = r1
            L_0x007a:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                if (r0 == 0) goto L_0x0247
                int r0 = r2.res_id
                int r3 = net.openvpn.unified.R$string.proxy_need_creds
                if (r0 == r3) goto L_0x0247
                int r3 = net.openvpn.unified.R$string.dynamic_challenge
                if (r0 == r3) goto L_0x0247
                goto L_0x00ab
            L_0x008d:
                int r4 = net.openvpn.unified.R$string.connected
                if (r3 != r4) goto L_0x00bf
                r0.currently_connected = r8
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.initial_connect = r1
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.auth_pending = r1
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.cancel_ping()
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                if (r0 == 0) goto L_0x0247
            L_0x00ab:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                r0.reset_proxy_context()
            L_0x00b4:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r3 = r0.get_current_profile()
                r0.broadcast_message((java.lang.String) r6, r3)
                goto L_0x0247
            L_0x00bf:
                int r4 = net.openvpn.unified.R$string.pause
                if (r3 != r4) goto L_0x00dd
                boolean r0 = r0.manual_pause
                if (r0 != 0) goto L_0x00d6
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                boolean r0 = r0.initial_connect
                if (r0 != 0) goto L_0x00d6
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.attempt_delayed_resume()
            L_0x00d6:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.paused = r8
                goto L_0x0247
            L_0x00dd:
                int r4 = net.openvpn.unified.R$string.resume
                if (r3 != r4) goto L_0x00e6
                r0.paused = r1
                goto L_0x0247
            L_0x00e6:
                int r4 = net.openvpn.unified.R$string.proxy_need_creds
                if (r3 != r4) goto L_0x010b
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                if (r0 == 0) goto L_0x0247
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                net.openvpn.openvpn.data.ProxyContext r0 = r0.get_proxy_context(r1)
                if (r0 == 0) goto L_0x0247
                boolean r3 = r0.should_launch_creds_dialog()
                if (r3 == 0) goto L_0x0247
                net.openvpn.openvpn.OpenVPNService r3 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProxyList r3 = r3.proxy_list
                r0.invalidate_proxy_creds(r3)
                goto L_0x0247
            L_0x010b:
                int r4 = net.openvpn.unified.R$string.dynamic_challenge
                if (r3 != r4) goto L_0x015a
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                if (r0 == 0) goto L_0x0247
                net.openvpn.openvpn.ClientAPI_DynamicChallenge r0 = new net.openvpn.openvpn.ClientAPI_DynamicChallenge
                r0.<init>()
                java.lang.String r3 = r14.info
                boolean r3 = net.openvpn.openvpn.OpenVPNClientHelperWrapper.parse_dynamic_challenge(r3, r0)
                if (r3 == 0) goto L_0x0247
                net.openvpn.openvpn.data.DynamicChallenge r3 = new net.openvpn.openvpn.data.DynamicChallenge
                r3.<init>()
                long r9 = android.os.SystemClock.elapsedRealtime()
                r11 = 60000(0xea60, double:2.9644E-319)
                long r9 = r9 + r11
                r3.expires = r9
                java.lang.String r4 = r14.info
                r3.cookie = r4
                net.openvpn.openvpn.data.Challenge r4 = r3.challenge
                java.lang.String r9 = r0.getChallenge()
                r4.set_challenge(r9)
                net.openvpn.openvpn.data.Challenge r4 = r3.challenge
                boolean r9 = r0.getEcho()
                r4.set_echo(r9)
                net.openvpn.openvpn.data.Challenge r4 = r3.challenge
                boolean r0 = r0.getResponseRequired()
                r4.set_response_required(r0)
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                r0.dynamic_challenge = r3
                goto L_0x00b4
            L_0x015a:
                int r4 = net.openvpn.unified.R$string.auth_failed
                if (r3 != r4) goto L_0x0184
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                if (r0 == 0) goto L_0x0179
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                java.lang.String r0 = r0.get_id()
                net.openvpn.openvpn.OpenVPNService r3 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.PasswordUtil r3 = r3.pwds
                java.lang.String r4 = "auth"
                r3.remove(r4, r0)
            L_0x0179:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                java.lang.String r3 = r14.info
                boolean r0 = r0.sendAonRequestCreds(r3)
                if (r0 == 0) goto L_0x0247
                return r8
            L_0x0184:
                int r4 = net.openvpn.unified.R$string.tap_not_supported
                if (r3 == r4) goto L_0x0238
                int r4 = net.openvpn.unified.R$string.core_thread_error
                if (r3 == r4) goto L_0x0238
                int r4 = net.openvpn.unified.R$string.core_thread_abandoned
                if (r3 == r4) goto L_0x0238
                int r4 = net.openvpn.unified.R$string.epki_invalid_alias
                if (r3 == r4) goto L_0x0238
                int r4 = net.openvpn.unified.R$string.insecure_hash_algorithm
                if (r3 != r4) goto L_0x019a
                goto L_0x0238
            L_0x019a:
                int r4 = net.openvpn.unified.R$string.info_msg
                if (r3 != r4) goto L_0x020e
                org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x01c9 }
                java.lang.String r3 = r14.info     // Catch:{ Exception -> 0x01c9 }
                r0.<init>(r3)     // Catch:{ Exception -> 0x01c9 }
                java.lang.String r3 = "notification_id"
                java.lang.String r0 = r0.getString(r3)     // Catch:{ Exception -> 0x01c9 }
                java.lang.String r3 = "BILLING_SUBEXPIRE"
                boolean r3 = r0.equals(r3)     // Catch:{ Exception -> 0x01c9 }
                if (r3 != 0) goto L_0x01c3
                java.lang.String r3 = "AUTH_CHANGE"
                boolean r3 = r0.equals(r3)     // Catch:{ Exception -> 0x01c9 }
                if (r3 != 0) goto L_0x01c3
                java.lang.String r3 = "CERT_REVOKE"
                boolean r0 = r0.equals(r3)     // Catch:{ Exception -> 0x01c9 }
                if (r0 == 0) goto L_0x01ce
            L_0x01c3:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this     // Catch:{ Exception -> 0x01c9 }
                r0.stop_thread()     // Catch:{ Exception -> 0x01c9 }
                goto L_0x01ce
            L_0x01c9:
                java.lang.String r0 = "Info Event JSON Parse error"
                android.util.Log.i(r5, r0)
            L_0x01ce:
                java.lang.String r0 = r14.info
                java.lang.String r3 = "WEB_AUTH"
                boolean r0 = r0.startsWith(r3)
                if (r0 == 0) goto L_0x01dd
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.set_last_event(r14, r1)
            L_0x01dd:
                java.lang.String r0 = r14.info
                java.lang.String r3 = "OPEN_URL"
                boolean r0 = r0.startsWith(r3)
                if (r0 == 0) goto L_0x01f5
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                boolean r0 = r0.handleOpenURL(r14)
                if (r0 == 0) goto L_0x01f5
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.set_last_event(r14, r1)
                return r8
            L_0x01f5:
                java.lang.String r0 = r14.info
                java.lang.String r3 = "CR_TEXT:"
                boolean r0 = r0.startsWith(r3)
                if (r0 == 0) goto L_0x0247
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                boolean r0 = r0.sendCRText(r14)
                if (r0 == 0) goto L_0x0208
                return r8
            L_0x0208:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.set_last_event(r14, r8)
                goto L_0x0247
            L_0x020e:
                int r4 = net.openvpn.unified.R$string.pem_password_fail
                if (r3 != r4) goto L_0x0247
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                if (r0 == 0) goto L_0x022d
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                java.lang.String r0 = r0.get_id()
                net.openvpn.openvpn.OpenVPNService r3 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.PasswordUtil r3 = r3.pwds
                java.lang.String r4 = "pk"
                r3.remove(r4, r0)
            L_0x022d:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                java.lang.String r3 = r14.info
                boolean r0 = r0.sendAonRequestCreds(r3)
                if (r0 == 0) goto L_0x0242
                return r8
            L_0x0238:
                net.openvpn.openvpn.ExternalConnectionManager r0 = r0.externalConnectionManager
                boolean r0 = r0.isExternalConnection()
                if (r0 != 0) goto L_0x0247
            L_0x0242:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.stop_notification()
            L_0x0247:
                int r0 = r14.res_id
                int r3 = net.openvpn.unified.R$string.epki_invalid_alias
                if (r0 != r3) goto L_0x027c
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileList r0 = r0.profile_list
                if (r0 == 0) goto L_0x027c
                net.openvpn.openvpn.OpenVPNService$ProfileListUtil r0 = new net.openvpn.openvpn.OpenVPNService$ProfileListUtil
                net.openvpn.openvpn.OpenVPNService r3 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileList r4 = r3.profile_list
                r0.<init>(r4)
                java.lang.String r3 = r14.info
                r0.invalidate_epki_alias(r3)
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                if (r0 == 0) goto L_0x0275
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.data.ProfileData r0 = r0.current_profile
                r0.external_pki_alias = r7
            L_0x0275:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                java.lang.Boolean r3 = java.lang.Boolean.TRUE
                r0.refresh_profile_list(r3)
            L_0x027c:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                boolean r0 = r0.enable_notifications
                r3 = 2
                if (r0 == 0) goto L_0x02a1
                int r0 = r14.priority
                if (r0 != r3) goto L_0x0295
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                int r4 = r14.res_id
                android.widget.Toast r0 = android.widget.Toast.makeText(r0, r4, r1)
            L_0x0291:
                r0.show()
                goto L_0x02a1
            L_0x0295:
                r4 = 3
                if (r0 != r4) goto L_0x02a1
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                int r4 = r14.res_id
                android.widget.Toast r0 = android.widget.Toast.makeText(r0, r4, r8)
                goto L_0x0291
            L_0x02a1:
                int r0 = r14.res_id
                int r4 = net.openvpn.unified.R$string.connected
                if (r0 != r4) goto L_0x02b2
                if (r2 == 0) goto L_0x02ad
                int r6 = r2.res_id
                if (r6 == r4) goto L_0x02b2
            L_0x02ad:
                net.openvpn.openvpn.data.EventMsg$Transition r2 = net.openvpn.openvpn.data.EventMsg.Transition.TO_CONNECTED
            L_0x02af:
                r14.transition = r2
                goto L_0x02bd
            L_0x02b2:
                if (r0 == r4) goto L_0x02bd
                if (r2 == 0) goto L_0x02bd
                int r2 = r2.res_id
                if (r2 != r4) goto L_0x02bd
                net.openvpn.openvpn.data.EventMsg$Transition r2 = net.openvpn.openvpn.data.EventMsg.Transition.TO_DISCONNECTED
                goto L_0x02af
            L_0x02bd:
                int r2 = r14.flags
                r2 = r2 & 4
                if (r2 == 0) goto L_0x02c9
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.last_event_prof_manage = r14
                goto L_0x02d6
            L_0x02c9:
                int r2 = r14.priority
                if (r2 < r3) goto L_0x02d6
                int r2 = net.openvpn.unified.R$string.auth_failed
                if (r0 == r2) goto L_0x02d6
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.set_last_event(r14, r1)
            L_0x02d6:
                int r0 = r14.res_id
                int r1 = net.openvpn.unified.R$string.ui_reset
                if (r0 == r1) goto L_0x02e0
                java.lang.String r7 = r14.toString()
            L_0x02e0:
                if (r7 == 0) goto L_0x02e5
                android.util.Log.i(r5, r7)
            L_0x02e5:
                int r0 = r14.res_id
                int r1 = net.openvpn.unified.R$string.core_thread_active
                if (r0 != r1) goto L_0x02f6
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.MessageQueue r0 = r0.message_queue
                java.lang.String r1 = "----- OpenVPN Start -----"
                r0.post_log((java.lang.String) r1)
            L_0x02f6:
                if (r7 == 0) goto L_0x0301
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.MessageQueue r0 = r0.message_queue
                r0.post_log((java.lang.String) r7)
            L_0x0301:
                int r0 = r14.res_id
                int r1 = net.openvpn.unified.R$string.disconnected
                if (r0 != r1) goto L_0x032f
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.MessageQueue r0 = r0.message_queue
                net.openvpn.openvpn.OpenVPNService r1 = net.openvpn.openvpn.OpenVPNService.this
                long r1 = r1.get_tunnel_bytes_per_cpu_second()
                java.lang.Long r1 = java.lang.Long.valueOf(r1)
                java.lang.Object[] r1 = new java.lang.Object[]{r1}
                java.lang.String r2 = "Tunnel bytes per CPU second: %d"
                java.lang.String r1 = java.lang.String.format(r2, r1)
                r0.post_log((java.lang.String) r1)
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.MessageQueue r0 = r0.message_queue
                java.lang.String r1 = "----- OpenVPN Stop -----"
                r0.post_log((java.lang.String) r1)
            L_0x032f:
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                r0.update_notification_event(r14)
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                net.openvpn.openvpn.ExternalConnectionManager r0 = r0.externalConnectionManager
                boolean r0 = r0.isExternalConnection()
                r14.is_aon = r0
                net.openvpn.openvpn.OpenVPNService r0 = net.openvpn.openvpn.OpenVPNService.this
                java.lang.String r1 = "event"
                r0.broadcast_message((java.lang.String) r1, r14)
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: net.openvpn.openvpn.OpenVPNService.EventHandler.on_event(net.openvpn.openvpn.data.EventMsg):boolean");
        }
    }

    private static class IncomingHandler extends Handler {
        private final WeakReference<OpenVPNService> serviceRef;

        IncomingHandler(OpenVPNService openVPNService) {
            super(Looper.getMainLooper());
            this.serviceRef = new WeakReference<>(openVPNService);
        }

        public void handleMessage(Message message) {
            String str;
            OpenVPNService openVPNService = this.serviceRef.get();
            if (openVPNService != null) {
                int i = message.what;
                if (i == 1) {
                    String string = message.getData().getString("key");
                    if (string == null) {
                        str = "No key was provided for the connection state observer";
                    } else if (!OpenVPNService.connectionStateObservers.containsKey(string)) {
                        openVPNService.addConnectionStateObserver(string, message.replyTo);
                        openVPNService.publishConnectionStateTo(string, openVPNService.lastKnownState);
                        return;
                    } else {
                        return;
                    }
                } else if (i != 2) {
                    super.handleMessage(message);
                    return;
                } else {
                    String string2 = message.getData().getString("key");
                    if (string2 == null) {
                        str = "No observer was specified to unregister";
                    } else {
                        openVPNService.removeConnectionStateObserver(string2);
                        return;
                    }
                }
                Log.e(OpenVPNService.TAG, str);
            }
        }
    }

    private class LogHandler implements MessageQueue.LogHandler {
        private LogHandler() {
        }

        private void log_message(LogMsg logMsg) {
            OpenVPNService openVPNService = OpenVPNService.this;
            if (!openVPNService.isLogsPaused) {
                logMsg.line = String.format("%s %s", new Object[]{openVPNService.get_locale_date_time(new Date()), logMsg.line});
                OpenVPNService.this.broadcast_message("log", logMsg);
                OpenVPNService.this.logsManager.onLog(logMsg);
            }
        }

        public boolean on_log(LogMsg logMsg) {
            Log.i(OpenVPNService.TAG, String.format("LOG: %s", new Object[]{logMsg.line}));
            log_message(logMsg);
            return true;
        }
    }

    public class ProfileListUtil {
        private final ProfileList list;

        private class CustomComparator implements Comparator<ProfileData> {
            private CustomComparator() {
            }

            public int compare(ProfileData profileData, ProfileData profileData2) {
                return profileData.name.compareTo(profileData2.name);
            }
        }

        ProfileListUtil(ProfileList profileList) {
            this.list = profileList;
        }

        /* access modifiers changed from: private */
        public void sort() {
            Collections.sort(this.list, new CustomComparator());
        }

        public void invalidate_epki_alias(String str) {
            Iterator it = this.list.iterator();
            while (it.hasNext()) {
                new ProfileUtil((ProfileData) it.next()).invalidate_epki_alias(str);
            }
        }

        public void load_profiles(String str) {
            boolean z;
            String[] strArr;
            Object obj;
            int i;
            int i2;
            String str2;
            String str3;
            String str4 = str;
            try {
                char c = 0;
                char c2 = 1;
                if (str4.equals("bundled")) {
                    obj = "assets";
                    strArr = OpenVPNService.this.getResources().getAssets().list("");
                    z = false;
                } else if (str4.equals("imported")) {
                    obj = "app private storage";
                    strArr = OpenVPNService.this.fileList();
                    z = true;
                } else {
                    throw new InternalError();
                }
                int length = strArr.length;
                int i3 = 0;
                while (i3 < length) {
                    String str5 = strArr[i3];
                    Log.i(OpenVPNService.TAG, "load_profiles = file: " + str5);
                    if (ProfileFN.has_ovpn_ext(str5)) {
                        try {
                            String read_file = Util.read_file(OpenVPNService.this.getApplicationContext(), str4, str5);
                            ClientAPI_Config clientAPI_Config = new ClientAPI_Config();
                            clientAPI_Config.setContent(read_file);
                            EvalConfig evalConfig = new EvalConfig(OpenVPNClientHelperWrapper.eval_config(clientAPI_Config));
                            if (evalConfig.error) {
                                Object[] objArr = new Object[2];
                                objArr[c] = str5;
                                objArr[c2] = evalConfig.message;
                                Log.i(OpenVPNService.TAG, String.format("PROFILE: error evaluating %s: %s", objArr));
                            } else {
                                boolean is_using_id = ProfileFN.is_using_id(str5);
                                if (ProfileFN.is_using_id(str5)) {
                                    str2 = ProfileFN.retrieve_profile_name(str5);
                                    str3 = ProfileFN.retrieve_id(str5);
                                } else {
                                    str2 = Util.get_profile_name_string(OpenVPNService.this.getApplicationContext(), str5, evalConfig, Boolean.valueOf(z), str4);
                                    str3 = null;
                                }
                                String str6 = str2;
                                Log.i(OpenVPNService.TAG, "profile_name = " + str6);
                                ProfileData profileData = r2;
                                i = i3;
                                i2 = length;
                                ProfileData profileData2 = new ProfileData(str, str3, str5, z, evalConfig, str6, OpenVPNService.this.prefs.get_string_by_profile(is_using_id ? str3 : str6, "epki_alias"));
                                this.list.add(profileData);
                                i3 = i + 1;
                                length = i2;
                                c = 0;
                                c2 = 1;
                            }
                        } catch (IOException unused) {
                            i = i3;
                            i2 = length;
                            Log.i(OpenVPNService.TAG, String.format("PROFILE: error reading %s from %s", new Object[]{str5, obj}));
                        }
                    }
                    i = i3;
                    i2 = length;
                    i3 = i + 1;
                    length = i2;
                    c = 0;
                    c2 = 1;
                }
            } catch (IOException e) {
                Log.e(OpenVPNService.TAG, "PROFILE: error enumerating assets", e);
            }
        }
    }

    public class ProfileUtil {
        public ProfileData data;

        public ProfileUtil(ProfileData profileData) {
            this.data = profileData;
        }

        /* access modifiers changed from: private */
        public String get_epki_alias() {
            ProfileData profileData = this.data;
            if (profileData == null) {
                return null;
            }
            String str = profileData.external_pki_alias;
            if (str == null) {
                str = OpenVPNService.this.prefs.get_string_by_profile(this.data.id, "epki_alias");
            }
            this.data.external_pki_alias = str;
            return str;
        }

        /* access modifiers changed from: private */
        public void invalidate_epki_alias(String str) {
            String str2 = this.data.external_pki_alias;
            if (str2 != null && str2.equals(str)) {
                this.data.external_pki_alias = null;
                OpenVPNService.this.prefs.delete_key_by_profile(this.data.id, "epki_alias");
            }
        }

        /* access modifiers changed from: private */
        public void persist_epki_alias(String str) {
            if (!str.equals("DISABLE_CLIENT_CERT")) {
                this.data.external_pki_alias = str;
                OpenVPNService.this.prefs.set_string_by_profile(this.data.id, "epki_alias", str);
                OpenVPNService.this.refresh_profile_list(Boolean.TRUE);
            }
        }

        public void forget_cert() {
            ProfileData profileData = this.data;
            if (profileData.external_pki_alias != null) {
                profileData.external_pki_alias = null;
                OpenVPNService.this.prefs.delete_key_by_profile(this.data.id, "epki_alias");
            }
        }

        public String get_type_string() {
            OpenVPNService openVPNService;
            int i;
            if (this.data.get_autologin()) {
                openVPNService = OpenVPNService.this;
                i = R$string.profile_type_autologin;
            } else if (this.data.get_epki()) {
                openVPNService = OpenVPNService.this;
                i = R$string.profile_type_epki;
            } else {
                openVPNService = OpenVPNService.this;
                i = R$string.profile_type_standard;
            }
            return openVPNService.getText(i).toString();
        }
    }

    private class ScreenReceiver extends BroadcastReceiver {
        private Context context;

        public ScreenReceiver(Context context2) {
            this.context = context2;
        }

        public void onReceive(Context context2, Intent intent) {
            String action = intent.getAction();
            String packageName = context2.getPackageName();
            if (action != null) {
                boolean z = packageName.equals("net.openvpn.openvpn") && OpenVPNService.this.prefs.get_boolean("pause_vpn_on_blanked_screen", false);
                if (action.equals("android.intent.action.SCREEN_OFF")) {
                    Log.i(OpenVPNService.TAG, String.format("ScreenReceiver: SCREEN_OFF pvbs=%b", new Object[]{Boolean.valueOf(z)}));
                    OpenVPNService openVPNService = OpenVPNService.this;
                    openVPNService.screen_on = false;
                    if (!openVPNService.paused && z) {
                        OpenVPNService.this.network_pause();
                    }
                } else if (action.equals("android.intent.action.SCREEN_ON")) {
                    Log.i(OpenVPNService.TAG, String.format("ScreenReceiver: SCREEN_ON pvbs=%b", new Object[]{Boolean.valueOf(z)}));
                    OpenVPNService openVPNService2 = OpenVPNService.this;
                    openVPNService2.screen_on = true;
                    if (((openVPNService2.paused && z) || OpenVPNService.this.paused_before_timeout) && OpenVPNService.this.isOnline() && !OpenVPNService.this.auth_pending) {
                        OpenVPNService.this.network_resume();
                        OpenVPNService.this.paused_before_timeout = false;
                    }
                }
            }
        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            this.context.registerReceiver(this, intentFilter);
        }

        public void unregister() {
            this.context.unregisterReceiver(this);
        }
    }

    private class TunBuilder extends VpnService.Builder implements OpenVPNClientThread.TunBuilder {
        private TunBuilder() {
            super(OpenVPNService.this);
        }

        private void addRouteAddress(String str, int i) {
            addRoute(NetworkUtil.getNetworkAddressV4(str, i), i);
        }

        private void log_error(String str, Exception exc) {
            Log.d(OpenVPNService.TAG, String.format("BUILDER_ERROR: %s %s", new Object[]{str, exc.toString()}));
        }

        public boolean tun_builder_add_address(String str, int i, String str2, boolean z, boolean z2) {
            try {
                Log.d(OpenVPNService.TAG, String.format("BUILDER: add_address %s/%d %s ipv6=%b net30=%b", new Object[]{str, Integer.valueOf(i), str2, Boolean.valueOf(z), Boolean.valueOf(z2)}));
                addAddress(str, i);
                if (!z) {
                    addRouteAddress(str, i);
                }
                return true;
            } catch (Exception e) {
                log_error("tun_builder_add_address", e);
                return false;
            }
        }

        public boolean tun_builder_add_route(String str, int i, boolean z) {
            try {
                Log.d(OpenVPNService.TAG, String.format("BUILDER: add_route %s/%d ipv6=%b", new Object[]{str, Integer.valueOf(i), Boolean.valueOf(z)}));
                addRoute(str, i);
                return true;
            } catch (Exception e) {
                log_error("tun_builder_add_route", e);
                return false;
            }
        }

        public int tun_builder_establish() {
            try {
                Log.d(OpenVPNService.TAG, "BUILDER: establish");
                if (Build.VERSION.SDK_INT >= 29) {
                    VpnService.Builder unused = setMetered(false);
                }
                PendingIntent r0 = OpenVPNService.this.get_configure_intent(0);
                if (r0 != null) {
                    setConfigureIntent(r0);
                }
                return establish().detachFd();
            } catch (Exception e) {
                log_error("tun_builder_establish", e);
                return -1;
            }
        }

        public boolean tun_builder_exclude_route(String str, int i, boolean z) {
            try {
                Log.d(OpenVPNService.TAG, String.format("BUILDER: exclude_route %s/%d ipv6=%b", new Object[]{str, Integer.valueOf(i), Boolean.valueOf(z)}));
                if (Build.VERSION.SDK_INT >= 33) {
                    InetAddress byName = InetAddress.getByName(str);
                    OpenVPNService$TunBuilder$$ExternalSyntheticApiModelOutline3.m();
                    VpnService.Builder unused = excludeRoute(OpenVPNService$TunBuilder$$ExternalSyntheticApiModelOutline2.m(byName, i));
                }
                return true;
            } catch (Exception e) {
                log_error("tun_builder_exclude_route", e);
                return false;
            }
        }

        public ClientAPI_StringVec tun_builder_get_local_networks(boolean z) {
            ClientAPI_StringVec clientAPI_StringVec = new ClientAPI_StringVec();
            Iterator<String> it = NetworkUtil.getLocalNetworks(OpenVPNService.this, z).iterator();
            while (it.hasNext()) {
                clientAPI_StringVec.add(it.next());
            }
            return clientAPI_StringVec;
        }

        public boolean tun_builder_reroute_gw(boolean z, boolean z2, long j) {
            try {
                Log.d(OpenVPNService.TAG, String.format("BUILDER: reroute_gw ipv4=%b ipv6=%b flags=%d", new Object[]{Boolean.valueOf(z), Boolean.valueOf(z2), Long.valueOf(j)}));
                if ((j & 65536) == 0) {
                    if (z) {
                        addRoute("0.0.0.0", 0);
                    }
                    if (z2) {
                        addRoute("::", 0);
                    }
                }
                return true;
            } catch (Exception e) {
                log_error("tun_builder_add_route", e);
                return false;
            }
        }

        public boolean tun_builder_set_allow_family(int i, boolean z) {
            try {
                Log.d(OpenVPNService.TAG, String.format("BUILDER: set_allow_family %s", new Object[]{Integer.valueOf(i)}));
                if (i == OsConstants.AF_INET && z) {
                    Log.d(OpenVPNService.TAG, String.format("BUILDER: set_allow_family OsConstants.AF_INET", new Object[0]));
                    allowFamily(i);
                }
                if (i == OsConstants.AF_INET6 && z) {
                    Log.d(OpenVPNService.TAG, String.format("BUILDER: set_allow_family OsConstants.AF_INET6", new Object[0]));
                    allowFamily(i);
                }
                return true;
            } catch (Exception e) {
                log_error("tun_builder_set_allow_family", e);
                return false;
            }
        }

        public boolean tun_builder_set_dns_options(DnsOptions dnsOptions) {
            String string;
            Iterator it = dnsOptions.getSearch_domains().iterator();
            while (it.hasNext()) {
                addSearchDomain(((DnsDomain) it.next()).getDomain());
            }
            boolean z = false;
            for (Map.Entry entry : new TreeMap(dnsOptions.getServers()).entrySet()) {
                DnsServer dnsServer = (DnsServer) entry.getValue();
                int intValue = ((Integer) entry.getKey()).intValue();
                if (DnsServer.Security.Yes.equals(dnsServer.getDnssec())) {
                    string = OpenVPNService.this.getString(R$string.dnsserver_ignore_dnnsec, new Object[]{Integer.valueOf(intValue), dnsServer.to_string().trim()});
                } else if (DnsServer.Transport.Plain.equals(dnsServer.getTransport()) || DnsServer.Transport.Unset.equals(dnsServer.getTransport())) {
                    Iterator it2 = dnsServer.getAddresses().iterator();
                    while (it2.hasNext()) {
                        DnsAddress dnsAddress = (DnsAddress) it2.next();
                        if (dnsAddress.getPort() == 0 || dnsAddress.getPort() == 53) {
                            addDnsServer(dnsAddress.getAddress());
                            z = true;
                        } else {
                            Log.d(OpenVPNService.TAG, OpenVPNService.this.getString(R$string.dnsserver_ignore_dnsport, new Object[]{dnsAddress.getAddress(), Long.valueOf(dnsAddress.getPort()), Integer.valueOf(intValue), dnsServer.to_string().trim()}));
                        }
                    }
                    if (z) {
                        return true;
                    }
                } else {
                    string = OpenVPNService.this.getString(R$string.dnsserver_ignore_tls_doh, new Object[]{Integer.valueOf(intValue), dnsServer.to_string().trim()});
                }
                Log.d(OpenVPNService.TAG, string);
            }
            Log.d(OpenVPNService.TAG, OpenVPNService.this.getString(R$string.dnsserver_no_valid_server));
            OpenVPNService.this.stop_thread();
            return false;
        }

        public boolean tun_builder_set_mtu(int i) {
            try {
                Log.d(OpenVPNService.TAG, String.format("BUILDER: set_mtu %d", new Object[]{Integer.valueOf(i)}));
                setMtu(i);
                return true;
            } catch (Exception e) {
                log_error("tun_builder_set_mtu", e);
                return false;
            }
        }

        public boolean tun_builder_set_remote_address(String str, boolean z) {
            try {
                Log.d(OpenVPNService.TAG, String.format("BUILDER: set_remote_address %s ipv6=%b", new Object[]{str, Boolean.valueOf(z)}));
                return true;
            } catch (Exception e) {
                log_error("tun_builder_set_remote_address", e);
                return false;
            }
        }

        public boolean tun_builder_set_session_name(String str) {
            try {
                Log.d(OpenVPNService.TAG, String.format("BUILDER: set_session_name %s", new Object[]{str}));
                setSession(str);
                return true;
            } catch (Exception e) {
                log_error("tun_builder_set_session_name", e);
                return false;
            }
        }

        public void tun_builder_teardown(boolean z) {
            try {
                Log.d(OpenVPNService.TAG, String.format("BUILDER: teardown disconnect=%b", new Object[]{Boolean.valueOf(z)}));
            } catch (Exception e) {
                log_error("tun_builder_teardown", e);
            }
        }
    }

    static {
        Util.loadNativeLibraries();
    }

    /* access modifiers changed from: private */
    public void addConnectionStateObserver(String str, Messenger messenger) {
        connectionStateObservers.put(str, messenger);
    }

    private void aonProvideCreds(String str, Intent intent) {
        Bundle bundleExtra = intent.getBundleExtra(str + ".CREDS");
        if (bundleExtra != null) {
            HashMap hashMap = new HashMap();
            String string = bundleExtra.getString("username");
            String string2 = bundleExtra.getString("password");
            String string3 = bundleExtra.getString("challengeResponse");
            String string4 = bundleExtra.getString("privateKeyPassword");
            if (string != null) {
                hashMap.put("username", string);
            }
            if (string2 != null) {
                hashMap.put("password", string2);
            }
            if (string3 != null) {
                hashMap.put("response", string3);
            }
            if (string4 != null) {
                hashMap.put("privateKeyPassword", string4);
            }
            this.externalConnectionManager.provideCreds(hashMap);
        }
        connect_action(str, intent, false);
    }

    static String append_env_vars(String str, List<EnvVar> list) {
        String str2 = "";
        for (int i = 0; i < list.size(); i++) {
            str2 = str2 + "setenv " + list.get(i).toString() + "\n";
        }
        return str + "\n" + str2;
    }

    /* access modifiers changed from: private */
    public void attempt_delayed_resume() {
        attempt_delayed_resume(Integer.valueOf(IPCUtils.StringChunker.CHUNK_LIMIT));
    }

    private void attempt_delayed_resume(Integer num) {
        Handler handler = new Handler();
        AnonymousClass39 r1 = new Runnable() {
            public void run() {
                if (!OpenVPNService.this.initial_connect && !OpenVPNService.this.manual_pause) {
                    OpenVPNService openVPNService = OpenVPNService.this;
                    if (openVPNService.screen_on && openVPNService.isOnline()) {
                        Log.d(OpenVPNService.TAG, "Attempting to resume connection...");
                        OpenVPNService.this.connect_resume();
                    }
                }
            }
        };
        Log.d(TAG, String.format("Pausing, will attempt to resume connection in %d", new Object[]{num}));
        handler.postDelayed(r1, (long) num.intValue());
    }

    /* access modifiers changed from: private */
    public void cancel_ping() {
        Request.Ping ping = this.pinger;
        if (ping != null) {
            ping.cancel();
        }
    }

    private String cert_format_pem(X509Certificate x509Certificate) {
        return String.format("-----BEGIN CERTIFICATE-----%n%s-----END CERTIFICATE-----%n", new Object[]{Base64.encodeToString(x509Certificate.getEncoded(), 0)});
    }

    private boolean checkVPNPermission() {
        if (VpnService.prepare(this) == null) {
            return true;
        }
        this.notificationService.showNotification(NOTIFICATION_ID, this.mNotifyBuilder);
        return false;
    }

    private boolean connect_action(final String str, final Intent intent, final boolean z) {
        if (this.active) {
            this.manual_pause = false;
            this.paused = false;
            stop_thread();
            Handler handler = this.connectDelayHandler;
            if (handler != null) {
                handler.removeCallbacks(this.connectDelayedTask);
            }
            this.connectDelayHandler = new Handler();
            AnonymousClass40 r0 = new Runnable() {
                public void run() {
                    boolean unused = OpenVPNService.this.do_connect_action(str, intent, z);
                }
            };
            this.connectDelayedTask = r0;
            this.connectDelayHandler.postDelayed(r0, 2000);
            return true;
        }
        do_connect_action(str, intent, z);
        return true;
    }

    /* access modifiers changed from: private */
    public void connect_resume() {
        if (this.active) {
            this.paused = false;
            this.mThread.resume();
        }
    }

    private void crypto_self_test() {
        String crypto_self_test = OpenVPNClientHelperWrapper.crypto_self_test();
        if (crypto_self_test.length() > 0) {
            Log.d(TAG, String.format("SERV: crypto_self_test\n%s", new Object[]{crypto_self_test}));
        }
    }

    private boolean delete_profile_action(String str, Intent intent) {
        String stringExtra = intent.getStringExtra(str + ".PROFILE");
        refresh_profile_list();
        ProfileData profileData = this.profile_list.get_profile_by_id(stringExtra);
        if (profileData == null) {
            return false;
        }
        if (!profileData.is_deleteable()) {
            EventMsg eventMsg = new EventMsg();
            eventMsg.name = "PROFILE_DELETE_FAILED";
            eventMsg.info = stringExtra;
            this.message_queue.post_event(eventMsg);
        } else {
            if (this.active && profileData == this.current_profile) {
                stop_thread();
            }
            if (!deleteFile(profileData.get_filename())) {
                stringExtra = profileData.get_id();
            } else {
                this.pwds.remove("auth", stringExtra);
                this.pwds.remove("pk", stringExtra);
                refresh_profile_list(Boolean.TRUE);
                gen_event(0, "PROFILE_DELETE_SUCCESS", profileData.get_id());
                return true;
            }
        }
        gen_event(1, "PROFILE_DELETE_FAILED", stringExtra);
        return false;
    }

    private void disconnect_action(String str, Intent intent) {
        this.paused = false;
        this.manual_pause = false;
        boolean booleanExtra = intent.getBooleanExtra(str + ".STOP", false);
        cancel_ping();
        this.auth_pending = false;
        ProfileData profileData = this.current_profile;
        if (profileData != null) {
            broadcast_message("current_profile", profileData);
        }
        stop_thread();
        Handler handler = this.connectDelayHandler;
        if (handler != null) {
            handler.removeCallbacks(this.connectDelayedTask);
        }
        if (booleanExtra && this.active) {
            this.message_queue.post_event(new EventMsg("CANCELLED"));
            this.should_stop = true;
        }
        publishConnectionStateToAll(ConnectionState.DISCONNECTED);
        stop_notification();
        this.externalConnectionManager.setExternalConnectionEnabled(false);
        this.externalConnectionManager.setNotificationShows(false);
    }

    /* access modifiers changed from: private */
    @SuppressLint({"SuspiciousIndentation"})
    public boolean do_connect_action(String str, Intent intent, boolean z) {
        ProxyContext proxyContext;
        VpnProfile fetchVPNProfile = fetchVPNProfile(str, intent);
        if (fetchVPNProfile == null) {
            return false;
        }
        Map<String, String> creds = this.externalConnectionManager.getCreds();
        String str2 = creds.get("username");
        String str3 = creds.get("password");
        String str4 = creds.get("response");
        String str5 = creds.get("privateKeyPassword");
        String profileId = fetchVPNProfile.getProfileId();
        String guiVersion = fetchVPNProfile.getGuiVersion();
        String proxyName = fetchVPNProfile.getProxyName();
        String proxyUsername = fetchVPNProfile.getProxyUsername();
        String proxyPassword = fetchVPNProfile.getProxyPassword();
        String proxyHost = fetchVPNProfile.getProxyHost();
        String proxyPort = fetchVPNProfile.getProxyPort();
        String proxyType = fetchVPNProfile.getProxyType();
        boolean isProxyAllowCleartextAuth = fetchVPNProfile.isProxyAllowCleartextAuth();
        boolean isProxyAllowCredsDialog = fetchVPNProfile.isProxyAllowCredsDialog();
        String server = fetchVPNProfile.getServer();
        String proto = fetchVPNProfile.getProto();
        String allowUnusedAddrFamilies = fetchVPNProfile.getAllowUnusedAddrFamilies();
        String connTimeout = fetchVPNProfile.getConnTimeout();
        if (str2 == null || str2.isEmpty()) {
            str2 = fetchVPNProfile.getUsername();
        }
        String str6 = str2;
        if (str3 == null || str3.isEmpty()) {
            str3 = fetchVPNProfile.getPassword();
        }
        String response = (str4 == null || str4.isEmpty()) ? fetchVPNProfile.getResponse() : str4;
        if (str5 == null || str5.isEmpty()) {
            str5 = fetchVPNProfile.getPkPassword();
        }
        String str7 = str5;
        String epkiAlias = fetchVPNProfile.getEpkiAlias();
        String compressionMode = fetchVPNProfile.getCompressionMode();
        this.profile_display_name = fetchVPNProfile.getProfileDisplayName();
        cancel_ping();
        this.auth_pending = false;
        String pw_repl = OpenVPNDebug.pw_repl(str6, str3);
        ProfileData locate_profile = locate_profile(profileId);
        if (locate_profile == null) {
            return false;
        }
        set_current_profile(locate_profile);
        String str8 = locate_profile.get_location();
        String str9 = locate_profile.get_filename();
        try {
            String append_env_vars = append_env_vars(Util.read_file(getApplicationContext(), str8, str9), construct_env_vars());
            if (proxyName != null) {
                ProxyContext proxyContext2 = locate_profile.get_proxy_context(true);
                proxyContext2.new_connection(intent, profileId, proxyName, proxyUsername, proxyPassword, proxyHost, proxyPort, proxyType, isProxyAllowCleartextAuth, isProxyAllowCredsDialog, this.proxy_list, z);
                proxyContext = proxyContext2;
            } else {
                locate_profile.reset_proxy_context();
                proxyContext = null;
            }
            Log.d(TAG, String.format("SERV: profile file len=%d", new Object[]{Integer.valueOf(append_env_vars.length())}));
            if (!Objects.equals(this.prefs.get_string("security_level"), "insecure") && isProxyAllowCleartextAuth) {
                gen_event(1, "BASIC_AUTH_PROXY_ERROR", (String) null);
                start_notification();
                return false;
            } else if (isOnline()) {
                return start_connection(locate_profile, append_env_vars, guiVersion, proxyContext, server, proto, allowUnusedAddrFamilies, connTimeout, str6, pw_repl, str7, response, epkiAlias, compressionMode);
            } else {
                EventMsg eventMsg = new EventMsg("NETWORK_UNREACHABLE");
                eventMsg.res_id = R$string.network_unavailable;
                eventMsg.icon_res_id = R$drawable.error;
                set_last_event(eventMsg, true);
                this.message_queue.post_event(eventMsg);
                return false;
            }
        } catch (IOException unused) {
            gen_event(1, "PROFILE_NOT_FOUND", String.format("%s/%s", new Object[]{str8, str9}));
            return false;
        }
    }

    private void externalDisconnect() {
        this.message_queue.post_event(new EventMsg("EXTERNAL_DISCONNECT"));
    }

    private VpnProfile fetchVPNProfile(String str, Intent intent) {
        VpnProfile.Builder builder = new VpnProfile.Builder();
        if (intent != null) {
            if (intent.hasExtra(str + ".PROFILE")) {
                VpnProfile.Builder profileId = builder.profileId(intent.getStringExtra(str + ".PROFILE"));
                VpnProfile.Builder guiVersion = profileId.guiVersion(intent.getStringExtra(str + ".GUI_VERSION"));
                VpnProfile.Builder proxyName = guiVersion.proxyName(intent.getStringExtra(str + ".PROXY_NAME"));
                VpnProfile.Builder proxyUsername = proxyName.proxyUsername(intent.getStringExtra(str + ".PROXY_USERNAME"));
                VpnProfile.Builder proxyPassword = proxyUsername.proxyPassword(intent.getStringExtra(str + ".PROXY_PASSWORD"));
                VpnProfile.Builder proxyHost = proxyPassword.proxyHost(intent.getStringExtra(str + ".PROXY_HOST"));
                VpnProfile.Builder proxyPort = proxyHost.proxyPort(intent.getStringExtra(str + ".PROXY_PORT"));
                VpnProfile.Builder proxyType = proxyPort.proxyType(intent.getStringExtra(str + ".PROXY_TYPE"));
                VpnProfile.Builder proxyAllowCleartextAuth = proxyType.proxyAllowCleartextAuth(intent.getBooleanExtra(str + ".PROXY_ALLOW_CLEARTEXT_AUTH", false));
                VpnProfile.Builder proxyAllowCredsDialog = proxyAllowCleartextAuth.proxyAllowCredsDialog(intent.getBooleanExtra(str + ".PROXY_ALLOW_CREDS_DIALOG", false));
                VpnProfile.Builder server = proxyAllowCredsDialog.server(intent.getStringExtra(str + ".SERVER"));
                VpnProfile.Builder proto = server.proto(intent.getStringExtra(str + ".PROTO"));
                VpnProfile.Builder allowUnusedAddrFamilies = proto.allowUnusedAddrFamilies(intent.getStringExtra(str + ".ALLOW_UNUSED_ADDR_FAMILIES"));
                VpnProfile.Builder connTimeout = allowUnusedAddrFamilies.connTimeout(intent.getStringExtra(str + ".CONN_TIMEOUT"));
                VpnProfile.Builder username = connTimeout.username(intent.getStringExtra(str + ".USERNAME"));
                VpnProfile.Builder password = username.password(intent.getStringExtra(str + ".PASSWORD"));
                VpnProfile.Builder pkPassword = password.pkPassword(intent.getStringExtra(str + ".PK_PASSWORD"));
                VpnProfile.Builder response = pkPassword.response(intent.getStringExtra(str + ".RESPONSE"));
                VpnProfile.Builder epkiAlias = response.epkiAlias(intent.getStringExtra(str + ".EPKI_ALIAS"));
                VpnProfile.Builder compressionMode = epkiAlias.compressionMode(intent.getStringExtra(str + ".COMPRESSION_MODE"));
                compressionMode.profileDisplayName(intent.getStringExtra(str + ".PROFILE_DISPLAY_NAME"));
                ProfileManager.setLastConnectedVpnProfile(this.prefs, builder.build());
                return builder.build();
            }
        }
        VpnProfile lastConnectedProfile = ProfileManager.getLastConnectedProfile(this.prefs);
        if (lastConnectedProfile == null) {
            return null;
        }
        builder.profileId(lastConnectedProfile.getProfileId()).guiVersion(lastConnectedProfile.getGuiVersion()).proxyName(lastConnectedProfile.getProxyName()).proxyUsername(lastConnectedProfile.getProxyUsername()).proxyPassword(lastConnectedProfile.getProxyPassword()).proxyHost(lastConnectedProfile.getProxyHost()).proxyPort(lastConnectedProfile.getProxyPort()).proxyType(lastConnectedProfile.getProxyType()).proxyAllowCleartextAuth(lastConnectedProfile.isProxyAllowCleartextAuth()).proxyAllowCredsDialog(lastConnectedProfile.isProxyAllowCredsDialog()).server(lastConnectedProfile.getServer()).proto(lastConnectedProfile.getProto()).allowUnusedAddrFamilies(lastConnectedProfile.getAllowUnusedAddrFamilies()).connTimeout(lastConnectedProfile.getConnTimeout()).username(lastConnectedProfile.getUsername()).response(lastConnectedProfile.getResponse()).epkiAlias(lastConnectedProfile.getEpkiAlias()).compressionMode(lastConnectedProfile.getCompressionMode()).profileDisplayName(lastConnectedProfile.getProfileDisplayName());
        return builder.build();
    }

    private void gen_event(int i, String str, String str2) {
        gen_event(i, str, str2, (String) null, 20);
    }

    private void gen_event(int i, String str, String str2, String str3) {
        gen_event(i, str, str2, str3, 20);
    }

    private void gen_event(int i, String str, String str2, String str3, int i2) {
        EventInfo eventInfo = (EventInfo) this.event_info.get(str);
        EventMsg eventMsg = new EventMsg();
        int i3 = i | 2;
        eventMsg.flags = i3;
        if (eventInfo != null) {
            eventMsg.progress = eventInfo.progress;
            eventMsg.priority = eventInfo.priority;
            eventMsg.res_id = eventInfo.res_id;
            eventMsg.icon_res_id = eventInfo.icon_res_id;
            eventMsg.sender = i2;
            eventMsg.flags = i3 | eventInfo.flags;
        } else {
            eventMsg.res_id = R$string.unknown;
        }
        eventMsg.name = str;
        if (str2 != null) {
            eventMsg.info = str2;
        } else {
            eventMsg.info = "";
        }
        if ((eventMsg.flags & 4) != 0) {
            eventMsg.expires = SystemClock.elapsedRealtime() + 60000;
        }
        eventMsg.profile_override = str3;
        this.message_queue.post_event(eventMsg);
    }

    @SuppressLint({"HardwareIds"})
    private String getFakeMacAddrFromSAAID(Context context) {
        char[] charArray = "0123456789ABCDEF".toCharArray();
        String string = Settings.Secure.getString(context.getContentResolver(), "android_id");
        StringBuilder sb = new StringBuilder();
        if (string.length() >= 6) {
            byte[] bytes = string.getBytes();
            for (int i = 0; i <= 6; i++) {
                if (i != 0) {
                    sb.append(":");
                }
                byte b = bytes[i] & 255;
                sb.append(charArray[b >>> 4]);
                sb.append(charArray[b & 15]);
            }
        }
        return sb.toString();
    }

    private String getTlsVersionMinOverride(String str, String str2) {
        if (str2.equals("tls_1_3")) {
            return str2;
        }
        if (str.equals("preferred") || str.equals("legacy")) {
            return "tls_1_2";
        }
        if (str.equals("insecure")) {
            return "tls_1_0";
        }
        throw new IllegalArgumentException("Unknown security level specified");
    }

    /* access modifiers changed from: private */
    public PendingIntent get_configure_intent(int i) {
        PendingIntent createPendingIntent = this.notificationService.createPendingIntent(this.notificationService.createIntent(MainActivity.class, "android.intent.action.VIEW"), 67108864);
        if (this.ipc_receiver.mClients.size() <= 0) {
            return createPendingIntent;
        }
        AnonymousClass41 r1 = new IPCCallback<PendingIntent, Void>() {
            public Void call() {
                return null;
            }
        };
        synchronized (r1) {
            broadcast_message("get_configure_intent", Integer.valueOf(i), r1);
            try {
                r1.wait(2000);
                createPendingIntent = (PendingIntent) r1.getValue();
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return createPendingIntent;
    }

    /* access modifiers changed from: private */
    public String get_locale_date_time(Date date) {
        return new SimpleDateFormat("[MMM dd, yyyy, HH:mm:ss]").format(date);
    }

    protected static String get_openvpn_core_platform() {
        return OpenVPNClientHelperWrapper.platform();
    }

    private String get_raw_stats() {
        StringBuffer stringBuffer = new StringBuffer();
        String[] stat_names = stat_names();
        Iterator it = stat_values_full().iterator();
        int i = 0;
        while (it.hasNext()) {
            int i2 = i + 1;
            String str = stat_names[i];
            Long l = (Long) it.next();
            if (l.longValue() > 0) {
                stringBuffer.append(String.format("  %s : %s\n", new Object[]{str, Long.valueOf(l.longValue())}));
            }
            i = i2;
        }
        return stringBuffer.toString();
    }

    private void handleConnectedState() {
        this.active = true;
        publishConnectionStateToAll(ConnectionState.CONNECTED);
        if (this.externalConnectionManager.isExternalConnection()) {
            this.externalConnectionManager.resetAttemptCounter();
            gen_event(0, "AON_RESUME", (String) null);
        }
    }

    private void handleExternalStart(Intent intent) {
        this.externalConnectionManager.setExternalConnectionEnabled(true);
        this.externalConnectionManager.setNotificationShows(true);
        gen_event(1, "AON_IS_AON", (String) null);
        Log.d(TAG, "Always-On VPN case detected");
        new Handler().post(new OpenVPNService$$ExternalSyntheticLambda3(this, intent));
    }

    /* access modifiers changed from: private */
    public boolean handleOpenURL(final EventMsg eventMsg) {
        Matcher matcher = Pattern.compile("OPEN_URL:(https?://.+)").matcher(eventMsg.info);
        if (matcher.find()) {
            String group = matcher.group(1);
            final Request.Ping.Options options = new Request.Ping.Options();
            options.setShouldRepeat(Boolean.TRUE);
            options.setRepeatTimeouts(this.ping_repeat_timeouts);
            try {
                options.setUrl(group);
                final Request.Ping ping = new Request.Ping(options, this);
                AnonymousClass42 r2 = new Runnable() {
                    public void run() {
                        Boolean bool = Boolean.FALSE;
                        try {
                            bool = ping.call();
                        } catch (Exception unused) {
                        }
                        OpenVPNService.this.pinger = null;
                        if (!ping.cancelled().booleanValue()) {
                            if (bool.booleanValue()) {
                                OpenVPNService.this.broadcast_message("event", eventMsg);
                                OpenVPNService.this.auth_pending = true;
                                return;
                            }
                            OpenVPNService.this.broadcast_message("event", new EventMsg("AUTH_PENDING_FAILED", options.getHostname()));
                        }
                    }
                };
                this.pinger = ping;
                new Thread(r2).start();
                return true;
            } catch (MalformedURLException unused) {
            }
        }
        return false;
    }

    private void handleReboot(String str, Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra(autoConnectKey, false);
        boolean booleanExtra2 = intent.getBooleanExtra(autoStartKey, false);
        String stringExtra = intent.getStringExtra(profileIDKey);
        if (booleanExtra2 || (booleanExtra && stringExtra != null)) {
            connect_action(str, intent, false);
        }
    }

    private void handleRegularStart(String str, Intent intent) {
        this.externalConnectionManager.setExternalConnectionEnabled(false);
        this.externalConnectionManager.setNotificationShows(false);
        this.last_core_event = null;
        Log.d(TAG, String.format("Regular case: onStartCommand callback=%s", new Object[]{str}));
        new Handler().post(new OpenVPNService$$ExternalSyntheticLambda1(this, str, intent));
    }

    private ImportResult import_and_save_profile(String str, String str2, ClientAPI_MergeConfig clientAPI_MergeConfig, ClientAPI_EvalConfig clientAPI_EvalConfig, ClientAPI_Config clientAPI_Config) {
        String valueOf = String.valueOf(System.currentTimeMillis());
        MergeConfig mergeConfig = new MergeConfig(clientAPI_MergeConfig);
        EvalConfig evalConfig = new EvalConfig(clientAPI_EvalConfig);
        Config config = new Config(clientAPI_Config);
        String create_file_name = ProfileFN.create_file_name(str, valueOf);
        if (str == null || create_file_name == null) {
            throw new ImportException("IMPORT_ERROR", "Failed to write file");
        }
        try {
            FileUtil.writeFileAppPrivate(this, create_file_name, str2);
            this.pwds.remove("auth", valueOf);
            this.pwds.remove("pk", valueOf);
            refresh_profile_list(Boolean.TRUE);
            return new ImportResult(getApplicationContext(), valueOf, evalConfig, mergeConfig, config, str, create_file_name);
        } catch (IOException unused) {
            throw new ImportException("IMPORT_ERROR", "Failed to write ovpn profile");
        }
    }

    private boolean import_profile_action(String str, Intent intent) {
        String stringExtra = intent.getStringExtra(str + ".CONTENT");
        intent.getStringExtra(str + ".FILENAME");
        intent.getBooleanExtra(str + ".MERGE", false);
        try {
            import_profile_from_config(OpenVPNClientHelperWrapper.merge_config_string(stringExtra), (String) null);
            return true;
        } catch (ImportException e) {
            throw e;
        } catch (ImportException unused) {
            return false;
        }
    }

    private ImportResult import_profile_from_config(ClientAPI_MergeConfig clientAPI_MergeConfig, String str) {
        String profileContent = clientAPI_MergeConfig.getProfileContent();
        String basename = clientAPI_MergeConfig.getBasename();
        ClientAPI_Config clientAPI_Config = new ClientAPI_Config();
        clientAPI_Config.setContent(profileContent);
        ClientAPI_EvalConfig eval_config = OpenVPNClientHelperWrapper.eval_config(clientAPI_Config);
        if (!eval_config.getError()) {
            return import_and_save_profile(str, profileContent, clientAPI_MergeConfig, eval_config, clientAPI_Config);
        }
        throw new ImportException("ERR_PROFILE_GENERIC", String.format("%s : %s", new Object[]{basename, eval_config.getMessage()}));
    }

    private boolean import_profile_via_path_action(String str, Intent intent) {
        ClientAPI_MergeConfig merge_config = OpenVPNClientHelperWrapper.merge_config(intent.getStringExtra(str + ".PATH"), true);
        String str2 = "PROFILE_" + merge_config.getStatus();
        if (str2.equals("PROFILE_MERGE_SUCCESS")) {
            try {
                import_profile_from_config(merge_config, (String) null);
                return true;
            } catch (ImportException unused) {
                return false;
            }
        } else {
            gen_event(1, str2, merge_config.getErrorText());
            return false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleExternalStart$0(Intent intent) {
        lambda$handleRegularStart$1(ACTION_CONNECT, intent);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$registerConnectionStateObserver$2(ConnectionStateChangeListener connectionStateChangeListener, Message message) {
        String string = message.getData().getString("state");
        if (string == null || message.what != 3) {
            return true;
        }
        connectionStateChangeListener.onChange(ConnectionState.valueOf(string));
        return true;
    }

    private ProfileData locate_profile(String str) {
        refresh_profile_list();
        ProfileData profileData = this.profile_list.get_profile_by_id(str);
        if (profileData != null) {
            return profileData;
        }
        gen_event(1, "PROFILE_NOT_FOUND", str);
        return null;
    }

    private void log_stats() {
        if (this.active) {
            String[] stat_names = stat_names();
            Iterator it = stat_values_full().iterator();
            int i = 0;
            while (it.hasNext()) {
                int i2 = i + 1;
                String str = stat_names[i];
                Long l = (Long) it.next();
                if (l.longValue() > 0) {
                    Log.i(TAG, String.format("STAT %s=%s", new Object[]{str, Long.valueOf(l.longValue())}));
                }
                i = i2;
            }
        }
    }

    private void manual_pause() {
        this.manual_pause = true;
        NotificationCompat.Builder builder = this.mNotifyBuilder;
        if (builder != null) {
            this.notificationService.updateNotificationActions(builder, new NotificationService.Action(getString(R$string.notification_resume), resumePendingService), new NotificationService.Action(getString(R$string.disconnect), disconnectPendingService));
            this.notificationService.showNotification(NOTIFICATION_ID, this.mNotifyBuilder);
        }
        network_pause();
    }

    private void manual_resume() {
        this.manual_pause = false;
        NotificationCompat.Builder builder = this.mNotifyBuilder;
        if (builder != null) {
            this.notificationService.updateNotificationActions(builder, new NotificationService.Action(getString(R$string.notification_pause), pausePendingService), new NotificationService.Action(getString(R$string.disconnect), disconnectPendingService));
            this.notificationService.showNotification(NOTIFICATION_ID, this.mNotifyBuilder);
        }
        if (isOnline()) {
            network_resume();
        }
    }

    public static long max_profile_size() {
        return (long) OpenVPNClientHelperWrapper.max_profile_size();
    }

    private void populate_event_info_map() {
        HashMap hashMap = new HashMap();
        this.event_info = hashMap;
        int i = R$string.reconnecting;
        int i2 = R$drawable.connecting;
        NotificationService.NotificationActionType notificationActionType = NotificationService.NotificationActionType.AUTH_PENDING;
        hashMap.put("RECONNECTING", new EventInfo(i, i2, 20, 2, 0, notificationActionType));
        NotificationService.NotificationActionType notificationActionType2 = notificationActionType;
        this.event_info.put("RESOLVE", new EventInfo(R$string.resolve, R$drawable.connecting, 30, 1, 0, notificationActionType2));
        this.event_info.put("WAIT_PROXY", new EventInfo(R$string.wait_proxy, R$drawable.connecting, 40, 1, 0, notificationActionType2));
        this.event_info.put("WAIT", new EventInfo(R$string.wait, R$drawable.connecting, 50, 1, 0, notificationActionType2));
        this.event_info.put("CONNECTING", new EventInfo(R$string.connecting, R$drawable.connecting, 60, 1, 0, notificationActionType2));
        this.event_info.put("GET_CONFIG", new EventInfo(R$string.get_config, R$drawable.connecting, 70, 1, 0, notificationActionType2));
        this.event_info.put("ASSIGN_IP", new EventInfo(R$string.assign_ip, R$drawable.connecting, 80, 0, 0, notificationActionType2));
        this.event_info.put("ADD_ROUTES", new EventInfo(R$string.add_routes, R$drawable.connecting, 90, 1, 0, notificationActionType2));
        HashMap hashMap2 = this.event_info;
        int i3 = R$string.connected;
        int i4 = R$drawable.connected;
        NotificationService.NotificationActionType notificationActionType3 = NotificationService.NotificationActionType.NORMAL;
        hashMap2.put("CONNECTED", new EventInfo(i3, i4, 100, 3, 0, notificationActionType3));
        this.event_info.put("DISCONNECTED", new EventInfo(R$string.disconnected, R$drawable.disconnected, 0, 2, 0));
        HashMap hashMap3 = this.event_info;
        int i5 = R$string.auth_failed;
        int i6 = R$drawable.error;
        NotificationService.NotificationActionType notificationActionType4 = NotificationService.NotificationActionType.PERSISTENT;
        hashMap3.put("AUTH_FAILED", new EventInfo(i5, i6, 0, 3, 0, notificationActionType4));
        NotificationService.NotificationActionType notificationActionType5 = notificationActionType4;
        this.event_info.put("AON_REQUEST_CREDS", new EventInfo(R$string.aon_request_creds, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("AUTH_PENDING", new EventInfo(R$string.auth_pending, R$drawable.connecting, 0, 2, 0, notificationActionType5));
        this.event_info.put("AUTH_PENDING_FAILED", new EventInfo(R$string.auth_pending_failed, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("PEM_PASSWORD_FAIL", new EventInfo(R$string.pem_password_fail, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("CERT_VERIFY_FAIL", new EventInfo(R$string.cert_verify_fail, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_VERSION_MIN", new EventInfo(R$string.tls_version_min, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("DYNAMIC_CHALLENGE", new EventInfo(R$string.dynamic_challenge, R$drawable.error, 0, 2, 0, notificationActionType5));
        this.event_info.put("TUN_SETUP_FAILED", new EventInfo(R$string.tun_setup_failed, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TUN_IFACE_CREATE", new EventInfo(R$string.tun_iface_create, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TAP_NOT_SUPPORTED", new EventInfo(R$string.tap_not_supported, R$drawable.error, 0, 3, 0));
        HashMap hashMap4 = this.event_info;
        int i7 = R$string.profile_not_found;
        int i8 = R$drawable.error;
        NotificationService.NotificationActionType notificationActionType6 = NotificationService.NotificationActionType.PURE;
        hashMap4.put("PROFILE_NOT_FOUND", new EventInfo(i7, i8, 0, 3, 0, notificationActionType6));
        this.event_info.put("CONFIG_FILE_PARSE_ERROR", new EventInfo(R$string.config_file_parse_error, R$drawable.error, 0, 3, 0));
        this.event_info.put("NEED_CREDS_ERROR", new EventInfo(R$string.need_creds_error, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("CREDS_ERROR", new EventInfo(R$string.creds_error, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("CONNECTION_TIMEOUT", new EventInfo(R$string.connection_timeout, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("INACTIVE_TIMEOUT", new EventInfo(R$string.inactive_timeout, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("ACTION_REQUIRED", new EventInfo(R$string.action_required, R$drawable.error, 0, 3, 0));
        this.event_info.put("SSL_CA_MD_TOO_WEAK", new EventInfo(R$string.insecure_hash_algorithm, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_ALERT_PROTOCOL_VERSION", new EventInfo(R$string.tls_alert_protocol_version, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_ALERT_UNKNOWN_CA", new EventInfo(R$string.tls_alert_unknown_ca, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_ALERT_HANDSHAKE_FAILURE", new EventInfo(R$string.tls_alert_handshake_failure, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_ALERT_CERTIFICATE_REQUIRED", new EventInfo(R$string.tls_alert_certificate_required, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_ALERT_CERTIFICATE_EXPIRED", new EventInfo(R$string.tls_alert_certificate_expired, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_ALERT_CERTIFICATE_REVOKED", new EventInfo(R$string.tls_alert_certificate_revoked, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_ALERT_BAD_CERTIFICATE", new EventInfo(R$string.tls_alert_bad_certificate, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_ALERT_UNSUPPORTED_CERTIFICATE", new EventInfo(R$string.tls_alert_unsupported_certificate, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("TLS_ALERT_MISC", new EventInfo(R$string.tls_alert_misc, R$drawable.error, 0, 3, 0, notificationActionType5));
        this.event_info.put("INFO", new EventInfo(R$string.info_msg, R$drawable.error, 0, 0, 0));
        this.event_info.put("WARN", new EventInfo(R$string.warn_msg, R$drawable.error, 0, 0, 0, notificationActionType6));
        this.event_info.put("TRANSPORT_ERROR", new EventInfo(R$string.transport_error, R$drawable.error, 0, 3, 0, notificationActionType));
        NotificationService.NotificationActionType notificationActionType7 = notificationActionType4;
        this.event_info.put("PROXY_NEED_CREDS", new EventInfo(R$string.proxy_need_creds, R$drawable.error, 0, 3, 0, notificationActionType7));
        this.event_info.put("PROXY_ERROR", new EventInfo(R$string.proxy_error, R$drawable.error, 0, 3, 0, notificationActionType7));
        this.event_info.put("PROXY_CONTEXT_EXPIRED", new EventInfo(R$string.proxy_context_expired, R$drawable.error, 0, 3, 0, notificationActionType7));
        this.event_info.put("EPKI_ERROR", new EventInfo(R$string.epki_error, R$drawable.error, 0, 3, 0, notificationActionType7));
        this.event_info.put("EPKI_INVALID_ALIAS", new EventInfo(R$string.epki_invalid_alias, R$drawable.error, 0, 0, 0, notificationActionType7));
        NotificationService.NotificationActionType notificationActionType8 = notificationActionType3;
        this.event_info.put("PAUSE", new EventInfo(R$string.pause, R$drawable.pause, 0, 3, 0, notificationActionType8));
        this.event_info.put("RESUME", new EventInfo(R$string.resume, R$drawable.connecting, 0, 2, 0, notificationActionType8));
        this.event_info.put("CORE_THREAD_ACTIVE", new EventInfo(R$string.core_thread_active, R$drawable.connecting, 10, 1, 0, notificationActionType6));
        this.event_info.put("CORE_THREAD_ERROR", new EventInfo(R$string.core_thread_error, R$drawable.error, 0, 3, 0));
        this.event_info.put("CORE_THREAD_ABANDONED", new EventInfo(R$string.core_thread_abandoned, R$drawable.error, 0, 3, 0));
        this.event_info.put("CORE_THREAD_DONE", new EventInfo(R$string.core_thread_done, R$drawable.connected, 0, 1, 0));
        NotificationService.NotificationActionType notificationActionType9 = notificationActionType4;
        this.event_info.put("CLIENT_HALT", new EventInfo(R$string.client_halt, R$drawable.error, 0, 3, 0, notificationActionType9));
        this.event_info.put("CLIENT_RESTART", new EventInfo(R$string.client_restart, R$drawable.connecting, 0, 2, 0));
        this.event_info.put("PROFILE_DELETE_SUCCESS", new EventInfo(R$string.profile_delete_success, R$drawable.delete, 0, 2, 12));
        this.event_info.put("PROFILE_DELETE_FAILED", new EventInfo(R$string.profile_delete_failed, R$drawable.error, 0, 2, 4));
        this.event_info.put("ERR_PROFILE_GENERIC", new EventInfo(R$string.profile_parse_error, R$drawable.error, 0, 3, 4));
        this.event_info.put("PROFILE_CONFLICT", new EventInfo(R$string.profile_conflict, R$drawable.error, 0, 3, 4));
        this.event_info.put("PROFILE_WRITE_ERROR", new EventInfo(R$string.profile_write_error, R$drawable.error, 0, 3, 4));
        this.event_info.put("PROFILE_FILENAME_ERROR", new EventInfo(R$string.profile_filename_error, R$drawable.error, 0, 3, 4));
        this.event_info.put("COMPRESSION_ENABLED", new EventInfo(R$string.warn_msg, R$drawable.error, 0, 0, 0, notificationActionType9));
        this.event_info.put("PROFILE_RENAME_FAILED", new EventInfo(R$string.profile_rename_failed, R$drawable.error, 0, 2, 4));
        this.event_info.put("PROFILE_MERGE_EXCEPTION", new EventInfo(R$string.profile_merge_exception, R$drawable.error, 0, 2, 4));
        this.event_info.put("PROFILE_MERGE_OVPN_EXT_FAIL", new EventInfo(R$string.profile_merge_ovpn_ext_fail, R$drawable.error, 0, 2, 4));
        this.event_info.put("PROFILE_MERGE_REF_FAIL", new EventInfo(R$string.profile_merge_ref_fail, R$drawable.error, 0, 2, 4));
        this.event_info.put("PROFILE_MERGE_MULTIPLE_REF_FAIL", new EventInfo(R$string.profile_merge_multiple_ref_fail, R$drawable.error, 0, 2, 4));
        this.event_info.put("SESSION_EXPIRED", new EventInfo(R$string.session_expired, R$drawable.error, 0, 3, 0, notificationActionType9));
        this.event_info.put("NEED_CREDS", new EventInfo(R$string.need_creds, R$drawable.error, 0, 3, 0, notificationActionType9));
        this.event_info.put("NTLM_MISSING_CRYPTO", new EventInfo(R$string.ntlm_missing_crypto, R$drawable.error, 0, 3, 0, notificationActionType9));
        this.event_info.put("UNUSED_OPTIONS_ERROR", new EventInfo(R$string.unused_options_error, R$drawable.error, 0, 3, 0, notificationActionType9));
        this.event_info.put("COMPRESS_ERROR", new EventInfo(R$string.compress_error, R$drawable.error, 0, 3, 0, notificationActionType9));
        this.event_info.put("BASIC_AUTH_PROXY_ERROR", new EventInfo(R$string.basic_auth_proxy_error, R$drawable.error, 0, 3, 0, notificationActionType9));
    }

    /* access modifiers changed from: private */
    /* renamed from: processAction */
    public void lambda$handleRegularStart$1(String str, Intent intent) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2142060634:
                if (str.equals(ACTION_RESUME)) {
                    c = 0;
                    break;
                }
                break;
            case -2125565839:
                if (str.equals(ACTION_CONNECT)) {
                    c = 1;
                    break;
                }
                break;
            case -1706485829:
                if (str.equals(ACTION_CONNECT_RESUME)) {
                    c = 2;
                    break;
                }
                break;
            case -1238903442:
                if (str.equals(ACTION_DELETE_PROFILE)) {
                    c = 3;
                    break;
                }
                break;
            case -763799747:
                if (str.equals(ACTION_PAUSE)) {
                    c = 4;
                    break;
                }
                break;
            case -722934891:
                if (str.equals(ACTION_DISCONNECT)) {
                    c = 5;
                    break;
                }
                break;
            case 504841217:
                if (str.equals(ACTION_RENAME_PROFILE)) {
                    c = 6;
                    break;
                }
                break;
            case 507405490:
                if (str.equals(AON_CANCEL)) {
                    c = 7;
                    break;
                }
                break;
            case 798292259:
                if (str.equals("android.intent.action.BOOT_COMPLETED")) {
                    c = 8;
                    break;
                }
                break;
            case 940313238:
                if (str.equals(ACTION_IMPORT_PROFILE_VIA_PATH)) {
                    c = 9;
                    break;
                }
                break;
            case 1253714280:
                if (str.equals(ACTION_IMPORT_PROFILE)) {
                    c = 10;
                    break;
                }
                break;
            case 1751233207:
                if (str.equals(ACTION_EXTERNAL_DISCONNECT)) {
                    c = 11;
                    break;
                }
                break;
            case 1763223165:
                if (str.equals(ACTION_SUBMIT_PROXY_CREDS)) {
                    c = 12;
                    break;
                }
                break;
            case 2036812450:
                if (str.equals(DEBUG_ACTION_BOOT_COMPLETED)) {
                    c = 13;
                    break;
                }
                break;
            case 2146361423:
                if (str.equals(AON_PROVIDE_CREDS)) {
                    c = 14;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                manual_resume();
                return;
            case 1:
                connect_action("net.openvpn.openvpn", intent, false);
                return;
            case 2:
                connect_resume();
                return;
            case 3:
                delete_profile_action("net.openvpn.openvpn", intent);
                return;
            case 4:
                manual_pause();
                return;
            case 5:
                break;
            case 6:
                rename_profile_action("net.openvpn.openvpn", intent);
                return;
            case 7:
                this.externalConnectionManager.resetAttemptCounter();
                break;
            case 8:
            case IPCConstants.VAL_BYTEARRAY:
                handleReboot("net.openvpn.openvpn", intent);
                return;
            case 9:
                import_profile_via_path_action("net.openvpn.openvpn", intent);
                return;
            case 10:
                import_profile_action("net.openvpn.openvpn", intent);
                return;
            case IPCConstants.VAL_LIST:
                disconnect_action("net.openvpn.openvpn", intent);
                externalDisconnect();
                return;
            case IPCConstants.VAL_SPARSEARRAY:
                submit_proxy_creds_action("net.openvpn.openvpn", intent);
                return;
            case IPCConstants.VAL_STRINGARRAY:
                this.externalConnectionManager.setExternalConnectionEnabled(true);
                aonProvideCreds("net.openvpn.openvpn", intent);
                return;
            default:
                return;
        }
        disconnect_action("net.openvpn.openvpn", intent);
    }

    /* access modifiers changed from: private */
    public void publishConnectionStateTo(String str, ConnectionState connectionState) {
        Messenger messenger = connectionStateObservers.get(str);
        if (messenger != null) {
            Message obtain = Message.obtain((Handler) null, 3);
            Bundle bundle = new Bundle();
            bundle.putString("state", connectionState.name());
            obtain.setData(bundle);
            try {
                messenger.send(obtain);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to send state to observer: " + str, e);
                connectionStateObservers.remove(str);
            }
        }
    }

    private void publishConnectionStateToAll(ConnectionState connectionState) {
        this.lastKnownState = connectionState;
        for (String publishConnectionStateTo : connectionStateObservers.keySet()) {
            publishConnectionStateTo(publishConnectionStateTo, connectionState);
        }
    }

    private void reconfigure_notification() {
        stopForeground(true);
        NotificationCompat.Builder builder = this.mNotifyBuilder;
        if (builder != null) {
            builder.setContentText(resString(R$string.action_required)).setAutoCancel(true).setOngoing(true);
            Notification build = this.mNotifyBuilder.build();
            Log.d(TAG, "Reconfigured notification: " + build);
            this.notificationService.showNotification(NOTIFICATION_ID, this.mNotifyBuilder);
        }
    }

    public static void registerConnectionStateObserver(String str, Messenger messenger, ConnectionStateChangeListener connectionStateChangeListener) {
        Message obtain = Message.obtain((Handler) null, 1);
        Bundle bundle = new Bundle();
        bundle.putString("key", str);
        obtain.setData(bundle);
        obtain.replyTo = new Messenger(new Handler(new OpenVPNService$$ExternalSyntheticLambda2(connectionStateChangeListener)));
        try {
            messenger.send(obtain);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to register observer", e);
        }
    }

    private void register_connectivity_receiver() {
        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver(this);
        this.mConnectivityReceiver = connectivityReceiver;
        connectivityReceiver.register();
        final CaptivePortalService captivePortalService = new CaptivePortalService(this);
        this.mConnectivityReceiver.setNetworkListener(new ConnectivityReceiver.NetworkStateChangeListener() {
            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onNetworkStateChange$0(CaptivePortalService captivePortalService, Boolean bool) {
                if (bool == null || !bool.booleanValue()) {
                    captivePortalService.cancelNotification();
                    return;
                }
                captivePortalService.showNotification();
                OpenVPNService.this.message_queue.post_event(new EventMsg("CAPTIVE_PORTAL_DETECTED", captivePortalService.getUrlString()));
            }

            public void onNetworkStateChange(NetworkEventInfo networkEventInfo) {
                boolean pvbs = OpenVPNService.this.mConnectivityReceiver.getPVBS();
                if (networkEventInfo.isConnected) {
                    CaptivePortalService captivePortalService = captivePortalService;
                    captivePortalService.checkForCaptivePortal(new OpenVPNService$38$$ExternalSyntheticLambda0(this, captivePortalService));
                } else {
                    captivePortalService.cancelNotification();
                }
                String str = networkEventInfo.name;
                str.hashCode();
                char c = 65535;
                switch (str.hashCode()) {
                    case -1484026525:
                        if (str.equals("NETWORK_CHANGED")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 914481356:
                        if (str.equals("NETWORK_DISCONNECTED")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 2040146424:
                        if (str.equals("NETWORK_CONNECTED")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        if (OpenVPNService.this.active && !OpenVPNService.this.paused) {
                            OpenVPNService.this.network_reconnect(1);
                            return;
                        }
                        return;
                    case 1:
                        if (!OpenVPNService.this.paused && OpenVPNService.this.active) {
                            OpenVPNService.this.network_pause();
                            return;
                        }
                        return;
                    case 2:
                        if (OpenVPNService.this.paused && OpenVPNService.this.active) {
                            if (!pvbs || OpenVPNService.this.screen_on) {
                                OpenVPNService.this.network_resume();
                                OpenVPNService.this.paused_before_timeout = false;
                                return;
                            }
                            return;
                        }
                        return;
                    default:
                        Log.d(OpenVPNService.TAG, "NetworkStateChangeEvent: unsupported event.");
                        return;
                }
            }
        });
        ScreenReceiver screenReceiver = new ScreenReceiver(this);
        this.mScreenReceiver = screenReceiver;
        screenReceiver.register();
    }

    /* access modifiers changed from: private */
    public void removeConnectionStateObserver(String str) {
        connectionStateObservers.remove(str);
    }

    private boolean rename_profile_action(String str, Intent intent) {
        String str2;
        String stringExtra = intent.getStringExtra(str + ".PROFILE");
        String stringExtra2 = intent.getStringExtra(str + ".NEW_PROFILE");
        refresh_profile_list();
        ProfileData profileData = this.profile_list.get_profile_by_id(stringExtra);
        if (profileData == null) {
            return false;
        }
        if (!profileData.is_renameable() || stringExtra2 == null || stringExtra2.length() == 0) {
            str2 = "PROFILE_RENAME_FAILED: rename preliminary checks";
        } else {
            refresh_profile_list(Boolean.TRUE);
            if (!profileData.get_name().equals(stringExtra2)) {
                str2 = "PROFILE_RENAME_FAILED: post-rename profile get";
            } else {
                gen_event(0, "PROFILE_RENAME_SUCCESS", stringExtra, profileData.get_name());
                return true;
            }
        }
        Log.d(TAG, str2);
        gen_event(1, "PROFILE_RENAME_FAILED", stringExtra);
        return false;
    }

    private String resString(int i) {
        return getResources().getString(i);
    }

    /* access modifiers changed from: private */
    public Integer retrieveProfileMaxSize() {
        Log.d(TAG, "trying to retrieve profile max size");
        Integer valueOf = Integer.valueOf(Long.valueOf(max_profile_size()).intValue());
        Log.d(TAG, "retrieveProfileMaxSize: " + valueOf);
        broadcast_message(GET_PROFILE_MAX_SIZE_SUCCESS_IPC_EVENT, valueOf);
        return valueOf;
    }

    /* access modifiers changed from: private */
    public boolean sendAonRequestCreds(String str) {
        Stream stream = Arrays.asList(new String[]{"REVOKED", "CERT_REVOKE", "ADMIN_SUSPEND", "Connectivity is blocked"}).stream();
        Objects.requireNonNull(str);
        if (!stream.noneMatch(new OpenVPNService$$ExternalSyntheticLambda0(str)) || !this.externalConnectionManager.isFirstAttempt()) {
            return false;
        }
        this.externalConnectionManager.incrementAttemptCounter();
        EventMsg eventMsg = new EventMsg("AON_REQUEST_CREDS", "");
        eventMsg.res_id = R$string.aon_request_creds;
        eventMsg.icon_res_id = R$drawable.error;
        gen_event(1, "AON_REQUEST_CREDS", (String) null);
        set_last_event(eventMsg, true);
        update_notification_event(eventMsg);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean sendCRText(EventMsg eventMsg) {
        if (!this.externalConnectionManager.isExternalConnection()) {
            return false;
        }
        this.externalConnectionManager.incrementAttemptCounter();
        EventMsg eventMsg2 = new EventMsg("OPENVPN_CR_TEXT_EVENT");
        eventMsg2.info = eventMsg.info;
        gen_event(1, "OPENVPN_CR_TEXT_EVENT", eventMsg.info);
        set_last_event(eventMsg2, true);
        return true;
    }

    private void set_last_event(EventMsg eventMsg) {
        set_last_event(eventMsg, false);
    }

    /* access modifiers changed from: private */
    public void set_last_event(EventMsg eventMsg, boolean z) {
        EventMsg eventMsg2 = this.last_core_event;
        boolean z2 = false;
        boolean z3 = eventMsg2 != null && eventMsg2.info.startsWith("OPEN_URL");
        EventMsg eventMsg3 = this.last_core_event;
        if (eventMsg3 != null && (eventMsg3.flags & 1) == 1) {
            z2 = true;
        }
        EventInfo eventInfo = eventMsg3 == null ? null : (EventInfo) this.event_info.get(eventMsg3.name);
        EventMsg eventMsg4 = this.last_core_event;
        if (eventMsg4 == null || ((eventMsg == null || eventInfo == null || eventMsg.res_id != R$string.disconnected || !z2) && (eventMsg.res_id != R$string.pause || !z3))) {
            Log.d(TAG, String.format("Setting LAST_EVENT=%s", new Object[]{eventMsg.name}));
            this.last_core_event = eventMsg;
        } else {
            Log.d(TAG, String.format("Preserving LAST_EVENT=%s over EVENT=%s", new Object[]{eventMsg4.name, eventMsg.name}));
        }
        if (z && eventMsg != null) {
            Log.d(TAG, String.format("Broadcasting LAST_EVENT=%s", new Object[]{eventMsg.name}));
            broadcast_message("last_core_event", eventMsg);
        }
    }

    private void setup_ipc() {
        this.ipc_receiver = new IPCReceiver();
        this.ipc_sender = new IPCSender();
        this.ipc_receiver.register(new IPCAction("is_active", new IPCCallback<Void, Boolean>() {
            public Boolean call() {
                return Boolean.valueOf(this.is_active());
            }
        }));
        this.ipc_receiver.register(new IPCAction("registered", new IPCCallback<Void, InitialBindData>() {
            public InitialBindData call() {
                Object[] objArr = new Object[1];
                EventMsg eventMsg = this.last_core_event;
                objArr[0] = eventMsg != null ? eventMsg.name : "NONE";
                Log.d(OpenVPNService.TAG, String.format("Registering client, LAST_EVENT=%s", objArr));
                ProfileData profileData = this.get_current_profile();
                OpenVPNService openVPNService = this;
                return new InitialBindData(profileData, openVPNService.last_core_event, openVPNService.profile_list, Boolean.valueOf(this.is_active()));
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_current_profile", new IPCCallback<Void, ProfileData>() {
            public ProfileData call() {
                return this.get_current_profile();
            }
        }));
        this.ipc_receiver.register(new IPCAction("reset_dynamic_challenge", new IPCCallback<Void, Void>() {
            public Void call() {
                OpenVPNService.this.current_profile.reset_dynamic_challenge();
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("reload_profiles_list", new IPCCallback<Void, Void>() {
            public Void call() {
                OpenVPNService.this.refresh_profile_list(Boolean.TRUE);
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("migrate_epki_aliases", new IPCCallback<ProfileData, Void>() {
            public Void call() {
                ProfileData profileData = (ProfileData) this.value;
                this.migrate_epki_aliases_to_ids(profileData.get_name(), profileData.get_id());
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_profile_list", new IPCCallback<Void, ProfileList>() {
            public ProfileList call() {
                this.refresh_profile_list(Boolean.TRUE);
                return this.profile_list;
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_stats_full", new IPCCallback<Void, ClientAPI_Array>() {
            public ClientAPI_Array call() {
                return this.stat_values_full();
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_conn_stats", new IPCCallback<Void, ConnectionStats>() {
            public ConnectionStats call() {
                return this.get_connection_stats();
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_speed_stats", new IPCCallback<Void, SpeedStats>() {
            public SpeedStats call() {
                return new SpeedStats(this.get_connection_stats(), new BandwidthInfo(this.getDownloadSpeed(), this.getUploadSpeed()));
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_tb_per_sec", new IPCCallback<Void, TunnelBytesInfo>() {
            public TunnelBytesInfo call() {
                return new TunnelBytesInfo(this.get_tunnel_bytes_per_cpu_second());
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_last_prof_event", new IPCCallback<Void, EventMsg>() {
            public EventMsg call() {
                return this.get_last_event_prof_manage();
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_log_history", new IPCCallback<Void, LogDeque>() {
            public LogDeque call() {
                Log.d(OpenVPNService.TAG, "Send logs from service via ipc:" + OpenVPNService.this.logsManager.log_history().size());
                return OpenVPNService.this.logsManager.log_history();
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_proxy_list", new IPCCallback<Void, ProxyList>() {
            public ProxyList call() {
                return this.proxy_list;
            }
        }));
        this.ipc_receiver.register(new IPCAction("gen_proxy_context_expired", new IPCCallback<Void, Void>() {
            public Void call() {
                this.gen_proxy_context_expired_event();
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("gen_ui_reset", new IPCCallback<Boolean, Void>() {
            public Void call() {
                this.gen_ui_reset_event(((Boolean) this.value).booleanValue());
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_last_core_event", new IPCCallback<Void, EventMsg>() {
            public EventMsg call() {
                return this.last_core_event;
            }
        }));
        this.ipc_receiver.register(new IPCAction("unset_last_core_event", new IPCCallback<Void, Void>() {
            public Void call() {
                this.last_core_event = null;
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("start_save_speed_data", new IPCCallback<Void, Void>() {
            public Void call() {
                this.startSaveSpeedData();
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("stop_save_speed_data", new IPCCallback<Void, Void>() {
            public Void call() {
                this.stopSavingSpeedData();
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("clear_logs", new IPCCallback<Void, Void>() {
            public Void call() {
                OpenVPNService.this.logsManager.clear_log_history();
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("add_log_message", new IPCCallback<LogMsg, Void>() {
            public Void call() {
                Log.d(OpenVPNService.TAG, "add_log_message action");
                OpenVPNService.this.logsManager.onLog((LogMsg) this.value);
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("flush_logs", new IPCCallback<Void, Void>() {
            public Void call() {
                OpenVPNService.this.logsManager.flush();
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("load_proxy_list", new IPCCallback<String, Boolean>() {
            public Boolean call() {
                this.load_proxy_list((String) this.value);
                return Boolean.valueOf(this.proxy_list != null);
            }
        }));
        this.ipc_receiver.register(new IPCAction("add_proxy", new IPCCallback<String, Boolean>() {
            public Boolean call() {
                this.add_proxy((String) this.value);
                return Boolean.TRUE;
            }
        }));
        this.ipc_receiver.register(new IPCAction("remove_proxy", new IPCCallback<String, Boolean>() {
            public Boolean call() {
                this.remove_proxy((String) this.value);
                return Boolean.TRUE;
            }
        }));
        this.ipc_receiver.register(new IPCAction("edit_proxy", new IPCCallback<EditProxyInfo, Boolean>() {
            public Boolean call() {
                this.remove_proxy(((EditProxyInfo) this.value).oldName);
                this.add_proxy(((EditProxyInfo) this.value).json);
                return Boolean.TRUE;
            }
        }));
        this.ipc_receiver.register(new IPCAction("remove_profile", new IPCCallback<String, Boolean>() {
            public Boolean call() {
                return this.delete_profile((String) this.value);
            }
        }));
        this.ipc_receiver.register(new IPCAction("get_bandwidth", new IPCCallback<Void, BandwidthInfo>() {
            public BandwidthInfo call() {
                return new BandwidthInfo(this.getDownloadSpeed(), this.getUploadSpeed());
            }
        }));
        this.ipc_receiver.register(new IPCAction("import_profile_via_react", new IPCCallback<ImportViaReactInfo, ImportProfileResult>() {
            public ImportProfileResult call() {
                try {
                    ImportViaReactInfo importViaReactInfo = (ImportViaReactInfo) this.value;
                    return new ImportProfileResult(true, this.import_profile_via_react_bridge(importViaReactInfo.path, importViaReactInfo.name), (ImportException) null);
                } catch (ImportException e) {
                    return new ImportProfileResult(false, (ImportResult) null, e);
                }
            }
        }));
        this.ipc_receiver.register(new IPCAction("rename_profile", new IPCCallback<RenameProfileInfo, Boolean>() {
            public Boolean call() {
                OpenVPNService openVPNService = this;
                T t = this.value;
                return openVPNService.rename_profile(((RenameProfileInfo) t).profile_id, ((RenameProfileInfo) t).new_profile_name);
            }
        }));
        this.ipc_receiver.register(new IPCAction("import_profile_from_path", new IPCCallback<String, ImportProfileResult>() {
            public ImportProfileResult call() {
                try {
                    return new ImportProfileResult(true, this.get_profile_config_from_file((String) this.value), (ImportException) null);
                } catch (ImportException e) {
                    return new ImportProfileResult(false, (ImportResult) null, e);
                }
            }
        }));
        this.ipc_receiver.register(new IPCAction(GET_PROFILE_MAX_SIZE_IPC_EVENT, new IPCCallback<Void, Integer>() {
            public Integer call() {
                Log.d(OpenVPNService.TAG, "receive event: get_profile_limits");
                return this.retrieveProfileMaxSize();
            }
        }));
        this.ipc_receiver.register(new IPCAction("stop_notification", new IPCCallback<Void, Void>() {
            public Void call() {
                this.stop_notification();
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("send_app_control_channel_msg", new IPCCallback<ControlChannelMessageData, Void>() {
            public Void call() {
                OpenVPNService openVPNService = this;
                T t = this.value;
                openVPNService.send_app_control_channel_msg(((ControlChannelMessageData) t).protocol, ((ControlChannelMessageData) t).message);
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("post_challenge_response", new IPCCallback<String, Void>() {
            public Void call() {
                this.post_challenge_response((String) this.value);
                return null;
            }
        }));
        this.ipc_receiver.register(new IPCAction("set_logs_paused", new IPCCallback<Boolean, Void>() {
            public Void call() {
                this.isLogsPaused = ((Boolean) this.value).booleanValue();
                return null;
            }
        }));
        this.prefs_ipc = new PrefUtil.IPCProvider(this.ipc_receiver, this.prefs);
    }

    private boolean start_connection(ProfileData profileData, String str, String str2, ProxyContext proxyContext, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12) {
        String str13;
        int i;
        boolean z;
        ProfileData profileData2 = profileData;
        String str14 = str2;
        ProxyContext proxyContext2 = proxyContext;
        String str15 = str3;
        String str16 = str4;
        String str17 = str5;
        String str18 = str7;
        String str19 = str8;
        String str20 = str9;
        String str21 = str10;
        String str22 = str11;
        if (!checkVPNPermission() || this.active) {
            return false;
        }
        publishConnectionStateToAll(ConnectionState.CONNECTING);
        this.isLogsPaused = this.prefs.get_boolean("isLogsPaused", false);
        this.enable_notifications = this.prefs.get_boolean("enable_notifications", false);
        OpenVPNClientThread openVPNClientThread = new OpenVPNClientThread();
        ClientAPI_Config clientAPI_Config = new ClientAPI_Config();
        clientAPI_Config.setContent(str);
        clientAPI_Config.setInfo(true);
        clientAPI_Config.setHwAddrOverride(getFakeMacAddrFromSAAID(getApplicationContext()));
        clientAPI_Config.setSsoMethods("webauth,crtext");
        if (str15 != null) {
            clientAPI_Config.setServerOverride(str15);
        }
        if (str16 != null) {
            clientAPI_Config.setProtoOverride(str16);
        }
        if (str17 != null) {
            clientAPI_Config.setAllowUnusedAddrFamilies(str17);
        }
        if (str20 != null) {
            clientAPI_Config.setPrivateKeyPassword(str20);
        }
        if (Build.VERSION.SDK_INT >= 33) {
            clientAPI_Config.setEnableRouteEmulation(false);
        }
        clientAPI_Config.setTunPersist(this.prefs.get_boolean("tun_persist", false));
        clientAPI_Config.setGoogleDnsFallback(this.prefs.get_boolean("google_dns_fallback", false));
        clientAPI_Config.setAltProxy(this.prefs.get_boolean("alt_proxy", false));
        String str23 = this.prefs.get_string("security_level");
        if (str23 == null) {
            str23 = "legacy";
        }
        char c = 65535;
        switch (str23.hashCode()) {
            case -1294005119:
                if (str23.equals("preferred")) {
                    c = 0;
                    break;
                }
                break;
            case -1106578487:
                if (str23.equals("legacy")) {
                    c = 1;
                    break;
                }
                break;
            case 541341916:
                if (str23.equals("insecure")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                clientAPI_Config.setCompressionMode("no");
                clientAPI_Config.setTlsCertProfileOverride(str23);
                clientAPI_Config.setEnableLegacyAlgorithms(false);
                clientAPI_Config.setEnableNonPreferredDCAlgorithms(false);
                break;
            case 1:
                clientAPI_Config.setCompressionMode("asym");
                clientAPI_Config.setTlsCertProfileOverride(str23);
                clientAPI_Config.setEnableLegacyAlgorithms(false);
                z = true;
                break;
            case 2:
                clientAPI_Config.setCompressionMode("yes");
                clientAPI_Config.setTlsCertProfileOverride(str23);
                z = true;
                clientAPI_Config.setEnableLegacyAlgorithms(true);
                break;
            default:
                throw new IllegalArgumentException("Unknown security level \"" + str23 + "\" specified");
        }
        clientAPI_Config.setEnableNonPreferredDCAlgorithms(z);
        String str24 = this.prefs.get_string("tls_version_min_override");
        if (str24 == null) {
            str24 = "tls_1_2";
        }
        clientAPI_Config.setTlsVersionMinOverride(getTlsVersionMinOverride(str23, str24));
        clientAPI_Config.setAllowLocalLanAccess(this.prefs.get_boolean("allow_lan_access", false));
        if (profileData.get_epki()) {
            if (str22 == null) {
                str13 = new ProfileUtil(profileData2).get_epki_alias();
            } else if (str22.equals("DENY_CLIENT_CERT")) {
                gen_event(1, "EPKI_INVALID_ALIAS", "");
                return false;
            } else {
                new ProfileUtil(profileData2).persist_epki_alias(str22);
                str13 = str22;
            }
            if (str13 != null) {
                if (str13.equals("DISABLE_CLIENT_CERT")) {
                    clientAPI_Config.setDisableClientCert(true);
                } else {
                    clientAPI_Config.setExternalPkiAlias(str13);
                }
            }
        } else {
            str13 = str22;
        }
        if (proxyContext2 != null) {
            proxyContext2.client_api_config(clientAPI_Config);
        }
        set_autostart_profile_id(profileData.get_id());
        if (str6 != null) {
            try {
                i = Integer.parseInt(str6);
            } catch (NumberFormatException unused) {
                i = 0;
            }
            clientAPI_Config.setConnTimeout(i);
        }
        if (str14 != null) {
            clientAPI_Config.setGuiVersion(str14);
        }
        if (str21 != null && !str10.isEmpty()) {
            clientAPI_Config.setRetryOnAuthFailed(false);
        }
        this.cc_event_handler.setEnabled(CCAccessControl.processConfig(clientAPI_Config));
        ClientAPI_EvalConfig eval_config = openVPNClientThread.eval_config(clientAPI_Config);
        if (eval_config.getError()) {
            gen_event(1, "CONFIG_FILE_PARSE_ERROR", eval_config.getMessage());
            return false;
        }
        ClientAPI_ProvideCreds clientAPI_ProvideCreds = new ClientAPI_ProvideCreds();
        ProfileData profileData3 = this.current_profile;
        if (profileData3 != null && profileData3.is_dynamic_challenge()) {
            if (str21 != null) {
                clientAPI_ProvideCreds.setResponse(str21);
            }
            clientAPI_ProvideCreds.setDynamicChallengeCookie(this.current_profile.dynamic_challenge.cookie);
            profileData.reset_dynamic_challenge();
        } else if (eval_config.getAutologin() || str18 == null || str7.length() != 0) {
            if (str18 != null) {
                clientAPI_ProvideCreds.setUsername(str18);
            }
            if (str19 != null) {
                clientAPI_ProvideCreds.setPassword(str19);
            }
            if (str21 != null) {
                clientAPI_ProvideCreds.setResponse(str21);
            }
        } else {
            gen_event(1, "NEED_CREDS_ERROR", (String) null);
            return false;
        }
        ProfileData profileData4 = this.current_profile;
        if (profileData4 != null && profileData4.is_dynamic_challenge()) {
            clientAPI_ProvideCreds.setDynamicChallengeCookie(this.current_profile.dynamic_challenge.cookie);
            this.current_profile.reset_dynamic_challenge();
        }
        if (this.externalConnectionManager.isExternalConnection() && this.externalConnectionManager.isFirstAttempt()) {
            boolean z2 = !clientAPI_ProvideCreds.getDynamicChallengeCookie().isEmpty() && !clientAPI_ProvideCreds.getResponse().isEmpty();
            boolean autologin = eval_config.getAutologin();
            boolean z3 = clientAPI_ProvideCreds.getPassword().isEmpty() || (clientAPI_ProvideCreds.getUsername().isEmpty() && eval_config.getUserlockedUsername().isEmpty());
            if ((!autologin && !z2) && z3) {
                this.externalConnectionManager.incrementAttemptCounter();
                gen_event(1, "AON_REQUEST_CREDS", (String) null);
                start_notification();
                return false;
            }
        }
        openVPNClientThread.provide_creds(clientAPI_ProvideCreds);
        Object[] objArr = new Object[10];
        objArr[0] = profileData2.name;
        objArr[1] = str18;
        objArr[2] = proxyContext2 != null ? proxyContext.name() : "undef";
        objArr[3] = str15;
        objArr[4] = str16;
        objArr[5] = str17;
        objArr[6] = str6;
        objArr[7] = str21;
        objArr[8] = str13;
        objArr[9] = str12;
        Log.i(TAG, String.format("SERV: CONNECT prof=%s user=%s proxy=%s serv=%s proto=%s allowUnusedAddrFamilies=%s to=%s resp=%s epki_alias=%s comp=%s", objArr));
        this.paused = false;
        this.manual_pause = false;
        gen_event(0, "CORE_THREAD_ACTIVE", (String) null);
        start_notification();
        openVPNClientThread.connect(this);
        this.mThread = openVPNClientThread;
        this.thread_started = SystemClock.elapsedRealtime();
        set_active(true);
        this.externalConnectionManager.resetCreds();
        return true;
    }

    private void start_notification() {
        if (this.current_profile != null) {
            Class<OpenVPNService> cls = OpenVPNService.class;
            disconnectPendingService = this.notificationService.createPendingIntent(this.notificationService.createIntent(cls, ACTION_EXTERNAL_DISCONNECT).putExtra("net.openvpn.openvpn.STOP", true), 201326592);
            pausePendingService = this.notificationService.createPendingIntent(this.notificationService.createIntent(cls, ACTION_PAUSE), 67108864);
            resumePendingService = this.notificationService.createPendingIntent(this.notificationService.createIntent(cls, ACTION_RESUME), 67108864);
            Intent createIntent = this.notificationService.createIntent(MainActivity.class, "android.intent.action.VIEW");
            String str = this.profile_display_name;
            NotificationCompat.Builder addAction = this.notificationService.createNotificationBuilder(createIntent).setContentTitle(str).setContentText(getString(R$string.notification_initial_content)).setOngoing(true).addAction(0, "Pause", pausePendingService).addAction(0, "Disconnect", disconnectPendingService);
            this.mNotifyBuilder = addAction;
            this.notificationService.showNotification(NOTIFICATION_ID, addAction);
        }
    }

    public static String[] stat_names() {
        int stats_n = ClientAPI_OpenVPNClient.stats_n();
        String[] strArr = new String[stats_n];
        for (int i = 0; i < stats_n; i++) {
            strArr[i] = ClientAPI_OpenVPNClient.stats_name(i);
        }
        return strArr;
    }

    /* access modifiers changed from: private */
    public void stop_notification() {
        Log.i(TAG, "stop_notification");
        if (this.mNotifyBuilder != null) {
            this.mNotifyBuilder = null;
            this.notificationService.stopForeground(true);
        }
    }

    /* access modifiers changed from: private */
    public void stop_thread() {
        cancel_ping();
        if (this.active) {
            this.mThread.stop();
            Log.d(TAG, "SERV: stop_thread succeeded");
        }
    }

    private boolean submit_proxy_creds_action(String str, Intent intent) {
        ProxyContext proxyContext;
        ProfileData locate_profile = locate_profile(intent.getStringExtra(str + ".PROFILE"));
        if (!(locate_profile == null || (proxyContext = locate_profile.get_proxy_context(false)) == null)) {
            String stringExtra = intent.getStringExtra(str + ".PROXY_NAME");
            String stringExtra2 = intent.getStringExtra(str + ".PROXY_USERNAME");
            String stringExtra3 = intent.getStringExtra(str + ".PROXY_PASSWORD");
            Intent submit_proxy_creds = proxyContext.submit_proxy_creds(stringExtra, stringExtra2, stringExtra3, intent.getBooleanExtra(str + ".PROXY_REMEMBER_CREDS", false), this.proxy_list);
            if (submit_proxy_creds != null) {
                connect_action(str, submit_proxy_creds, true);
                return true;
            }
        }
        gen_event(1, "PROXY_CONTEXT_EXPIRED", (String) null);
        return false;
    }

    public static void unregisterConnectionStateObserver(String str, Messenger messenger) {
        Message obtain = Message.obtain((Handler) null, 2);
        Bundle bundle = new Bundle();
        bundle.putString("key", str);
        obtain.setData(bundle);
        try {
            messenger.send(obtain);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to unregister observer", e);
        }
    }

    private void unregister_connectivity_receiver() {
        ConnectivityReceiver connectivityReceiver = this.mConnectivityReceiver;
        if (connectivityReceiver != null) {
            connectivityReceiver.unregister();
        }
        ScreenReceiver screenReceiver = this.mScreenReceiver;
        if (screenReceiver != null) {
            screenReceiver.unregister();
        }
    }

    private void update_notification_actions(EventMsg eventMsg) {
        NotificationService.NotificationActionType notificationActionType;
        EventInfo eventInfo = (EventInfo) this.event_info.get(eventMsg.name);
        if (eventInfo != null && (notificationActionType = eventInfo.notif_action_type) != NotificationService.NotificationActionType.NONE) {
            int i = AnonymousClass43.$SwitchMap$net$openvpn$openvpn$NotificationService$NotificationActionType[notificationActionType.ordinal()];
            if (i == 1) {
                this.notificationService.updateNotificationActions(this.mNotifyBuilder, new NotificationService.Action(getString(R$string.disconnect), disconnectPendingService));
            } else if (i == 2) {
                this.notificationService.updateNotificationActions(this.mNotifyBuilder, new NotificationService.Action(getString(R$string.disconnect), disconnectPendingService));
            } else if (i == 3) {
                this.notificationService.updateNotificationActions(this.mNotifyBuilder, new NotificationService.Action(getString(this.manual_pause ? R$string.notification_resume : R$string.notification_pause), this.manual_pause ? resumePendingService : pausePendingService), new NotificationService.Action(getString(R$string.disconnect), disconnectPendingService));
            }
        }
    }

    private void update_notification_content(EventMsg eventMsg) {
        if (eventMsg.res_id == -1) {
            Log.e(TAG, "Unknown event: " + eventMsg);
            return;
        }
        NotificationIconMapping.IconInfo iconInfo = NotificationIconMapping.getIconInfo(eventMsg.icon_res_id);
        if (iconInfo != null) {
            this.notificationService.updateNotificationContent(this.mNotifyBuilder, iconInfo.targetIcon, resString((!iconInfo.useManualPauseText || !this.manual_pause) ? eventMsg.res_id : R$string.manual_pause));
        } else {
            this.notificationService.updateNotificationContent(this.mNotifyBuilder, R$drawable.ic_notification, resString(eventMsg.res_id));
        }
    }

    /* access modifiers changed from: private */
    public void update_notification_event(EventMsg eventMsg) {
        String str;
        if (eventMsg.res_id != R$string.pause || !this.auth_pending) {
            Log.d(TAG, "Notification event: " + eventMsg + ", Builder: " + this.mNotifyBuilder);
            if (this.mNotifyBuilder == null) {
                str = "Notification builder is null";
            } else {
                EventInfo eventInfo = (EventInfo) this.event_info.get(eventMsg.name);
                if (eventInfo == null) {
                    str = "No event info for: " + eventMsg.name;
                } else {
                    update_notification_actions(eventMsg);
                    if (eventMsg.priority >= 1) {
                        update_notification_content(eventMsg);
                        if (eventInfo.notif_action_type == NotificationService.NotificationActionType.PERSISTENT) {
                            reconfigure_notification();
                            return;
                        } else {
                            startForeground(NOTIFICATION_ID, this.mNotifyBuilder.build());
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
            Log.e(TAG, str);
        }
    }

    private void update_notification_speed_data() {
        if (this.mNotifyBuilder != null && this.mThread != null && this.active && this.currently_connected && this.screen_on) {
            String string = getString(R$string.connected);
            this.mNotifyBuilder.setContentText(string);
            Log.d(TAG, "Updating notification with speed data: " + string);
            this.notificationService.showNotification(NOTIFICATION_ID, this.mNotifyBuilder);
        }
    }

    public void acc_event(ClientAPI_AppCustomControlMessageEvent clientAPI_AppCustomControlMessageEvent) {
        this.cc_event_handler.handle(clientAPI_AppCustomControlMessageEvent);
    }

    public void add_proxy(String str) {
        try {
            this.proxy_list.put(ProxyItem.FromJSON((JSONObject) new JSONTokener(str).nextValue()));
        } catch (JSONException unused) {
        }
    }

    public void broadcast_message(String str) {
        broadcast_message(str, (Object) null, (IPCCallback) null);
    }

    public <T> void broadcast_message(String str, T t) {
        broadcast_message(str, t, (IPCCallback) null);
    }

    public <T> void broadcast_message(String str, T t, IPCCallback iPCCallback) {
        Set<Messenger> set = this.ipc_receiver.mClients;
        Iterator<Messenger> it = set.iterator();
        if (it.hasNext() || iPCCallback == null) {
            while (it.hasNext()) {
                Messenger next = it.next();
                try {
                    this.ipc_sender.send(next, str, t, iPCCallback);
                } catch (RemoteException unused) {
                    Log.i(TAG, "Dead client, dropping messenger");
                    set.remove(next);
                    if (iPCCallback != null) {
                        synchronized (iPCCallback) {
                            iPCCallback.setResult(null);
                            try {
                                iPCCallback.call();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                            iPCCallback.notifyAll();
                        }
                    } else {
                        return;
                    }
                }
            }
            return;
        }
        synchronized (iPCCallback) {
            iPCCallback.setValue(null);
            try {
                iPCCallback.call();
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
            iPCCallback.notifyAll();
        }
    }

    public void broadcast_message(String str, IPCCallback iPCCallback) {
        broadcast_message(str, (Object) null, iPCCallback);
    }

    public List<EnvVar> construct_env_vars() {
        String valueOf = String.valueOf(10568);
        String str = Build.VERSION.RELEASE;
        String string = Settings.Secure.getString(getApplicationContext().getContentResolver(), "android_id");
        ArrayList arrayList = new ArrayList();
        arrayList.add(new EnvVar("UV_ASCLI_VER", BuildConfig.REANIMATED_VERSION_JAVA + "-" + valueOf));
        arrayList.add(new EnvVar("UV_PLAT_REL", str));
        arrayList.add(new EnvVar("UV_UUID", string));
        return arrayList;
    }

    public Boolean delete_profile(String str) {
        refresh_profile_list();
        ProfileData profileData = this.profile_list.get_profile_by_id(str);
        ProfileUtil profileUtil = new ProfileUtil(profileData);
        if (profileData == null) {
            return Boolean.FALSE;
        }
        if (!profileData.is_deleteable()) {
            return Boolean.FALSE;
        }
        if (this.active && profileData == this.current_profile) {
            stop_thread();
        }
        if (!deleteFile(profileData.get_filename())) {
            return Boolean.FALSE;
        }
        this.pwds.remove("auth", str);
        this.pwds.remove("pk", str);
        if (profileData.have_external_pki_alias()) {
            profileUtil.invalidate_epki_alias(profileData.get_epki_alias());
        }
        Boolean bool = Boolean.TRUE;
        refresh_profile_list(bool);
        return bool;
    }

    public void done(ClientAPI_Status clientAPI_Status) {
        boolean error = clientAPI_Status.getError();
        String message = clientAPI_Status.getMessage();
        Log.d(TAG, String.format("EXIT: connect() exited, err=%b, msg='%s'", new Object[]{Boolean.valueOf(error), message}));
        log_stats();
        if (error) {
            if (message == null || !message.equals("CORE_THREAD_ABANDONED")) {
                String status = clientAPI_Status.getStatus();
                if (status.length() == 0) {
                    status = "CORE_THREAD_ERROR";
                }
                gen_event(1, status, message);
            } else {
                gen_event(1, "CORE_THREAD_ABANDONED", (String) null);
            }
        }
        this.message_queue.post_event("CORE_THREAD_DONE");
        set_active(false);
        cancel_ping();
        this.initial_connect = true;
        this.logsManager.flush();
    }

    public void event(ClientAPI_Event clientAPI_Event) {
        Request.Ping ping;
        EventMsg eventMsg = new EventMsg();
        if (clientAPI_Event.getError()) {
            eventMsg.flags |= 1;
        }
        eventMsg.name = clientAPI_Event.getName();
        eventMsg.info = clientAPI_Event.getInfo();
        EventInfo eventInfo = (EventInfo) this.event_info.get(eventMsg.name);
        if (eventInfo != null) {
            eventMsg.progress = eventInfo.progress;
            eventMsg.priority = eventInfo.priority;
            int i = eventInfo.res_id;
            eventMsg.res_id = i;
            eventMsg.icon_res_id = eventInfo.icon_res_id;
            eventMsg.flags |= eventInfo.flags;
            if (i == R$string.connected && this.mThread != null) {
                eventMsg.conn_info = new ConnectionInfo(this.mThread.connection_info());
            }
        } else {
            eventMsg.res_id = R$string.unknown;
        }
        Log.d(TAG, String.format("Received EVENT=%s", new Object[]{eventMsg.name}));
        if (eventMsg.res_id == R$string.transport_error && !isOnline()) {
            Log.i(TAG, "Transport error ignored while core thread is paused.");
            return;
        }
        if (eventMsg.res_id == R$string.connection_timeout && (ping = this.pinger) != null) {
            eventMsg.res_id = R$string.auth_pending_failed;
            eventMsg.name = "AUTH_PENDING_FAILED";
            eventMsg.info = ping.getHostname();
        }
        if (eventMsg.res_id == R$string.connection_timeout) {
            eventMsg.name = "CONNECTION_TIMEOUT";
            eventMsg.info = get_raw_stats();
        }
        if (eventInfo != null) {
            int i2 = eventInfo.res_id;
            if (i2 == R$string.connected) {
                handleConnectedState();
            } else if (i2 == R$string.action_required) {
                this.active = false;
            }
        }
        this.message_queue.post_event(eventMsg);
        int i3 = eventMsg.res_id;
        if ((i3 != R$string.pause || !this.auth_pending) && i3 != R$string.info_msg) {
            set_last_event(eventMsg, true);
        }
    }

    public void external_pki_cert_request(ClientAPI_ExternalPKICertRequest clientAPI_ExternalPKICertRequest) {
        String str;
        try {
            X509Certificate[] certificateChain = KeyChain.getCertificateChain(this, clientAPI_ExternalPKICertRequest.getAlias());
            if (certificateChain == null) {
                clientAPI_ExternalPKICertRequest.setError(true);
                clientAPI_ExternalPKICertRequest.setInvalidAlias(true);
            } else if (certificateChain.length >= 1) {
                clientAPI_ExternalPKICertRequest.setCert(cert_format_pem(certificateChain[0]));
                if (certificateChain.length >= 2) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < certificateChain.length; i++) {
                        sb.append(cert_format_pem(certificateChain[i]));
                    }
                    clientAPI_ExternalPKICertRequest.setSupportingChain(sb.toString());
                }
            } else {
                clientAPI_ExternalPKICertRequest.setError(true);
                clientAPI_ExternalPKICertRequest.setInvalidAlias(true);
                clientAPI_ExternalPKICertRequest.setErrorText(resString(R$string.epki_missing_cert));
            }
        } catch (KeyChainException unused) {
            clientAPI_ExternalPKICertRequest.setError(true);
            clientAPI_ExternalPKICertRequest.setInvalidAlias(true);
            str = "EPKI error: identity not found";
            clientAPI_ExternalPKICertRequest.setErrorText(str);
        } catch (Exception e) {
            Log.e(TAG, "Unknown EPKI error in external_pki_cert_request", e);
            clientAPI_ExternalPKICertRequest.setError(true);
            clientAPI_ExternalPKICertRequest.setInvalidAlias(true);
            str = e.toString();
            clientAPI_ExternalPKICertRequest.setErrorText(str);
        }
    }

    public void external_pki_sign_request(ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest) {
        String str;
        StringBuilder sb;
        try {
            MessageQueue messageQueue = this.message_queue;
            Object[] objArr = new Object[3];
            objArr[0] = clientAPI_ExternalPKISignRequest.getAlgorithm();
            objArr[1] = clientAPI_ExternalPKISignRequest.getHashalg();
            objArr[2] = clientAPI_ExternalPKISignRequest.getSaltlen().isEmpty() ? "empty" : clientAPI_ExternalPKISignRequest.getSaltlen();
            messageQueue.post_log(String.format("EPKI sign request: %s %s saltlen=%s", objArr));
            PrivateKey privateKey = KeyChain.getPrivateKey(this, clientAPI_ExternalPKISignRequest.getAlias());
            if (privateKey == null) {
                clientAPI_ExternalPKISignRequest.setError(true);
                clientAPI_ExternalPKISignRequest.setInvalidAlias(true);
                return;
            }
            clientAPI_ExternalPKISignRequest.setSig(Signing.processSignRequestWithKey(clientAPI_ExternalPKISignRequest, privateKey));
            this.message_queue.post_log("EPKI sign request: completed");
        } catch (Signing.UnsupportedSignRequestAlgorithm e) {
            Log.e(TAG, "EPKI error in external_pki_sign_request (unsupported request alg)", e);
            clientAPI_ExternalPKISignRequest.setError(true);
            sb = new StringBuilder();
            sb.append("EPKI sign request failed: Unsupported signature algorithm requested. ");
            str = e.getMessage();
            sb.append(str);
            clientAPI_ExternalPKISignRequest.setErrorText(sb.toString());
        } catch (Exception e2) {
            Log.e(TAG, "EPKI error in external_pki_sign_request", e2);
            clientAPI_ExternalPKISignRequest.setError(true);
            sb = new StringBuilder();
            sb.append("EPKI sign request failed: ");
            str = e2.toString();
            sb.append(str);
            clientAPI_ExternalPKISignRequest.setErrorText(sb.toString());
        }
    }

    public void gen_proxy_context_expired_event() {
        gen_event(0, "PROXY_CONTEXT_EXPIRED", (String) null);
    }

    public void gen_ui_reset_event(boolean z) {
        gen_event(z ? 16 : 0, "UI_RESET", (String) null, (String) null);
    }

    public int[] getDownloadSpeed() {
        TrafficSpeedTracker trafficSpeedTracker2 = this.trafficSpeedTracker;
        return trafficSpeedTracker2 != null ? trafficSpeedTracker2.getDownloadSpeed() : new int[0];
    }

    public int[] getUploadSpeed() {
        TrafficSpeedTracker trafficSpeedTracker2 = this.trafficSpeedTracker;
        return trafficSpeedTracker2 != null ? trafficSpeedTracker2.getUploadSpeed() : new int[0];
    }

    public ConnectionStats get_connection_stats() {
        ConnectionStats connectionStats = new ConnectionStats();
        OpenVPNClientThread openVPNClientThread = this.mThread;
        if (openVPNClientThread == null || !this.active) {
            connectionStats.duration = 0;
            connectionStats.bytes_in = 0;
            connectionStats.bytes_out = 0;
        } else {
            ClientAPI_TransportStats transport_stats = openVPNClientThread.transport_stats();
            connectionStats.last_packet_received = -1;
            int elapsedRealtime = ((int) (SystemClock.elapsedRealtime() - this.thread_started)) / 1000;
            connectionStats.duration = elapsedRealtime;
            if (elapsedRealtime < 0) {
                connectionStats.duration = 0;
            }
            connectionStats.bytes_in = transport_stats.getBytesIn();
            connectionStats.bytes_out = transport_stats.getBytesOut();
            int lastPacketReceived = transport_stats.getLastPacketReceived();
            if (lastPacketReceived >= 0) {
                connectionStats.last_packet_received = lastPacketReceived >> 10;
            }
        }
        update_notification_speed_data();
        return connectionStats;
    }

    public ProfileData get_current_profile() {
        ProfileData profileData = this.current_profile;
        if (profileData != null) {
            return profileData;
        }
        ProfileList profileList = get_profile_list();
        if (profileList.size() >= 1) {
            return (ProfileData) profileList.get(0);
        }
        return null;
    }

    public EventMsg get_last_event_prof_manage() {
        EventMsg eventMsg = this.last_event_prof_manage;
        if (eventMsg == null || eventMsg.is_expired()) {
            return null;
        }
        return this.last_event_prof_manage;
    }

    public ImportResult get_profile_config_from_file(String str) {
        ClientAPI_MergeConfig merge_config = OpenVPNClientHelperWrapper.merge_config(str, true);
        String str2 = "PROFILE_" + merge_config.getStatus();
        if (str2.equals("PROFILE_MERGE_SUCCESS")) {
            String valueOf = String.valueOf(System.currentTimeMillis());
            String profileContent = merge_config.getProfileContent();
            ClientAPI_Config clientAPI_Config = new ClientAPI_Config();
            clientAPI_Config.setContent(profileContent);
            ClientAPI_EvalConfig eval_config = OpenVPNClientHelperWrapper.eval_config(clientAPI_Config);
            Config config = new Config(clientAPI_Config);
            EvalConfig evalConfig = new EvalConfig(eval_config);
            MergeConfig mergeConfig = new MergeConfig(merge_config);
            if (!eval_config.getError()) {
                return new ImportResult(getApplicationContext(), valueOf, evalConfig, mergeConfig, config, (String) null, (String) null);
            }
            throw new ImportException("ERR_PROFILE_GENERIC", eval_config.getMessage());
        }
        throw new ImportException(str2, merge_config.getErrorText());
    }

    public ProfileList get_profile_list() {
        refresh_profile_list();
        return this.profile_list;
    }

    public long get_tunnel_bytes_per_cpu_second() {
        CPUUsage cPUUsage = this.cpu_usage;
        if (cPUUsage == null) {
            return 0;
        }
        double usage = cPUUsage.usage();
        if (usage <= 0.0d) {
            return 0;
        }
        ClientAPI_InterfaceStats tun_stats = this.mThread.tun_stats();
        return (long) (((double) (tun_stats.getBytesIn() + tun_stats.getBytesOut())) / usage);
    }

    public ImportResult import_profile_via_react_bridge(String str, String str2) {
        ClientAPI_MergeConfig merge_config = OpenVPNClientHelperWrapper.merge_config(str, true);
        String str3 = "PROFILE_" + merge_config.getStatus();
        if (str3.equals("PROFILE_MERGE_SUCCESS")) {
            return import_profile_from_config(merge_config, str2);
        }
        throw new ImportException(str3, merge_config.getErrorText());
    }

    public boolean isOnline() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean is_active() {
        return this.active;
    }

    public void load_proxy_list(String str) {
        if (this.proxy_list == null) {
            crypto_self_test();
            populate_event_info_map();
            register_connectivity_receiver();
            this.pwds = new PasswordUtil(PreferenceManager.getDefaultSharedPreferences(this));
            ProxyList proxyList = new ProxyList(resString(R$string.proxy_none));
            this.proxy_list = proxyList;
            proxyList.persistor.set_backing_file(this, "proxies.json");
            this.proxy_list.persistor.load();
        }
        this.proxy_list.persistor.load_from_string(str);
    }

    public void log(ClientAPI_LogInfo clientAPI_LogInfo) {
        LogMsg logMsg = new LogMsg();
        logMsg.line = clientAPI_LogInfo.getText();
        this.message_queue.post_log(logMsg);
    }

    public void migrate_epki_aliases_to_ids(String str, String str2) {
        String str3 = this.prefs.get_string_by_profile(str, "epki_alias");
        this.prefs.delete_key_by_profile(str, "epki_alias");
        this.prefs.set_string_by_profile(str2, "epki_alias", str3);
    }

    public void network_pause() {
        if (this.active) {
            this.paused = true;
            cancel_ping();
            this.mThread.pause("");
        }
    }

    public void network_reconnect(int i) {
        if (this.active) {
            this.mThread.reconnect(i);
        }
    }

    public void network_resume() {
        if (this.active && !this.manual_pause) {
            this.paused = false;
            this.mThread.resume();
        }
    }

    public IBinder onBind(Intent intent) {
        if (intent == null) {
            return this.serviceMessenger.getBinder();
        }
        String action = intent.getAction();
        if (action == null) {
            return this.serviceMessenger.getBinder();
        }
        if (action.equals(ACTION_BIND)) {
            Log.d(TAG, String.format("SERV: onBind intent=%s", new Object[]{intent}));
            return this.ipc_receiver.getBinder();
        }
        Log.d(TAG, String.format("SERV: onBind SUPER intent=%s", new Object[]{intent}));
        return super.onBind(intent);
    }

    public void onCreate() {
        super.onCreate();
        this.prefs = new PrefUtil(getApplicationContext());
        NotificationService instance = NotificationService.getInstance(getApplicationContext());
        this.notificationService = instance;
        instance.setForegroundService(this);
        this.logsManager = LogsManager.getInstance(getApplicationContext(), Boolean.TRUE);
        setup_ipc();
        Log.d(TAG, "IPC is configured");
        if (!MainApplication.libraryLoadError) {
            Log.d(TAG, "SERV: Service onCreate called");
            crypto_self_test();
            MessageQueue messageQueue = new MessageQueue(new LogHandler(), new EventHandler());
            this.message_queue = messageQueue;
            this.cc_event_handler = new CCEventHandler(messageQueue, this);
            populate_event_info_map();
            register_connectivity_receiver();
            this.pwds = new PasswordUtil(PreferenceManager.getDefaultSharedPreferences(this));
            ProxyList proxyList = new ProxyList(resString(R$string.proxy_none));
            this.proxy_list = proxyList;
            proxyList.persistor.set_backing_file(this, "proxies.json");
            this.proxy_list.persistor.load();
            this.externalConnectionManager = new ExternalConnectionManager();
        }
    }

    public void onDestroy() {
        EventMsg eventMsg;
        Log.d(TAG, "SERV: onDestroy called");
        this.shutdown_pending = true;
        stop_thread();
        try {
            unregister_connectivity_receiver();
            eventMsg = new EventMsg("DISCONNECTED", "Service destroyed");
        } catch (Exception e) {
            Log.e(TAG, "OpenVPNService onDestroy error: " + e.getMessage(), e);
            eventMsg = new EventMsg("DISCONNECTED", "Service destroyed");
        } catch (Throwable th) {
            EventMsg eventMsg2 = new EventMsg("DISCONNECTED", "Service destroyed");
            set_last_event(eventMsg2, true);
            this.message_queue.post_event(eventMsg2);
            super.onDestroy();
            throw th;
        }
        set_last_event(eventMsg, true);
        this.message_queue.post_event(eventMsg);
        super.onDestroy();
    }

    public void onRevoke() {
        Log.d(TAG, "SERV: onRevoke called");
        EventMsg eventMsg = new EventMsg("CANCELLED", "");
        this.message_queue.post_event(eventMsg);
        set_last_event(eventMsg, true);
        stop_thread();
        publishConnectionStateToAll(ConnectionState.DISCONNECTED);
        stop_notification();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        String action = intent != null ? intent.getAction() : "";
        if ((intent != null && "android.net.VpnService".equals(action)) || "android.intent.action.BOOT_COMPLETED".equals(action) || DEBUG_ACTION_BOOT_COMPLETED.equals(action) || QUICK_TILE_CONNECT.equals(action)) {
            handleExternalStart(intent);
            return 1;
        } else if (intent == null) {
            return 1;
        } else {
            handleRegularStart(action, intent);
            return 1;
        }
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, String.format("SERV: onUnbind called intent=%s", new Object[]{intent.toString()}));
        return super.onUnbind(intent);
    }

    public boolean pause_on_connection_timeout() {
        boolean z = true;
        if ((this.mScreenReceiver == null || this.screen_on) && this.initial_connect) {
            z = false;
        }
        this.paused_before_timeout = z;
        Log.d(TAG, String.format("pause_on_connection_timeout %b", new Object[]{Boolean.valueOf(z)}));
        return z;
    }

    public void post_challenge_response(String str) {
        this.mThread.post_cc_msg(str);
    }

    public void refresh_profile_list() {
        ProfileList profileList = new ProfileList();
        ProfileListUtil profileListUtil = new ProfileListUtil(profileList);
        profileListUtil.load_profiles("bundled");
        profileListUtil.load_profiles("imported");
        profileListUtil.sort();
        Log.d(TAG, "SERV: refresh profiles:");
        Iterator it = profileList.iterator();
        while (it.hasNext()) {
            Log.d(TAG, String.format("SERV: %s", new Object[]{((ProfileData) it.next()).name}));
        }
        this.profile_list = profileList;
    }

    public void refresh_profile_list(Boolean bool) {
        refresh_profile_list();
        if (bool.booleanValue()) {
            broadcast_message("profile_list", this.profile_list);
        }
    }

    public void remove_proxy(String str) {
        this.proxy_list.remove(str);
    }

    public Boolean rename_profile(String str, String str2) {
        refresh_profile_list();
        ProfileData profileData = this.profile_list.get_profile_by_id(str);
        if (profileData == null) {
            return Boolean.FALSE;
        }
        if (!profileData.is_renameable() || str2 == null || str2.length() == 0) {
            return Boolean.FALSE;
        }
        File filesDir = getFilesDir();
        if (!FileUtil.renameFile(String.format("%s/%s", new Object[]{filesDir.getPath(), profileData.orig_filename}), String.format("%s/%s", new Object[]{filesDir.getPath(), ProfileFN.create_file_name(str2, str)}))) {
            return Boolean.FALSE;
        }
        Boolean bool = Boolean.TRUE;
        refresh_profile_list(bool);
        return !this.profile_list.get_profile_by_id(str).get_name().equals(str2) ? Boolean.FALSE : bool;
    }

    public void send_app_control_channel_msg(String str, String str2) {
        this.mThread.send_app_control_channel_msg(str, str2);
    }

    public void set_active(boolean z) {
        this.active = z;
        broadcast_message("is_active", Boolean.valueOf(z));
    }

    public void set_autostart_profile_id(String str) {
        if (str != null) {
            this.prefs.set_string("autostart_profile_id", str);
        } else {
            this.prefs.delete_key("autostart_profile_id");
        }
    }

    public void set_current_profile(ProfileData profileData) {
        ProfileData profileData2 = this.current_profile;
        if (profileData2 != null && profileData2.equals(profileData)) {
            profileData.dynamic_challenge = this.current_profile.dynamic_challenge;
        }
        this.current_profile = profileData;
        broadcast_message("current_profile", profileData);
    }

    public boolean socket_protect(int i) {
        return protect(i);
    }

    public void startSaveSpeedData() {
        if (this.trafficSpeedTracker == null) {
            TrafficSpeedTracker trafficSpeedTracker2 = new TrafficSpeedTracker(getMainLooper(), IPCUtils.StringChunker.CHUNK_LIMIT, this);
            this.trafficSpeedTracker = trafficSpeedTracker2;
            trafficSpeedTracker2.schedule();
        }
    }

    public ClientAPI_Array stat_values_full() {
        if (this.mThread != null) {
            return new ClientAPI_Array(this.mThread.stats_bundle());
        }
        return null;
    }

    public void stopSavingSpeedData() {
        TrafficSpeedTracker trafficSpeedTracker2 = this.trafficSpeedTracker;
        if (trafficSpeedTracker2 != null) {
            trafficSpeedTracker2.revoke();
            this.trafficSpeedTracker = null;
        }
    }

    public OpenVPNClientThread.TunBuilder tun_builder_new() {
        return new TunBuilder();
    }
}
