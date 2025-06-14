package net.openvpn.openvpn;

public class ClientAPI_ServerEntry
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_ServerEntry() {
        this(ovpncliJNI.new_ClientAPI_ServerEntry(), true);
    }

    protected ClientAPI_ServerEntry(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_ServerEntry obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_ServerEntry(swigCPtr);
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

    public String getFriendlyName() {
        return ovpncliJNI.ClientAPI_ServerEntry_friendlyName_get(swigCPtr, this);
    }

    public void setFriendlyName(String value) {
        ovpncliJNI.ClientAPI_ServerEntry_friendlyName_set(swigCPtr, this, value);
    }

    public String getServer() {
        return ovpncliJNI.ClientAPI_ServerEntry_server_get(swigCPtr, this);
    }

    public void setServer(String value) {
        ovpncliJNI.ClientAPI_ServerEntry_server_set(swigCPtr, this, value);
    }
}
