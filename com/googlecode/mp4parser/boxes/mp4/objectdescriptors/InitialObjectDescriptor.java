package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class InitialObjectDescriptor extends ObjectDescriptorBase {
    int audioProfileLevelIndication;
    List<ESDescriptor> esDescriptors;
    List<ExtensionDescriptor> extensionDescriptors;
    int graphicsProfileLevelIndication;
    int includeInlineProfileLevelFlag;
    int oDProfileLevelIndication;
    private int objectDescriptorId;
    int sceneProfileLevelIndication;
    List<BaseDescriptor> unknownDescriptors;
    int urlFlag;
    int urlLength;
    String urlString;
    int visualProfileLevelIndication;

    public InitialObjectDescriptor() {
        this.esDescriptors = new ArrayList();
        this.extensionDescriptors = new ArrayList();
        this.unknownDescriptors = new ArrayList();
    }

    public void parseDetail(ByteBuffer byteBuffer) {
        BaseDescriptor createFrom;
        int readUInt16 = IsoTypeReader.readUInt16(byteBuffer);
        this.objectDescriptorId = (65472 & readUInt16) >> 6;
        this.urlFlag = (readUInt16 & 63) >> 5;
        this.includeInlineProfileLevelFlag = (readUInt16 & 31) >> 4;
        readUInt16 = getSize() - 2;
        if (this.urlFlag == 1) {
            this.urlLength = IsoTypeReader.readUInt8(byteBuffer);
            this.urlString = IsoTypeReader.readString(byteBuffer, this.urlLength);
            readUInt16 -= this.urlLength + 1;
        } else {
            this.oDProfileLevelIndication = IsoTypeReader.readUInt8(byteBuffer);
            this.sceneProfileLevelIndication = IsoTypeReader.readUInt8(byteBuffer);
            this.audioProfileLevelIndication = IsoTypeReader.readUInt8(byteBuffer);
            this.visualProfileLevelIndication = IsoTypeReader.readUInt8(byteBuffer);
            this.graphicsProfileLevelIndication = IsoTypeReader.readUInt8(byteBuffer);
            int i = readUInt16 - 5;
            if (i > 2) {
                createFrom = ObjectDescriptorFactory.createFrom(-1, byteBuffer);
                i -= createFrom.getSize();
                if (createFrom instanceof ESDescriptor) {
                    this.esDescriptors.add((ESDescriptor) createFrom);
                    readUInt16 = i;
                } else {
                    this.unknownDescriptors.add(createFrom);
                }
            }
            readUInt16 = i;
        }
        if (readUInt16 > 2) {
            createFrom = ObjectDescriptorFactory.createFrom(-1, byteBuffer);
            if (createFrom instanceof ExtensionDescriptor) {
                this.extensionDescriptors.add((ExtensionDescriptor) createFrom);
            } else {
                this.unknownDescriptors.add(createFrom);
            }
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("InitialObjectDescriptor");
        stringBuilder.append("{objectDescriptorId=").append(this.objectDescriptorId);
        stringBuilder.append(", urlFlag=").append(this.urlFlag);
        stringBuilder.append(", includeInlineProfileLevelFlag=").append(this.includeInlineProfileLevelFlag);
        stringBuilder.append(", urlLength=").append(this.urlLength);
        stringBuilder.append(", urlString='").append(this.urlString).append('\'');
        stringBuilder.append(", oDProfileLevelIndication=").append(this.oDProfileLevelIndication);
        stringBuilder.append(", sceneProfileLevelIndication=").append(this.sceneProfileLevelIndication);
        stringBuilder.append(", audioProfileLevelIndication=").append(this.audioProfileLevelIndication);
        stringBuilder.append(", visualProfileLevelIndication=").append(this.visualProfileLevelIndication);
        stringBuilder.append(", graphicsProfileLevelIndication=").append(this.graphicsProfileLevelIndication);
        stringBuilder.append(", esDescriptors=").append(this.esDescriptors);
        stringBuilder.append(", extensionDescriptors=").append(this.extensionDescriptors);
        stringBuilder.append(", unknownDescriptors=").append(this.unknownDescriptors);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
