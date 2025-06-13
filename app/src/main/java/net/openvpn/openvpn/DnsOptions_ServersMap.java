package net.openvpn.openvpn;

import androidx.annotation.NonNull;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;

public class DnsOptions_ServersMap extends AbstractMap<Integer, DnsServer>
{
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;

    public static class Iterator
    {
        private transient long swigCPtr;
        protected transient boolean swigCMemOwn;

        protected Iterator(long cPtr, boolean cMemoryOwn) {
            this.swigCPtr = cPtr;
            this.swigCMemOwn = cMemoryOwn;
        }

        protected static long getCPtr(Iterator obj) {
            return (obj == null) ? 0 : obj.swigCPtr;
        }

        public synchronized void delete() {
            if (swigCPtr != 0) {
                if (swigCMemOwn) {
                    swigCMemOwn = false;
                    ovpncliJNI.delete_DnsOptions_ServersMap_Iterator(swigCPtr);
                }
                swigCPtr = 0;
            }
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                delete();
            } finally {
                super.finalize();
            }
        }

        private int getKey() {
            return ovpncliJNI.DnsOptions_ServersMap_Iterator_getKey(swigCPtr, this);
        }

        private DnsServer getValue() {
            return new DnsServer(ovpncliJNI.DnsOptions_ServersMap_Iterator_getValue(swigCPtr, this), true);
        }

        private Iterator getNextUnchecked() {
            return new Iterator(ovpncliJNI.DnsOptions_ServersMap_Iterator_getNextUnchecked(swigCPtr, this), true);
        }

        private boolean isNot(Iterator other) {
            return ovpncliJNI.DnsOptions_ServersMap_Iterator_isNot(swigCPtr, this, getCPtr(other), other);
        }

        private void setValue(DnsServer server) {
            ovpncliJNI.DnsOptions_ServersMap_Iterator_setValue(swigCPtr, this, DnsServer.getCPtr(server), server);
        }
    }

    // === DnsOptions_ServersMap ===
    public DnsOptions_ServersMap() {
        this(ovpncliJNI.new_DnsOptions_ServersMap__SWIG_0(), true);
    }

    public DnsOptions_ServersMap(DnsOptions_ServersMap other) {
        this(ovpncliJNI.new_DnsOptions_ServersMap__SWIG_1(getCPtr(other), other), true);
    }

    protected DnsOptions_ServersMap(long cPtr, boolean cMemoryOwn) {
        this.swigCPtr = cPtr;
        this.swigCMemOwn = cMemoryOwn;
    }

    protected static long getCPtr(DnsOptions_ServersMap obj) {
        return (obj == null) ? 0 : obj.swigCPtr;
    }

    public synchronized void delete() {
        if (swigCPtr != 0) {
            if (swigCMemOwn) {
                swigCMemOwn = false;
                ovpncliJNI.delete_DnsOptions_ServersMap(swigCPtr);
            }
            swigCPtr = 0;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            delete();
        } finally {
            super.finalize();
        }
    }

    @Override
    public void clear() {
        ovpncliJNI.DnsOptions_ServersMap_clear(swigCPtr, this);
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        return ovpncliJNI.DnsOptions_ServersMap_containsImpl(swigCPtr, this, (Integer) key);
    }

    @Override
    public DnsServer get(Object key) {
        if (!(key instanceof Integer)) return null;
        Iterator iter = find((Integer) key);
        return iter.isNot(end()) ? iter.getValue() : null;
    }

    @Override
    public DnsServer put(Integer key, DnsServer value) {
        Iterator iter = find(key);
        if (iter.isNot(end())) {
            DnsServer old = iter.getValue();
            iter.setValue(value);
            return old;
        }
        putUnchecked(key, value);
        return null;
    }

    @Override
    public DnsServer remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Iterator iter = find((Integer) key);
        if (iter.isNot(end())) {
            DnsServer old = iter.getValue();
            removeUnchecked(iter);
            return old;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return ovpncliJNI.DnsOptions_ServersMap_isEmpty(swigCPtr, this);
    }

    @Override
    public int size() {
        return ovpncliJNI.DnsOptions_ServersMap_sizeImpl(swigCPtr, this);
    }

    @Override @NonNull
    public Set<Entry<Integer, DnsServer>> entrySet()
    {
        Set<Entry<Integer, DnsServer>> entries = new HashSet<>();
        Iterator iter = begin();
        Iterator end = end();
        while (iter.isNot(end))
        {
            final Iterator current = iter;
            entries.add(new Entry<>()
            {
                public Integer getKey() {
                    return current.getKey();
                }

                public DnsServer getValue() {
                    return current.getValue();
                }

                public DnsServer setValue(DnsServer newValue) {
                    DnsServer old = current.getValue();
                    current.setValue(newValue);
                    return old;
                }
            });
            iter = iter.getNextUnchecked();
        }
        return entries;
    }

    // --- Internal native wrappers ---
    private Iterator begin() {
        return new Iterator(ovpncliJNI.DnsOptions_ServersMap_begin(swigCPtr, this), true);
    }

    private Iterator end() {
        return new Iterator(ovpncliJNI.DnsOptions_ServersMap_end(swigCPtr, this), true);
    }

    private Iterator find(int key) {
        return new Iterator(ovpncliJNI.DnsOptions_ServersMap_find(swigCPtr, this, key), true);
    }

    private void putUnchecked(int key, DnsServer server) {
        ovpncliJNI.DnsOptions_ServersMap_putUnchecked(swigCPtr, this, key, DnsServer.getCPtr(server), server);
    }

    private void removeUnchecked(Iterator iter) {
        ovpncliJNI.DnsOptions_ServersMap_removeUnchecked(swigCPtr, this, Iterator.getCPtr(iter), iter);
    }
}
