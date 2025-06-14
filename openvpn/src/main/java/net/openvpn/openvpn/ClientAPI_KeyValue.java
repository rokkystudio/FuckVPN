package net.openvpn.openvpn;

public class ClientAPI_KeyValue
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ClientAPI_KeyValue() {
        this(ovpncliJNI.new_ClientAPI_KeyValue__SWIG_0(), true);
    }

    public ClientAPI_KeyValue(String key, String value) {
        this(ovpncliJNI.new_ClientAPI_KeyValue__SWIG_1(key, value), true);
    }

    protected ClientAPI_KeyValue(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_KeyValue obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_KeyValue(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    // Proper finalize with super.finalize() to avoid warnings
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

    public String getKey() {
        return ovpncliJNI.ClientAPI_KeyValue_key_get(
                swigCPtr, this
        );
    }

    public void setKey(String key) {
        ovpncliJNI.ClientAPI_KeyValue_key_set(
                swigCPtr, this, key
        );
    }

    public String getValue() {
        return ovpncliJNI.ClientAPI_KeyValue_value_get(
                swigCPtr, this
        );
    }

    public void setValue(String value) {
        ovpncliJNI.ClientAPI_KeyValue_value_set(
                swigCPtr, this, value
        );
    }
}
