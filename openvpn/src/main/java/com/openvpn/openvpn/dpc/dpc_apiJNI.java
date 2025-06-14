package com.openvpn.openvpn.dpc;

import com.openvpn.openvpn.dpc.Request;

public abstract class dpc_apiJNI
{
    public static native boolean JsonValue_isMember(long jsonPtr, JsonValue jsonValue, String key);

    public static native String JsonValue_toStyledString(long jsonPtr, JsonValue jsonValue);

    public static native long Request_Antivirus_process_contains_get(long antivirusPtr, Request.Antivirus antivirus);

    public static native void Request_Antivirus_process_contains_set(long antivirusPtr, Request.Antivirus antivirus, long value);

    public static native int Request_Antivirus_process_max_get(long antivirusPtr, Request.Antivirus antivirus);

    public static native void Request_Antivirus_process_max_set(long antivirusPtr, Request.Antivirus antivirus, int max);

    public static native long Request_Certificate_issuers_get(long certificatePtr, Request.Certificate certificate);

    public static native void Request_Certificate_issuers_set(long certificatePtr, Request.Certificate certificate, long issuers);

    public static native boolean Request_DiscEncryption_full_get(long encryptionPtr, Request.DiscEncryption encryption);

    public static native void Request_DiscEncryption_full_set(long encryptionPtr, Request.DiscEncryption encryption, boolean isFull);

    public static native String Request_DiscEncryption_volume_get(long encryptionPtr, Request.DiscEncryption encryption);

    public static native void Request_DiscEncryption_volume_set(long encryptionPtr, Request.DiscEncryption encryption, String volume);

    public static native long Request_EPKICertificate_issuers_get(long certPtr, Request.EPKICertificate certificate);

    public static native void Request_EPKICertificate_issuers_set(long certPtr, Request.EPKICertificate certificate, long issuers);

    public static native String Request_EPKISignature_algorithm_get(long signaturePtr, Request.EPKISignature signature);

    public static native void Request_EPKISignature_algorithm_set(long signaturePtr, Request.EPKISignature signature, String algorithm);

    public static native String Request_EPKISignature_data_get(long signaturePtr, Request.EPKISignature signature);

    public static native void Request_EPKISignature_data_set(long signaturePtr, Request.EPKISignature signature, String data);

    public static native String Request_EPKISignature_hashalg_get(long signaturePtr, Request.EPKISignature signature);

    public static native void Request_EPKISignature_hashalg_set(long signaturePtr, Request.EPKISignature signature, String hashAlg);

    public static native long Request_EPKISignature_issuers_get(long signaturePtr, Request.EPKISignature signature);

    public static native void Request_EPKISignature_issuers_set(long signaturePtr, Request.EPKISignature signature, long issuers);

    public static native String Request_EPKISignature_saltlen_get(long signaturePtr, Request.EPKISignature signature);

    public static native void Request_EPKISignature_saltlen_set(long signaturePtr, Request.EPKISignature signature, String saltLen);

    public static native boolean Request_OSInfo_enabled_get(long osInfoPtr, Request.OSInfo osInfo);

    public static native void Request_OSInfo_enabled_set(long osInfoPtr, Request.OSInfo osInfo, boolean enabled);

    public static native long Request_antivirus_get(long requestPtr, Request request);

    public static native void Request_antivirus_set(long requestPtr, Request request, long antivirus);

    public static native long Request_certificate_get(long requestPtr, Request request);

    public static native void Request_certificate_set(long requestPtr, Request request, long certificate);

    public static native String Request_correlation_id_get(long requestPtr, Request request);

    public static native void Request_correlation_id_set(long requestPtr, Request request, String correlationId);

    public static native long Request_disc_encryption_get(long requestPtr, Request request);

    public static native void Request_disc_encryption_set(long requestPtr, Request request, long encryption);

    public static native long Request_epki_certificate_get(long requestPtr, Request request);

    public static native void Request_epki_certificate_set(long requestPtr, Request request, long certificate);

    public static native long Request_epki_signature_get(long requestPtr, Request request);

    public static native void Request_epki_signature_set(long requestPtr, Request request, long signature);

    public static native long Request_fromJSON(long contextPtr, JsonValue jsonValue);

    public static native long Request_os_info_get(long requestPtr, Request request);

    public static native void Request_os_info_set(long requestPtr, Request request, long osInfo);

    public static native String Request_timestamp_get(long requestPtr, Request request);

    public static native void Request_timestamp_set(long requestPtr, Request request, String timestamp);

    public static native String Request_user_id_get(long requestPtr, Request request);

    public static native void Request_user_id_set(long requestPtr, Request request, String userId);

    public static native String Request_ver_get(long requestPtr, Request request);

    public static native void Request_ver_set(long requestPtr, Request request, String version);

    public static native long Response_antivirus_get(long responsePtr, Response response);

    public static native void Response_antivirus_set(long responsePtr, Response response, long antivirus);

    public static native long Response_certificate_get(long responsePtr, Response response);

    public static native void Response_certificate_set(long responsePtr, Response response, long certificate);

    public static native long Response_client_info_get(long responsePtr, Response response);

    public static native void Response_client_info_set(long responsePtr, Response response, long clientInfo);

    public static native String Response_correlation_id_get(long responsePtr, Response response);

    public static native void Response_correlation_id_set(long responsePtr, Response response, String correlationId);

    public static native long Response_disc_encryption_get(long responsePtr, Response response);

    public static native void Response_disc_encryption_set(long responsePtr, Response response, long encryption);

    public static native long Response_epki_certificate_get(long responsePtr, Response response);

    public static native void Response_epki_certificate_set(long responsePtr, Response response, long certificate);

    public static native long Response_epki_signature_get(long responsePtr, Response response);

    public static native void Response_epki_signature_set(long responsePtr, Response response, long signature);

    public static native long Response_errors_get(long responsePtr, Response response);

    public static native void Response_errors_set(long responsePtr, Response response, long errors);

    public static native boolean Response_hasErrors(long responsePtr, Response response);

    public static native String Response_timestamp_get(long responsePtr, Response response);

    public static native void Response_timestamp_set(long responsePtr, Response response, String timestamp);

    public static native long Response_toJSON(long responsePtr, Response response);

    public static native String Response_toJSONString(long responsePtr, Response response);

    public static native String Response_ver_get(long responsePtr, Response response);

    public static native void Response_ver_set(long responsePtr, Response response, String version);

    public static native void delete_JsonValue(long ptr);

    public static native void delete_Request(long ptr);

    public static native void delete_Request_Antivirus(long ptr);

    public static native void delete_Request_Certificate(long ptr);

    public static native void delete_Request_DiscEncryption(long ptr);

    public static native void delete_Request_EPKICertificate(long ptr);

    public static native void delete_Request_EPKISignature(long ptr);

    public static native void delete_Request_OSInfo(long ptr);

    public static native void delete_Response(long ptr);

    public static native boolean isCAAllowed(String ca);

    public static native boolean isProtocolSupported(String protocol);

    public static native long new_JsonValue__SWIG_0(int type);

    public static native long new_JsonValue__SWIG_1();

    public static native long new_JsonValue__SWIG_2(long sourcePtr, JsonValue source);

    public static native long new_Request_Antivirus();

    public static native long new_Request_Certificate();

    public static native long new_Request_DiscEncryption();

    public static native long new_Request_EPKICertificate();

    public static native long new_Request_EPKISignature();

    public static native long new_Request_OSInfo();

    public static native long new_Response();

    public static native int nullValue_get();

    public static native long parseString(String json, long contextPtr, Response response);

    public static native void process(long contextPtr, Request request, long responsePtr, Response response);

    public static native void processAntivirus(long contextPtr, Request request, long responsePtr, Response response);

    public static native void processCertificate(long contextPtr, Request request, long responsePtr, Response response);

    public static native void processDiscEncryption(long contextPtr, Request request, long responsePtr, Response response);

    public static native void processEPKICertificate(long contextPtr, Request request, long responsePtr, Response response);

    public static native void processEPKISignature(long contextPtr, Request request, long responsePtr, Response response);

    public static native void processOSInfo(long contextPtr, Request request, long responsePtr, Response response);

    public static native void setCommonResponseFields(long contextPtr, JsonValue jsonValue, long responsePtr, Response response);

    public static native boolean validateJSON(long contextPtr, JsonValue jsonValue, long responsePtr, Response response);

    public static native boolean validateRequest(long contextPtr, Request request, long responsePtr, Response response);
}
