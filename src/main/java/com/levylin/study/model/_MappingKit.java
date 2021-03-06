package com.levylin.study.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("article", "id", Article.class);
		arp.addMapping("category_sub", "id", CategorySub.class);
		arp.addMapping("category_super", "id", CategorySuper.class);
		arp.addMapping("comment", "id", Comment.class);
		arp.addMapping("message", "id", Message.class);
		arp.addMapping("project", "id", Project.class);
		arp.addMapping("user", "id", User.class);
	}
}

