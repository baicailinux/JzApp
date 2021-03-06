package com.suda.jzapp.util;


import com.avos.avoscloud.AVObject;
import com.suda.jzapp.dao.cloud.avos.pojo.account.AVAccount;
import com.suda.jzapp.dao.cloud.avos.pojo.account.AVAccountIndex;
import com.suda.jzapp.dao.cloud.avos.pojo.record.AVRecord;
import com.suda.jzapp.dao.cloud.avos.pojo.record.AVRecordType;
import com.suda.jzapp.dao.cloud.avos.pojo.record.AVRecordTypeIndex;
import com.suda.jzapp.dao.cloud.avos.pojo.system.AVUpdateCheck;
import com.suda.jzapp.dao.cloud.avos.pojo.user.MyAVUser;
import com.suda.jzapp.dao.cloud.avos.pojo.user.UserLink;

/**
 * Created by Suda on 2015/9/16.
 */
public class LoadAVOSLib {

    public static void LoadLib() {
        AVObject.registerSubclass(AVRecordType.class);
        AVObject.registerSubclass(AVRecord.class);
        AVObject.registerSubclass(AVAccount.class);
        AVObject.registerSubclass(AVAccountIndex.class);
        AVObject.registerSubclass(MyAVUser.class);
        AVObject.registerSubclass(AVRecordTypeIndex.class);
        AVObject.registerSubclass(UserLink.class);
        AVObject.registerSubclass(AVUpdateCheck.class);
    }
}
