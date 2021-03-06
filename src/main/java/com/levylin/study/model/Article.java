package com.levylin.study.model;

import com.levylin.study.config.BlogConstants;
import com.levylin.study.model.base.BaseArticle;
import com.levylin.study.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Generated by JFinal.
 */
public class Article extends BaseArticle<Article> {
    public static final Article dao = new Article();
    private static final long serialVersionUID = 3875646016654360016L;

    private String _content;

    public String get_content() {
        if (null == _content) {
            Document doc = Jsoup.parse(getContent());
            Elements eles = doc.select("img[src^=/img/u]");
            Iterator<Element> iter = eles.listIterator();
            while (iter.hasNext()) {
                Element ele = iter.next();
                String src = ele.attr("src");
                //"http://abap_img.cloudfoundry.com" +
                ele.attr("data-src", src);
                ele.attr("src", "/img/s/grey.png");
                ele.attr("class", "lazy");
                ele.attr("alt", getTitle());
            }
            eles = doc.select("pre.prettyprint");
            iter = eles.listIterator();
            Set<String> brushs = new HashSet<String>();
            while (iter.hasNext()) {
                Element ele = iter.next();
                String cls = ele.attr("class");
                String lang = cls.substring(cls.lastIndexOf(":") + 1);
                brushs.add(BlogConstants.SHL_MAPPING.get(lang));
            }
            for (String brush : brushs) {
                doc.append("<script type=\"text/javascript\" src=\"/syntaxhighlighter/scripts/shBrush" + brush + ".js\"></script>");
            }
            _content = doc.html();
        }
        return _content;
    }

    private String brief;

    public String getBrief() {
        if (null == brief) {
            brief = StringUtil.subString(Jsoup.parse(getContent()).text(), 2500);
        }
        return brief;
    }

    private String _desc;

    public String get_desc() {
        if (null == _desc) {
            _desc = StringUtil.subString(Jsoup.parse(getContent()).text(), 300);
        }
        return _desc;
    }

    private String _type;

    public String get_type() {
        if (null == _type) {
            switch (getInt("type")) {
                case 0:
                    _type = "原创";
                    break;
                case 1:
                    _type = "转载";
                    break;
            }
        }
        return _type;
    }

    private CategorySub _categorySub;

    public CategorySub getCategorySub() {
        if (_categorySub == null) {
            _categorySub = CategorySub.dao.findById(getCategorySubId());
        }
        return _categorySub;
    }

    private List<String> _tags;

    public List<String> get_tags() {
        if (null == _tags) {
            _tags = Arrays.asList(getTags().split(","));
        }
        return _tags;
    }

    private Project _project;

    public Project getProject() {
        if (_project == null) {
            _project = Project.dao.findById(getProjectId());
        }
        return _project;
    }

    private List<Comment> _comments;

    public List<Comment> getComments() {
        if (_comments == null) {
            _comments = Comment.dao
                    .find("select * from comment where articleId = ? and pId = 0 order by id asc", getId());
        }
        return _comments;
    }

    public List<Article> _relates;

    public List<Article> getRelates() {
        if (_relates == null) {
            _relates = Article.dao
                    .find("select id,title from article where finish = 1 and id != ? and categorySubId = ? order by id desc limit 0,4", getId(), getCategorySubId());
        }
        return _relates;
    }
}
