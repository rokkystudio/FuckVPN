package net.openvpn.openvpn;

public class ClientAPI_TransportStats
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_TransportStats() {
        this(ovpncliJNI.new_ClientAPI_TransportStats(), true);
    }

    protected ClientAPI_TransportStats(long cPtr, boolean memoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = memoryOwn;
    }

    protected static long getCPtr(ClientAPI_TransportStats obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_TransportStats(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            delete();
        } finally {
            super.finalize();
        }
    }

    public long getBytesIn() {
        return ovpncliJNI.ClientAPI_TransportStats_bytesIn_get(swigCPtr, this);
    }

    public void setBytesIn(long value) {
        ovpncliJNI.ClientAPI_TransportStats_bytesIn_set(swigCPtr, this, value);
    }

    public long getBytesOut() {
        return ovpncliJNI.ClientAPI_TransportStats_bytesOut_get(swigCPtr, this);
    }

    public void setBytesOut(long value) {
        ovpncliJNI.ClientAPI_TransportStats_bytesOut_set(swigCPtr, this, value);
    }

    public long getPacketsIn() {
        return ovpncliJNI.ClientAPI_TransportStats_packetsIn_get(swigCPtr, this);
    }

    public void setPacketsIn(long value) {
        ovpncliJNI.ClientAPI_TransportStats_packetsIn_set(swigCPtr, this, value);
    }

    public long getPacketsOut() {
        return ovpncliJNI.ClientAPI_TransportStats_packetsOut_get(swigCPtr, this);
    }

    public void setPacketsOut(long value) {
        ovpncliJNI.ClientAPI_TransportStats_packetsOut_set(swigCPtr, this, value);
    }

    public int getLastPacketReceived() {
        return ovpncliJNI.ClientAPI_TransportStats_lastPacketReceived_get(swigCPtr, this);
    }

    public void setLastPacketReceived(int value) {
        ovpncliJNI.ClientAPI_TransportStats_lastPacketReceived_set(swigCPtr, this, value);
    }
}
