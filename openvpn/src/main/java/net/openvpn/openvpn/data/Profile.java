package net.openvpn.openvpn.data;

public class Profile {
    public boolean allow_password_save;
    public boolean autologin;
    public DynamicChallenge dynamic_challenge;
    public EvalConfig ec;
    public String errorText;
    public boolean external_pki;
    public String external_pki_alias;
    public boolean filename_is_url_encoded_profile_name;
    public String id;
    public String location;
    public String name;
    public String orig_filename;
    public boolean private_key_password_required;
    public String profile_content;
    public ProxyContext proxy_context;
    public ServerList server_list;
    public Challenge static_challenge;
    public String userlocked_username;

    protected Profile() {
    }

    public Profile(String str, String str2, String str3, boolean z, EvalConfig evalConfig, String str4, String str5) {
        if (str2 != null) {
            this.id = str2;
        }
        this.location = str;
        this.orig_filename = str3;
        this.filename_is_url_encoded_profile_name = z;
        this.ec = evalConfig;
        if (evalConfig.error) {
            this.errorText = evalConfig.message;
        } else {
            this.userlocked_username = evalConfig.userlockedUsername;
            this.autologin = evalConfig.autologin;
            this.external_pki = evalConfig.externalPki;
            this.private_key_password_required = evalConfig.privateKeyPasswordRequired;
            this.allow_password_save = evalConfig.allowPasswordSave;
            this.name = str4;
        }
        String str6 = evalConfig.staticChallenge;
        if (str6.length() > 0) {
            this.static_challenge = new Challenge(str6, evalConfig.staticChallengeEcho, true);
        }
        this.server_list = evalConfig.serverList;
        this.external_pki_alias = str5;
    }

    public boolean challenge_defined() {
        expire_dynamic_challenge();
        return (this.static_challenge == null && this.dynamic_challenge == null) ? false : true;
    }

    public boolean equals(Profile profile) {
        String str;
        String str2 = this.profile_content;
        return (str2 == null || (str = profile.profile_content) == null) ? get_name().equals(profile.get_name()) && get_location().equals(profile.get_location()) && this.orig_filename.equals(profile.orig_filename) : str2.equals(str);
    }

    /* access modifiers changed from: protected */
    public void expire_dynamic_challenge() {
        DynamicChallenge dynamicChallenge = this.dynamic_challenge;
        if (dynamicChallenge != null && dynamicChallenge.is_expired()) {
            this.dynamic_challenge = null;
        }
    }

    public boolean get_allow_password_save() {
        return this.allow_password_save;
    }

    public boolean get_autologin() {
        return this.autologin;
    }

    public Challenge get_challenge() {
        expire_dynamic_challenge();
        DynamicChallenge dynamicChallenge = this.dynamic_challenge;
        return dynamicChallenge != null ? dynamicChallenge.challenge : this.static_challenge;
    }

    public long get_dynamic_challenge_expire_delay() {
        if (is_dynamic_challenge()) {
            return this.dynamic_challenge.expire_delay();
        }
        return 0;
    }

    public boolean get_epki() {
        return this.external_pki;
    }

    public String get_epki_alias() {
        return this.external_pki_alias;
    }

    public String get_error() {
        return this.errorText;
    }

    public String get_filename() {
        return this.orig_filename;
    }

    public String get_id() {
        return this.id;
    }

    public String get_location() {
        return this.location;
    }

    public String get_name() {
        return this.name;
    }

    public boolean get_private_key_password_required() {
        return this.private_key_password_required;
    }

    public ProxyContext get_proxy_context(boolean z) {
        ProxyContext proxyContext = this.proxy_context;
        if (proxyContext != null && !proxyContext.is_expired()) {
            return this.proxy_context;
        }
        this.proxy_context = z ? new ProxyContext() : null;
        return this.proxy_context;
    }

    public ServerList get_server_list() {
        return this.server_list;
    }

    public String get_userlocked_username() {
        return this.userlocked_username;
    }

    public boolean have_external_pki_alias() {
        return this.external_pki && this.external_pki_alias != null;
    }

    public boolean is_deleteable() {
        String str = this.location;
        return str != null && !str.equals("bundled");
    }

    public boolean is_dynamic_challenge() {
        expire_dynamic_challenge();
        return this.dynamic_challenge != null;
    }

    public boolean is_renameable() {
        return is_deleteable();
    }

    public boolean need_external_pki_alias() {
        return this.external_pki && this.external_pki_alias == null;
    }

    public void reset_dynamic_challenge() {
        this.dynamic_challenge = null;
    }

    public void reset_proxy_context() {
        this.proxy_context = null;
    }

    public boolean server_list_defined() {
        return this.server_list.size() > 0;
    }

    public void set_id(String str) {
        String str2 = this.id;
        if (str2 == null || str2.isEmpty()) {
            this.id = str;
        }
    }

    public void set_name(String str) {
        this.name = str;
    }

    public String toString() {
        Object[] objArr = new Object[10];
        objArr[0] = this.id;
        objArr[1] = this.name;
        objArr[2] = this.orig_filename;
        objArr[3] = this.userlocked_username;
        objArr[4] = Boolean.valueOf(this.autologin);
        objArr[5] = Boolean.valueOf(this.external_pki);
        objArr[6] = this.external_pki_alias;
        objArr[7] = this.server_list.toString();
        Challenge challenge = this.static_challenge;
        String str = "null";
        objArr[8] = challenge != null ? challenge.toString() : str;
        DynamicChallenge dynamicChallenge = this.dynamic_challenge;
        if (dynamicChallenge != null) {
            str = dynamicChallenge.toString();
        }
        objArr[9] = str;
        return String.format("Profile id='%s' name='%s' ofn='%s' userlock=%s auto=%b epki=%b/%s sl=%s sc=%s dc=%s", objArr);
    }

    public boolean userlocked_username_defined() {
        return this.userlocked_username.length() > 0;
    }
}
