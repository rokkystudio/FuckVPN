package net.openvpn.openvpn;

public class ClientAPI_MergeConfig
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ClientAPI_MergeConfig() {
        this(ovpncliJNI.new_ClientAPI_MergeConfig(), true);
    }

    protected ClientAPI_MergeConfig(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_MergeConfig obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_MergeConfig(swigCPtr);
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

    public String getBasename() {
        return ovpncliJNI.ClientAPI_MergeConfig_basename_get(
                swigCPtr, this
        );
    }

    public void setBasename(String value) {
        ovpncliJNI.ClientAPI_MergeConfig_basename_set(
                swigCPtr, this, value
        );
    }

    public String getErrorText() {
        return ovpncliJNI.ClientAPI_MergeConfig_errorText_get(
                swigCPtr, this
        );
    }

    public void setErrorText(String value) {
        ovpncliJNI.ClientAPI_MergeConfig_errorText_set(
                swigCPtr, this, value
        );
    }

    public String getProfileContent() {
        return ovpncliJNI.ClientAPI_MergeConfig_profileContent_get(
                swigCPtr, this
        );
    }

    public void setProfileContent(String value) {
        ovpncliJNI.ClientAPI_MergeConfig_profileContent_set(
                swigCPtr, this, value
        );
    }

    public ClientAPI_StringVec getRefPathList() {
        long ptr = ovpncliJNI.ClientAPI_MergeConfig_refPathList_get(
                swigCPtr, this
        );
        return (ptr == 0) ? null : new ClientAPI_StringVec(ptr, false);
    }

    public void setRefPathList(ClientAPI_StringVec vec) {
        ovpncliJNI.ClientAPI_MergeConfig_refPathList_set(
                swigCPtr, this,
                ClientAPI_StringVec.getCPtr(vec),
                vec
        );
    }

    public String getStatus() {
        return ovpncliJNI.ClientAPI_MergeConfig_status_get(
                swigCPtr, this
        );
    }

    public void setStatus(String value) {
        ovpncliJNI.ClientAPI_MergeConfig_status_set(
                swigCPtr, this, value
        );
    }
}
