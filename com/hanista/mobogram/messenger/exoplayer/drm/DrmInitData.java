package com.hanista.mobogram.messenger.exoplayer.drm;

import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface DrmInitData {

    public static final class Mapped implements DrmInitData {
        private final Map<UUID, SchemeInitData> schemeData;

        public Mapped() {
            this.schemeData = new HashMap();
        }

        public SchemeInitData get(UUID uuid) {
            return (SchemeInitData) this.schemeData.get(uuid);
        }

        public void put(UUID uuid, SchemeInitData schemeInitData) {
            this.schemeData.put(uuid, schemeInitData);
        }
    }

    public static final class SchemeInitData {
        public final byte[] data;
        public final String mimeType;

        public SchemeInitData(String str, byte[] bArr) {
            this.mimeType = (String) Assertions.checkNotNull(str);
            this.data = (byte[]) Assertions.checkNotNull(bArr);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof SchemeInitData)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            SchemeInitData schemeInitData = (SchemeInitData) obj;
            return this.mimeType.equals(schemeInitData.mimeType) && Arrays.equals(this.data, schemeInitData.data);
        }

        public int hashCode() {
            return this.mimeType.hashCode() + (Arrays.hashCode(this.data) * 31);
        }
    }

    public static final class Universal implements DrmInitData {
        private SchemeInitData data;

        public Universal(SchemeInitData schemeInitData) {
            this.data = schemeInitData;
        }

        public SchemeInitData get(UUID uuid) {
            return this.data;
        }
    }

    SchemeInitData get(UUID uuid);
}
