package net.openvpn.openvpn;

public class ClientAPI_Event
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ClientAPI_Event() {
        this(ovpncliJNI.new_ClientAPI_Event(), true);
    }

    protected ClientAPI_Event(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_Event obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_Event(swigCPtr);
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

    // === Getters ===

    public boolean getError() {
        return ovpncliJNI.ClientAPI_Event_error_get(swigCPtr, this);
    }

    public boolean getFatal() {
        return ovpncliJNI.ClientAPI_Event_fatal_get(swigCPtr, this);
    }

    public String getInfo() {
        return ovpncliJNI.ClientAPI_Event_info_get(swigCPtr, this);
    }

    public String getName() {
        return ovpncliJNI.ClientAPI_Event_name_get(swigCPtr, this);
    }

    // === Setters ===

    public void setError(boolean value) {
        ovpncliJNI.ClientAPI_Event_error_set(swigCPtr, this, value);
    }

    public void setFatal(boolean value) {
        ovpncliJNI.ClientAPI_Event_fatal_set(swigCPtr, this, value);
    }

    public void setInfo(String value) {
        ovpncliJNI.ClientAPI_Event_info_set(swigCPtr, this, value);
    }

    public void setName(String value) {
        ovpncliJNI.ClientAPI_Event_name_set(swigCPtr, this, value);
    }
}
