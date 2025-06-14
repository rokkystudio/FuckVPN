package net.openvpn.openvpn.ipc;

import java.util.List;

/**
 * Интерфейс для объектов, которые могут быть разбиты на чанки (части)
 * и восстановлены для межпроцессной передачи (IPC).
 * <p>
 * Используется для сериализации/десериализации сложных структур через Parcelable.
 */
public interface IPCChunkable
{

    /**
     * Исключение, выбрасываемое при ошибке в процессе восстановления Chunkable объекта.
     */
    class BadChunkableException extends Exception {
        public BadChunkableException(String message) {
            super(message);
        }
    }

    /**
     * Интерфейс для восстановления объекта из списка chunk-токенов.
     */
    abstract class Creator<T> {
        public abstract T createFromTokens(List<IPCChunkToken<?>> tokens);
    }

    /**
     * Возвращает количество чанков, на которые разбит объект.
     */
    int countChunks();

    /**
     * Возвращает список токенов, описывающих чанки объекта.
     */
    List<IPCChunkToken<?>> getTokens();

    /**
     * Следует ли разбивать объект на чанки.
     */
    boolean shouldChunk();
}
