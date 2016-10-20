package com.levylin.study.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.levylin.study.config.BlogConstants;
import com.levylin.study.model.Article;
import com.levylin.study.model.Project;

public class ProjectController extends Controller {
    public void index() {
        final Integer projectId = getParaToInt(0);
        Integer pageNum = getParaToInt("p", 1);
        Page<Article> page = Article.dao.paginateByCache("article", "projectId_" + projectId + "_" + pageNum, pageNum, BlogConstants.PAGE_SIZE,
                "select * ",
                "from article where finish = 1 and projectId = ? order by id desc",
                projectId);
        Project project = CacheKit.get("article", "projectId_" + projectId, new IDataLoader() {
            @Override
            public Object load() {
                return Project.dao.findById(projectId);
            }
        });
        setAttr("page", page);
        setAttr("title", "项目:" + project.getName() + " -- " + BlogConstants.TITLE);
        render("articles.html");
    }
}
