package net.openvpn.openvpn.ipc;

import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Базовый класс для сериализуемых IPC-сообщений.
 */
public class IPCMessage<T> implements Parcelable {
    public static final Parcelable.Creator<IPCMessage<?>> CREATOR = new Parcelable.Creator<>() {
        @Override
        public IPCMessage<?> createFromParcel(Parcel parcel) {
            return new IPCMessage<>(parcel);
        }

        @Override
        public IPCMessage<?>[] newArray(int size) {
            return new IPCMessage<?>[size];
        }
    };

    protected static final String TAG = "IPCMessage";

    public String action_name;
    public String id;
    public IPCToken<T> token;

    public IPCMessage() {
        this.token = new IPCToken<>(null);
    }

    public IPCMessage(T data) {
        this.token = new IPCToken<>(data);
    }

    public IPCMessage(String action, String id, T data) {
        this(data);
        this.action_name = action;
        this.id = id;
    }

    protected IPCMessage(Parcel parcel) {
        this.action_name = parcel.readString();
        this.id = parcel.readString();
        this.token = IPCToken.readFrom(parcel);
    }

    public static IPCMessage<?> readFrom(Message message) {
        message.getData().setClassLoader(IPCMessage.class.getClassLoader());
        return message.getData().getParcelable(IPCConstants.Field.Data);
    }

    public T getData() {
        return token.data;
    }

    public void writeTo(Message message) {
        message.getData().putParcelable(IPCConstants.Field.Data, this);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action_name);
        dest.writeString(id);
        token.writeTo(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
