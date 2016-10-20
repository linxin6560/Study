package com.levylin.study.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.levylin.study.config.BlogConstants;
import com.levylin.study.email.Email;
import com.levylin.study.lucene.ArticleLuceneService;
import com.levylin.study.model.Article;
import com.levylin.study.model.Comment;
import com.levylin.study.model.Message;
import com.levylin.study.model.Project;

import java.util.Date;
import java.util.List;

public class IndexController extends Controller {
    public void index() {
        final Integer pageNum = getParaToInt("p", 1);
        Page<Article> page_news = Article.dao.paginateByCache("article", "page_news_" + pageNum, pageNum, BlogConstants.PAGE_SIZE,
                "select id,title,viewCount,replyCount,createDateTime,content,tags,type",
                " from article where finish = 1 order by id desc");
        setAttr("page_news", page_news);

        List<Article> hotsView = Article.dao.findByCache("article", "hotsView", "select id,title,viewCount from article where finish = 1 order by viewCount desc limit 0,10");
        setAttr("hotsView", hotsView);

        List<Article> hotsReply = Article.dao.findByCache("article", "hotsReplay", "select id,title,viewCount from article where finish = 1 order by replyCount desc limit 0,10");
        setAttr("hotsReply", hotsReply);

        List<Project> projects = Project.dao.findByCache("project", "top", "select id,name,finish from project order by id desc limit 0,10");
        setAttr("projects", projects);

        List<Comment> comments = Comment.dao.findByCache("article", "recently_comments", "select id,content,articleId,nick from comment order by id desc limit 0,10");
        setAttr("comments", comments);

        List<Message> messages = Message.dao.findByCache("item", "recently_messages", "select id,email,content,dateTime,nick from message order by id desc limit 0,10");
        setAttr("messages", messages);

        render("index.html");
    }

    public void search() {
        String keyword = getPara("q");
        Integer pageNo = getParaToInt("p", 1);
        Page<Article> page = null;
        if (!StrKit.isBlank(keyword)) {
            page = ArticleLuceneService.me().query(keyword, 10, pageNo);
        }
        setAttr("q", keyword);
        setAttr("page", page);
        render("article/search_articles.html");
    }

    public void addMessage() {
        render("addMessage.html");
    }

    public void addMessageSubmit() {
        Message message = getModel(Message.class);
        message.set("dateTime", new Date());
        message.save();
        CacheKit.remove("item", "recently_messages");
        Email _email = new Email();
        _email.setSubject("有来自" + message.getNick() + "的新留言");
        _email.setContent("<strong>内容</strong>:" + message.getContent() + "<div><a href='http://abap.cloudfoundry.com/admin'>快去处理吧</a></div>");
        _email.send();
        JSONObject json = new JSONObject();
        json.put("error", 0);
        json.put("msg", "success");
        renderJson(json.toJSONString());
    }
}
