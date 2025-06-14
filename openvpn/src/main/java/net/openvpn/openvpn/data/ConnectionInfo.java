package net.openvpn.openvpn.data;

import android.os.Parcel;
import android.os.Parcelable;
import net.openvpn.openvpn.ClientAPI_ConnectionInfo;

public class ConnectionInfo implements Parcelable
{
    public static final Creator<ConnectionInfo> CREATOR = new Creator<>() {
        @Override
        public ConnectionInfo createFromParcel(Parcel in) {
            return new ConnectionInfo(in);
        }

        @Override
        public ConnectionInfo[] newArray(int size) {
            return new ConnectionInfo[size];
        }
    };

    public boolean defined;
    public String user;
    public String serverHost;
    public String serverPort;
    public String serverProto;
    public String serverIp;
    public String vpnIp4;
    public String vpnIp6;
    public String gw4;
    public String gw6;
    public String clientIp;
    public String tunName;

    public ConnectionInfo() {
        // Default constructor
    }

    protected ConnectionInfo(Parcel in) {
        defined = in.readByte() != 0;
        user = in.readString();
        serverHost = in.readString();
        serverPort = in.readString();
        serverProto = in.readString();
        serverIp = in.readString();
        vpnIp4 = in.readString();
        vpnIp6 = in.readString();
        gw4 = in.readString();
        gw6 = in.readString();
        clientIp = in.readString();
        tunName = in.readString();
    }

    public ConnectionInfo(ClientAPI_ConnectionInfo nativeInfo) {
        defined = nativeInfo.getDefined();
        user = nativeInfo.getUser();
        serverHost = nativeInfo.getServerHost();
        serverPort = nativeInfo.getServerPort();
        serverProto = nativeInfo.getServerProto();
        serverIp = nativeInfo.getServerIp();
        vpnIp4 = nativeInfo.getVpnIp4();
        vpnIp6 = nativeInfo.getVpnIp6();
        gw4 = nativeInfo.getGw4();
        gw6 = nativeInfo.getGw6();
        clientIp = nativeInfo.getClientIp();
        tunName = nativeInfo.getTunName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeByte((byte) (defined ? 1 : 0));
        out.writeString(user);
        out.writeString(serverHost);
        out.writeString(serverPort);
        out.writeString(serverProto);
        out.writeString(serverIp);
        out.writeString(vpnIp4);
        out.writeString(vpnIp6);
        out.writeString(gw4);
        out.writeString(gw6);
        out.writeString(clientIp);
        out.writeString(tunName);
    }
}