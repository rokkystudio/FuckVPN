package net.openvpn.openvpn.ipc;

import android.os.Parcel;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class IPCUtils {
    private static final String TAG = "IPCUtils";

    public static class Class {
        private static Comparator<Field> FieldComparator = new Comparator<Field>() {
            public int compare(Field field, Field field2) {
                return field.getName().compareTo(field2.getName());
            }
        };

        public static <T> void copyInstance(T t, T t2) {
            copyInstance(t, t2, (List<String>) null);
        }

        public static <T> void copyInstance(T t, T t2, List<String> list) {
            for (Field field : t.getClass().getFields()) {
                if (!((list != null && list.contains(field.getName())) || Modifier.isStatic(field.getModifiers()))) {
                    try {
                        field.set(t2, field.get(t));
                    } catch (IllegalAccessException e) {
                        Log.e(IPCUtils.TAG, "copyInstance", e);
                    }
                }
            }
        }

        public static <T> void readFromParcel(T t, Parcel parcel, List<String> list) {
            Field[] fields = t.getClass().getFields();
            Arrays.sort(fields, FieldComparator);
            for (Field field : fields) {
                if (!((list != null && list.contains(field.getName())) || Modifier.isStatic(field.getModifiers()))) {
                    try {
                        field.set(t, parcel.readValue((ClassLoader) null));
                    } catch (IllegalAccessException e) {
                        Log.e(IPCUtils.TAG, "copyInstance", e);
                    }
                }
            }
        }

        public static <T> void writeToParcel(T t, Parcel parcel, List<String> list) {
            Field[] fields = t.getClass().getFields();
            Arrays.sort(fields, FieldComparator);
            for (Field field : fields) {
                if (!((list != null && list.contains(field.getName())) || Modifier.isStatic(field.getModifiers()))) {
                    try {
                        parcel.writeValue(field.get(t));
                    } catch (IllegalAccessException e) {
                        Log.e(IPCUtils.TAG, "copyInstance", e);
                    }
                }
            }
        }
    }

    public static class StringChunker {
        public static final int CHUNK_LIMIT = 10000;

        public static void chunk(String str, List<IPCChunkToken> list, int i) {
            chunk(str, list, CHUNK_LIMIT, i);
        }

        public static void chunk(String str, List<IPCChunkToken> list, int i, int i2) {
            int countChunks = countChunks(str, i);
            int i3 = 0;
            int i4 = list.size() > 0 ? list.get(list.size() - 1).index + 1 : 0;
            while (i3 < countChunks) {
                int i5 = i3 + 1;
                list.add(new IPCChunkToken(str.substring(i3 * i, Math.min(i5 * i, str.length())), i3 + i4, i2));
                i3 = i5;
            }
        }

        public static int countChunks(String str) {
            return countChunks(str, CHUNK_LIMIT);
        }

        public static int countChunks(String str, int i) {
            if (!shouldChunk(str, i)) {
                return 1;
            }
            return (int) Math.ceil(((double) str.length()) / ((double) i));
        }

        public static String restore(List<IPCChunkToken> list) {
            StringBuilder sb = new StringBuilder();
            for (IPCChunkToken data : list) {
                sb.append(data.getData());
            }
            return sb.toString();
        }

        public static boolean shouldChunk(String str) {
            return shouldChunk(str, CHUNK_LIMIT);
        }

        public static boolean shouldChunk(String str, int i) {
            return str.length() > i;
        }
    }
}
