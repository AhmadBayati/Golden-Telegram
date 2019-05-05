package com.hanista.mobogram.messenger.exoplayer.text.subrip;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.exoplayer.text.SubtitleParser;
import com.hanista.mobogram.messenger.exoplayer.util.LongArray;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SubripParser implements SubtitleParser {
    private static final Pattern SUBRIP_TIMESTAMP;
    private static final Pattern SUBRIP_TIMING_LINE;
    private static final String TAG = "SubripParser";
    private final StringBuilder textBuilder;

    static {
        SUBRIP_TIMING_LINE = Pattern.compile("(\\S*)\\s*-->\\s*(\\S*)");
        SUBRIP_TIMESTAMP = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+),(\\d+)");
    }

    public SubripParser() {
        this.textBuilder = new StringBuilder();
    }

    private static long parseTimecode(String str) {
        Matcher matcher = SUBRIP_TIMESTAMP.matcher(str);
        if (matcher.matches()) {
            return (Long.parseLong(matcher.group(4)) + (((((Long.parseLong(matcher.group(1)) * 60) * 60) * 1000) + ((Long.parseLong(matcher.group(2)) * 60) * 1000)) + (Long.parseLong(matcher.group(3)) * 1000))) * 1000;
        }
        throw new NumberFormatException("has invalid format");
    }

    public boolean canParse(String str) {
        return MimeTypes.APPLICATION_SUBRIP.equals(str);
    }

    public SubripSubtitle parse(byte[] bArr, int i, int i2) {
        ArrayList arrayList = new ArrayList();
        LongArray longArray = new LongArray();
        ParsableByteArray parsableByteArray = new ParsableByteArray(bArr, i + i2);
        parsableByteArray.setPosition(i);
        while (true) {
            String readLine = parsableByteArray.readLine();
            if (readLine == null) {
                Cue[] cueArr = new Cue[arrayList.size()];
                arrayList.toArray(cueArr);
                return new SubripSubtitle(cueArr, longArray.toArray());
            } else if (readLine.length() != 0) {
                try {
                    Integer.parseInt(readLine);
                    Object readLine2 = parsableByteArray.readLine();
                    Matcher matcher = SUBRIP_TIMING_LINE.matcher(readLine2);
                    if (matcher.find()) {
                        int i3;
                        longArray.add(parseTimecode(matcher.group(1)));
                        if (TextUtils.isEmpty(matcher.group(2))) {
                            i3 = 0;
                        } else {
                            longArray.add(parseTimecode(matcher.group(2)));
                            i3 = 1;
                        }
                        this.textBuilder.setLength(0);
                        while (true) {
                            Object readLine3 = parsableByteArray.readLine();
                            if (TextUtils.isEmpty(readLine3)) {
                                break;
                            }
                            if (this.textBuilder.length() > 0) {
                                this.textBuilder.append("<br>");
                            }
                            this.textBuilder.append(readLine3.trim());
                        }
                        arrayList.add(new Cue(Html.fromHtml(this.textBuilder.toString())));
                        if (i3 != 0) {
                            arrayList.add(null);
                        }
                    } else {
                        Log.w(TAG, "Skipping invalid timing: " + readLine2);
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Skipping invalid index: " + readLine);
                }
            }
        }
    }
}
