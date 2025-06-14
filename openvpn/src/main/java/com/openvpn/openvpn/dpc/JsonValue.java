package com.openvpn.openvpn.dpc;

public class JsonValue
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    protected JsonValue(long pointer, boolean ownsMemory) {
        this.swigCMemOwn = ownsMemory;
        this.swigCPtr = pointer;
    }

    protected static long getCPtr(JsonValue obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                dpc_apiJNI.delete_JsonValue(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    @Override
    protected void finalize() {
        delete();
    }

    public boolean isMember(String key) {
        return dpc_apiJNI.JsonValue_isMember(swigCPtr, this, key);
    }

    public String toStyledString() {
        return dpc_apiJNI.JsonValue_toStyledString(swigCPtr, this);
    }
}
