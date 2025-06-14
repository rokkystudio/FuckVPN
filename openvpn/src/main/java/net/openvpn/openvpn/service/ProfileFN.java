package net.openvpn.openvpn.service;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ProfileFN {
    private static final String TAG = "OpenVPNService";
    private static final String id_query = "id:";
    private static final String pn_query = "pn:";
    private static final String separator = "-s-";

    public static String create_file_name(String str, String str2) {
        String encode_profile_fn = encode_profile_fn(str);
        if (encode_profile_fn == null) {
            Log.e(TAG, "create_file_name: UnsupportedEncodingException when encoding profile filename");
            return null;
        }
        return pn_query + strip_ovpn_ext(encode_profile_fn) + separator + id_query + str2 + ".ovpn";
    }

    public static String decode_profile_fn(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException when decoding profile filename", e);
            return null;
        }
    }

    public static String encode_profile_fn(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8") + ".ovpn";
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException when encoding profile filename", e);
            return null;
        }
    }

    public static boolean has_ovpn_ext(String str) {
        if (str != null) {
            return str.endsWith(".ovpn") || str.endsWith(".OVPN");
        }
        return false;
    }

    public static boolean is_using_id(String str) {
        return str.startsWith(pn_query);
    }

    public static String retrieve_id(String str) {
        if (!is_using_id(str)) {
            return null;
        }
        for (String str2 : strip_ovpn_ext(str).split(separator)) {
            if (str2.startsWith(id_query)) {
                return str2.substring(3);
            }
        }
        return null;
    }

    public static String retrieve_profile_name(String str) {
        if (!is_using_id(str)) {
            return null;
        }
        for (String str2 : strip_ovpn_ext(str).split(separator)) {
            if (str2.startsWith(pn_query)) {
                return decode_profile_fn(str2.substring(3));
            }
        }
        return null;
    }

    public static String strip_ovpn_ext(String str) {
        return (str == null || !has_ovpn_ext(str)) ? str : str.substring(0, str.length() - 5);
    }
}
