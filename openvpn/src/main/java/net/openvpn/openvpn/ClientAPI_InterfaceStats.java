package net.openvpn.openvpn;

public class ClientAPI_InterfaceStats
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ClientAPI_InterfaceStats() {
        this(ovpncliJNI.new_ClientAPI_InterfaceStats(), true);
    }

    protected ClientAPI_InterfaceStats(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_InterfaceStats obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_InterfaceStats(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    // finalize() with correct access and super call
    protected void finalize() {
        try {
            delete();
        } finally {
            try {
                super.finalize();
            } catch (Throwable ignored) {
            }
        }
    }

    public long getBytesIn() {
        return ovpncliJNI.ClientAPI_InterfaceStats_bytesIn_get(
                swigCPtr, this
        );
    }

    public void setBytesIn(long value) {
        ovpncliJNI.ClientAPI_InterfaceStats_bytesIn_set(
                swigCPtr, this, value
        );
    }

    public long getBytesOut() {
        return ovpncliJNI.ClientAPI_InterfaceStats_bytesOut_get(
                swigCPtr, this
        );
    }

    public void setBytesOut(long value) {
        ovpncliJNI.ClientAPI_InterfaceStats_bytesOut_set(
                swigCPtr, this, value
        );
    }

    public long getErrorsIn() {
        return ovpncliJNI.ClientAPI_InterfaceStats_errorsIn_get(
                swigCPtr, this
        );
    }

    public void setErrorsIn(long value) {
        ovpncliJNI.ClientAPI_InterfaceStats_errorsIn_set(
                swigCPtr, this, value
        );
    }

    public long getErrorsOut() {
        return ovpncliJNI.ClientAPI_InterfaceStats_errorsOut_get(
                swigCPtr, this
        );
    }

    public void setErrorsOut(long value) {
        ovpncliJNI.ClientAPI_InterfaceStats_errorsOut_set(
                swigCPtr, this, value
        );
    }

    public long getPacketsIn() {
        return ovpncliJNI.ClientAPI_InterfaceStats_packetsIn_get(
                swigCPtr, this
        );
    }

    public void setPacketsIn(long value) {
        ovpncliJNI.ClientAPI_InterfaceStats_packetsIn_set(
                swigCPtr, this, value
        );
    }

    public long getPacketsOut() {
        return ovpncliJNI.ClientAPI_InterfaceStats_packetsOut_get(
                swigCPtr, this
        );
    }

    public void setPacketsOut(long value) {
        ovpncliJNI.ClientAPI_InterfaceStats_packetsOut_set(
                swigCPtr, this, value
        );
    }
}
