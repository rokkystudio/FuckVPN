package net.openvpn.openvpn;

public class ovpncliJNI
{
    static {
        swig_module_init();
    }

    public static native String ClientAPI_AppCustomControlMessageEvent_payload_get(
            long j, ClientAPI_AppCustomControlMessageEvent clientAPI_AppCustomControlMessageEvent);

    public static native void ClientAPI_AppCustomControlMessageEvent_payload_set(
            long j, ClientAPI_AppCustomControlMessageEvent clientAPI_AppCustomControlMessageEvent, String str);

    public static native String ClientAPI_AppCustomControlMessageEvent_protocol_get(
            long j, ClientAPI_AppCustomControlMessageEvent clientAPI_AppCustomControlMessageEvent);

    public static native void ClientAPI_AppCustomControlMessageEvent_protocol_set(
            long j, ClientAPI_AppCustomControlMessageEvent clientAPI_AppCustomControlMessageEvent, String str);

    public static native long ClientAPI_Config_SWIGUpcast(long j);

    public static native String ClientAPI_Config_allowUnusedAddrFamilies_get(
            long j, ClientAPI_Config clientAPI_Config);

    public static native void ClientAPI_Config_allowUnusedAddrFamilies_set(
            long j, ClientAPI_Config clientAPI_Config, String str);

    public static native String ClientAPI_Config_compressionMode_get(
            long j, ClientAPI_Config clientAPI_Config);

    public static native void ClientAPI_Config_compressionMode_set(
            long j, ClientAPI_Config clientAPI_Config, String str);

    public static native long ClientAPI_Config_contentList_get(
            long j, ClientAPI_Config clientAPI_Config);

    public static native void ClientAPI_Config_contentList_set(
            long j, ClientAPI_Config clientAPI_Config, long j2);

    public static native String ClientAPI_Config_content_get(
            long j, ClientAPI_Config clientAPI_Config);

    public static native void ClientAPI_Config_content_set(
            long j, ClientAPI_Config clientAPI_Config, String str);

    public static native String ClientAPI_Config_externalPkiAlias_get(
            long j, ClientAPI_Config clientAPI_Config);

    public static native void ClientAPI_Config_externalPkiAlias_set(
            long j, ClientAPI_Config clientAPI_Config, String str);

    public static native long ClientAPI_Config_peerInfo_get(
            long j, ClientAPI_Config clientAPI_Config);

    public static native void ClientAPI_Config_peerInfo_set(
            long j, ClientAPI_Config clientAPI_Config, long j2);

    public static native String ClientAPI_Config_protoOverride_get(
            long j, ClientAPI_Config clientAPI_Config);

    public static native void ClientAPI_Config_protoOverride_set(
            long j, ClientAPI_Config clientAPI_Config, String str);

    public static native int ClientAPI_Config_protoVersionOverride_get(
            long j, ClientAPI_Config clientAPI_Config);

    public static native void ClientAPI_Config_protoVersionOverride_set(
            long j, ClientAPI_Config clientAPI_Config, int i);

    public static native String ClientAPI_ConnectionInfo_clientIp_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_clientIp_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native boolean ClientAPI_ConnectionInfo_defined_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_defined_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, boolean z);

    public static native String ClientAPI_ConnectionInfo_gw4_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_gw4_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_gw6_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_gw6_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_serverHost_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_serverHost_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_serverIp_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_serverIp_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_serverPort_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_serverPort_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_serverProto_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_serverProto_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_tunName_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_tunName_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_user_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_user_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_vpnIp4_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_vpnIp4_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_vpnIp6_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_vpnIp6_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_ConnectionInfo_vpnMtu_get(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo);

    public static native void ClientAPI_ConnectionInfo_vpnMtu_set(
            long j, ClientAPI_ConnectionInfo clientAPI_ConnectionInfo, String str);

    public static native String ClientAPI_DynamicChallenge_challenge_get(
            long j, ClientAPI_DynamicChallenge clientAPI_DynamicChallenge);

    public static native void ClientAPI_DynamicChallenge_challenge_set(
            long j, ClientAPI_DynamicChallenge clientAPI_DynamicChallenge, String str);

    public static native boolean ClientAPI_DynamicChallenge_echo_get(
            long j, ClientAPI_DynamicChallenge clientAPI_DynamicChallenge);

    public static native void ClientAPI_DynamicChallenge_echo_set(
            long j, ClientAPI_DynamicChallenge clientAPI_DynamicChallenge, boolean z);

    public static native boolean ClientAPI_DynamicChallenge_responseRequired_get(
            long j, ClientAPI_DynamicChallenge clientAPI_DynamicChallenge);

    public static native void ClientAPI_DynamicChallenge_responseRequired_set(
            long j, ClientAPI_DynamicChallenge clientAPI_DynamicChallenge, boolean z);

    public static native String ClientAPI_DynamicChallenge_stateID_get(
            long j, ClientAPI_DynamicChallenge clientAPI_DynamicChallenge);

    public static native void ClientAPI_DynamicChallenge_stateID_set(
            long j, ClientAPI_DynamicChallenge clientAPI_DynamicChallenge, String str);

    public static native boolean ClientAPI_EvalConfig_allowPasswordSave_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_allowPasswordSave_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, boolean z);

    public static native boolean ClientAPI_EvalConfig_autologin_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_autologin_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, boolean z);

    public static native boolean ClientAPI_EvalConfig_dcoCompatible_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_dcoCompatible_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, boolean z);

    public static native String ClientAPI_EvalConfig_dcoIncompatibilityReason_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_dcoIncompatibilityReason_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native boolean ClientAPI_EvalConfig_error_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_error_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, boolean z);

    public static native boolean ClientAPI_EvalConfig_externalPki_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_externalPki_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, boolean z);

    public static native String ClientAPI_EvalConfig_friendlyName_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_friendlyName_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native String ClientAPI_EvalConfig_message_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_message_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native boolean ClientAPI_EvalConfig_privateKeyPasswordRequired_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_privateKeyPasswordRequired_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, boolean z);

    public static native String ClientAPI_EvalConfig_profileName_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_profileName_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native String ClientAPI_EvalConfig_remoteHost_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_remoteHost_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native String ClientAPI_EvalConfig_remotePort_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_remotePort_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native String ClientAPI_EvalConfig_remoteProto_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_remoteProto_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native long ClientAPI_EvalConfig_serverList_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_serverList_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig,
            long j2, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector);

    public static native boolean ClientAPI_EvalConfig_staticChallengeEcho_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_staticChallengeEcho_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, boolean z);

    public static native String ClientAPI_EvalConfig_staticChallenge_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_staticChallenge_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native String ClientAPI_EvalConfig_userlockedUsername_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_userlockedUsername_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native String ClientAPI_EvalConfig_vpnCa_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_vpnCa_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native String ClientAPI_EvalConfig_windowsDriver_get(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig);

    public static native void ClientAPI_EvalConfig_windowsDriver_set(
            long j, ClientAPI_EvalConfig clientAPI_EvalConfig, String str);

    public static native boolean ClientAPI_Event_error_get(
            long j, ClientAPI_Event clientAPI_Event);

    public static native void ClientAPI_Event_error_set(
            long j, ClientAPI_Event clientAPI_Event, boolean z);

    public static native boolean ClientAPI_Event_fatal_get(
            long j, ClientAPI_Event clientAPI_Event);

    public static native void ClientAPI_Event_fatal_set(
            long j, ClientAPI_Event clientAPI_Event, boolean z);

    public static native String ClientAPI_Event_info_get(
            long j, ClientAPI_Event clientAPI_Event);

    public static native void ClientAPI_Event_info_set(
            long j, ClientAPI_Event clientAPI_Event, String str);

    public static native String ClientAPI_Event_name_get(
            long j, ClientAPI_Event clientAPI_Event);

    public static native void ClientAPI_Event_name_set(
            long j, ClientAPI_Event clientAPI_Event, String str);

    public static native boolean ClientAPI_ExternalPKIBase_sign(
            long j, ClientAPI_ExternalPKIBase clientAPI_ExternalPKIBase,
            String str, String str2, long j2, String str3, String str4, String str5);

    public static native long ClientAPI_ExternalPKICertRequest_SWIGUpcast(long j);

    public static native String ClientAPI_ExternalPKICertRequest_cert_get(
            long j, ClientAPI_ExternalPKICertRequest clientAPI_ExternalPKICertRequest);

    public static native void ClientAPI_ExternalPKICertRequest_cert_set(
            long j, ClientAPI_ExternalPKICertRequest clientAPI_ExternalPKICertRequest, String str);

    public static native String ClientAPI_ExternalPKICertRequest_supportingChain_get(
            long j, ClientAPI_ExternalPKICertRequest clientAPI_ExternalPKICertRequest);

    public static native void ClientAPI_ExternalPKICertRequest_supportingChain_set(
            long j, ClientAPI_ExternalPKICertRequest clientAPI_ExternalPKICertRequest, String str);

    public static native String ClientAPI_ExternalPKIRequestBase_alias_get(
            long j, ClientAPI_ExternalPKIRequestBase clientAPI_ExternalPKIRequestBase);

    public static native void ClientAPI_ExternalPKIRequestBase_alias_set(
            long j, ClientAPI_ExternalPKIRequestBase clientAPI_ExternalPKIRequestBase, String str);

    public static native String ClientAPI_ExternalPKIRequestBase_errorText_get(
            long j, ClientAPI_ExternalPKIRequestBase clientAPI_ExternalPKIRequestBase);

    public static native void ClientAPI_ExternalPKIRequestBase_errorText_set(
            long j, ClientAPI_ExternalPKIRequestBase clientAPI_ExternalPKIRequestBase, String str);

    public static native boolean ClientAPI_ExternalPKIRequestBase_error_get(
            long j, ClientAPI_ExternalPKIRequestBase clientAPI_ExternalPKIRequestBase);

    public static native void ClientAPI_ExternalPKIRequestBase_error_set(
            long j, ClientAPI_ExternalPKIRequestBase clientAPI_ExternalPKIRequestBase, boolean z);

    public static native boolean ClientAPI_ExternalPKIRequestBase_invalidAlias_get(
            long j, ClientAPI_ExternalPKIRequestBase clientAPI_ExternalPKIRequestBase);

    public static native void ClientAPI_ExternalPKIRequestBase_invalidAlias_set(
            long j, ClientAPI_ExternalPKIRequestBase clientAPI_ExternalPKIRequestBase, boolean z);

    public static native long ClientAPI_ExternalPKISignRequest_SWIGUpcast(long j);

    public static native String ClientAPI_ExternalPKISignRequest_algorithm_get(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest);

    public static native void ClientAPI_ExternalPKISignRequest_algorithm_set(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest, String str);

    public static native String ClientAPI_ExternalPKISignRequest_data_get(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest);

    public static native void ClientAPI_ExternalPKISignRequest_data_set(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest, String str);

    public static native String ClientAPI_ExternalPKISignRequest_hashalg_get(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest);

    public static native void ClientAPI_ExternalPKISignRequest_hashalg_set(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest, String str);

    public static native String ClientAPI_ExternalPKISignRequest_saltlen_get(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest);

    public static native void ClientAPI_ExternalPKISignRequest_saltlen_set(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest, String str);

    public static native String ClientAPI_ExternalPKISignRequest_sig_get(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest);

    public static native void ClientAPI_ExternalPKISignRequest_sig_set(
            long j, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest, String str);

    public static native long ClientAPI_InterfaceStats_bytesIn_get(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats);

    public static native void ClientAPI_InterfaceStats_bytesIn_set(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats, long j2);

    public static native long ClientAPI_InterfaceStats_bytesOut_get(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats);

    public static native void ClientAPI_InterfaceStats_bytesOut_set(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats, long j2);

    public static native long ClientAPI_InterfaceStats_errorsIn_get(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats);

    public static native void ClientAPI_InterfaceStats_errorsIn_set(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats, long j2);

    public static native long ClientAPI_InterfaceStats_errorsOut_get(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats);

    public static native void ClientAPI_InterfaceStats_errorsOut_set(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats, long j2);

    public static native long ClientAPI_InterfaceStats_packetsIn_get(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats);

    public static native void ClientAPI_InterfaceStats_packetsIn_set(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats, long j2);

    public static native long ClientAPI_InterfaceStats_packetsOut_get(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats);

    public static native void ClientAPI_InterfaceStats_packetsOut_set(
            long j, ClientAPI_InterfaceStats clientAPI_InterfaceStats, long j2);

    public static native String ClientAPI_KeyValue_key_get(
            long j, ClientAPI_KeyValue clientAPI_KeyValue);

    public static native void ClientAPI_KeyValue_key_set(
            long j, ClientAPI_KeyValue clientAPI_KeyValue, String str);

    public static native String ClientAPI_KeyValue_value_get(
            long j, ClientAPI_KeyValue clientAPI_KeyValue);

    public static native void ClientAPI_KeyValue_value_set(
            long j, ClientAPI_KeyValue clientAPI_KeyValue, String str);

    public static native long ClientAPI_LLVector_capacity(
            long j, ClientAPI_LLVector clientAPI_LLVector);

    public static native void ClientAPI_LLVector_clear(
            long j, ClientAPI_LLVector clientAPI_LLVector);

    public static native void ClientAPI_LLVector_doAdd__SWIG_0(
            long j, ClientAPI_LLVector clientAPI_LLVector, long j2);

    public static native void ClientAPI_LLVector_doAdd__SWIG_1(
            long j, ClientAPI_LLVector clientAPI_LLVector, int i, long j2);

    public static native long ClientAPI_LLVector_doGet(
            long j, ClientAPI_LLVector clientAPI_LLVector, int i);

    public static native long ClientAPI_LLVector_doRemove(
            long j, ClientAPI_LLVector clientAPI_LLVector, int i);

    public static native void ClientAPI_LLVector_doRemoveRange(
            long j, ClientAPI_LLVector clientAPI_LLVector, int i, int i2);

    public static native long ClientAPI_LLVector_doSet(
            long j, ClientAPI_LLVector clientAPI_LLVector, int i, long j2);

    public static native int ClientAPI_LLVector_doSize(
            long j, ClientAPI_LLVector clientAPI_LLVector);

    public static native boolean ClientAPI_LLVector_isEmpty(
            long j, ClientAPI_LLVector clientAPI_LLVector);

    public static native void ClientAPI_LLVector_reserve(
            long j, ClientAPI_LLVector clientAPI_LLVector, long j2);

    public static native String ClientAPI_LogInfo_text_get(
            long j, ClientAPI_LogInfo clientAPI_LogInfo);

    public static native void ClientAPI_LogInfo_text_set(
            long j, ClientAPI_LogInfo clientAPI_LogInfo, String str);

    public static native String ClientAPI_MergeConfig_basename_get(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig);

    public static native void ClientAPI_MergeConfig_basename_set(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig, String str);

    public static native String ClientAPI_MergeConfig_errorText_get(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig);

    public static native void ClientAPI_MergeConfig_errorText_set(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig, String str);

    public static native String ClientAPI_MergeConfig_profileContent_get(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig);

    public static native void ClientAPI_MergeConfig_profileContent_set(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig, String str);

    public static native long ClientAPI_MergeConfig_refPathList_get(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig);

    public static native void ClientAPI_MergeConfig_refPathList_set(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig,
            long j2, ClientAPI_StringVec clientAPI_StringVec);

    public static native String ClientAPI_MergeConfig_status_get(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig);

    public static native void ClientAPI_MergeConfig_status_set(
            long j, ClientAPI_MergeConfig clientAPI_MergeConfig, String str);

    public static native String ClientAPI_OpenVPNClientHelper_copyright();

    public static native String ClientAPI_OpenVPNClientHelper_crypto_self_test(
            long j, ClientAPI_OpenVPNClientHelper clientAPI_OpenVPNClientHelper);

    public static native long ClientAPI_OpenVPNClientHelper_eval_config(
            long j, ClientAPI_OpenVPNClientHelper clientAPI_OpenVPNClientHelper,
            long j2, ClientAPI_Config clientAPI_Config);

    public static native int ClientAPI_OpenVPNClientHelper_max_profile_size();

    public static native long ClientAPI_OpenVPNClientHelper_merge_config(
            long j, ClientAPI_OpenVPNClientHelper clientAPI_OpenVPNClientHelper, String str, boolean z);

    public static native long ClientAPI_OpenVPNClientHelper_merge_config_string(
            long j, ClientAPI_OpenVPNClientHelper clientAPI_OpenVPNClientHelper, String str);

    public static native boolean ClientAPI_OpenVPNClientHelper_parse_dynamic_challenge(
            String str, long j, ClientAPI_DynamicChallenge clientAPI_DynamicChallenge);

    public static native String ClientAPI_OpenVPNClientHelper_platform();

    public static native long ClientAPI_OpenVPNClient_SWIGUpcast(long j);

    public static native void ClientAPI_OpenVPNClient_acc_event(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_AppCustomControlMessageEvent clientAPI_AppCustomControlMessageEvent);

    public static native void ClientAPI_OpenVPNClient_change_ownership(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, long j, boolean z);

    public static native void ClientAPI_OpenVPNClient_clock_tick(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native void ClientAPI_OpenVPNClient_clock_tickSwigExplicitClientAPI_OpenVPNClient(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native long ClientAPI_OpenVPNClient_connect(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native long ClientAPI_OpenVPNClient_connection_info(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native void ClientAPI_OpenVPNClient_director_connect(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, long j, boolean z, boolean z2);

    public static native long ClientAPI_OpenVPNClient_eval_config(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_Config clientAPI_Config);

    public static native void ClientAPI_OpenVPNClient_event(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_Event clientAPI_Event);

    public static native void ClientAPI_OpenVPNClient_external_pki_cert_request(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_ExternalPKICertRequest clientAPI_ExternalPKICertRequest);

    public static native void ClientAPI_OpenVPNClient_external_pki_sign_request(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_ExternalPKISignRequest clientAPI_ExternalPKISignRequest);

    public static native void ClientAPI_OpenVPNClient_log(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_LogInfo clientAPI_LogInfo);

    public static native void ClientAPI_OpenVPNClient_pause(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str);

    public static native boolean ClientAPI_OpenVPNClient_pause_on_connection_timeout(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native void ClientAPI_OpenVPNClient_post_cc_msg(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str);

    public static native long ClientAPI_OpenVPNClient_provide_creds(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_ProvideCreds clientAPI_ProvideCreds);

    public static native void ClientAPI_OpenVPNClient_reconnect(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, int i);

    public static native void ClientAPI_OpenVPNClient_remote_override(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_RemoteOverride clientAPI_RemoteOverride);

    public static native void ClientAPI_OpenVPNClient_remote_overrideSwigExplicitClientAPI_OpenVPNClient(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_RemoteOverride clientAPI_RemoteOverride);

    public static native boolean ClientAPI_OpenVPNClient_remote_override_enabled(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native boolean ClientAPI_OpenVPNClient_remote_override_enabledSwigExplicitClientAPI_OpenVPNClient(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native void ClientAPI_OpenVPNClient_resume(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native void ClientAPI_OpenVPNClient_send_app_control_channel_msg(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, String str2);

    public static native boolean ClientAPI_OpenVPNClient_session_token(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient,
            long j2, ClientAPI_SessionToken clientAPI_SessionToken);

    public static native boolean ClientAPI_OpenVPNClient_socket_protect(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, int i, String str, boolean z);

    public static native boolean ClientAPI_OpenVPNClient_socket_protectSwigExplicitClientAPI_OpenVPNClient(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, int i, String str, boolean z);

    public static native void ClientAPI_OpenVPNClient_start_cert_check__SWIG_0(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, String str2, long j2);

    public static native void ClientAPI_OpenVPNClient_start_cert_check__SWIG_1(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, String str2);

    public static native void ClientAPI_OpenVPNClient_start_cert_check_epki(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, long j2);

    public static native long ClientAPI_OpenVPNClient_stats_bundle(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native int ClientAPI_OpenVPNClient_stats_n();

    public static native String ClientAPI_OpenVPNClient_stats_name(int i);

    public static native long ClientAPI_OpenVPNClient_stats_value(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, int i);

    public static native void ClientAPI_OpenVPNClient_stop(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native long ClientAPI_OpenVPNClient_transport_stats(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native long ClientAPI_OpenVPNClient_tun_stats(
            long j, ClientAPI_OpenVPNClient clientAPI_OpenVPNClient);

    public static native String ClientAPI_ProvideCreds_dynamicChallengeCookie_get(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds);

    public static native void ClientAPI_ProvideCreds_dynamicChallengeCookie_set(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds, String str);

    public static native String ClientAPI_ProvideCreds_http_proxy_pass_get(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds);

    public static native void ClientAPI_ProvideCreds_http_proxy_pass_set(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds, String str);

    public static native String ClientAPI_ProvideCreds_http_proxy_user_get(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds);

    public static native void ClientAPI_ProvideCreds_http_proxy_user_set(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds, String str);

    public static native String ClientAPI_ProvideCreds_password_get(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds);

    public static native void ClientAPI_ProvideCreds_password_set(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds, String str);

    public static native String ClientAPI_ProvideCreds_response_get(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds);

    public static native void ClientAPI_ProvideCreds_response_set(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds, String str);

    public static native String ClientAPI_ProvideCreds_username_get(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds);

    public static native void ClientAPI_ProvideCreds_username_set(
            long j, ClientAPI_ProvideCreds clientAPI_ProvideCreds, String str);

    public static native String ClientAPI_RemoteOverride_error_get(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride);

    public static native void ClientAPI_RemoteOverride_error_set(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride, String str);

    public static native String ClientAPI_RemoteOverride_host_get(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride);

    public static native void ClientAPI_RemoteOverride_host_set(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride, String str);

    public static native String ClientAPI_RemoteOverride_ip_get(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride);

    public static native void ClientAPI_RemoteOverride_ip_set(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride, String str);

    public static native String ClientAPI_RemoteOverride_port_get(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride);

    public static native void ClientAPI_RemoteOverride_port_set(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride, String str);

    public static native String ClientAPI_RemoteOverride_proto_get(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride);

    public static native void ClientAPI_RemoteOverride_proto_set(
            long j, ClientAPI_RemoteOverride clientAPI_RemoteOverride, String str);

    public static native long ClientAPI_ServerEntryVector_capacity(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector);

    public static native void ClientAPI_ServerEntryVector_clear(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector);

    public static native void ClientAPI_ServerEntryVector_doAdd__SWIG_0(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector,
            long j2, ClientAPI_ServerEntry clientAPI_ServerEntry);

    public static native void ClientAPI_ServerEntryVector_doAdd__SWIG_1(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector,
            int i, long j2, ClientAPI_ServerEntry clientAPI_ServerEntry);

    public static native long ClientAPI_ServerEntryVector_doGet(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector, int i);

    public static native long ClientAPI_ServerEntryVector_doRemove(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector, int i);

    public static native void ClientAPI_ServerEntryVector_doRemoveRange(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector, int i, int i2);

    public static native long ClientAPI_ServerEntryVector_doSet(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector, int i,
            long j2, ClientAPI_ServerEntry clientAPI_ServerEntry);

    public static native int ClientAPI_ServerEntryVector_doSize(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector);

    public static native boolean ClientAPI_ServerEntryVector_isEmpty(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector);

    public static native void ClientAPI_ServerEntryVector_reserve(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector, long j2);

    public static native String ClientAPI_ServerEntry_friendlyName_get(
            long j, ClientAPI_ServerEntry clientAPI_ServerEntry);

    public static native void ClientAPI_ServerEntry_friendlyName_set(
            long j, ClientAPI_ServerEntry clientAPI_ServerEntry, String str);

    public static native String ClientAPI_ServerEntry_server_get(
            long j, ClientAPI_ServerEntry clientAPI_ServerEntry);

    public static native void ClientAPI_ServerEntry_server_set(
            long j, ClientAPI_ServerEntry clientAPI_ServerEntry, String str);

    public static native String ClientAPI_SessionToken_session_id_get(
            long j, ClientAPI_SessionToken clientAPI_SessionToken);

    public static native void ClientAPI_SessionToken_session_id_set(
            long j, ClientAPI_SessionToken clientAPI_SessionToken, String str);

    public static native String ClientAPI_SessionToken_username_get(
            long j, ClientAPI_SessionToken clientAPI_SessionToken);

    public static native void ClientAPI_SessionToken_username_set(
            long j, ClientAPI_SessionToken clientAPI_SessionToken, String str);

    public static native boolean ClientAPI_Status_error_get(
            long j, ClientAPI_Status clientAPI_Status);

    public static native void ClientAPI_Status_error_set(
            long j, ClientAPI_Status clientAPI_Status, boolean z);

    public static native String ClientAPI_Status_message_get(
            long j, ClientAPI_Status clientAPI_Status);

    public static native void ClientAPI_Status_message_set(
            long j, ClientAPI_Status clientAPI_Status, String str);

    public static native String ClientAPI_Status_status_get(
            long j, ClientAPI_Status clientAPI_Status);

    public static native void ClientAPI_Status_status_set(
            long j, ClientAPI_Status clientAPI_Status, String str);

    public static native long ClientAPI_StringVec_capacity(
            long j, ClientAPI_StringVec clientAPI_StringVec);

    public static native void ClientAPI_StringVec_clear(
            long j, ClientAPI_StringVec clientAPI_StringVec);

    public static native void ClientAPI_StringVec_doAdd__SWIG_0(
            long j, ClientAPI_StringVec clientAPI_StringVec, String str);

    public static native void ClientAPI_StringVec_doAdd__SWIG_1(
            long j, ClientAPI_StringVec clientAPI_StringVec, int i, String str);

    public static native String ClientAPI_StringVec_doGet(
            long j, ClientAPI_StringVec clientAPI_StringVec, int i);

    public static native String ClientAPI_StringVec_doRemove(
            long j, ClientAPI_StringVec clientAPI_StringVec, int i);

    public static native void ClientAPI_StringVec_doRemoveRange(
            long j, ClientAPI_StringVec clientAPI_StringVec, int i, int i2);

    public static native String ClientAPI_StringVec_doSet(
            long j, ClientAPI_StringVec clientAPI_StringVec, int i, String str);

    public static native int ClientAPI_StringVec_doSize(
            long j, ClientAPI_StringVec clientAPI_StringVec);

    public static native boolean ClientAPI_StringVec_isEmpty(
            long j, ClientAPI_StringVec clientAPI_StringVec);

    public static native void ClientAPI_StringVec_reserve(
            long j, ClientAPI_StringVec clientAPI_StringVec, long j2);

    public static native long ClientAPI_TransportStats_bytesIn_get(
            long j, ClientAPI_TransportStats clientAPI_TransportStats);

    public static native void ClientAPI_TransportStats_bytesIn_set(
            long j, ClientAPI_TransportStats clientAPI_TransportStats, long j2);

    public static native long ClientAPI_TransportStats_bytesOut_get(
            long j, ClientAPI_TransportStats clientAPI_TransportStats);

    public static native void ClientAPI_TransportStats_bytesOut_set(
            long j, ClientAPI_TransportStats clientAPI_TransportStats, long j2);

    public static native int ClientAPI_TransportStats_lastPacketReceived_get(
            long j, ClientAPI_TransportStats clientAPI_TransportStats);

    public static native void ClientAPI_TransportStats_lastPacketReceived_set(
            long j, ClientAPI_TransportStats clientAPI_TransportStats, int i);

    public static native long ClientAPI_TransportStats_packetsIn_get(
            long j, ClientAPI_TransportStats clientAPI_TransportStats);

    public static native void ClientAPI_TransportStats_packetsIn_set(
            long j, ClientAPI_TransportStats clientAPI_TransportStats, long j2);

    public static native long ClientAPI_TransportStats_packetsOut_get(
            long j, ClientAPI_TransportStats clientAPI_TransportStats);

    public static native void ClientAPI_TransportStats_packetsOut_set(
            long j, ClientAPI_TransportStats clientAPI_TransportStats, long j2);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_add_address(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str,
            int i, String str2, boolean z, boolean z2);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_add_proxy_bypass(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_add_route(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str,
            int i, int i2, boolean z);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_add_wins_server(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str);

    public static native int ClientAPI_TunBuilderBase_tun_builder_establish(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase);

    public static native void ClientAPI_TunBuilderBase_tun_builder_establish_lite(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_exclude_route(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str,
            int i, int i2, boolean z);

    public static native long ClientAPI_TunBuilderBase_tun_builder_get_local_networks(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, boolean z);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_new(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_persist(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_reroute_gw(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, boolean z, boolean z2, long j2);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_allow_family(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, int i, boolean z);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_allow_local_dns(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, boolean z);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_dns_options(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, long j2, DnsOptions dnsOptions);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_layer(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, int i);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_mtu(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, int i);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_proxy_auto_config_url(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_proxy_http(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str, int i);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_proxy_https(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str, int i);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_remote_address(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str, boolean z);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_route_metric_default(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, int i);

    public static native boolean ClientAPI_TunBuilderBase_tun_builder_set_session_name(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, String str);

    public static native void ClientAPI_TunBuilderBase_tun_builder_teardown(
            long j, ClientAPI_TunBuilderBase clientAPI_TunBuilderBase, boolean z);

    public static native boolean ConfigCommon_allowLocalDnsResolvers_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_allowLocalDnsResolvers_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_allowLocalLanAccess_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_allowLocalLanAccess_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_altProxy_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_altProxy_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native String ConfigCommon_appCustomProtocols_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_appCustomProtocols_set(
            long j, ConfigCommon configCommon, String str);

    public static native boolean ConfigCommon_autologinSessions_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_autologinSessions_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native long ConfigCommon_clockTickMS_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_clockTickMS_set(
            long j, ConfigCommon configCommon, long j2);

    public static native int ConfigCommon_connTimeout_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_connTimeout_set(
            long j, ConfigCommon configCommon, int i);

    public static native boolean ConfigCommon_dco_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_dco_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native int ConfigCommon_defaultKeyDirection_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_defaultKeyDirection_set(
            long j, ConfigCommon configCommon, int i);

    public static native boolean ConfigCommon_dhcpSearchDomainsAsSplitDomains_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_dhcpSearchDomainsAsSplitDomains_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_disableClientCert_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_disableClientCert_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_echo_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_echo_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_enableLegacyAlgorithms_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_enableLegacyAlgorithms_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_enableNonPreferredDCAlgorithms_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_enableNonPreferredDCAlgorithms_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_enableRouteEmulation_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_enableRouteEmulation_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_generateTunBuilderCaptureEvent_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_generateTunBuilderCaptureEvent_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_googleDnsFallback_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_googleDnsFallback_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native String ConfigCommon_gremlinConfig_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_gremlinConfig_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_guiVersion_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_guiVersion_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_hwAddrOverride_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_hwAddrOverride_set(
            long j, ConfigCommon configCommon, String str);

    public static native boolean ConfigCommon_info_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_info_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native String ConfigCommon_platformVersion_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_platformVersion_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_portOverride_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_portOverride_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_privateKeyPassword_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_privateKeyPassword_set(
            long j, ConfigCommon configCommon, String str);

    public static native boolean ConfigCommon_proxyAllowCleartextAuth_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_proxyAllowCleartextAuth_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native String ConfigCommon_proxyHost_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_proxyHost_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_proxyPassword_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_proxyPassword_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_proxyPort_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_proxyPort_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_proxyUsername_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_proxyUsername_set(
            long j, ConfigCommon configCommon, String str);

    public static native boolean ConfigCommon_retryOnAuthFailed_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_retryOnAuthFailed_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native String ConfigCommon_serverOverride_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_serverOverride_set(
            long j, ConfigCommon configCommon, String str);

    public static native int ConfigCommon_sslDebugLevel_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_sslDebugLevel_set(
            long j, ConfigCommon configCommon, int i);

    public static native String ConfigCommon_ssoMethods_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_ssoMethods_set(
            long j, ConfigCommon configCommon, String str);

    public static native boolean ConfigCommon_synchronousDnsLookup_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_synchronousDnsLookup_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native String ConfigCommon_tlsCertProfileOverride_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_tlsCertProfileOverride_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_tlsCipherList_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_tlsCipherList_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_tlsCiphersuitesList_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_tlsCiphersuitesList_set(
            long j, ConfigCommon configCommon, String str);

    public static native String ConfigCommon_tlsVersionMinOverride_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_tlsVersionMinOverride_set(
            long j, ConfigCommon configCommon, String str);

    public static native boolean ConfigCommon_tunPersist_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_tunPersist_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native boolean ConfigCommon_wintun_get(
            long j, ConfigCommon configCommon);

    public static native void ConfigCommon_wintun_set(
            long j, ConfigCommon configCommon, boolean z);

    public static native String DnsAddress_address_get(
            long j, DnsAddress dnsAddress);

    public static native void DnsAddress_address_set(
            long j, DnsAddress dnsAddress, String str);

    public static native long DnsAddress_port_get(
            long j, DnsAddress dnsAddress);

    public static native void DnsAddress_port_set(
            long j, DnsAddress dnsAddress, long j2);

    public static native String DnsAddress_to_string(
            long j, DnsAddress dnsAddress);

    public static native void DnsAddress_validate(
            long j, DnsAddress dnsAddress, String str);

    public static native String DnsDomain_domain_get(
            long j, DnsDomain dnsDomain);

    public static native void DnsDomain_domain_set(
            long j, DnsDomain dnsDomain, String str);

    public static native String DnsDomain_to_string(
            long j, DnsDomain dnsDomain);

    public static native void DnsDomain_validate(
            long j, DnsDomain dnsDomain, String str);

    public static native long DnsOptions_AddressList_capacity(
            long j, DnsOptions_AddressList dnsOptions_AddressList);

    public static native void DnsOptions_AddressList_clear(
            long j, DnsOptions_AddressList dnsOptions_AddressList);

    public static native void DnsOptions_AddressList_doAdd__SWIG_0(
            long j, DnsOptions_AddressList dnsOptions_AddressList,
            long j2, DnsAddress dnsAddress);

    public static native void DnsOptions_AddressList_doAdd__SWIG_1(
            long j, DnsOptions_AddressList dnsOptions_AddressList,
            int i, long j2, DnsAddress dnsAddress);

    public static native long DnsOptions_AddressList_doGet(
            long j, DnsOptions_AddressList dnsOptions_AddressList, int i);

    public static native long DnsOptions_AddressList_doRemove(
            long j, DnsOptions_AddressList dnsOptions_AddressList, int i);

    public static native void DnsOptions_AddressList_doRemoveRange(
            long j, DnsOptions_AddressList dnsOptions_AddressList, int i, int i2);

    public static native long DnsOptions_AddressList_doSet(
            long j, DnsOptions_AddressList dnsOptions_AddressList,
            int i, long j2, DnsAddress dnsAddress);

    public static native int DnsOptions_AddressList_doSize(
            long j, DnsOptions_AddressList dnsOptions_AddressList);

    public static native boolean DnsOptions_AddressList_isEmpty(
            long j, DnsOptions_AddressList dnsOptions_AddressList);

    public static native void DnsOptions_AddressList_reserve(
            long j, DnsOptions_AddressList dnsOptions_AddressList, long j2);

    public static native long DnsOptions_DomainsList_capacity(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList);

    public static native void DnsOptions_DomainsList_clear(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList);

    public static native void DnsOptions_DomainsList_doAdd__SWIG_0(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList,
            long j2, DnsDomain dnsDomain);

    public static native void DnsOptions_DomainsList_doAdd__SWIG_1(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList,
            int i, long j2, DnsDomain dnsDomain);

    public static native long DnsOptions_DomainsList_doGet(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList, int i);

    public static native long DnsOptions_DomainsList_doRemove(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList, int i);

    public static native void DnsOptions_DomainsList_doRemoveRange(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList, int i, int i2);

    public static native long DnsOptions_DomainsList_doSet(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList, int i, long j2, DnsDomain dnsDomain);

    public static native int DnsOptions_DomainsList_doSize(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList);

    public static native boolean DnsOptions_DomainsList_isEmpty(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList);

    public static native void DnsOptions_DomainsList_reserve(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList, long j2);

    public static native int DnsOptions_ServersMap_Iterator_getKey(
            long j, DnsOptions_ServersMap.Iterator iterator);

    public static native long DnsOptions_ServersMap_Iterator_getNextUnchecked(
            long j, DnsOptions_ServersMap.Iterator iterator);

    public static native long DnsOptions_ServersMap_Iterator_getValue(
            long j, DnsOptions_ServersMap.Iterator iterator);

    public static native boolean DnsOptions_ServersMap_Iterator_isNot(
            long j, DnsOptions_ServersMap.Iterator iterator,
            long j2, DnsOptions_ServersMap.Iterator iterator2);

    public static native void DnsOptions_ServersMap_Iterator_setValue(
            long j, DnsOptions_ServersMap.Iterator iterator,
            long j2, DnsServer dnsServer);

    public static native long DnsOptions_ServersMap_begin(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap);

    public static native void DnsOptions_ServersMap_clear(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap);

    public static native boolean DnsOptions_ServersMap_containsImpl(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap, int i);

    public static native long DnsOptions_ServersMap_end(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap);

    public static native long DnsOptions_ServersMap_find(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap, int i);

    public static native boolean DnsOptions_ServersMap_isEmpty(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap);

    public static native void DnsOptions_ServersMap_putUnchecked(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap,
            int i, long j2, DnsServer dnsServer);

    public static native void DnsOptions_ServersMap_removeUnchecked(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap,
            long j2, DnsOptions_ServersMap.Iterator iterator);

    public static native int DnsOptions_ServersMap_sizeImpl(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap);

    public static native boolean DnsOptions_from_dhcp_options_get(
            long j, DnsOptions dnsOptions);

    public static native void DnsOptions_from_dhcp_options_set(
            long j, DnsOptions dnsOptions, boolean z);

    public static native long DnsOptions_search_domains_get(
            long j, DnsOptions dnsOptions);

    public static native void DnsOptions_search_domains_set(
            long j, DnsOptions dnsOptions, long j2,
            DnsOptions_DomainsList dnsOptions_DomainsList);

    public static native long DnsOptions_servers_get(
            long j, DnsOptions dnsOptions);

    public static native void DnsOptions_servers_set(
            long j, DnsOptions dnsOptions, long j2,
            DnsOptions_ServersMap dnsOptions_ServersMap);

    public static native String DnsOptions_to_string(
            long j, DnsOptions dnsOptions);

    public static native long DnsServer_addresses_get(
            long j, DnsServer dnsServer);

    public static native void DnsServer_addresses_set(
            long j, DnsServer dnsServer, long j2,
            DnsOptions_AddressList dnsOptions_AddressList);

    public static native int DnsServer_dnssec_get(
            long j, DnsServer dnsServer);

    public static native void DnsServer_dnssec_set(
            long j, DnsServer dnsServer, int i);

    public static native String DnsServer_dnssec_string(
            long j, DnsServer dnsServer, int i);

    public static native long DnsServer_domains_get(
            long j, DnsServer dnsServer);

    public static native void DnsServer_domains_set(
            long j, DnsServer dnsServer, long j2,
            DnsOptions_DomainsList dnsOptions_DomainsList);

    public static native String DnsServer_sni_get(long j, DnsServer dnsServer);

    public static native void DnsServer_sni_set(long j, DnsServer dnsServer, String str);

    public static native String DnsServer_to_string__SWIG_0(long j, DnsServer dnsServer, String str);

    public static native String DnsServer_to_string__SWIG_1(long j, DnsServer dnsServer);

    public static native int DnsServer_transport_get(long j, DnsServer dnsServer);

    public static native void DnsServer_transport_set(long j, DnsServer dnsServer, int i);

    public static native String DnsServer_transport_string(long j, DnsServer dnsServer, int i);

    public static void SwigDirector_ClientAPI_OpenVPNClient_acc_event(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, long j) {
        clientAPI_OpenVPNClient.acc_event(
            new ClientAPI_AppCustomControlMessageEvent(j, false)
        );
    }

    public static void SwigDirector_ClientAPI_OpenVPNClient_clock_tick(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient) {
        clientAPI_OpenVPNClient.clock_tick();
    }

    public static void SwigDirector_ClientAPI_OpenVPNClient_event(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, long j) {
        clientAPI_OpenVPNClient.event(
            new ClientAPI_Event(j, false)
        );
    }

    public static void SwigDirector_ClientAPI_OpenVPNClient_external_pki_cert_request(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, long j) {
        clientAPI_OpenVPNClient.external_pki_cert_request(
            new ClientAPI_ExternalPKICertRequest(j, false)
        );
    }

    public static void SwigDirector_ClientAPI_OpenVPNClient_external_pki_sign_request(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, long j) {
        clientAPI_OpenVPNClient.external_pki_sign_request(
            new ClientAPI_ExternalPKISignRequest(j, false)
        );
    }

    public static void SwigDirector_ClientAPI_OpenVPNClient_log(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, long j) {
        clientAPI_OpenVPNClient.log(
            new ClientAPI_LogInfo(j, false)
        );
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_pause_on_connection_timeout(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient) {
        return clientAPI_OpenVPNClient.pause_on_connection_timeout();
    }

    public static void SwigDirector_ClientAPI_OpenVPNClient_remote_override(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, long j) {
        clientAPI_OpenVPNClient.remote_override(
                new ClientAPI_RemoteOverride(j, false)
        );
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_remote_override_enabled(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient) {
        return clientAPI_OpenVPNClient.remote_override_enabled();
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_socket_protect(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, int i, String str, boolean z) {
        return clientAPI_OpenVPNClient.socket_protect(i, str, z);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_add_address(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, int i, String str2, boolean z, boolean z2) {
        return clientAPI_OpenVPNClient.tun_builder_add_address(str, i, str2, z, z2);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_add_proxy_bypass(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str) {
        return clientAPI_OpenVPNClient.tun_builder_add_proxy_bypass(str);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_add_route(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, int i, int i2, boolean z) {
        return clientAPI_OpenVPNClient.tun_builder_add_route(str, i, i2, z);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_add_wins_server(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str) {
        return clientAPI_OpenVPNClient.tun_builder_add_wins_server(str);
    }

    public static int SwigDirector_ClientAPI_OpenVPNClient_tun_builder_establish(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient) {
        return clientAPI_OpenVPNClient.tun_builder_establish();
    }

    public static void SwigDirector_ClientAPI_OpenVPNClient_tun_builder_establish_lite(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient) {
        clientAPI_OpenVPNClient.tun_builder_establish_lite();
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_exclude_route(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, int i, int i2, boolean z) {
        return clientAPI_OpenVPNClient.tun_builder_exclude_route(str, i, i2, z);
    }

    public static long SwigDirector_ClientAPI_OpenVPNClient_tun_builder_get_local_networks(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, boolean z) {
        return ClientAPI_StringVec.getCPtr(clientAPI_OpenVPNClient.tun_builder_get_local_networks(z));
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_new(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient) {
        return clientAPI_OpenVPNClient.tun_builder_new();
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_persist(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient) {
        return clientAPI_OpenVPNClient.tun_builder_persist();
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_reroute_gw(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, boolean z, boolean z2, long j) {
        return clientAPI_OpenVPNClient.tun_builder_reroute_gw(z, z2, j);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_allow_family(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, int i, boolean z) {
        return clientAPI_OpenVPNClient.tun_builder_set_allow_family(i, z);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_allow_local_dns(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, boolean z) {
        return clientAPI_OpenVPNClient.tun_builder_set_allow_local_dns(z);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_dns_options(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, long j) {
        return clientAPI_OpenVPNClient.tun_builder_set_dns_options(
                new DnsOptions(j, false)
        );
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_layer(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, int i) {
        return clientAPI_OpenVPNClient.tun_builder_set_layer(i);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_mtu(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, int i) {
        return clientAPI_OpenVPNClient.tun_builder_set_mtu(i);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_proxy_auto_config_url(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str) {
        return clientAPI_OpenVPNClient.tun_builder_set_proxy_auto_config_url(str);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_proxy_http(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, int i) {
        return clientAPI_OpenVPNClient.tun_builder_set_proxy_http(str, i);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_proxy_https(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, int i) {
        return clientAPI_OpenVPNClient.tun_builder_set_proxy_https(str, i);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_remote_address(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str, boolean z) {
        return clientAPI_OpenVPNClient.tun_builder_set_remote_address(str, z);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_route_metric_default(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, int i) {
        return clientAPI_OpenVPNClient.tun_builder_set_route_metric_default(i);
    }

    public static boolean SwigDirector_ClientAPI_OpenVPNClient_tun_builder_set_session_name(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, String str) {
        return clientAPI_OpenVPNClient.tun_builder_set_session_name(str);
    }

    public static void SwigDirector_ClientAPI_OpenVPNClient_tun_builder_teardown(
            ClientAPI_OpenVPNClient clientAPI_OpenVPNClient, boolean z) {
        clientAPI_OpenVPNClient.tun_builder_teardown(z);
    }

    public static native void delete_ClientAPI_AppCustomControlMessageEvent(long j);

    public static native void delete_ClientAPI_Config(long j);

    public static native void delete_ClientAPI_ConnectionInfo(long j);

    public static native void delete_ClientAPI_DynamicChallenge(long j);

    public static native void delete_ClientAPI_EvalConfig(long j);

    public static native void delete_ClientAPI_Event(long j);

    public static native void delete_ClientAPI_ExternalPKIBase(long j);

    public static native void delete_ClientAPI_ExternalPKICertRequest(long j);

    public static native void delete_ClientAPI_ExternalPKIRequestBase(long j);

    public static native void delete_ClientAPI_ExternalPKISignRequest(long j);

    public static native void delete_ClientAPI_InterfaceStats(long j);

    public static native void delete_ClientAPI_KeyValue(long j);

    public static native void delete_ClientAPI_LLVector(long j);

    public static native void delete_ClientAPI_LogInfo(long j);

    public static native void delete_ClientAPI_MergeConfig(long j);

    public static native void delete_ClientAPI_OpenVPNClient(long j);

    public static native void delete_ClientAPI_OpenVPNClientHelper(long j);

    public static native void delete_ClientAPI_ProvideCreds(long j);

    public static native void delete_ClientAPI_RemoteOverride(long j);

    public static native void delete_ClientAPI_ServerEntry(long j);

    public static native void delete_ClientAPI_ServerEntryVector(long j);

    public static native void delete_ClientAPI_SessionToken(long j);

    public static native void delete_ClientAPI_Status(long j);

    public static native void delete_ClientAPI_StringVec(long j);

    public static native void delete_ClientAPI_TransportStats(long j);

    public static native void delete_ClientAPI_TunBuilderBase(long j);

    public static native void delete_ConfigCommon(long j);

    public static native void delete_DnsAddress(long j);

    public static native void delete_DnsDomain(long j);

    public static native void delete_DnsOptions(long j);

    public static native void delete_DnsOptions_AddressList(long j);

    public static native void delete_DnsOptions_DomainsList(long j);

    public static native void delete_DnsOptions_ServersMap(long j);

    public static native void delete_DnsOptions_ServersMap_Iterator(long j);

    public static native void delete_DnsServer(long j);

    public static native void delete_ExternalPKIImpl(long j);

    public static native long new_ClientAPI_AppCustomControlMessageEvent();

    public static native long new_ClientAPI_Config();

    public static native long new_ClientAPI_ConnectionInfo();

    public static native long new_ClientAPI_DynamicChallenge();

    public static native long new_ClientAPI_EvalConfig();

    public static native long new_ClientAPI_Event();

    public static native long new_ClientAPI_ExternalPKICertRequest();

    public static native long new_ClientAPI_ExternalPKIRequestBase();

    public static native long new_ClientAPI_ExternalPKISignRequest();

    public static native long new_ClientAPI_InterfaceStats();

    public static native long new_ClientAPI_KeyValue__SWIG_0();

    public static native long new_ClientAPI_KeyValue__SWIG_1(String str, String str2);

    public static native long new_ClientAPI_LLVector__SWIG_0();

    public static native long new_ClientAPI_LLVector__SWIG_1(
            long j, ClientAPI_LLVector clientAPI_LLVector);

    public static native long new_ClientAPI_LLVector__SWIG_2(int i, long j);

    public static native long new_ClientAPI_LogInfo__SWIG_0();

    public static native long new_ClientAPI_LogInfo__SWIG_1(String str);

    public static native long new_ClientAPI_MergeConfig();

    public static native long new_ClientAPI_OpenVPNClient();

    public static native long new_ClientAPI_OpenVPNClientHelper();

    public static native long new_ClientAPI_ProvideCreds();

    public static native long new_ClientAPI_RemoteOverride();

    public static native long new_ClientAPI_ServerEntry();

    public static native long new_ClientAPI_ServerEntryVector__SWIG_0();

    public static native long new_ClientAPI_ServerEntryVector__SWIG_1(
            long j, ClientAPI_ServerEntryVector clientAPI_ServerEntryVector);

    public static native long new_ClientAPI_ServerEntryVector__SWIG_2(
            int i, long j, ClientAPI_ServerEntry clientAPI_ServerEntry);

    public static native long new_ClientAPI_SessionToken();

    public static native long new_ClientAPI_Status();

    public static native long new_ClientAPI_StringVec__SWIG_0();

    public static native long new_ClientAPI_StringVec__SWIG_1(
            long j, ClientAPI_StringVec clientAPI_StringVec);

    public static native long new_ClientAPI_StringVec__SWIG_2(int i, String str);

    public static native long new_ClientAPI_TransportStats();

    public static native long new_ClientAPI_TunBuilderBase();

    public static native long new_ConfigCommon();

    public static native long new_DnsAddress();

    public static native long new_DnsDomain();

    public static native long new_DnsOptions();

    public static native long new_DnsOptions_AddressList__SWIG_0();

    public static native long new_DnsOptions_AddressList__SWIG_1(
            long j, DnsOptions_AddressList dnsOptions_AddressList);

    public static native long new_DnsOptions_AddressList__SWIG_2(
            int i, long j, DnsAddress dnsAddress);

    public static native long new_DnsOptions_DomainsList__SWIG_0();

    public static native long new_DnsOptions_DomainsList__SWIG_1(
            long j, DnsOptions_DomainsList dnsOptions_DomainsList);

    public static native long new_DnsOptions_DomainsList__SWIG_2(
            int i, long j, DnsDomain dnsDomain);

    public static native long new_DnsOptions_ServersMap__SWIG_0();

    public static native long new_DnsOptions_ServersMap__SWIG_1(
            long j, DnsOptions_ServersMap dnsOptions_ServersMap);

    public static native long new_DnsServer();

    public static native long new_ExternalPKIImpl();

    private static native void swig_module_init();
}