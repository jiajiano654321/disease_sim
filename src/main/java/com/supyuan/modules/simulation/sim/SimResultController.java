package com.supyuan.modules.simulation.sim;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.supyuan.component.base.BaseProjectController;
import com.supyuan.jfinal.component.annotation.ControllerBind;
import com.supyuan.modules.simulation.data.AlgSpaceVO;
import com.supyuan.modules.simulation.data.AlgTimeVO;

import net.sf.json.JSONObject;

@ControllerBind(controllerKey = "/sim/result")
public class SimResultController extends BaseProjectController {

	private static final String REXPInteger = null;

	private String PATH = "/pages/simulation/sim/result_";

	private String FIND_SIM_DATA_CODE = "select d.* from sim_data d where "
			+ "d.collection_id = ? and year(date) = ? order by d.code asc";

	private String FIND_SIM_DATA_DATE = "select d.* from sim_data d where "
			+ "d.collection_id = ? and year(date) = ? order by d.date asc";
	private String FIND_SUM_SIM_DATA_SQL = "select sum(d.count) as count ,d.date as date from sim_data d "
			+ "where d.collection_id = ? and year(date) = ? group by d.date ";

	private String FIND_POPU_NUM = "select max(d.popu_num) popu from sim_data d "
			+ "where d.collection_id = ? and year(date) = ? group by code";

	private String FIND_SUM_POP = "select sum(a.pop) as sum_pop  FROM (select 	max(d.popu_num) AS pop from sim_data d where "
			+ "d.collection_id = ? and year(date) = ? group by code ) a";
	
	private String FIND_WEEK_COUNT = "select count(DISTINCT(date)) as count FROM sim_data d "
			+ "where d.collection_id = ? and year(date) = ?";
	
	private SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void time() {
		Integer id = getParaToInt(0);
		Integer n = getParaToInt(1);
		double det = Double.parseDouble(getPara(2));
		double gam = Double.parseDouble(getPara(3));
		double a = Double.parseDouble(getPara(4));
		double b = Double.parseDouble(getPara(5));
		double fau = Double.parseDouble(getPara(6));
		double b1 = Double.parseDouble(getPara(7));
		double fau1 = Double.parseDouble(getPara(8));
		AlgTimeVO algTimeVO = new AlgTimeVO(id, n, det, gam, a, b, fau, b1, fau1);
		setAttr("vo", algTimeVO);
		render(PATH + "time.html");
	}

	public void space() {
		render(PATH + "space.html");
	}

	public void getNums() {
		String readData = HttpKit.readData(getRequest());
		JSONObject jsonobject = JSONObject.fromObject(readData);
		AlgTimeVO parse = (AlgTimeVO) JSONObject.toBean(jsonobject, AlgTimeVO.class);
		String year = parse.getYear();
		Long yearStart=null,yearEnd = null;
		try {
			yearStart = sDateFormat.parse(year + "-01-01 00:00:00").getTime();
			yearEnd = sDateFormat.parse(year + "-12-31 23:59:59").getTime();
			
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		int collectionId = parse.getCollectionId();
		List<Record> sumDatas = Db.find(FIND_SUM_SIM_DATA_SQL, collectionId, year);
		Record sumPop = Db.findFirst(FIND_SUM_POP, collectionId, year);
		int[] nums = new int[sumDatas.size()];
		for (int i = 0; i < sumDatas.size(); i++) {
			nums[i] = sumDatas.get(i).getBigDecimal("count").intValue();
		}
		REXPInteger numRexp = new REXPInteger(nums);
		Integer i0 = sumDatas.get(0).getBigDecimal("count").intValue();
		Integer len = sumDatas.size();
		Integer sumInt = sumPop.getBigDecimal("sum_pop").intValue();
		RConnection rc = null;
		try {
			rc = new RConnection();
			rc.eval("rm(list = ls(all = TRUE)) ");
			String fileName = "D:\\rscript\\test.r";
			rc.assign("i0", new REXPInteger(i0));
			rc.assign("len", new REXPInteger(len));
			rc.assign("InitPop", new REXPInteger(sumInt));
			rc.assign("annual_num", numRexp);
			rc.assign("n", new REXPInteger(parse.getN()));
			rc.assign("det", new REXPDouble(parse.getDet()));
			rc.assign("gam", new REXPDouble(parse.getGam()));
			rc.assign("fileName", fileName);
			rc.eval("source(fileName)");
			rc.eval("pars <- c(a = " + parse.getA() + ",b = " + parse.getB() + ",fau =" + parse.getFaul() + ",b1="
					+ parse.getB1() + ",fau1=" + parse.getFaul1() + ")");
			System.out.println("運算開始：----" + new Timestamp(System.currentTimeMillis()));
			REXP rexp = rc.eval("modFit(f = HFMDcost , p = pars)");
			REXPDouble pars = (REXPDouble) rexp.asList().get(0);
			rc.assign("par", pars);
			REXP models = rc.eval("SEIR(par)");
			System.out.println("運算结束：----" + new Timestamp(System.currentTimeMillis()));
			REXPDouble model = (REXPDouble) models.asList().get(3);
			double[] dmodel = model.asDoubles();
			List<Object[]> obsList = new ArrayList<Object[]>();
			List<Object[]> numsList = new ArrayList<Object[]>();
			List<List<Object[]>> results = new ArrayList<List<Object[]>>();
			for (int i = 0; i < sumDatas.size(); i++) {
				Record sumData = sumDatas.get(i);
				long date = sumData.getDate("date").getTime();
				Object[] obs = new Object[2];
				obs[0] = date;
				obs[1] = sumData.getBigDecimal("count").doubleValue();
				Object[] ns = new Object[2];
				ns[0] = date;
				ns[1] = dmodel[i];
				obsList.add(obs);
				numsList.add(ns);
			}
			double[] asDoubles = pars.asDoubles();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("pars", asDoubles);
			results.add(obsList);
			results.add(numsList);
			resultMap.put("results", results);
			resultMap.put("start", yearStart);
			resultMap.put("end", yearEnd);
			renderJson(resultMap);
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		} catch (REngineException e) {
			e.printStackTrace();
		} finally {
			if (rc != null) {
				rc.close();
			}
		}
	}

	public void getSpaceNums() {
		String readData = HttpKit.readData(getRequest());
		JSONObject jsonobject = JSONObject.fromObject(readData);
		AlgSpaceVO parse = (AlgSpaceVO) JSONObject.toBean(jsonobject, AlgSpaceVO.class);
		String year = parse.getYear();
		int collectionId = parse.getCollectionId();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Record> datas = Db.find(FIND_SIM_DATA_CODE, collectionId, year);
		List<Record> dataDorders = Db.find(FIND_SIM_DATA_DATE, collectionId, year);
		List<Record> codePop = Db.find(FIND_POPU_NUM, collectionId, year);
		Record first = Db.findFirst(FIND_WEEK_COUNT,collectionId, year);
		RList list = getNumList(dataDorders);
		List<List<double[]>> geoList = null;
//		List<List<double[]>> geoRealList = getGeoRealList(dataDorders);
//		List<List<double[]>> geoList = getGeoSimList(dataDorders, list,first.getInt("count"));

		int[] popus = new int[codePop.size()];
		for (int i = 0; i < codePop.size(); i++) {
			popus[i] = codePop.get(i).getInt("popu").intValue();
		}
		REXPInteger numRexp = new REXPInteger(popus);
		RConnection rc = null;
		try {
			rc = new RConnection();
			rc.eval("rm(list = ls(all = TRUE)) ");
			String fileName = "D:\\rscript\\test2.r";
			rc.assign("popu", numRexp);
			rc.assign("num",REXP.createDataFrame(list));
			rc.assign("m", new REXPInteger(parse.getN()));
			rc.assign("det", new REXPDouble(parse.getDet()));
			rc.assign("gam", new REXPDouble(parse.getGam()));
			rc.assign("a",new REXPDouble(parse.getA()));
			rc.assign("b", new REXPDouble(parse.getB()));
			rc.assign("fau", new REXPDouble(parse.getFau()));
			rc.assign("b1", new REXPDouble(parse.getB1()));
			rc.assign("fau1", new REXPDouble(parse.getFau1()));
			double[] kArr = {parse.getK1(),parse.getK2()};
			rc.assign("k", new REXPDouble(kArr));
			rc.assign("fileName", fileName);
			rc.eval("source(fileName)");
//			REXP cuts = rc.eval("CUTS()");
//			resultMap.put("cuts", cuts.asDoubles());
			rc.eval("pars <- c("+ parse.getM1() +"," + parse.getM2() + "," + parse.getM3() + "," +parse.getM4() + ")");
			REXP eval = rc.eval("HFMD(pars)");
			geoList = getGeoSimList(dataDorders, eval.asDoubles());
			REXP cuts = rc.eval("CUTS_SIM()");
			resultMap.put("cuts", cuts.asDoubles());
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REngineException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		} finally {
			if (rc != null) {
				rc.close();
			}
		}

		resultMap.put("elements", geoList);
		renderJson(resultMap);
	}

	private List<List<int[]>> getElementList(RList list) {
		List<List<int[]>> elements = new ArrayList<List<int[]>>();
		REXPInteger ints = (REXPInteger) list.get(0);
		for (int i = 0; i < ints.length(); i++) {
			List<int[]> hes = new ArrayList<int[]>();
			int x = 0, y = 0;
			for (int j = 0; j < list.size(); j++) {
				REXPInteger ins = (REXPInteger) list.get(j);
				int[] ais = ins.asIntegers();
				int k = ais[i];
//				HeatElement he = new HeatElement(x,y,k);
				int[] arr = { x, y, k };
				hes.add(arr);
				x++;
				if (x == 32) {
					y++;
					x = 0;
				}
			}
			elements.add(hes);
		}
		return elements;
	}

	private RList getNumList(List<Record> datas) {
		RList list = new RList();
		Date code = datas.get(0).getDate("date");
		List<Integer> dataInts = new ArrayList<Integer>();
		for (int i = 0; i < datas.size(); i++) {
			Date nextCode = datas.get(i).getDate("date");
			if (!code.equals(nextCode)) {
				int[] array = new int[dataInts.size()];
				for (int j = 0; j < dataInts.size(); j++) {
					array[j] = dataInts.get(j);
				}
				list.put(Integer.toString(i),new REXPInteger(array));
				dataInts.clear();
				code = nextCode;
			}
			dataInts.add(datas.get(i).getInt("count") == null ? 0 : datas.get(i).getInt("count"));
		}
		int[] array = new int[dataInts.size()];
		for (int j = 0; j < dataInts.size(); j++) {
			array[j] = dataInts.get(j);
		}
		list.put(Integer.toString(list.size() + 1),new REXPInteger(array));
		return list;
	}

	private List<List<double[]>> getGeoRealList(List<Record> datas) {
		List<List<double[]>> geoElements = new ArrayList<List<double[]>>();
		Date date = datas.get(0).getDate("date");
		List<double[]> geoElement = new ArrayList<double[]>();
		for (int i = 0; i < datas.size(); i++) {
			Date nextDate = datas.get(i).getDate("date");
			double x = datas.get(i).getDouble("longitude") == null ? 0.0 : datas.get(i).getDouble("longitude");
			double y = datas.get(i).getDouble("latitude") == null ? 0.0 : datas.get(i).getDouble("latitude");
			double value = datas.get(i).getInt("count") == null ? 0.0 : datas.get(i).getInt("count").doubleValue();
			if (i == datas.size() - 1) {
				double[] array = { x, y, value };
				geoElement.add(array);
				geoElements.add(geoElement);
				break;
			}
			if (date.equals(nextDate)) {
				double[] array = { x, y, value };
				geoElement.add(array);
			} else {
				geoElements.add(geoElement);
				geoElement = new ArrayList<double[]>();
				double[] array = { x, y, value };
				geoElement.add(array);
				date = nextDate;
			}
		}
		return geoElements;
	}

	private List<List<double[]>> getGeoSimList(List<Record> datas,double[] list) {
		List<List<double[]>> geoElements = new ArrayList<List<double[]>>();
		Date date = datas.get(0).getDate("date");
		List<double[]> geoElement = new ArrayList<double[]>();
		for (int i = 0; i < datas.size(); i++) {
			Date nextDate = datas.get(i).getDate("date");
			double x = datas.get(i).getDouble("longitude") == null ? 0.0 : datas.get(i).getDouble("longitude");
			double y = datas.get(i).getDouble("latitude") == null ? 0.0 : datas.get(i).getDouble("latitude");
//			double value = datas.get(i).getInt("count") == null ? 0.0 : datas.get(i).getInt("count").doubleValue();
//			int inX = i / week, inY = i % week;
			double value = list[i];
			if (i == datas.size() - 1) {
				double[] array = { x, y, value };
				geoElement.add(array);
				geoElements.add(geoElement);
				break;
			}
			if (date.equals(nextDate)) {
				double[] array = { x, y, value };
				geoElement.add(array);
			} else {
				geoElements.add(geoElement);
				geoElement = new ArrayList<double[]>();
				double[] array = { x, y, value };
				geoElement.add(array);
				date = nextDate;
			}
		}
		return geoElements;
	}
}
