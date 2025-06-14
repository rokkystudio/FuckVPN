package net.openvpn.openvpn.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.openvpn.openvpn.ipc.IPCChunkToken;
import net.openvpn.openvpn.ipc.IPCChunkable;
import net.openvpn.openvpn.ipc.IPCUtils;

public class ProfileData extends Profile implements Parcelable, IPCChunkable {
    public static final IPCChunkable.Creator<ProfileData> CHUNKABLE_CREATOR = new IPCChunkable.Creator<ProfileData>() {
        public ProfileData createFromTokens(List<IPCChunkToken> list) {
            ProfileData profileData = (ProfileData) list.get(0).getData();
            profileData.profile_content = IPCUtils.StringChunker.restore(list.subList(1, list.size()));
            return profileData;
        }
    };
    public static final Creator<ProfileData> CREATOR = new Creator<ProfileData>() {
        public ProfileData createFromParcel(Parcel parcel) {
            return ProfileData.FromParcel(parcel);
        }

        public ProfileData[] newArray(int i) {
            return new ProfileData[i];
        }
    };

    public ProfileData() {
    }

    public ProfileData(String str, String str2, String str3, boolean z, EvalConfig evalConfig, String str4, String str5) {
        super(str, str2, str3, z, evalConfig, str4, str5);
    }

    public static ProfileData FromParcel(Parcel parcel) {
        String readString = parcel.readString();
        String readString2 = parcel.readString();
        String readString3 = parcel.readString();
        String readString4 = parcel.readString();
        String readString5 = parcel.readString();
        String readString6 = parcel.readString();
        String readString7 = parcel.readString();
        String readString8 = parcel.readString();
        ProfileData profileData = new ProfileData();
        profileData.location = readString;
        profileData.orig_filename = readString2;
        profileData.errorText = readString3;
        profileData.userlocked_username = readString4;
        profileData.name = readString5;
        profileData.id = readString6;
        profileData.external_pki_alias = readString7;
        profileData.profile_content = readString8;
        profileData.server_list = (ServerList) parcel.readValue(ServerList.class.getClassLoader());
        profileData.static_challenge = (Challenge) parcel.readValue(Challenge.class.getClassLoader());
        profileData.dynamic_challenge = (DynamicChallenge) parcel.readValue(DynamicChallenge.class.getClassLoader());
        profileData.proxy_context = (ProxyContext) parcel.readValue(ProxyContext.class.getClassLoader());
        boolean z = true;
        profileData.autologin = parcel.readByte() != 0;
        profileData.external_pki = parcel.readByte() != 0;
        profileData.private_key_password_required = parcel.readByte() != 0;
        if (parcel.readByte() == 0) {
            z = false;
        }
        profileData.allow_password_save = z;
        return profileData;
    }

    public int countChunks() {
        if (!shouldChunk()) {
            return 1;
        }
        return IPCUtils.StringChunker.countChunks(this.profile_content) + 1;
    }

    public int describeContents() {
        return 0;
    }

    public List<IPCChunkToken> getTokens() {
        int countChunks = countChunks();
        ArrayList arrayList = new ArrayList(countChunks);
        if (countChunks == 1) {
            arrayList.add(new IPCChunkToken(this, 0, countChunks, getClass()));
            return arrayList;
        }
        ProfileData profileData = new ProfileData();
        IPCUtils.Class.copyInstance(this, profileData, Arrays.asList(new String[]{"profile_content"}));
        arrayList.add(new IPCChunkToken(profileData, 0, countChunks, getClass()));
        IPCUtils.StringChunker.chunk(this.profile_content, arrayList, countChunks);
        return arrayList;
    }

    public boolean shouldChunk() {
        String str = this.profile_content;
        return str != null && IPCUtils.StringChunker.shouldChunk(str);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.location);
        parcel.writeString(this.orig_filename);
        parcel.writeString(this.errorText);
        parcel.writeString(this.userlocked_username);
        parcel.writeString(this.name);
        parcel.writeString(this.id);
        parcel.writeString(this.external_pki_alias);
        parcel.writeString(this.profile_content);
        parcel.writeValue(this.server_list);
        parcel.writeValue(this.static_challenge);
        parcel.writeValue(this.dynamic_challenge);
        parcel.writeValue(this.proxy_context);
        parcel.writeByte(this.autologin ? (byte) 1 : 0);
        parcel.writeByte(this.external_pki ? (byte) 1 : 0);
        parcel.writeByte(this.private_key_password_required ? (byte) 1 : 0);
        parcel.writeByte(this.allow_password_save ? (byte) 1 : 0);
    }
}
