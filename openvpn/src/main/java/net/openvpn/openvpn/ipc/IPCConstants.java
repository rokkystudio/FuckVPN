package net.openvpn.openvpn.ipc;

/**
 * Набор констант, используемых для сериализации и межпроцессного взаимодействия.
 */
public final class IPCConstants
{
    // Типы сериализуемых данных
    public static final int VAL_STRING = 0;
    public static final int VAL_INTEGER = 1;
    public static final int VAL_MAP = 2;
    public static final int VAL_BUNDLE = 3;
    public static final int VAL_PARCELABLE = 4;
    public static final int VAL_SHORT = 5;
    public static final int VAL_LONG = 6;
    public static final int VAL_FLOAT = 7;
    public static final int VAL_DOUBLE = 8;
    public static final int VAL_BOOLEAN = 9;
    public static final int VAL_CHARSEQUENCE = 10;
    public static final int VAL_LIST = 11;
    public static final int VAL_SPARSEARRAY = 12;
    public static final int VAL_BYTEARRAY = 13;
    public static final int VAL_STRINGARRAY = 14;
    public static final int VAL_IBINDER = 15;
    public static final int VAL_PARCELABLEARRAY = 16;
    public static final int VAL_OBJECTARRAY = 17;
    public static final int VAL_INTARRAY = 18;
    public static final int VAL_LONGARRAY = 19;
    public static final int VAL_BYTE = 20;
    public static final int VAL_SERIALIZABLE = 21;
    public static final int VAL_SPARSEBOOLEANARRAY = 22;
    public static final int VAL_BOOLEANARRAY = 23;
    public static final int VAL_CHARSEQUENCEARRAY = 24;
    public static final int VAL_PERSISTABLEBUNDLE = 25;
    public static final int VAL_SIZE = 26;
    public static final int VAL_SIZEF = 27;
    public static final int VAL_DOUBLEARRAY = 28;
    public static final int VAL_CHUNK = 29;
    public static final int VAL_CHUNKABLE = 30;
    public static final int VAL_NULL = -1;

    /**
     * Поля, используемые в parcel/bundle-передаче данных.
     */
    public static final class Field {
        public static final String Action = "action";
        public static final String Data = "token";
        public static final String ID = "id";
        public static final String Result = "result";

        private Field() {} // предотвращает создание экземпляров
    }

    /**
     * Типы сообщений, участвующих в IPC-обмене.
     */
    public static final class Type {
        public static final int REQUEST = 1;
        public static final int REQUEST_CHUNK = 2;
        public static final int REQUEST_CHUNK_ACK = 3;
        public static final int REQUEST_CHUNK_FIN = 4;

        public static final int RESPONSE = 1;
        public static final int RESPONSE_CHUNK = 2;
        public static final int RESPONSE_CHUNK_ACK = 3;
        public static final int RESPONSE_CHUNK_FIN = 4;

        public static final int REGISTER = 5;
        public static final int UNREGISTER = 6;

        private Type() {}
    }

    private IPCConstants() {} // предотвращает создание экземпляров
}
