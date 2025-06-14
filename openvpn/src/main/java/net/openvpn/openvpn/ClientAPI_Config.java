package net.openvpn.openvpn;

public class ClientAPI_Config extends ConfigCommon
{
    private transient long swigCPtr;

    public ClientAPI_Config() {
        this(ovpncliJNI.new_ClientAPI_Config(), true);
    }

    protected ClientAPI_Config(long cPtr, boolean cMemoryOwn) {
        super(ovpncliJNI.ClientAPI_Config_SWIGUpcast(cPtr), cMemoryOwn);
        this.swigCPtr = cPtr;
    }

    protected static long getCPtr(ClientAPI_Config obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    @Override
    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_Config(swigCPtr);
            }
            swigCPtr = 0;
        }
        super.delete();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            delete();
        } finally {
            super.finalize();
        }
    }

    public String getAllowUnusedAddrFamilies() {
        return ovpncliJNI.ClientAPI_Config_allowUnusedAddrFamilies_get(
                swigCPtr, this
        );
    }

    public void setAllowUnusedAddrFamilies(String value) {
        ovpncliJNI.ClientAPI_Config_allowUnusedAddrFamilies_set(
                swigCPtr, this, value
        );
    }

    public String getCompressionMode() {
        return ovpncliJNI.ClientAPI_Config_compressionMode_get(
                swigCPtr, this
        );
    }

    public void setCompressionMode(String value) {
        ovpncliJNI.ClientAPI_Config_compressionMode_set(
                swigCPtr, this, value
        );
    }

    public String getContent() {
        return ovpncliJNI.ClientAPI_Config_content_get(
                swigCPtr, this
        );
    }

    public void setContent(String value) {
        ovpncliJNI.ClientAPI_Config_content_set(
                swigCPtr, this, value
        );
    }

    public ClientAPIKeyValueListPointer getContentList() {
        long ptr = ovpncliJNI.ClientAPI_Config_contentList_get(
                swigCPtr, this
        );
        return (ptr == 0) ? null : new ClientAPIKeyValueListPointer(ptr, false);
    }

    public void setContentList(ClientAPIKeyValueListPointer value) {
        ovpncliJNI.ClientAPI_Config_contentList_set(
                swigCPtr, this,
                ClientAPIKeyValueListPointer.getCPtr(value)
        );
    }

    public String getExternalPkiAlias() {
        return ovpncliJNI.ClientAPI_Config_externalPkiAlias_get(
                swigCPtr, this
        );
    }

    public void setExternalPkiAlias(String value) {
        ovpncliJNI.ClientAPI_Config_externalPkiAlias_set(
                swigCPtr, this, value
        );
    }

    public ClientAPIKeyValueListPointer getPeerInfo() {
        long ptr = ovpncliJNI.ClientAPI_Config_peerInfo_get(
                swigCPtr, this
        );
        return (ptr == 0) ? null : new ClientAPIKeyValueListPointer(ptr, false);
    }

    public void setPeerInfo(ClientAPIKeyValueListPointer value) {
        ovpncliJNI.ClientAPI_Config_peerInfo_set(
                swigCPtr, this,
                ClientAPIKeyValueListPointer.getCPtr(value)
        );
    }

    public String getProtoOverride() {
        return ovpncliJNI.ClientAPI_Config_protoOverride_get(
                swigCPtr, this
        );
    }

    public void setProtoOverride(String value) {
        ovpncliJNI.ClientAPI_Config_protoOverride_set(
                swigCPtr, this, value
        );
    }

    public int getProtoVersionOverride() {
        return ovpncliJNI.ClientAPI_Config_protoVersionOverride_get(
                swigCPtr, this
        );
    }

    public void setProtoVersionOverride(int value) {
        ovpncliJNI.ClientAPI_Config_protoVersionOverride_set(
                swigCPtr, this, value
        );
    }
}
