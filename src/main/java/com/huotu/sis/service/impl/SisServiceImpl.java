package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.SisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by Administrator on 2016/1/29.
 */
@Service
public class SisServiceImpl implements SisService {
    @Autowired
    SisRepository sisRepository;

    @Override
    public long getSisLevelId(User user) throws Exception {
        Sis sis=sisRepository.findByUser(user);
        if(!Objects.isNull(sis)){
            SisLevel sisLevel=sis.getSisLevel();
            if(!Objects.isNull(sisLevel)){
                return sisLevel.getId();
            }
        }
        return 0;
    }

    @Override
    public Integer getSisLevel(User user) {
        Sis sis=sisRepository.findByUser(user);
        if(!Objects.isNull(sis)){
            SisLevel sisLevel=sis.getSisLevel();
            if(!Objects.isNull(sisLevel)){
                return sisLevel.getLevelNo();
            }
        }
        return null;
    }
}
