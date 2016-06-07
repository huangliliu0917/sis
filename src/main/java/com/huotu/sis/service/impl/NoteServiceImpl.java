package com.huotu.sis.service.impl;

import com.huotu.sis.common.EnumHelper;
import com.huotu.sis.common.StringHelper;
import com.huotu.sis.model.sisweb.NoteModel;
import com.huotu.sis.model.sisweb.VerificationType;
import com.huotu.sis.service.NoteService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
 * Created by slt on 2016/2/29.
 */
@Service
public class NoteServiceImpl implements NoteService {


    @Override
    public NoteModel setNoteModel(String phone, int type,Long merchantId) {
        NoteModel noteModel=new NoteModel();
        noteModel.setPhone(phone);
        noteModel.setCode(StringHelper.RandomNum(new Random(), 4));
        noteModel.setType(EnumHelper.getEnumType(VerificationType.class, type));
        noteModel.setMerchantId(merchantId);
        noteModel.setDate(new Date());
        return noteModel;
    }
}
