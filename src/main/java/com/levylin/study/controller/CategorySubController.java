package com.levylin.study.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.levylin.study.config.BlogConstants;
import com.levylin.study.model.Article;
import com.levylin.study.model.CategorySub;

public class CategorySubController extends Controller {
    public void index() {
        final Integer categorySubId = getParaToInt(0);
        Integer pageNum = getParaToInt("p", 1);
        Page<Article> page = Article.dao.paginateByCache("article", "categorySubId_" + categorySubId + "_" + pageNum, pageNum, BlogConstants.PAGE_SIZE,
                "select * ",
                "from article where finish = 1 and categorySubId = ? order by id desc",
                categorySubId);
        CategorySub categorySub = CacheKit.get("article", "categorySubId_" + categorySubId, new IDataLoader() {
            @Override
            public Object load() {
                return CategorySub.dao.findById(categorySubId);
            }
        });
        setAttr("page", page);
        setAttr("title", "类别:" + categorySub.getName() + " -- " + BlogConstants.TITLE);
        render("articles.html");
    }
}
