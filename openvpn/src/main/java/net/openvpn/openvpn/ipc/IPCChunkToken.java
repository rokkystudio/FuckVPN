package net.openvpn.openvpn.ipc;

import android.os.Parcel;

/**
 * Специализированный токен для передачи чанков данных по IPC.
 */
public class IPCChunkToken<T> extends IPCToken<T> {
    public int index;     // Порядковый номер чанка
    public int total;     // Общее количество чанков
    public String loader; // Имя класса для восстановления

    public IPCChunkToken(T data, int index, int total) {
        super(data);
        this.index = index;
        this.total = total;
    }

    public IPCChunkToken(T data, int index, int total, Class<?> clazz) {
        this(data, index, total);
        this.loader = clazz.getName();
    }

    public static <T> IPCChunkToken<T> readFrom(Parcel parcel) {
        @SuppressWarnings("unchecked")
        T data = (T) IPCToken.doReadFrom(parcel);
        int index = parcel.readInt();
        int total = parcel.readInt();
        String loader = parcel.readString();
        IPCChunkToken<T> token = new IPCChunkToken<>(data, index, total);
        token.loader = loader;
        return token;
    }

    @Override
    public void doWriteTo(Parcel parcel, Object data, int flags) {
        super.doWriteTo(parcel, data, flags);
        parcel.writeInt(index);
        parcel.writeInt(total);
        parcel.writeString(loader);
    }

    public void writeTo(Parcel parcel, int flags) {
        doWriteTo(parcel, this.data, flags);
    }
}
