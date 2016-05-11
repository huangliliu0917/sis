package com.huotu.sis.service.impl;

import com.huotu.sis.service.IndirectPushFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

/**
 * Created by lgh on 2016/5/9.
 */
@Service
public class IndirectPushFlowServiceImpl implements IndirectPushFlowService {

    @Autowired
    private EntityManager entityManager;



}
