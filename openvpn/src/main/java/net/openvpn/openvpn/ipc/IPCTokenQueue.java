package net.openvpn.openvpn.ipc;

import android.os.Messenger;
import java.util.List;

public class IPCTokenQueue {
    public String action;
    public int index = 0;
    public String message_id;
    public Messenger messenger;
    public List<IPCChunkToken> tokens;

    IPCTokenQueue(List<IPCChunkToken> list, Messenger messenger2, String str, String str2) {
        this.tokens = list;
        this.messenger = messenger2;
        this.message_id = str;
        this.action = str2;
    }

    public IPCChunkToken get(int i) {
        return this.tokens.get(i);
    }

    public boolean hasNext() {
        return this.index < this.tokens.size();
    }

    public IPCChunkToken next() {
        if (!hasNext()) {
            return null;
        }
        List<IPCChunkToken> list = this.tokens;
        int i = this.index;
        this.index = i + 1;
        return list.get(i);
    }

    public boolean oneLeft() {
        return this.index == this.tokens.size() - 1;
    }
}
