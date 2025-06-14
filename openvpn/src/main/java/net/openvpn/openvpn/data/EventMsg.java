package net.openvpn.openvpn.data;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import java.util.Locale;

import fuck.system.vpn.R;

public class EventMsg implements Parcelable
{
    public static final int DEFAULT_ICON_RES_ID = -1;
    public static final int DEFAULT_RES_ID = -1;

    public static final int F_ERROR = 1;
    public static final int F_FROM_JAVA = 2;
    public static final int F_PROF_MANAGE = 4;
    public static final int F_UI_RESET = 8;
    public static final int F_EXCLUDE_SELF = 16;
    public static final int F_PROF_IMPORT = 32;

    public static final Parcelable.Creator<EventMsg> CREATOR = new Parcelable.Creator<>() {
        @Override
        public EventMsg createFromParcel(Parcel in) {
            return new EventMsg(in);
        }

        @Override
        public EventMsg[] newArray(int size) {
            return new EventMsg[size];
        }
    };

    public ConnectionInfo conn_info;
    public long expires;
    public int flags;
    public int icon_res_id;
    public String info = "";
    public boolean is_aon;
    public String name = "";
    public int priority;
    public String profile_override;
    public int progress;
    public int res_id;
    public int sender;
    public Transition transition;

    public enum Transition {
        NO_CHANGE,
        TO_CONNECTED,
        TO_DISCONNECTED
    }

    public EventMsg() {
        flags = 0;
        res_id = DEFAULT_RES_ID;
        icon_res_id = DEFAULT_ICON_RES_ID;
        progress = 0;
        priority = 1;
        expires = 0;
        sender = 0;
        transition = Transition.NO_CHANGE;
        is_aon = false;
    }

    protected EventMsg(Parcel in) {
        this();
        flags = in.readInt();
        res_id = in.readInt();
        icon_res_id = in.readInt();
        progress = in.readInt();
        priority = in.readInt();
        expires = in.readLong();
        name = in.readString();
        info = in.readString();
        profile_override = in.readString();
        sender = in.readInt();
        int transitionOrdinal = in.readInt();
        transition = (transitionOrdinal == -1) ? Transition.NO_CHANGE : Transition.values()[transitionOrdinal];
        conn_info = in.readParcelable(ConnectionInfo.class.getClassLoader());

        if (Build.VERSION.SDK_INT >= 29) {
            is_aon = in.readBoolean();
        }
    }

    public EventMsg(String name) {
        this(name, "");
    }

    public EventMsg(String name, String info) {
        this();
        this.name = name;
        this.info = info;
    }

    public static EventMsg disconnected() {
        EventMsg msg = new EventMsg("DISCONNECTED", "");
        msg.flags = F_FROM_JAVA;
        msg.res_id = R.string.disconnected;
        msg.icon_res_id = R.drawable.ic_disconnect;
        return msg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int parcelFlags) {
        out.writeInt(flags);
        out.writeInt(res_id);
        out.writeInt(icon_res_id);
        out.writeInt(progress);
        out.writeInt(priority);
        out.writeLong(expires);
        out.writeString(name);
        out.writeString(info);
        out.writeString(profile_override);
        out.writeInt(sender);
        out.writeInt(transition == null ? -1 : transition.ordinal());
        out.writeParcelable(conn_info, parcelFlags);

        if (Build.VERSION.SDK_INT >= 29) {
            out.writeBoolean(is_aon);
        }
    }

    public boolean is_expired() {
        return expires != 0 && SystemClock.elapsedRealtime() > expires;
    }

    public boolean is_reflected(int origin) {
        if (sender == 0) return false;
        return (flags & F_EXCLUDE_SELF) != 0 || sender != origin;
    }

    @Override
    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "EVENT: %s", name));
        if (!info.isEmpty()) {
            sb.append(String.format(Locale.US, " info='%s'", info));
        }
        if (transition != Transition.NO_CHANGE) {
            sb.append(String.format(Locale.US, " trans=%s", transition));
        }
        return sb.toString();
    }

    public String toStringFull() {
        return String.format(
                Locale.US,
                "EVENT: name=%s info='%s' trans=%s flags=%d progress=%d prio=%d res=%d",
                name, info, transition, flags, progress, priority, res_id
        );
    }
}
