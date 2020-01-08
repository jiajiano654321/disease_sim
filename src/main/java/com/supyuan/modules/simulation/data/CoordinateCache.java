package com.supyuan.modules.simulation.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.log.Log;
import com.supyuan.system.dict.DictCache;
import com.supyuan.util.cache.CacheManager;

public class CoordinateCache {
	private final static Log log = Log.getLog(DictCache.class);
	private final static String cacheName = "CoordinateCache";
	private static Map<Integer,Coordinate> cordinateMap = new HashMap<Integer,Coordinate>();
	
	/**
	 * 初始化Map
	 * 
	 * @author flyfox 2013-11-15
	 */
	public static void init() {
		log.info("####坐标系Cache初始化......");
		CoordinateCache.initCoordinate();
	}
	
	public static void initCoordinate() {
		List<Coordinate> listDetail = new ArrayList<Coordinate>();
		// detailSort
		listDetail = Coordinate.dao.find("select t.* from sim_coordinate t");
		for (Coordinate detail : listDetail) {
			cordinateMap.put(detail.getInt("code"),detail);
		}
	}
	
	public static double getLongitude(Integer code) {
		return cordinateMap.get(code).getDouble("longitude");
	}
	
	public static double getLatitude(Integer code) {
		return cordinateMap.get(code).getDouble("latitude");
	}
}
