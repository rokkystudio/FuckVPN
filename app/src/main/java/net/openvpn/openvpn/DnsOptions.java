package net.openvpn.openvpn;

public class DnsOptions {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public DnsOptions() {
        this(ovpncliJNI.new_DnsOptions(), true);
    }

    protected DnsOptions(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(DnsOptions obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_DnsOptions(swigCPtr);
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
    public boolean getFromDhcpOptions() {
        return ovpncliJNI.DnsOptions_from_dhcp_options_get(swigCPtr, this);
    }

    public DnsOptions_DomainsList getSearchDomains() {
        long ptr = ovpncliJNI.DnsOptions_search_domains_get(swigCPtr, this);
        return ptr == 0 ? null : new DnsOptions_DomainsList(ptr, false);
    }

    public DnsOptions_ServersMap getServers() {
        long ptr = ovpncliJNI.DnsOptions_servers_get(swigCPtr, this);
        return ptr == 0 ? null : new DnsOptions_ServersMap(ptr, false);
    }

    // === Setters ===
    public void setFromDhcpOptions(boolean value) {
        ovpncliJNI.DnsOptions_from_dhcp_options_set(swigCPtr, this, value);
    }

    public void setSearchDomains(DnsOptions_DomainsList list) {
        ovpncliJNI.DnsOptions_search_domains_set(swigCPtr, this, DnsOptions_DomainsList.getCPtr(list), list);
    }

    public void setServers(DnsOptions_ServersMap map) {
        ovpncliJNI.DnsOptions_servers_set(swigCPtr, this, DnsOptions_ServersMap.getCPtr(map), map);
    }

    public String to_string() {
        return ovpncliJNI.DnsOptions_to_string(swigCPtr, this);
    }
}
