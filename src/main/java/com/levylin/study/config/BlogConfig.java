package com.levylin.study.config;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.config.*;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;
import com.levylin.study.controller.CategorySubController;
import com.levylin.study.controller.CategorySuperController;
import com.levylin.study.controller.MeController;
import com.levylin.study.controller.TagController;
import com.levylin.study.controller.admin.CategoryController;
import com.levylin.study.controller.admin.FileController;
import com.levylin.study.controller.admin.PictureController;
import com.levylin.study.controller.admin.UserController;
import com.levylin.study.handler.HtmlExtensionHandler;
import com.levylin.study.lucene.ArticleLuceneService;
import com.levylin.study.model.CategorySuper;
import com.levylin.study.model._MappingKit;
import freemarker.template.TemplateModelException;
import org.apache.log4j.Logger;

import java.util.TimeZone;
import java.util.TreeSet;

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
        _MappingKit.mapping(arp);
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
            TreeSet<CategorySuper> treeSet = new TreeSet<CategorySuper>(CategorySuper.dao.find("select * from category_super"));
            FreeMarkerRender.getConfiguration().setSharedVariable("categorySuperList", treeSet);
        } catch (TemplateModelException e) {
            log.error("set freemarkerrender share variable categorySuperList failed", e);
        }
    }
}