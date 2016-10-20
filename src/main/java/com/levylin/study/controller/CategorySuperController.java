package com.levylin.study.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.levylin.study.core.BlogConstants;
import com.levylin.study.pojo.Article;
import com.levylin.study.pojo.CategorySuper;

public class CategorySuperController extends Controller {
    public void index() {
        final Integer categorySuperId = getParaToInt(0);
        Integer pageNum = getParaToInt("p", 1);
        Page<Article> page = Article.dao.paginateByCache("article", "categorySuperId_" + categorySuperId + "_" + pageNum, pageNum, BlogConstants.PAGE_SIZE,
                "select a.* ",
                "from article a,category_sub c where a.finish = 1 and a.categorySubId = c.id and c.pId = ? order by id desc",
                categorySuperId);
        CategorySuper categorySuper = CacheKit.get("article", "categorySuperId_" + categorySuperId, new IDataLoader() {
            @Override
            public Object load() {
                return CategorySuper.dao.findById(categorySuperId);
            }
        });
        setAttr("page", page);
        setAttr("title", "类别:" + categorySuper.getStr(CategorySuper.NAME) + " -- " + BlogConstants.TITLE);
        render("articles.html");
    }
}
