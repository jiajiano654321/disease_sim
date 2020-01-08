package com.supyuan.util;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RengineUtils {

	public static void main(String[] args) {
		// 在Eclipse中加载jar包，进行测试，代码如下:
		// 创建连接
		RConnection rc = null;
		try {
			rc = new RConnection();
			String fileName = "D:\\rscript\\test.r";
			int data[] = null;
			RList list = new RList();
			data = new int[3]; //开辟一个长度为3的数组
			data[0] = 100;
			data[1] = 0;
			data[2] = 0;
			list.add(new REXPInteger(data));
			list.add(new REXPInteger(data));
			list.add(new REXPInteger(data));
			rc.assign("fileName", fileName);
			rc.assign("nums", REXP.createDataFrame(list) );
//			rc.assign("nums", new REXPInteger(data) );
//			REXP eval = rc.eval("nums[1]");
//			rc.assign("nums", data);
			rc.eval("source(fileName)");
			REXP rexp = rc.eval("SEIR(pars="+ "c(a = 1.1,b = 0.27,fau =7.53,b1=0.1,fau1=11.56)" +")");
			RList asList = rexp.asList();
			System.out.println(asList);
			// Java代码 调用 测试R函数
//			System.out.println(rexp.asString());// R version 3.1.2 (2014-10-31)
//			REXP rexp = rc.eval("R.version.string");// 测试连接，方法是eval(String arg0)
//			String vector = "c(1,3,5,7,9)";
//			REXP meanR = rConnection.eval("meanVal<-mean(" + vector + ")");
//			System.out.println("meanR = " + meanR.asDouble());// 5.0
//
//			double mean = rConnection.eval("meanVal").asDouble();
//			System.out.println("mean = " + mean);
			
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		} catch (REngineException e) {
			e.printStackTrace();
		}finally {
			if(rc != null) {
				rc.close();
			}
		}

        //初始化代码
//        FileResourceLoader resourceLoader = new FileResourceLoader();
//        Configuration cfg;
//		try {
//			cfg = Configuration.defaultConfiguration();
//			GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
//			//获取模板
////			Template t = gt.getTemplate("hello,${name}");
////			t.binding("name", "Beetl");
//			//渲染结果
////			String str = t.render();
////			System.out.println(str);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
}
