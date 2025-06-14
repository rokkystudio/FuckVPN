package net.openvpn.openvpn;

public class OpenVPNClientHelperWrapper
{
    private static ClientAPI_OpenVPNClientHelper instance;

    private static ClientAPI_OpenVPNClientHelper getInstance() {
        if (instance == null) {
            instance = new ClientAPI_OpenVPNClientHelper();
        }
        return instance;
    }

    public static String copyright() {
        return ClientAPI_OpenVPNClientHelper.copyright();
    }

    public static String crypto_self_test() {
        return getInstance().crypto_self_test();
    }

    public static ClientAPI_EvalConfig eval_config(ClientAPI_Config config) {
        return getInstance().eval_config(config);
    }

    public static int max_profile_size() {
        return ClientAPI_OpenVPNClientHelper.max_profile_size();
    }

    public static ClientAPI_MergeConfig merge_config(String configText, boolean partial) {
        return getInstance().merge_config(configText, partial);
    }

    public static ClientAPI_MergeConfig merge_config_string(String configText) {
        return getInstance().merge_config_string(configText);
    }

    public static boolean parse_dynamic_challenge(String challenge, ClientAPI_DynamicChallenge out) {
        return ClientAPI_OpenVPNClientHelper.parse_dynamic_challenge(challenge, out);
    }

    public static String platform() {
        return ClientAPI_OpenVPNClientHelper.platform();
    }
}
