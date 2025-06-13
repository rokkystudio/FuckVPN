package net.openvpn.openvpn;

public class ClientAPI_LogInfo
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ClientAPI_LogInfo() {
        this(ovpncliJNI.new_ClientAPI_LogInfo__SWIG_0(), true);
    }

    public ClientAPI_LogInfo(String text) {
        this(ovpncliJNI.new_ClientAPI_LogInfo__SWIG_1(text), true);
    }

    protected ClientAPI_LogInfo(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_LogInfo obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_LogInfo(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    @Override
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

    public String getText() {
        return ovpncliJNI.ClientAPI_LogInfo_text_get(
                swigCPtr, this
        );
    }

    public void setText(String text) {
        ovpncliJNI.ClientAPI_LogInfo_text_set(
                swigCPtr, this, text
        );
    }
}
