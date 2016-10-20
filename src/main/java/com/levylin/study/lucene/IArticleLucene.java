package com.levylin.study.lucene;

import com.jfinal.plugin.activerecord.Page;
import com.levylin.study.model.Article;

public interface IArticleLucene {
    public void add(Article article);

    public void indexAll();

    public Page<Article> query(String keyword, Integer pageSize, Integer pageNo);
}
