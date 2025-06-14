package net.openvpn.openvpn.ipc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Коллекция чанков IPC с возможностью сортировки и извлечения токенов.
 */
public class IPCChunkList<T> extends ArrayList<IPCChunk<T>>
{
    private static class ChunkComparator<T> implements Comparator<IPCChunk<T>> {
        @Override
        public int compare(IPCChunk<T> a, IPCChunk<T> b) {
            return Integer.compare(a.token.index, b.token.index);
        }
    }

    public List<IPCChunkToken<T>> getTokens() {
        List<IPCChunkToken<T>> tokens = new ArrayList<>(size());
        for (IPCChunk<T> chunk : this) {
            tokens.add(chunk.token);
        }
        return tokens;
    }

    public void sortChunks() {
        sort(new ChunkComparator<>());
    }
}
