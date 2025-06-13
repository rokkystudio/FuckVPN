package net.openvpn.openvpn;

public class ClientAPI_ExternalPKICertRequest extends ClientAPI_ExternalPKIRequestBase
{
    private transient long swigCPtr;

    public ClientAPI_ExternalPKICertRequest() {
        this(ovpncliJNI.new_ClientAPI_ExternalPKICertRequest(), true);
    }

    protected ClientAPI_ExternalPKICertRequest(long cPtr, boolean cMemoryOwn) {
        super(ovpncliJNI.ClientAPI_ExternalPKICertRequest_SWIGUpcast(cPtr), cMemoryOwn);
        this.swigCPtr = cPtr;
    }

    protected static long getCPtr(ClientAPI_ExternalPKICertRequest obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    @Override
    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_ExternalPKICertRequest(swigCPtr);
            }
            swigCPtr = 0;
        }
        super.delete();
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

    public String getCert() {
        return ovpncliJNI.ClientAPI_ExternalPKICertRequest_cert_get(
                swigCPtr, this
        );
    }

    public void setCert(String value) {
        ovpncliJNI.ClientAPI_ExternalPKICertRequest_cert_set(
                swigCPtr, this, value
        );
    }

    public String getSupportingChain() {
        return ovpncliJNI.ClientAPI_ExternalPKICertRequest_supportingChain_get(
                swigCPtr, this
        );
    }

    public void setSupportingChain(String value) {
        ovpncliJNI.ClientAPI_ExternalPKICertRequest_supportingChain_set(
                swigCPtr, this, value
        );
    }
}
