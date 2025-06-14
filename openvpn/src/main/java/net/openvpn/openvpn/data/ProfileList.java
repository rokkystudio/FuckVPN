package net.openvpn.openvpn.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.openvpn.openvpn.ipc.IPCChunkToken;
import net.openvpn.openvpn.ipc.IPCChunkable;
import net.openvpn.openvpn.ipc.IPCUtils;

public class ProfileList extends ArrayList<ProfileData> implements Parcelable, IPCChunkable {
    public static final IPCChunkable.Creator<ProfileList> CHUNKABLE_CREATOR = new IPCChunkable.Creator<ProfileList>() {
        public ProfileList createFromTokens(List<IPCChunkToken> list) {
            ProfileList profileList = (ProfileList) list.get(0).getData();
            int i = 1;
            while (list.size() > i) {
                int i2 = list.get(i).total + i;
                profileList.add(ProfileData.CHUNKABLE_CREATOR.createFromTokens(list.subList(i, i2)));
                i = i2;
            }
            return profileList;
        }
    };
    public static final Creator<ProfileList> CREATOR = new Creator<ProfileList>() {
        public ProfileList createFromParcel(Parcel parcel) {
            return new ProfileList(parcel);
        }

        public ProfileList[] newArray(int i) {
            return new ProfileList[i];
        }
    };

    public ProfileList() {
    }

    protected ProfileList(Parcel parcel) {
        parcel.readList(this, ProfileData.class.getClassLoader());
    }

    public int countChunks() {
        int i = 1;
        if (!shouldChunk()) {
            return 1;
        }
        Iterator it = iterator();
        while (it.hasNext()) {
            i += ((ProfileData) it.next()).countChunks();
        }
        return i;
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
        ProfileList profileList = new ProfileList();
        IPCUtils.Class.copyInstance(this, profileList);
        arrayList.add(new IPCChunkToken(profileList, 0, countChunks, getClass()));
        Iterator it = iterator();
        while (it.hasNext()) {
            arrayList.addAll(((ProfileData) it.next()).getTokens());
        }
        return arrayList;
    }

    public ProfileData get_profile_by_id(String str) {
        if (str == null) {
            return null;
        }
        Iterator it = iterator();
        while (it.hasNext()) {
            ProfileData profileData = (ProfileData) it.next();
            if (str.equals(profileData.get_id())) {
                return profileData;
            }
        }
        return null;
    }

    public ProfileData get_profile_by_name(String str) {
        if (str == null) {
            return null;
        }
        Iterator it = iterator();
        while (it.hasNext()) {
            ProfileData profileData = (ProfileData) it.next();
            if (str.equals(profileData.name)) {
                return profileData;
            }
        }
        return null;
    }

    public String[] profile_ids() {
        String[] strArr = new String[size()];
        for (int i = 0; i < size(); i++) {
            strArr[i] = ((ProfileData) get(i)).id;
        }
        return strArr;
    }

    public String[] profile_names() {
        String[] strArr = new String[size()];
        for (int i = 0; i < size(); i++) {
            strArr[i] = ((ProfileData) get(i)).name;
        }
        return strArr;
    }

    public boolean shouldChunk() {
        return size() > 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(this);
    }
}
