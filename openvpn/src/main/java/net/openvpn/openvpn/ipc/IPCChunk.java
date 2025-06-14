package net.openvpn.openvpn.ipc;

import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Представляет собой единичный фрагмент сериализованных данных (чанк),
 * используемый в межпроцессном взаимодействии (IPC).
 * <p>
 * Этот класс оборачивает данные {@link IPCChunkToken}, включающие полезную нагрузку (data),
 * а также метаданные, необходимые для восстановления класса в другом процессе.
 * <p>
 * Используется для передачи Parcelable-объектов через {@link android.os.Message},
 * в сочетании с механизмом {@link IPCChunkable} и его {@link IPCChunkable.Creator},
 * который позволяет восстанавливать объекты по имени класса.
 *
 * @param <T> Тип данных, содержащихся в чанке.
 */
public class IPCChunk<T> extends IPCMessage<T> implements Comparable<IPCChunk<T>>
{
    public static final Parcelable.Creator<IPCChunk<?>> CREATOR = new Parcelable.Creator<>() {
        @Override
        public IPCChunk<?> createFromParcel(Parcel parcel) {
            return new IPCChunk<>(parcel);
        }

        @Override
        public IPCChunk<?>[] newArray(int size) {
            return new IPCChunk<?>[size];
        }
    };

    public IPCChunkToken<T> token;

    private IPCChunk(Parcel parcel) {
        this.action_name = parcel.readString();
        this.id = parcel.readString();
        this.token = IPCChunkToken.readFrom(parcel);
    }

    public IPCChunk(IPCChunkToken<T> token) {
        this.token = token;
    }

    public IPCChunk(IPCChunkToken<T> token, String actionName, String id) {
        this(token);
        this.action_name = actionName;
        this.id = id;
    }

    public static IPCChunk<?> readFrom(Message message) {
        message.getData().setClassLoader(IPCChunk.class.getClassLoader());
        return message.getData().getParcelable(IPCConstants.Field.Data);
    }

    @Override
    public int compareTo(IPCChunk<T> other) {
        return new IPCChunkList.Comparator().compare(this, other);
    }

    public IPCChunkable.Creator<T> getCreator() throws IPCChunkable.BadChunkableException
    {
        if (token == null || token.loader == null) return null;

        String className = token.loader;

        try {
            Field field = Class.forName(className).getField("CHUNKABLE_CREATOR");

            if (!IPCChunkable.Creator.class.isAssignableFrom(field.getType())) {
                throw new IPCChunkable.BadChunkableException(
                        "CHUNKABLE_CREATOR must implement IPCChunkable.Creator in class " + className
                );
            }

            if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) == 0) {
                throw new IPCChunkable.BadChunkableException(
                        "CHUNKABLE_CREATOR must be static in class " + className
                );
            }

            @SuppressWarnings("unchecked")
            IPCChunkable.Creator<T> creator = (IPCChunkable.Creator<T>) field.get(null);
            return creator;

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            Log.w("IPCChunk", "Failed to restore CHUNKABLE_CREATOR", e);
            throw new IPCChunkable.BadChunkableException("Error loading CHUNKABLE_CREATOR from " + className);
        }
    }

    public T getData() {
        return token.data;
    }

    @Override
    public void writeTo(Message message) {
        // Используем базовую логику для возможности расширения в будущем.
        message.getData().putParcelable(IPCConstants.Field.Data, this);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action_name);
        dest.writeString(id);
        token.writeTo(dest, flags);
    }
}
