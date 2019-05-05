package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.UserBox;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyBoxParserImpl extends AbstractBoxParser {
    static String[] EMPTY_STRING_ARRAY;
    StringBuilder buildLookupStrings;
    String clazzName;
    Pattern constuctorPattern;
    Properties mapping;
    String[] param;

    static {
        EMPTY_STRING_ARRAY = new String[0];
    }

    public PropertyBoxParserImpl(Properties properties) {
        this.constuctorPattern = Pattern.compile("(.*)\\((.*?)\\)");
        this.buildLookupStrings = new StringBuilder();
        this.mapping = properties;
    }

    public PropertyBoxParserImpl(String... strArr) {
        this.constuctorPattern = Pattern.compile("(.*)\\((.*?)\\)");
        this.buildLookupStrings = new StringBuilder();
        InputStream resourceAsStream = getClass().getResourceAsStream("/isoparser-default.properties");
        InputStream openStream;
        try {
            this.mapping = new Properties();
            this.mapping.load(resourceAsStream);
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader == null) {
                contextClassLoader = ClassLoader.getSystemClassLoader();
            }
            Enumeration resources = contextClassLoader.getResources("isoparser-custom.properties");
            while (resources.hasMoreElements()) {
                openStream = ((URL) resources.nextElement()).openStream();
                this.mapping.load(openStream);
                openStream.close();
            }
            for (String resourceAsStream2 : strArr) {
                this.mapping.load(getClass().getResourceAsStream(resourceAsStream2));
            }
            try {
                resourceAsStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        } catch (Throwable th) {
            try {
                resourceAsStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }

    public Box createBox(String str, byte[] bArr, String str2) {
        invoke(str, bArr, str2);
        try {
            Class cls = Class.forName(this.clazzName);
            if (this.param.length <= 0) {
                return (Box) cls.newInstance();
            }
            Class[] clsArr = new Class[this.param.length];
            Object[] objArr = new Object[this.param.length];
            for (int i = 0; i < this.param.length; i++) {
                if ("userType".equals(this.param[i])) {
                    objArr[i] = bArr;
                    clsArr[i] = byte[].class;
                } else if ("type".equals(this.param[i])) {
                    objArr[i] = str;
                    clsArr[i] = String.class;
                } else if ("parent".equals(this.param[i])) {
                    objArr[i] = str2;
                    clsArr[i] = String.class;
                } else {
                    throw new InternalError("No such param: " + this.param[i]);
                }
            }
            return (Box) cls.getConstructor(clsArr).newInstance(objArr);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        } catch (Throwable e22) {
            throw new RuntimeException(e22);
        } catch (Throwable e222) {
            throw new RuntimeException(e222);
        } catch (Throwable e2222) {
            throw new RuntimeException(e2222);
        }
    }

    public void invoke(String str, byte[] bArr, String str2) {
        Object property;
        if (bArr == null) {
            property = this.mapping.getProperty(str);
            if (property == null) {
                String stringBuilder = this.buildLookupStrings.append(str2).append('-').append(str).toString();
                this.buildLookupStrings.setLength(0);
                property = this.mapping.getProperty(stringBuilder);
            }
        } else if (UserBox.TYPE.equals(str)) {
            property = this.mapping.getProperty("uuid[" + Hex.encodeHex(bArr).toUpperCase() + "]");
            if (property == null) {
                property = this.mapping.getProperty(new StringBuilder(String.valueOf(str2)).append("-uuid[").append(Hex.encodeHex(bArr).toUpperCase()).append("]").toString());
            }
            if (property == null) {
                property = this.mapping.getProperty(UserBox.TYPE);
            }
        } else {
            throw new RuntimeException("we have a userType but no uuid box type. Something's wrong");
        }
        if (property == null) {
            property = this.mapping.getProperty("default");
        }
        if (property == null) {
            throw new RuntimeException("No box object found for " + str);
        } else if (property.endsWith(")")) {
            Matcher matcher = this.constuctorPattern.matcher(property);
            if (matcher.matches()) {
                this.clazzName = matcher.group(1);
                if (matcher.group(2).length() == 0) {
                    this.param = EMPTY_STRING_ARRAY;
                    return;
                } else {
                    this.param = matcher.group(2).length() > 0 ? matcher.group(2).split(",") : new String[0];
                    return;
                }
            }
            throw new RuntimeException("Cannot work with that constructor: " + property);
        } else {
            this.param = EMPTY_STRING_ARRAY;
            this.clazzName = property;
        }
    }
}
