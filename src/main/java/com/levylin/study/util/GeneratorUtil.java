package com.levylin.study.util;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;

/**
 * GeneratorUtil
 */
public class GeneratorUtil {

    public static DataSource getDataSource() {
        Prop p = PropKit.use("conf.properties");
        String dbname, username, password, host, port;
        dbname = p.get("dbname");
        username = p.get("username", "root");
        password = p.get("password", "123456");
        host = p.get("host", "localhost");
        port = p.get("port", "3306");
        C3p0Plugin c3p0Plugin = new C3p0Plugin("jdbc:mysql://" + host + ":" + port + "/" + dbname, username, password);
        c3p0Plugin.start();
        return c3p0Plugin.getDataSource();
    }

    public static void main(String[] args) {
        // base model 所使用的包名
        String baseModelPackageName = "com.levylin.study.model.base";
        // base model 文件保存路径
        String baseModelOutputDir = PathKit.getWebRootPath() + "/../java/com/levylin/study/model/base";

        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = "com.levylin.study.model";
        // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
        String modelOutputDir = baseModelOutputDir + "/..";

        // 创建生成器
        Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
//        // 设置数据库方言
        gernerator.setDialect(new MysqlDialect());
        // 设置是否在 Model 中生成 dao 对象
        gernerator.setGenerateDaoInModel(true);
        // 设置是否生成字典文件
        gernerator.setGenerateDataDictionary(false);
        // 生成
        gernerator.generate();
    }
}




