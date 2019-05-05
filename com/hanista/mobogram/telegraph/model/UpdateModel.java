package com.hanista.mobogram.telegraph.model;

import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocation;

public class UpdateModel {
    private String changeDate;
    private Long id;
    private boolean isNew;
    private String newValue;
    private String oldValue;
    private int type;
    private int userId;

    public UpdateModel(Long l, int i, String str, String str2, int i2, boolean z, String str3) {
        this.id = l;
        this.type = i;
        this.oldValue = str;
        this.newValue = str2;
        this.userId = i2;
        this.isNew = z;
        this.changeDate = str3;
    }

    public String getChangeDate() {
        return this.changeDate;
    }

    public Long getId() {
        return this.id;
    }

    public int getMessage() {
        return this.type == 1 ? this.newValue.equals("1") ? C0338R.string.get_online : C0338R.string.get_offline : this.type == 2 ? C0338R.string.changed_name : this.type == 3 ? C0338R.string.changed_photo : this.type == 4 ? C0338R.string.changed_phone : C0338R.string.change_status;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public FileLocation getPhotoBig() {
        if (this.type != 3 || this.oldValue == null) {
            return null;
        }
        FileLocation tL_fileLocation = new TL_fileLocation();
        String[] split = this.oldValue.split("#");
        if (split.length != 8) {
            return null;
        }
        tL_fileLocation.dc_id = Integer.parseInt(split[4]);
        tL_fileLocation.local_id = Integer.parseInt(split[5]);
        tL_fileLocation.volume_id = Long.parseLong(split[6]);
        tL_fileLocation.secret = Long.parseLong(split[7]);
        return tL_fileLocation;
    }

    public FileLocation getPhotoSmall() {
        if (this.type != 3 || this.oldValue == null) {
            return null;
        }
        FileLocation tL_fileLocation = new TL_fileLocation();
        String[] split = this.oldValue.split("#");
        if (split.length != 8) {
            return null;
        }
        tL_fileLocation.dc_id = Integer.parseInt(split[0]);
        tL_fileLocation.local_id = Integer.parseInt(split[1]);
        tL_fileLocation.volume_id = Long.parseLong(split[2]);
        tL_fileLocation.secret = Long.parseLong(split[3]);
        return tL_fileLocation;
    }

    public int getType() {
        return this.type;
    }

    public int getUserId() {
        return this.userId;
    }

    public boolean isNew() {
        return this.isNew;
    }

    public void setChangeDate(String str) {
        this.changeDate = str;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public void setNew(boolean z) {
        this.isNew = z;
    }

    public void setNewValue(String str) {
        this.newValue = str;
    }

    public void setOldValue(String str) {
        this.oldValue = str;
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setUserId(int i) {
        this.userId = i;
    }
}
