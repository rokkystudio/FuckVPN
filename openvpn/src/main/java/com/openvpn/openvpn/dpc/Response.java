package com.openvpn.openvpn.dpc;

public class Response
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public Response() {
        this(dpc_apiJNI.new_Response(), true);
    }

    protected Response(long ptr, boolean ownsMemory) {
        this.swigCMemOwn = ownsMemory;
        this.swigCPtr = ptr;
    }

    protected static long getCPtr(Response obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                dpc_apiJNI.delete_Response(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    @Override
    protected void finalize() {
        delete();
    }

    public boolean hasErrors() {
        return dpc_apiJNI.Response_hasErrors(swigCPtr, this);
    }

    public String toJSONString() {
        return dpc_apiJNI.Response_toJSONString(swigCPtr, this);
    }
}
