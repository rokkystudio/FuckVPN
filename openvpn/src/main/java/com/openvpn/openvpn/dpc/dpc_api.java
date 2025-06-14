package com.openvpn.openvpn.dpc;

public abstract class dpc_api
{
    public static boolean isCAAllowed(String str) {
        return dpc_apiJNI.isCAAllowed(str);
    }

    public static boolean isProtocolSupported(String str) {
        return dpc_apiJNI.isProtocolSupported(str);
    }

    public static JsonValue parseString(String str, Response response) {
        return new JsonValue(dpc_apiJNI.parseString(str, Response.getCPtr(response), response), true);
    }

    public static void process(Request request, Response response) {
        dpc_apiJNI.process(Request.getCPtr(request), request, Response.getCPtr(response), response);
    }

    public static void setCommonResponseFields(JsonValue jsonValue, Response response) {
        dpc_apiJNI.setCommonResponseFields(JsonValue.getCPtr(jsonValue), jsonValue, Response.getCPtr(response), response);
    }

    public static boolean validateJSON(JsonValue jsonValue, Response response) {
        return dpc_apiJNI.validateJSON(JsonValue.getCPtr(jsonValue), jsonValue, Response.getCPtr(response), response);
    }

    public static boolean validateRequest(Request request, Response response) {
        return dpc_apiJNI.validateRequest(Request.getCPtr(request), request, Response.getCPtr(response), response);
    }
}
