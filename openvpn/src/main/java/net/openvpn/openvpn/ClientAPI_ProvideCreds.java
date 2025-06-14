package net.openvpn.openvpn;

public class ClientAPI_ProvideCreds
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_ProvideCreds() {
        this(ovpncliJNI.new_ClientAPI_ProvideCreds(), true);
    }

    protected ClientAPI_ProvideCreds(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_ProvideCreds obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_ProvideCreds(swigCPtr);
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

    public String getUsername() {
        return ovpncliJNI.ClientAPI_ProvideCreds_username_get(swigCPtr, this);
    }

    public void setUsername(String value) {
        ovpncliJNI.ClientAPI_ProvideCreds_username_set(swigCPtr, this, value);
    }

    public String getPassword() {
        return ovpncliJNI.ClientAPI_ProvideCreds_password_get(swigCPtr, this);
    }

    public void setPassword(String value) {
        ovpncliJNI.ClientAPI_ProvideCreds_password_set(swigCPtr, this, value);
    }

    public String getHttpProxyUser() {
        return ovpncliJNI.ClientAPI_ProvideCreds_http_proxy_user_get(swigCPtr, this);
    }

    public void setHttpProxyUser(String value) {
        ovpncliJNI.ClientAPI_ProvideCreds_http_proxy_user_set(swigCPtr, this, value);
    }

    public String getHttpProxyPass() {
        return ovpncliJNI.ClientAPI_ProvideCreds_http_proxy_pass_get(swigCPtr, this);
    }

    public void setHttpProxyPass(String value) {
        ovpncliJNI.ClientAPI_ProvideCreds_http_proxy_pass_set(swigCPtr, this, value);
    }

    public String getDynamicChallengeCookie() {
        return ovpncliJNI.ClientAPI_ProvideCreds_dynamicChallengeCookie_get(swigCPtr, this);
    }

    public void setDynamicChallengeCookie(String value) {
        ovpncliJNI.ClientAPI_ProvideCreds_dynamicChallengeCookie_set(swigCPtr, this, value);
    }

    public String getResponse() {
        return ovpncliJNI.ClientAPI_ProvideCreds_response_get(swigCPtr, this);
    }

    public void setResponse(String value) {
        ovpncliJNI.ClientAPI_ProvideCreds_response_set(swigCPtr, this, value);
    }
}
