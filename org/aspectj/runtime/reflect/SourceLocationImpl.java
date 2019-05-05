package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.SourceLocation;

class SourceLocationImpl implements SourceLocation {
    String fileName;
    int line;
    Class withinType;

    SourceLocationImpl(Class cls, String str, int i) {
        this.withinType = cls;
        this.fileName = str;
        this.line = i;
    }

    public int getColumn() {
        return -1;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getLine() {
        return this.line;
    }

    public Class getWithinType() {
        return this.withinType;
    }

    public String toString() {
        return new StringBuffer().append(getFileName()).append(":").append(getLine()).toString();
    }
}
