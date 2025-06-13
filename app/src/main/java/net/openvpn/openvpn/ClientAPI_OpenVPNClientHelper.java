package net.openvpn.openvpn;

public class ClientAPI_OpenVPNClientHelper
{
    protected transient boolean swigCMemOwn;
    private transient long swigCPtr;

    public ClientAPI_OpenVPNClientHelper() {
        this(ovpncliJNI.new_ClientAPI_OpenVPNClientHelper(), true);
    }

    protected ClientAPI_OpenVPNClientHelper(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(ClientAPI_OpenVPNClientHelper obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_ClientAPI_OpenVPNClientHelper(swigCPtr);
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

    public String crypto_self_test() {
        return ovpncliJNI.ClientAPI_OpenVPNClientHelper_crypto_self_test(
            swigCPtr, this
        );
    }

    public ClientAPI_EvalConfig eval_config(ClientAPI_Config config) {
        return new ClientAPI_EvalConfig(
            ovpncliJNI.ClientAPI_OpenVPNClientHelper_eval_config(
                swigCPtr, this,
                ClientAPI_Config.getCPtr(config), config
            ), true
        );
    }

    public ClientAPI_MergeConfig merge_config(String profilePath, boolean partial) {
        return new ClientAPI_MergeConfig(
            ovpncliJNI.ClientAPI_OpenVPNClientHelper_merge_config(
                swigCPtr, this,
                profilePath, partial
            ), true
        );
    }

    public ClientAPI_MergeConfig merge_config_string(String configStr) {
        return new ClientAPI_MergeConfig(
            ovpncliJNI.ClientAPI_OpenVPNClientHelper_merge_config_string(
                swigCPtr, this,
                configStr
            ), true
        );
    }

    public static boolean parse_dynamic_challenge(String json, ClientAPI_DynamicChallenge challenge) {
        return ovpncliJNI.ClientAPI_OpenVPNClientHelper_parse_dynamic_challenge(
            json,
            ClientAPI_DynamicChallenge.getCPtr(challenge),
            challenge
        );
    }

    public static int max_profile_size() {
        return ovpncliJNI.ClientAPI_OpenVPNClientHelper_max_profile_size();
    }

    public static String platform() {
        return ovpncliJNI.ClientAPI_OpenVPNClientHelper_platform();
    }

    public static String copyright() {
        return ovpncliJNI.ClientAPI_OpenVPNClientHelper_copyright();
    }
}
