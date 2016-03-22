package com.huotu.sis.service.impl;

import com.huotu.sis.exception.InterrelatedException;
import com.huotu.sis.exception.NoteSendException;
import com.huotu.sis.model.NoteModel;
import com.huotu.sis.service.VerificationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 负责处理短信发送的异常
 * Created by Administrator on 2016/1/29.
 */
public class NoteSendHandle {
    private static Log log = LogFactory.getLog(NoteSendHandle.class);

    /**
     * 发送短信
     * @param model                 发送短信的model
     * @param verificationService   提供发送短信的厂商实现
     * @throws NoteSendException    发送短信时出现的异常
     */
    public static void sendCode(NoteModel model,VerificationService verificationService) throws NoteSendException{
        try{
            verificationService.sendCode(model);
        } catch (IllegalStateException ex) {
            //验证码发送间隔为90秒
            throw new NoteSendException("验证码发送间隔为90秒");

        } catch (IllegalArgumentException ex) {
            throw new NoteSendException("不合法的手机号");

        } catch (NoSuchMethodException ex) {
            //发送类别不受支持！
            throw new NoteSendException("发送类别不受支持");

        } catch (InterrelatedException ex) {
            //第三方错误！
            log.error("短信发送失败", ex);
            throw new NoteSendException("短信发送失败:"+ex.getMessage());
        }
    }



}
