package com.googlecode.mp4parser.srt;

public class SrtParser {
    private static long parse(String str) {
        return (((((Long.parseLong(str.split(":")[0].trim()) * 60) * 60) * 1000) + ((Long.parseLong(str.split(":")[1].trim()) * 60) * 1000)) + (Long.parseLong(str.split(":")[2].split(",")[0].trim()) * 1000)) + Long.parseLong(str.split(":")[2].split(",")[1].trim());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.googlecode.mp4parser.authoring.tracks.TextTrackImpl parse(java.io.InputStream r9) {
        /*
        r0 = new java.io.LineNumberReader;
        r1 = new java.io.InputStreamReader;
        r2 = "UTF-8";
        r1.<init>(r9, r2);
        r0.<init>(r1);
        r7 = new com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;
        r7.<init>();
    L_0x0012:
        r1 = r0.readLine();
        if (r1 != 0) goto L_0x0019;
    L_0x0018:
        return r7;
    L_0x0019:
        r1 = r0.readLine();
        r6 = "";
    L_0x0020:
        r2 = r0.readLine();
        if (r2 == 0) goto L_0x0033;
    L_0x0026:
        r3 = r2.trim();
        r4 = "";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x005c;
    L_0x0033:
        r2 = "-->";
        r2 = r1.split(r2);
        r3 = 0;
        r2 = r2[r3];
        r2 = parse(r2);
        r4 = "-->";
        r1 = r1.split(r4);
        r4 = 1;
        r1 = r1[r4];
        r4 = parse(r1);
        r8 = r7.getSubs();
        r1 = new com.googlecode.mp4parser.authoring.tracks.TextTrackImpl$Line;
        r1.<init>(r2, r4, r6);
        r8.add(r1);
        goto L_0x0012;
    L_0x005c:
        r3 = new java.lang.StringBuilder;
        r4 = java.lang.String.valueOf(r6);
        r3.<init>(r4);
        r2 = r3.append(r2);
        r3 = "\n";
        r2 = r2.append(r3);
        r6 = r2.toString();
        goto L_0x0020;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.srt.SrtParser.parse(java.io.InputStream):com.googlecode.mp4parser.authoring.tracks.TextTrackImpl");
    }
}
