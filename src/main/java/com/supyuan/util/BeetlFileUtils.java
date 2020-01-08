package com.supyuan.util;

import java.io.IOException;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;

public class BeetlFileUtils {

	static BeetlFileUtils bfu = null;
	private GroupTemplate gt = null;

	public static BeetlFileUtils getFileUtils() {
		if (bfu == null) {
			bfu = new BeetlFileUtils();
		}
		return bfu;
	}

	public BeetlFileUtils() {
		FileResourceLoader resourceLoader = new FileResourceLoader(this.getClass().getResource("/beetl").getPath(),
				"utf-8");
		Configuration cfg;
		try {
			cfg = Configuration.defaultConfiguration();
			gt = new GroupTemplate(resourceLoader, cfg);
			Template template = gt.getTemplate("template.btl");
			System.out.println(template.render().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GroupTemplate getGroupTemlate() {
		return gt;
	}
}
