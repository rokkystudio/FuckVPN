package net.openvpn.openvpn;

public class ClientAPI_Status
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_Status() {
        this(ovpncliJNI.new_ClientAPI_Status(), true);
    }

    protected ClientAPI_Status(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_Status obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_Status(swigCPtr);
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

    public boolean getError() {
        return ovpncliJNI.ClientAPI_Status_error_get(swigCPtr, this);
    }

    public void setError(boolean value) {
        ovpncliJNI.ClientAPI_Status_error_set(swigCPtr, this, value);
    }

    public String getMessage() {
        return ovpncliJNI.ClientAPI_Status_message_get(swigCPtr, this);
    }

    public void setMessage(String value) {
        ovpncliJNI.ClientAPI_Status_message_set(swigCPtr, this, value);
    }

    public String getStatus() {
        return ovpncliJNI.ClientAPI_Status_status_get(swigCPtr, this);
    }

    public void setStatus(String value) {
        ovpncliJNI.ClientAPI_Status_status_set(swigCPtr, this, value);
    }
}
