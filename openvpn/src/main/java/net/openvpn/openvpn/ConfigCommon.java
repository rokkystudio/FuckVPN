package net.openvpn.openvpn;

public class ConfigCommon {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public ConfigCommon() {
        this(ovpncliJNI.new_ConfigCommon(), true);
    }

    protected ConfigCommon(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ConfigCommon obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ConfigCommon(swigCPtr);
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
    public boolean getAllowLocalDnsResolvers() {
        return ovpncliJNI.ConfigCommon_allowLocalDnsResolvers_get(swigCPtr, this);
    }

    public boolean getAllowLocalLanAccess() {
        return ovpncliJNI.ConfigCommon_allowLocalLanAccess_get(swigCPtr, this);
    }

    public boolean getAltProxy() {
        return ovpncliJNI.ConfigCommon_altProxy_get(swigCPtr, this);
    }

    public String getAppCustomProtocols() {
        return ovpncliJNI.ConfigCommon_appCustomProtocols_get(swigCPtr, this);
    }

    public boolean getAutologinSessions() {
        return ovpncliJNI.ConfigCommon_autologinSessions_get(swigCPtr, this);
    }

    public long getClockTickMS() {
        return ovpncliJNI.ConfigCommon_clockTickMS_get(swigCPtr, this);
    }

    public int getConnTimeout() {
        return ovpncliJNI.ConfigCommon_connTimeout_get(swigCPtr, this);
    }

    public boolean getDco() {
        return ovpncliJNI.ConfigCommon_dco_get(swigCPtr, this);
    }

    public int getDefaultKeyDirection() {
        return ovpncliJNI.ConfigCommon_defaultKeyDirection_get(swigCPtr, this);
    }

    public boolean getDhcpSearchDomainsAsSplitDomains() {
        return ovpncliJNI.ConfigCommon_dhcpSearchDomainsAsSplitDomains_get(swigCPtr, this);
    }

    public boolean getDisableClientCert() {
        return ovpncliJNI.ConfigCommon_disableClientCert_get(swigCPtr, this);
    }

    public boolean getEcho() {
        return ovpncliJNI.ConfigCommon_echo_get(swigCPtr, this);
    }

    public boolean getEnableLegacyAlgorithms() {
        return ovpncliJNI.ConfigCommon_enableLegacyAlgorithms_get(swigCPtr, this);
    }

    public boolean getEnableNonPreferredDCAlgorithms() {
        return ovpncliJNI.ConfigCommon_enableNonPreferredDCAlgorithms_get(swigCPtr, this);
    }

    public boolean getEnableRouteEmulation() {
        return ovpncliJNI.ConfigCommon_enableRouteEmulation_get(swigCPtr, this);
    }

    public boolean getGenerateTunBuilderCaptureEvent() {
        return ovpncliJNI.ConfigCommon_generateTunBuilderCaptureEvent_get(swigCPtr, this);
    }

    public boolean getGoogleDnsFallback() {
        return ovpncliJNI.ConfigCommon_googleDnsFallback_get(swigCPtr, this);
    }

    public String getGremlinConfig() {
        return ovpncliJNI.ConfigCommon_gremlinConfig_get(swigCPtr, this);
    }

    public String getGuiVersion() {
        return ovpncliJNI.ConfigCommon_guiVersion_get(swigCPtr, this);
    }

    public String getHwAddrOverride() {
        return ovpncliJNI.ConfigCommon_hwAddrOverride_get(swigCPtr, this);
    }

    public boolean getInfo() {
        return ovpncliJNI.ConfigCommon_info_get(swigCPtr, this);
    }

    public String getPlatformVersion() {
        return ovpncliJNI.ConfigCommon_platformVersion_get(swigCPtr, this);
    }

    public String getPortOverride() {
        return ovpncliJNI.ConfigCommon_portOverride_get(swigCPtr, this);
    }

    public String getPrivateKeyPassword() {
        return ovpncliJNI.ConfigCommon_privateKeyPassword_get(swigCPtr, this);
    }

    public boolean getProxyAllowCleartextAuth() {
        return ovpncliJNI.ConfigCommon_proxyAllowCleartextAuth_get(swigCPtr, this);
    }

    public String getProxyHost() {
        return ovpncliJNI.ConfigCommon_proxyHost_get(swigCPtr, this);
    }

    public String getProxyPassword() {
        return ovpncliJNI.ConfigCommon_proxyPassword_get(swigCPtr, this);
    }

    public String getProxyPort() {
        return ovpncliJNI.ConfigCommon_proxyPort_get(swigCPtr, this);
    }

    public String getProxyUsername() {
        return ovpncliJNI.ConfigCommon_proxyUsername_get(swigCPtr, this);
    }

    public boolean getRetryOnAuthFailed() {
        return ovpncliJNI.ConfigCommon_retryOnAuthFailed_get(swigCPtr, this);
    }

    public String getServerOverride() {
        return ovpncliJNI.ConfigCommon_serverOverride_get(swigCPtr, this);
    }

    public int getSslDebugLevel() {
        return ovpncliJNI.ConfigCommon_sslDebugLevel_get(swigCPtr, this);
    }

    public String getSsoMethods() {
        return ovpncliJNI.ConfigCommon_ssoMethods_get(swigCPtr, this);
    }

    public boolean getSynchronousDnsLookup() {
        return ovpncliJNI.ConfigCommon_synchronousDnsLookup_get(swigCPtr, this);
    }

    public String getTlsCertProfileOverride() {
        return ovpncliJNI.ConfigCommon_tlsCertProfileOverride_get(swigCPtr, this);
    }

    public String getTlsCipherList() {
        return ovpncliJNI.ConfigCommon_tlsCipherList_get(swigCPtr, this);
    }

    public String getTlsCiphersuitesList() {
        return ovpncliJNI.ConfigCommon_tlsCiphersuitesList_get(swigCPtr, this);
    }

    public String getTlsVersionMinOverride() {
        return ovpncliJNI.ConfigCommon_tlsVersionMinOverride_get(swigCPtr, this);
    }

    public boolean getTunPersist() {
        return ovpncliJNI.ConfigCommon_tunPersist_get(swigCPtr, this);
    }

    public boolean getWintun() {
        return ovpncliJNI.ConfigCommon_wintun_get(swigCPtr, this);
    }

    // === Setters ===
    public void setAllowLocalDnsResolvers(boolean value) {
        ovpncliJNI.ConfigCommon_allowLocalDnsResolvers_set(swigCPtr, this, value);
    }

    public void setAllowLocalLanAccess(boolean value) {
        ovpncliJNI.ConfigCommon_allowLocalLanAccess_set(swigCPtr, this, value);
    }

    public void setAltProxy(boolean value) {
        ovpncliJNI.ConfigCommon_altProxy_set(swigCPtr, this, value);
    }

    public void setAppCustomProtocols(String value) {
        ovpncliJNI.ConfigCommon_appCustomProtocols_set(swigCPtr, this, value);
    }

    public void setAutologinSessions(boolean value) {
        ovpncliJNI.ConfigCommon_autologinSessions_set(swigCPtr, this, value);
    }

    public void setClockTickMS(long value) {
        ovpncliJNI.ConfigCommon_clockTickMS_set(swigCPtr, this, value);
    }

    public void setConnTimeout(int value) {
        ovpncliJNI.ConfigCommon_connTimeout_set(swigCPtr, this, value);
    }

    public void setDco(boolean value) {
        ovpncliJNI.ConfigCommon_dco_set(swigCPtr, this, value);
    }

    public void setDefaultKeyDirection(int value) {
        ovpncliJNI.ConfigCommon_defaultKeyDirection_set(swigCPtr, this, value);
    }

    public void setDhcpSearchDomainsAsSplitDomains(boolean value) {
        ovpncliJNI.ConfigCommon_dhcpSearchDomainsAsSplitDomains_set(swigCPtr, this, value);
    }

    public void setDisableClientCert(boolean value) {
        ovpncliJNI.ConfigCommon_disableClientCert_set(swigCPtr, this, value);
    }

    public void setEcho(boolean value) {
        ovpncliJNI.ConfigCommon_echo_set(swigCPtr, this, value);
    }

    public void setEnableLegacyAlgorithms(boolean value) {
        ovpncliJNI.ConfigCommon_enableLegacyAlgorithms_set(swigCPtr, this, value);
    }

    public void setEnableNonPreferredDCAlgorithms(boolean value) {
        ovpncliJNI.ConfigCommon_enableNonPreferredDCAlgorithms_set(swigCPtr, this, value);
    }

    public void setEnableRouteEmulation(boolean value) {
        ovpncliJNI.ConfigCommon_enableRouteEmulation_set(swigCPtr, this, value);
    }

    public void setGenerateTunBuilderCaptureEvent(boolean value) {
        ovpncliJNI.ConfigCommon_generateTunBuilderCaptureEvent_set(swigCPtr, this, value);
    }

    public void setGoogleDnsFallback(boolean value) {
        ovpncliJNI.ConfigCommon_googleDnsFallback_set(swigCPtr, this, value);
    }

    public void setGremlinConfig(String value) {
        ovpncliJNI.ConfigCommon_gremlinConfig_set(swigCPtr, this, value);
    }

    public void setGuiVersion(String value) {
        ovpncliJNI.ConfigCommon_guiVersion_set(swigCPtr, this, value);
    }

    public void setHwAddrOverride(String value) {
        ovpncliJNI.ConfigCommon_hwAddrOverride_set(swigCPtr, this, value);
    }

    public void setInfo(boolean value) {
        ovpncliJNI.ConfigCommon_info_set(swigCPtr, this, value);
    }

    public void setPlatformVersion(String value) {
        ovpncliJNI.ConfigCommon_platformVersion_set(swigCPtr, this, value);
    }

    public void setPortOverride(String value) {
        ovpncliJNI.ConfigCommon_portOverride_set(swigCPtr, this, value);
    }

    public void setPrivateKeyPassword(String value) {
        ovpncliJNI.ConfigCommon_privateKeyPassword_set(swigCPtr, this, value);
    }

    public void setProxyAllowCleartextAuth(boolean value) {
        ovpncliJNI.ConfigCommon_proxyAllowCleartextAuth_set(swigCPtr, this, value);
    }

    public void setProxyHost(String value) {
        ovpncliJNI.ConfigCommon_proxyHost_set(swigCPtr, this, value);
    }

    public void setProxyPassword(String value) {
        ovpncliJNI.ConfigCommon_proxyPassword_set(swigCPtr, this, value);
    }

    public void setProxyPort(String value) {
        ovpncliJNI.ConfigCommon_proxyPort_set(swigCPtr, this, value);
    }

    public void setProxyUsername(String value) {
        ovpncliJNI.ConfigCommon_proxyUsername_set(swigCPtr, this, value);
    }

    public void setRetryOnAuthFailed(boolean value) {
        ovpncliJNI.ConfigCommon_retryOnAuthFailed_set(swigCPtr, this, value);
    }

    public void setServerOverride(String value) {
        ovpncliJNI.ConfigCommon_serverOverride_set(swigCPtr, this, value);
    }

    public void setSslDebugLevel(int value) {
        ovpncliJNI.ConfigCommon_sslDebugLevel_set(swigCPtr, this, value);
    }

    public void setSsoMethods(String value) {
        ovpncliJNI.ConfigCommon_ssoMethods_set(swigCPtr, this, value);
    }

    public void setSynchronousDnsLookup(boolean value) {
        ovpncliJNI.ConfigCommon_synchronousDnsLookup_set(swigCPtr, this, value);
    }

    public void setTlsCertProfileOverride(String value) {
        ovpncliJNI.ConfigCommon_tlsCertProfileOverride_set(swigCPtr, this, value);
    }

    public void setTlsCipherList(String value) {
        ovpncliJNI.ConfigCommon_tlsCipherList_set(swigCPtr, this, value);
    }

    public void setTlsCiphersuitesList(String value) {
        ovpncliJNI.ConfigCommon_tlsCiphersuitesList_set(swigCPtr, this, value);
    }

    public void setTlsVersionMinOverride(String value) {
        ovpncliJNI.ConfigCommon_tlsVersionMinOverride_set(swigCPtr, this, value);
    }

    public void setTunPersist(boolean value) {
        ovpncliJNI.ConfigCommon_tunPersist_set(swigCPtr, this, value);
    }

    public void setWintun(boolean value) {
        ovpncliJNI.ConfigCommon_wintun_set(swigCPtr, this, value);
    }
}
