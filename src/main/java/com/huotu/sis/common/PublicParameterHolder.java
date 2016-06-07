package com.huotu.sis.common;

import com.huotu.sis.model.sisweb.PublicParameterModel;

/**
 * Created by lgh on 2016/1/8.
 */
public class PublicParameterHolder {
    private static final ThreadLocal<PublicParameterModel> holder = new ThreadLocal();

    public static PublicParameterModel get() {
        return holder.get();
    }

    public static void set(PublicParameterModel model) {
        holder.set(model);
    }





}
