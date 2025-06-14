package net.openvpn.openvpn;

public class ClientAPI_AppCustomControlMessageEvent
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ClientAPI_AppCustomControlMessageEvent() {
        this(ovpncliJNI.new_ClientAPI_AppCustomControlMessageEvent(), true);
    }

    protected ClientAPI_AppCustomControlMessageEvent(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_AppCustomControlMessageEvent obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_AppCustomControlMessageEvent(swigCPtr);
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

    public String getPayload() {
        return ovpncliJNI.ClientAPI_AppCustomControlMessageEvent_payload_get(
                swigCPtr, this);
    }

    public void setPayload(String payload) {
        ovpncliJNI.ClientAPI_AppCustomControlMessageEvent_payload_set(
                swigCPtr, this, payload);
    }

    public String getProtocol() {
        return ovpncliJNI.ClientAPI_AppCustomControlMessageEvent_protocol_get(
                swigCPtr, this);
    }

    public void setProtocol(String protocol) {
        ovpncliJNI.ClientAPI_AppCustomControlMessageEvent_protocol_set(
                swigCPtr, this, protocol);
    }
}