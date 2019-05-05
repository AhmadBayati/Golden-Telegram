package com.googlecode.mp4parser.util;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {
    static final /* synthetic */ boolean $assertionsDisabled;
    static Pattern component;

    static {
        $assertionsDisabled = !Path.class.desiredAssertionStatus();
        component = Pattern.compile("(....|\\.\\.)(\\[(.*)\\])?");
    }

    private Path() {
    }

    public static String createPath(Box box) {
        return createPath(box, TtmlNode.ANONYMOUS_REGION_ID);
    }

    private static String createPath(Box box, String str) {
        Container parent = box.getParent();
        int i = 0;
        for (Box box2 : parent.getBoxes()) {
            if (box2.getType().equals(box.getType())) {
                if (box2 == box) {
                    break;
                }
                i++;
            }
        }
        String stringBuilder = new StringBuilder(String.valueOf(String.format("/%s[%d]", new Object[]{box.getType(), Integer.valueOf(i)}))).append(str).toString();
        return parent instanceof Box ? createPath((Box) parent, stringBuilder) : stringBuilder;
    }

    public static <T extends Box> T getPath(Box box, String str) {
        List paths = getPaths(box, str, true);
        return paths.isEmpty() ? null : (Box) paths.get(0);
    }

    public static <T extends Box> T getPath(Container container, String str) {
        List paths = getPaths(container, str, true);
        return paths.isEmpty() ? null : (Box) paths.get(0);
    }

    public static <T extends Box> T getPath(AbstractContainerBox abstractContainerBox, String str) {
        List paths = getPaths(abstractContainerBox, str, true);
        return paths.isEmpty() ? null : (Box) paths.get(0);
    }

    public static <T extends Box> List<T> getPaths(Box box, String str) {
        return getPaths(box, str, false);
    }

    private static <T extends Box> List<T> getPaths(Box box, String str, boolean z) {
        return getPaths((Object) box, str, z);
    }

    public static <T extends Box> List<T> getPaths(Container container, String str) {
        return getPaths(container, str, false);
    }

    private static <T extends Box> List<T> getPaths(Container container, String str, boolean z) {
        return getPaths((Object) container, str, z);
    }

    private static <T extends Box> List<T> getPaths(AbstractContainerBox abstractContainerBox, String str, boolean z) {
        return getPaths((Object) abstractContainerBox, str, z);
    }

    private static <T extends Box> List<T> getPaths(Object obj, String str, boolean z) {
        Container container;
        int i = 0;
        if (str.startsWith("/")) {
            CharSequence substring = str.substring(1);
            container = obj;
            while (container instanceof Box) {
                container = ((Box) container).getParent();
            }
        } else {
            container = obj;
        }
        if (substring.length() != 0) {
            String substring2;
            if (substring.contains("/")) {
                substring2 = substring.substring(substring.indexOf(47) + 1);
                substring = substring.substring(0, substring.indexOf(47));
            } else {
                substring2 = TtmlNode.ANONYMOUS_REGION_ID;
            }
            Matcher matcher = component.matcher(substring);
            if (matcher.matches()) {
                String group = matcher.group(1);
                if ("..".equals(group)) {
                    return container instanceof Box ? getPaths(((Box) container).getParent(), substring2, z) : Collections.emptyList();
                } else {
                    if (!(container instanceof Container)) {
                        return Collections.emptyList();
                    }
                    int parseInt = matcher.group(2) != null ? Integer.parseInt(matcher.group(3)) : -1;
                    List<T> linkedList = new LinkedList();
                    for (Box box : container.getBoxes()) {
                        int i2;
                        if (box.getType().matches(group)) {
                            if (parseInt == -1 || parseInt == i) {
                                linkedList.addAll(getPaths(box, substring2, z));
                            }
                            i2 = i + 1;
                        } else {
                            i2 = i;
                        }
                        if ((z || parseInt >= 0) && !linkedList.isEmpty()) {
                            return linkedList;
                        }
                        i = i2;
                    }
                    return linkedList;
                }
            }
            throw new RuntimeException(new StringBuilder(String.valueOf(substring)).append(" is invalid path.").toString());
        } else if (container instanceof Box) {
            return Collections.singletonList((Box) container);
        } else {
            throw new RuntimeException("Result of path expression seems to be the root container. This is not allowed!");
        }
    }

    public static boolean isContained(Box box, String str) {
        if ($assertionsDisabled || str.startsWith("/")) {
            return getPaths(box, str).contains(box);
        }
        throw new AssertionError("Absolute path required");
    }
}
