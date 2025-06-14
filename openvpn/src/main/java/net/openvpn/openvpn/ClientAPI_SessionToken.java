package net.openvpn.openvpn;

public class ClientAPI_SessionToken
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_SessionToken() {
        this(ovpncliJNI.new_ClientAPI_SessionToken(), true);
    }

    protected ClientAPI_SessionToken(long cPtr, boolean cMemoryOwn) {
        this.swigCMemOwn = cMemoryOwn;
        this.swigCPtr = cPtr;
    }

    protected static long getCPtr(ClientAPI_SessionToken obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_SessionToken(swigCPtr);
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

    public String getSessionId() {
        return ovpncliJNI.ClientAPI_SessionToken_session_id_get(swigCPtr, this);
    }

    public void setSessionId(String value) {
        ovpncliJNI.ClientAPI_SessionToken_session_id_set(swigCPtr, this, value);
    }

    public String getUsername() {
        return ovpncliJNI.ClientAPI_SessionToken_username_get(swigCPtr, this);
    }

    public void setUsername(String value) {
        ovpncliJNI.ClientAPI_SessionToken_username_set(swigCPtr, this, value);
    }
}
