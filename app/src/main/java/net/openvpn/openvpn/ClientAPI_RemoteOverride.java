package net.openvpn.openvpn;

public class ClientAPI_RemoteOverride
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_RemoteOverride() {
        this(ovpncliJNI.new_ClientAPI_RemoteOverride(), true);
    }

    protected ClientAPI_RemoteOverride(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_RemoteOverride obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_RemoteOverride(swigCPtr);
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

    public String getError() {
        return ovpncliJNI.ClientAPI_RemoteOverride_error_get(swigCPtr, this);
    }

    public void setError(String value) {
        ovpncliJNI.ClientAPI_RemoteOverride_error_set(swigCPtr, this, value);
    }

    public String getHost() {
        return ovpncliJNI.ClientAPI_RemoteOverride_host_get(swigCPtr, this);
    }

    public void setHost(String value) {
        ovpncliJNI.ClientAPI_RemoteOverride_host_set(swigCPtr, this, value);
    }

    public String getIp() {
        return ovpncliJNI.ClientAPI_RemoteOverride_ip_get(swigCPtr, this);
    }

    public void setIp(String value) {
        ovpncliJNI.ClientAPI_RemoteOverride_ip_set(swigCPtr, this, value);
    }

    public String getPort() {
        return ovpncliJNI.ClientAPI_RemoteOverride_port_get(swigCPtr, this);
    }

    public void setPort(String value) {
        ovpncliJNI.ClientAPI_RemoteOverride_port_set(swigCPtr, this, value);
    }

    public String getProto() {
        return ovpncliJNI.ClientAPI_RemoteOverride_proto_get(swigCPtr, this);
    }

    public void setProto(String value) {
        ovpncliJNI.ClientAPI_RemoteOverride_proto_set(swigCPtr, this, value);
    }
}
