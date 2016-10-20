package com.levylin.study.core;

import java.util.TimeZone;
import java.util.TreeSet;

import com.jfinal.kit.StrKit;
import com.levylin.study.controller.*;
import com.levylin.study.controller.admin.*;
import com.levylin.study.handler.HtmlExtensionHandler;
import com.levylin.study.lucene.ArticleLuceneService;
import com.levylin.study.pojo.*;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;

import freemarker.template.TemplateModelException;

public class BlogConfig extends JFinalConfig {

    private static final Logger log = Logger.getLogger(BlogConfig.class);

    private final String json = System.getenv("VCAP_SERVICES");
    private boolean isLocal = StrKit.isBlank(json);

    @Override
    public void configConstant(Constants me) {
        loadPropertyFile("conf.properties");
        if (isLocal) {
            me.setDevMode(true);
        }
        me.setBaseViewPath("/pages");
        me.setError404View("/404.html");
        me.setError500View("/500.html");
    }

    @Override
    public void configRoute(Routes me) {

        me.add("/", com.levylin.study.controller.IndexController.class);
        me.add("/article", com.levylin.study.controller.ArticleController.class, "article");
        me.add("/categorySuper", CategorySuperController.class, "article");
        me.add("/categorySub", CategorySubController.class, "article");
        me.add("/project", com.levylin.study.controller.ProjectController.class, "article");
        me.add("/tag", TagController.class, "article");
        me.add("/me", MeController.class, "me");
        //backend
        me.add("/admin", com.levylin.study.controller.admin.IndexController.class, "admin");
        me.add("/admin/user", UserController.class, "admin/user");
        me.add("/admin/article", com.levylin.study.controller.admin.ArticleController.class, "admin/article");
        me.add("/admin/article/category", CategoryController.class, "admin/article/category");
        me.add("/admin/article/project", com.levylin.study.controller.admin.ProjectController.class, "admin/article/project");
        me.add("/admin/picture", PictureController.class, "admin/picture");
        me.add("/admin/file", FileController.class);
    }

    @Override
    public void configPlugin(Plugins me) {
        me.add(new EhCachePlugin());
        String dbname, username, password, host, port;
        if (isLocal) {
            dbname = getProperty("dbname");
            username = getProperty("username", "root");
            password = getProperty("password", "root");
            host = getProperty("host", "localhost");
            port = getProperty("port", "3306");
        } else {
            JSONObject credentials = JSONObject.parseObject(json)
                    .getJSONArray("mysql-5.1").getJSONObject(0)
                    .getJSONObject("credentials");
            dbname = credentials.getString("name");
            username = credentials.getString("username");
            password = credentials.getString("password");
            host = credentials.getString("host");
            port = credentials.getString("port");
        }
        C3p0Plugin cp = new C3p0Plugin("jdbc:mysql://" + host + ":" + port
                + "/" + dbname, username, password);
        me.add(cp);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
        if (isLocal) {
            arp.setShowSql(true);
        }
        me.add(arp);
        arp.addMapping("user", User.class);
        arp.addMapping("article", Article.class);
        arp.addMapping("comment", Comment.class);
        arp.addMapping("category_sub", CategorySub.class);
        arp.addMapping("category_super", CategorySuper.class);
        arp.addMapping("project", Project.class);
        arp.addMapping("message", Message.class);
    }

    @Override
    public void configInterceptor(Interceptors me) {
    }

    @Override
    public void configHandler(Handlers me) {
        me.add(new HtmlExtensionHandler());
    }

    @Override
    public void afterJFinalStart() {
        // 设置默认时间为北京时间
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        BlogConstants.PAGE_SIZE = getPropertyToInt("pageSize");
        BlogConstants.TITLE = getProperty("title");
        BlogConstants.EMAIL_USERNAME = getProperty("email_username");
        BlogConstants.EMAIL_PASSWORD = getProperty("email_password");
        BlogConstants.EMAIL_PROTOCOL = getProperty("email_protocol", "smtp");
        BlogConstants.EMAIL_HOST = getProperty("email_host");
        if (StrKit.isBlank(BlogConstants.EMAIL_USERNAME)) {
            System.out.println("ERROR:用户名为空!!!");
        }
        if (StrKit.isBlank(BlogConstants.EMAIL_PASSWORD)) {
            System.out.println("ERROR:邮箱密码为空!!!");
        }
        if (StrKit.isBlank(BlogConstants.EMAIL_HOST)) {
            System.out.println("ERROR:邮箱服务器为空!!!");
        }
        try {
            FreeMarkerRender.getConfiguration().setSharedVariable("title", BlogConstants.TITLE);
        } catch (TemplateModelException e) {
            log.error("set freemarkerrender share variable title failed", e);
        }
        updateCategorySuperList();
        ArticleLuceneService.me().indexAll();
    }

    public static void updateCategorySuperList() {
        try {
            FreeMarkerRender.getConfiguration().setSharedVariable(
                    "categorySuperList",
                    new TreeSet<CategorySuper>(CategorySuper.dao
                            .find("select * from category_super")));
        } catch (TemplateModelException e) {
            log.error("set freemarkerrender share variable categorySuperList failed", e);
        }
    }

}
