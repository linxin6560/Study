package com.levylin.study.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class SessionInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        Object user = invocation.getController().getSessionAttr("user");
        if (user == null) {
            invocation.getController().render("/pages/admin/login.html");
        } else {
            invocation.invoke();
        }
    }
}
