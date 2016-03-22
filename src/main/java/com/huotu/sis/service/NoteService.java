package com.huotu.sis.service;

import com.huotu.sis.model.NoteModel;

/**
 * Created by lgh on 2015/12/30.
 */
public interface NoteService {

    /**
     * 设置发送短信的model
     * @param phone         电话号码
     * @param type          类型(场景)
     * @param merchantId    商户ID
     * @return  发送短信的model
     */
    NoteModel setNoteModel(String phone, int type, Long merchantId);




}
