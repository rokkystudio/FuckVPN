package net.openvpn.openvpn;

public class ClientAPI_EvalConfig
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ClientAPI_EvalConfig() {
        this(ovpncliJNI.new_ClientAPI_EvalConfig(), true);
    }

    protected ClientAPI_EvalConfig(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_EvalConfig obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_EvalConfig(swigCPtr);
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

    // === Getters ===

    public boolean getAllowPasswordSave() {
        return ovpncliJNI.ClientAPI_EvalConfig_allowPasswordSave_get(swigCPtr, this);
    }

    public boolean getAutologin() {
        return ovpncliJNI.ClientAPI_EvalConfig_autologin_get(swigCPtr, this);
    }

    public boolean getDcoCompatible() {
        return ovpncliJNI.ClientAPI_EvalConfig_dcoCompatible_get(swigCPtr, this);
    }

    public String getDcoIncompatibilityReason() {
        return ovpncliJNI.ClientAPI_EvalConfig_dcoIncompatibilityReason_get(swigCPtr, this);
    }

    public boolean getError() {
        return ovpncliJNI.ClientAPI_EvalConfig_error_get(swigCPtr, this);
    }

    public boolean getExternalPki() {
        return ovpncliJNI.ClientAPI_EvalConfig_externalPki_get(swigCPtr, this);
    }

    public String getFriendlyName() {
        return ovpncliJNI.ClientAPI_EvalConfig_friendlyName_get(swigCPtr, this);
    }

    public String getMessage() {
        return ovpncliJNI.ClientAPI_EvalConfig_message_get(swigCPtr, this);
    }

    public boolean getPrivateKeyPasswordRequired() {
        return ovpncliJNI.ClientAPI_EvalConfig_privateKeyPasswordRequired_get(swigCPtr, this);
    }

    public String getProfileName() {
        return ovpncliJNI.ClientAPI_EvalConfig_profileName_get(swigCPtr, this);
    }

    public String getRemoteHost() {
        return ovpncliJNI.ClientAPI_EvalConfig_remoteHost_get(swigCPtr, this);
    }

    public String getRemotePort() {
        return ovpncliJNI.ClientAPI_EvalConfig_remotePort_get(swigCPtr, this);
    }

    public String getRemoteProto() {
        return ovpncliJNI.ClientAPI_EvalConfig_remoteProto_get(swigCPtr, this);
    }

    public ClientAPI_ServerEntryVector getServerList() {
        long ptr = ovpncliJNI.ClientAPI_EvalConfig_serverList_get(swigCPtr, this);
        return (ptr == 0) ? null : new ClientAPI_ServerEntryVector(ptr, false);
    }

    public String getStaticChallenge() {
        return ovpncliJNI.ClientAPI_EvalConfig_staticChallenge_get(swigCPtr, this);
    }

    public boolean getStaticChallengeEcho() {
        return ovpncliJNI.ClientAPI_EvalConfig_staticChallengeEcho_get(swigCPtr, this);
    }

    public String getUserlockedUsername() {
        return ovpncliJNI.ClientAPI_EvalConfig_userlockedUsername_get(swigCPtr, this);
    }

    public String getVpnCa() {
        return ovpncliJNI.ClientAPI_EvalConfig_vpnCa_get(swigCPtr, this);
    }

    public String getWindowsDriver() {
        return ovpncliJNI.ClientAPI_EvalConfig_windowsDriver_get(swigCPtr, this);
    }

    // === Setters ===

    public void setAllowPasswordSave(boolean value) {
        ovpncliJNI.ClientAPI_EvalConfig_allowPasswordSave_set(swigCPtr, this, value);
    }

    public void setAutologin(boolean value) {
        ovpncliJNI.ClientAPI_EvalConfig_autologin_set(swigCPtr, this, value);
    }

    public void setDcoCompatible(boolean value) {
        ovpncliJNI.ClientAPI_EvalConfig_dcoCompatible_set(swigCPtr, this, value);
    }

    public void setDcoIncompatibilityReason(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_dcoIncompatibilityReason_set(
                swigCPtr, this, value
        );
    }

    public void setError(boolean value) {
        ovpncliJNI.ClientAPI_EvalConfig_error_set(swigCPtr, this, value);
    }

    public void setExternalPki(boolean value) {
        ovpncliJNI.ClientAPI_EvalConfig_externalPki_set(swigCPtr, this, value);
    }

    public void setFriendlyName(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_friendlyName_set(swigCPtr, this, value);
    }

    public void setMessage(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_message_set(swigCPtr, this, value);
    }

    public void setPrivateKeyPasswordRequired(boolean value) {
        ovpncliJNI.ClientAPI_EvalConfig_privateKeyPasswordRequired_set(swigCPtr, this, value);
    }

    public void setProfileName(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_profileName_set(swigCPtr, this, value);
    }

    public void setRemoteHost(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_remoteHost_set(swigCPtr, this, value);
    }

    public void setRemotePort(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_remotePort_set(swigCPtr, this, value);
    }

    public void setRemoteProto(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_remoteProto_set(swigCPtr, this, value);
    }

    public void setServerList(ClientAPI_ServerEntryVector value) {
        ovpncliJNI.ClientAPI_EvalConfig_serverList_set(
                swigCPtr, this,
                ClientAPI_ServerEntryVector.getCPtr(value), value
        );
    }

    public void setStaticChallenge(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_staticChallenge_set(swigCPtr, this, value);
    }

    public void setStaticChallengeEcho(boolean value) {
        ovpncliJNI.ClientAPI_EvalConfig_staticChallengeEcho_set(swigCPtr, this, value);
    }

    public void setUserlockedUsername(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_userlockedUsername_set(swigCPtr, this, value);
    }

    public void setVpnCa(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_vpnCa_set(swigCPtr, this, value);
    }

    public void setWindowsDriver(String value) {
        ovpncliJNI.ClientAPI_EvalConfig_windowsDriver_set(swigCPtr, this, value);
    }
}
