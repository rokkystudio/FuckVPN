package net.openvpn.openvpn;

public class OpenVPNClientThread extends ClientAPI_OpenVPNClient implements Runnable
{
    private int bytesInIndex = -1;
    private int bytesOutIndex = -1;
    private boolean connectCalled = false;
    private ClientAPI_Status connectStatus;
    private EventReceiver parent;
    private Thread thread;
    private TunBuilder tunBuilder;

    public static class ConnectCalledTwice extends RuntimeException {}

    public interface EventReceiver {
        void acc_event(ClientAPI_AppCustomControlMessageEvent event);
        void done(ClientAPI_Status status);
        void event(ClientAPI_Event event);
        void external_pki_cert_request(ClientAPI_ExternalPKICertRequest request);
        void external_pki_sign_request(ClientAPI_ExternalPKISignRequest request);
        void log(ClientAPI_LogInfo log);
        boolean pause_on_connection_timeout();
        boolean socket_protect(int socket);
        TunBuilder tun_builder_new();
    }

    public interface TunBuilder {
        boolean tun_builder_add_address(String ip, int prefixLength, String gateway, boolean ipv6, boolean net30);
        boolean tun_builder_add_route(String network, int prefixLength, boolean ipv6);
        int tun_builder_establish();
        boolean tun_builder_exclude_route(String network, int prefixLength, boolean ipv6);
        ClientAPI_StringVec tun_builder_get_local_networks(boolean ipv6);
        boolean tun_builder_reroute_gw(boolean ipv4, boolean ipv6, long flags);
        boolean tun_builder_set_allow_family(int family, boolean allow);
        boolean tun_builder_set_dns_options(DnsOptions dnsOptions);
        boolean tun_builder_set_mtu(int mtu);
        boolean tun_builder_set_remote_address(String address, boolean ipv6);
        boolean tun_builder_set_session_name(String name);
        void tun_builder_teardown(boolean disconnect);
    }

    public OpenVPNClientThread() {
        int statsCount = ClientAPI_OpenVPNClient.stats_n();
        for (int i = 0; i < statsCount; i++) {
            String name = ClientAPI_OpenVPNClient.stats_name(i);
            if ("BYTES_IN".equals(name)) bytesInIndex = i;
            if ("BYTES_OUT".equals(name)) bytesOutIndex = i;
        }
    }

    private void callDone(ClientAPI_Status status) {
        EventReceiver receiver = finalizeThread(status);
        if (receiver != null) {
            receiver.done(connectStatus);
        }
    }

    private synchronized EventReceiver finalizeThread(ClientAPI_Status status) {
        EventReceiver receiver = parent;
        if (receiver != null) {
            connectStatus = status;
            parent = null;
            tunBuilder = null;
            thread = null;
        }
        return receiver;
    }

    public void acc_event(ClientAPI_AppCustomControlMessageEvent event) {
        if (parent != null) parent.acc_event(event);
    }

    public long bytes_in() {
        return super.stats_value(bytesInIndex);
    }

    public long bytes_out() {
        return super.stats_value(bytesOutIndex);
    }

    public void connect(EventReceiver receiver) {
        if (!connectCalled) {
            connectCalled = true;
            parent = receiver;
            connectStatus = null;
            thread = new Thread(this, "OpenVPNClientThread");
            thread.start();
        } else {
            throw new ConnectCalledTwice();
        }
    }

    public void event(ClientAPI_Event event) {
        if (parent != null) parent.event(event);
    }

    public void external_pki_cert_request(ClientAPI_ExternalPKICertRequest request) {
        if (parent != null) parent.external_pki_cert_request(request);
    }

    public void external_pki_sign_request(ClientAPI_ExternalPKISignRequest request) {
        if (parent != null) parent.external_pki_sign_request(request);
    }

    public void log(ClientAPI_LogInfo log) {
        if (parent != null) parent.log(log);
    }

    public boolean pause_on_connection_timeout() {
        return parent != null && parent.pause_on_connection_timeout();
    }

    @Override
    public void run() {
        callDone(super.connect());
    }

    public boolean socket_protect(int socket, String name, boolean isUdp) {
        return parent != null && parent.socket_protect(socket);
    }

    public boolean tun_builder_add_address(String ip, int prefix, String gateway, boolean ipv6, boolean net30) {
        return tunBuilder != null && tunBuilder.tun_builder_add_address(ip, prefix, gateway, ipv6, net30);
    }

    public boolean tun_builder_add_route(String network, int prefix, int metric, boolean ipv6) {
        return tunBuilder != null && tunBuilder.tun_builder_add_route(network, prefix, ipv6);
    }

    public int tun_builder_establish() {
        return tunBuilder != null ? tunBuilder.tun_builder_establish() : -1;
    }

    public boolean tun_builder_exclude_route(String network, int prefix, int metric, boolean ipv6) {
        return tunBuilder != null && tunBuilder.tun_builder_exclude_route(network, prefix, ipv6);
    }

    public ClientAPI_StringVec tun_builder_get_local_networks(boolean ipv6) {
        return tunBuilder != null ? tunBuilder.tun_builder_get_local_networks(ipv6) : null;
    }

    public boolean tun_builder_new() {
        if (parent == null) return false;
        tunBuilder = parent.tun_builder_new();
        return tunBuilder != null;
    }

    public boolean tun_builder_reroute_gw(boolean ipv4, boolean ipv6, long flags) {
        return tunBuilder != null && tunBuilder.tun_builder_reroute_gw(ipv4, ipv6, flags);
    }

    public boolean tun_builder_set_allow_family(int family, boolean allow) {
        return tunBuilder != null && tunBuilder.tun_builder_set_allow_family(family, allow);
    }

    public boolean tun_builder_set_dns_options(DnsOptions options) {
        return tunBuilder != null && tunBuilder.tun_builder_set_dns_options(options);
    }

    public boolean tun_builder_set_mtu(int mtu) {
        return tunBuilder != null && tunBuilder.tun_builder_set_mtu(mtu);
    }

    public boolean tun_builder_set_remote_address(String address, boolean ipv6) {
        return tunBuilder != null && tunBuilder.tun_builder_set_remote_address(address, ipv6);
    }

    public boolean tun_builder_set_session_name(String name) {
        return tunBuilder != null && tunBuilder.tun_builder_set_session_name(name);
    }

    public void tun_builder_teardown(boolean disconnect) {
        if (tunBuilder != null) tunBuilder.tun_builder_teardown(disconnect);
    }
}
