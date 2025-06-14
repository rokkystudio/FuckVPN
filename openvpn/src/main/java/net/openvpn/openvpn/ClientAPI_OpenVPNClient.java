package net.openvpn.openvpn;

public class ClientAPI_OpenVPNClient extends ClientAPI_TunBuilderBase
{
    private transient long swigCPtr;

    public ClientAPI_OpenVPNClient() {
        this(ovpncliJNI.new_ClientAPI_OpenVPNClient(), true);
        ovpncliJNI.ClientAPI_OpenVPNClient_director_connect(
            this, this.swigCPtr, true, true
        );
    }

    protected ClientAPI_OpenVPNClient(long cPtr, boolean cMemoryOwn) {
        super(ovpncliJNI.ClientAPI_OpenVPNClient_SWIGUpcast(cPtr), cMemoryOwn);
        this.swigCPtr = cPtr;
    }

    protected static long getCPtr(ClientAPI_OpenVPNClient obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public static int stats_n() {
        return ovpncliJNI.ClientAPI_OpenVPNClient_stats_n();
    }

    public static String stats_name(int i) {
        return ovpncliJNI.ClientAPI_OpenVPNClient_stats_name(i);
    }

    public void acc_event(ClientAPI_AppCustomControlMessageEvent e) {
        ovpncliJNI.ClientAPI_OpenVPNClient_acc_event(
            this.swigCPtr, this,
            ClientAPI_AppCustomControlMessageEvent.getCPtr(e), e
        );
    }

    public void clock_tick() {
        if (getClass() == ClientAPI_OpenVPNClient.class) {
            ovpncliJNI.ClientAPI_OpenVPNClient_clock_tick(
                this.swigCPtr, this
            );
        } else {
            ovpncliJNI.ClientAPI_OpenVPNClient_clock_tickSwigExplicitClientAPI_OpenVPNClient(
                this.swigCPtr, this
            );
        }
    }

    public ClientAPI_Status connect() {
        return new ClientAPI_Status(
            ovpncliJNI.ClientAPI_OpenVPNClient_connect(this.swigCPtr, this), true
        );
    }

    public ClientAPI_ConnectionInfo connection_info() {
        return new ClientAPI_ConnectionInfo(
            ovpncliJNI.ClientAPI_OpenVPNClient_connection_info(this.swigCPtr, this), true
        );
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_OpenVPNClient(swigCPtr);
            }
            swigCPtr = 0;
        }
        super.delete();
    }

    public ClientAPI_EvalConfig eval_config(ClientAPI_Config cfg) {
        return new ClientAPI_EvalConfig(
            ovpncliJNI.ClientAPI_OpenVPNClient_eval_config(
                this.swigCPtr, this,
                ClientAPI_Config.getCPtr(cfg), cfg
            ),
            true
        );
    }

    public void event(ClientAPI_Event e) {
        ovpncliJNI.ClientAPI_OpenVPNClient_event(
            this.swigCPtr, this,
            ClientAPI_Event.getCPtr(e), e
        );
    }

    public void external_pki_cert_request(ClientAPI_ExternalPKICertRequest req) {
        ovpncliJNI.ClientAPI_OpenVPNClient_external_pki_cert_request(
            this.swigCPtr, this,
            ClientAPI_ExternalPKICertRequest.getCPtr(req), req
        );
    }

    public void external_pki_sign_request(ClientAPI_ExternalPKISignRequest req) {
        ovpncliJNI.ClientAPI_OpenVPNClient_external_pki_sign_request(
            this.swigCPtr, this,
            ClientAPI_ExternalPKISignRequest.getCPtr(req), req
        );
    }

    @Override
    public void finalize() {
        try {
            delete();
        } finally {
            try {
                super.finalize();
            } catch (Throwable ignored) {
            }
        }
    }

    public void log(ClientAPI_LogInfo info) {
        ovpncliJNI.ClientAPI_OpenVPNClient_log(
            this.swigCPtr, this,
            ClientAPI_LogInfo.getCPtr(info), info
        );
    }

    public void pause(String reason) {
        ovpncliJNI.ClientAPI_OpenVPNClient_pause(this.swigCPtr, this, reason);
    }

    public boolean pause_on_connection_timeout() {
        return ovpncliJNI.ClientAPI_OpenVPNClient_pause_on_connection_timeout(this.swigCPtr, this);
    }

    public void post_cc_msg(String msg) {
        ovpncliJNI.ClientAPI_OpenVPNClient_post_cc_msg(this.swigCPtr, this, msg);
    }

    public ClientAPI_Status provide_creds(ClientAPI_ProvideCreds creds) {
        return new ClientAPI_Status(
            ovpncliJNI.ClientAPI_OpenVPNClient_provide_creds(
                this.swigCPtr, this,
                ClientAPI_ProvideCreds.getCPtr(creds), creds
            ),
            true
        );
    }

    public void reconnect(int delay) {
        ovpncliJNI.ClientAPI_OpenVPNClient_reconnect(this.swigCPtr, this, delay);
    }

    public void remote_override(ClientAPI_RemoteOverride override) {
        if (getClass() == ClientAPI_OpenVPNClient.class) {
            ovpncliJNI.ClientAPI_OpenVPNClient_remote_override(
                this.swigCPtr, this,
                ClientAPI_RemoteOverride.getCPtr(override), override
            );
        } else {
            ovpncliJNI.ClientAPI_OpenVPNClient_remote_overrideSwigExplicitClientAPI_OpenVPNClient(
                this.swigCPtr, this,
                ClientAPI_RemoteOverride.getCPtr(override), override
            );
        }
    }

    public boolean remote_override_enabled() {
        return getClass() == ClientAPI_OpenVPNClient.class
                ? ovpncliJNI.ClientAPI_OpenVPNClient_remote_override_enabled(
                        this.swigCPtr, this)
                : ovpncliJNI.ClientAPI_OpenVPNClient_remote_override_enabledSwigExplicitClientAPI_OpenVPNClient(
                        this.swigCPtr, this);
    }

    public void resume() {
        ovpncliJNI.ClientAPI_OpenVPNClient_resume(this.swigCPtr, this);
    }

    public void send_app_control_channel_msg(String key, String value) {
        ovpncliJNI.ClientAPI_OpenVPNClient_send_app_control_channel_msg(
            this.swigCPtr, this, key, value
        );
    }

    public boolean session_token(ClientAPI_SessionToken token) {
        return ovpncliJNI.ClientAPI_OpenVPNClient_session_token(
            this.swigCPtr, this,
            ClientAPI_SessionToken.getCPtr(token), token
        );
    }

    public boolean socket_protect(int fd, String addr, boolean ipv6) {
        return getClass() == ClientAPI_OpenVPNClient.class
                ? ovpncliJNI.ClientAPI_OpenVPNClient_socket_protect(
                        this.swigCPtr, this, fd, addr, ipv6)
                : ovpncliJNI.ClientAPI_OpenVPNClient_socket_protectSwigExplicitClientAPI_OpenVPNClient(
                        this.swigCPtr, this, fd, addr, ipv6);
    }

    public void start_cert_check(String subject, String issuer) {
        ovpncliJNI.ClientAPI_OpenVPNClient_start_cert_check__SWIG_1(
                this.swigCPtr, this, subject, issuer
        );
    }

    public void start_cert_check(String subject, String issuer,
                                 OptionalStringPointer chain) {
        ovpncliJNI.ClientAPI_OpenVPNClient_start_cert_check__SWIG_0(
                this.swigCPtr, this, subject, issuer,
                OptionalStringPointer.getCPtr(chain)
        );
    }

    public void start_cert_check_epki(String subject,
                                      OptionalStringPointer chain) {
        ovpncliJNI.ClientAPI_OpenVPNClient_start_cert_check_epki(
                this.swigCPtr, this, subject,
                OptionalStringPointer.getCPtr(chain)
        );
    }

    public ClientAPI_LLVector stats_bundle() {
        return new ClientAPI_LLVector(
                ovpncliJNI.ClientAPI_OpenVPNClient_stats_bundle(this.swigCPtr, this), true
        );
    }

    public long stats_value(int i) {
        return ovpncliJNI.ClientAPI_OpenVPNClient_stats_value(this.swigCPtr, this, i);
    }

    public void stop() {
        ovpncliJNI.ClientAPI_OpenVPNClient_stop(this.swigCPtr, this);
    }

    protected void swigDirectorDisconnect() {
        this.swigCMemOwn = false;
        delete();
    }

    public void swigReleaseOwnership() {
        this.swigCMemOwn = false;
        ovpncliJNI.ClientAPI_OpenVPNClient_change_ownership(
                this, this.swigCPtr, false
        );
    }

    public void swigTakeOwnership() {
        this.swigCMemOwn = true;
        ovpncliJNI.ClientAPI_OpenVPNClient_change_ownership(
                this, this.swigCPtr, true
        );
    }

    public ClientAPI_TransportStats transport_stats() {
        return new ClientAPI_TransportStats(
                ovpncliJNI.ClientAPI_OpenVPNClient_transport_stats(this.swigCPtr, this), true
        );
    }

    public ClientAPI_InterfaceStats tun_stats() {
        return new ClientAPI_InterfaceStats(
                ovpncliJNI.ClientAPI_OpenVPNClient_tun_stats(this.swigCPtr, this), true
        );
    }
}
