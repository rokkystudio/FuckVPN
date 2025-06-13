package net.openvpn.openvpn;

public class ClientAPI_ExternalPKIBase
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    protected ClientAPI_ExternalPKIBase(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_ExternalPKIBase obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_ExternalPKIBase(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    @Override
    protected void finalize() {
        try {
            delete();
        } catch (Throwable ignored) {
        }
    }

    public boolean sign(String mdAlgorithm, String data, CppStringPointer signatureOut,
        String certRef, String certIssuer, String keyLabel) {
        return ovpncliJNI.ClientAPI_ExternalPKIBase_sign(
            swigCPtr, this, mdAlgorithm, data,
            CppStringPointer.getCPtr(signatureOut), certRef, certIssuer, keyLabel
        );
    }
}
