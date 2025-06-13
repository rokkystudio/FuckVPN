package net.openvpn.openvpn;

public class ClientAPI_TunBuilderBase
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_TunBuilderBase() {
        this(ovpncliJNI.new_ClientAPI_TunBuilderBase(), true);
    }

    protected ClientAPI_TunBuilderBase(long cPtr, boolean memoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = memoryOwn;
    }

    protected static long getCPtr(ClientAPI_TunBuilderBase obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_TunBuilderBase(swigCPtr);
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

    public boolean tun_builder_add_address(
            String addr, int prefixLength, String gateway, boolean ipv6, boolean net30) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_address(
                swigCPtr, this, addr, prefixLength, gateway, ipv6, net30
        );
    }

    public boolean tun_builder_add_proxy_bypass(String addr) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_proxy_bypass(swigCPtr, this, addr);
    }

    public boolean tun_builder_add_route(String addr, int prefixLength, int metric, boolean ipv6) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_route(
                swigCPtr, this, addr, prefixLength, metric, ipv6
        );
    }

    public boolean tun_builder_add_wins_server(String addr) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_add_wins_server(swigCPtr, this, addr);
    }

    public int tun_builder_establish() {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_establish(swigCPtr, this);
    }

    public void tun_builder_establish_lite() {
        ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_establish_lite(swigCPtr, this);
    }

    public boolean tun_builder_exclude_route(String addr, int prefixLength, int metric, boolean ipv6) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_exclude_route(
                swigCPtr, this, addr, prefixLength, metric, ipv6
        );
    }

    public ClientAPI_StringVec tun_builder_get_local_networks(boolean ipv6) {
        return new ClientAPI_StringVec(
                ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_get_local_networks(swigCPtr, this, ipv6),
                true
        );
    }

    public boolean tun_builder_new() {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_new(swigCPtr, this);
    }

    public boolean tun_builder_persist() {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_persist(swigCPtr, this);
    }

    public boolean tun_builder_reroute_gw(boolean ipv4, boolean ipv6, long flags) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_reroute_gw(
                swigCPtr, this, ipv4, ipv6, flags
        );
    }

    public boolean tun_builder_set_allow_family(int family, boolean allow) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_allow_family(swigCPtr, this, family, allow);
    }

    public boolean tun_builder_set_allow_local_dns(boolean allow) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_allow_local_dns(swigCPtr, this, allow);
    }

    public boolean tun_builder_set_dns_options(DnsOptions options) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_dns_options(
                swigCPtr, this, DnsOptions.getCPtr(options), options
        );
    }

    public boolean tun_builder_set_layer(int layer) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_layer(swigCPtr, this, layer);
    }

    public boolean tun_builder_set_mtu(int mtu) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_mtu(swigCPtr, this, mtu);
    }

    public boolean tun_builder_set_proxy_auto_config_url(String url) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_proxy_auto_config_url(swigCPtr, this, url);
    }

    public boolean tun_builder_set_proxy_http(String host, int port) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_proxy_http(swigCPtr, this, host, port);
    }

    public boolean tun_builder_set_proxy_https(String host, int port) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_proxy_https(swigCPtr, this, host, port);
    }

    public boolean tun_builder_set_remote_address(String addr, boolean ipv6) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_remote_address(swigCPtr, this, addr, ipv6);
    }

    public boolean tun_builder_set_route_metric_default(int metric) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_route_metric_default(swigCPtr, this, metric);
    }

    public boolean tun_builder_set_session_name(String name) {
        return ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_set_session_name(swigCPtr, this, name);
    }

    public void tun_builder_teardown(boolean disconnect) {
        ovpncliJNI.ClientAPI_TunBuilderBase_tun_builder_teardown(swigCPtr, this, disconnect);
    }
}
