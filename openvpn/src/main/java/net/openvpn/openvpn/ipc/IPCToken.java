package net.openvpn.openvpn.ipc;

import android.os.Parcel;
import android.os.Parcelable;

public class IPCToken<T> {
    public T data;

    IPCToken(T t) {
        this.data = t;
    }

    static <T> T doReadFrom(Parcel parcel) {
        int readInt = parcel.readInt();
        if (readInt == -1) {
            return null;
        }
        boolean z = true;
        if (readInt == 9) {
            if (parcel.readByte() == 0) {
                z = false;
            }
            return Boolean.valueOf(z);
        } else if (readInt == 0) {
            return parcel.readString();
        } else {
            if (readInt == 1) {
                return Integer.valueOf(parcel.readInt());
            }
            if (readInt == 4) {
                try {
                    return parcel.readParcelable(Class.forName(parcel.readString()).getClassLoader());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    static <T> IPCToken<T> readFrom(Parcel parcel) {
        IPCToken<T> iPCToken = new IPCToken<>((Object) null);
        iPCToken.data = doReadFrom(parcel);
        return iPCToken;
    }

    /* access modifiers changed from: protected */
    public void doWriteTo(Parcel parcel, Object obj, int i) {
        if (obj == null) {
            parcel.writeInt(-1);
        } else if (obj instanceof Boolean) {
            parcel.writeInt(9);
            parcel.writeByte(((Boolean) obj).booleanValue() ? (byte) 1 : 0);
        } else if (obj instanceof String) {
            parcel.writeInt(0);
            parcel.writeString((String) obj);
        } else if (obj instanceof Integer) {
            parcel.writeInt(1);
            parcel.writeInt(((Integer) obj).intValue());
        } else if (obj instanceof Parcelable) {
            parcel.writeInt(4);
            parcel.writeString(obj.getClass().getName());
            parcel.writeParcelable((Parcelable) obj, i);
        }
    }

    public T getData() {
        return this.data;
    }

    public void writeTo(Parcel parcel, int i) {
        doWriteTo(parcel, this.data, i);
    }
}
