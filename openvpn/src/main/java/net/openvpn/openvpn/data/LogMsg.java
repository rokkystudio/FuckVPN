package net.openvpn.openvpn.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class LogMsg implements Parcelable
{
    private static final String TAG = "LogMsg";

    public String line;

    public static final Creator<LogMsg> CREATOR = new Creator<>() {
        @Override
        public LogMsg createFromParcel(Parcel in) {
            return fromParcel(in);
        }

        @Override
        public LogMsg[] newArray(int size) {
            return new LogMsg[size];
        }
    };

    public static LogMsg fromJSON(JSONObject json) {
        LogMsg logMsg = new LogMsg();
        try {
            logMsg.line = json.getString("line");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return logMsg;
    }

    public static LogMsg fromParcel(Parcel in) {
        LogMsg logMsg = new LogMsg();
        logMsg.line = in.readString();
        return logMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("line", line);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return json;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(line);
    }

    @Override @NonNull
    public String toString() {
        return line != null ? line : "";
    }
}