package com.supyuan.modules.simulation.sim;

import java.util.ArrayList;
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

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.supyuan.component.base.BaseProjectController;
import com.supyuan.jfinal.component.annotation.ControllerBind;

@ControllerBind(controllerKey = "/sim/result")
public class SimResultController extends BaseProjectController {

	private String PATH = "/pages/simulation/sim/result_";

	private String FIND_SIM_DATA_SQL = "select d.* from sim_data d where "
			+ "d.collection_id = 19 and date > '2013-12-31 23:59:59' and date < '2015-01-01 00:00:00'";
	private String FIND_SUM_SIM_DATA_SQL = "select sum(d.count) as count ,d.date as date from sim_data d "
			+ "where d.collection_id = 19 and date > '2013-12-31 23:59:59' and date < '2015-01-01 00:00:00' group by d.date ";

	private String FIND_POPU_NUM = "select max(d.popu_num) popu from sim_data d "
			+ "where d.collection_id = 19 and date > '2013-12-31 23:59:59' and date < '2015-01-01 00:00:00' group by code";

	private String FIND_SUM_POP = "select sum(a.pop) as sum_pop  FROM (select 	max(d.popu_num) AS pop from sim_data d where d.collection_id = 19 and date > '2013-12-31 23:59:59' and date < '2015-01-01 00:00:00' group by code ) a";

	public void time() {
		render(PATH + "time.html");
	}

	public void space() {
		render(PATH + "space.html");
	}

	public void getNums() {
		List<Record> sumDatas = Db.find(FIND_SUM_SIM_DATA_SQL);
		Record sumPop = Db.findFirst(FIND_SUM_POP);
		RList list = new RList();
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
			rc.assign("fileName", fileName);
			rc.eval("source(fileName)");
			REXP rexp = rc.eval("modFit(f = HFMDcost , p = pars)");
			REXPDouble pars = (REXPDouble) rexp.asList().get(0);
			rc.assign("par", pars);
			REXP models = rc.eval("SEIR(par)");
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
			results.add(obsList);
			results.add(numsList);
			renderJson(results);
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
		Map<String ,Object> resultMap = new HashMap<String,Object>();
		List<Record> datas = Db.find(FIND_SIM_DATA_SQL);
		List<Record> codePop = Db.find(FIND_POPU_NUM);
		RList list =getNumList(datas);
		List<List<int[]>> elements = getElementList(list);
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
			rc.assign("num", REXP.createDataFrame(list));
			rc.assign("fileName", fileName);
			rc.eval("source(fileName)");
			REXP cuts = rc.eval("CUTS()");
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

		resultMap.put("elements", elements);
		renderJson(resultMap);
	}

	private List<List<int[]>> getElementList(RList list) {
		List<List<int[]>> elements = new ArrayList<List<int[]>>();
		REXPInteger ints = (REXPInteger)list.get(0);	
		for(int i = 0; i < ints.length();i++) {
			List<int[]> hes = new ArrayList<int[]>();
			int x = 0,y = 0;
			for(int j = 0; j < list.size();j++) {
				REXPInteger ins = (REXPInteger)list.get(j);
				int[] ais = ins.asIntegers();
				int k = ais[i];
//				HeatElement he = new HeatElement(x,y,k);
				int[] arr = {x,y,k};
				hes.add(arr);
				x++;
				if(x == 32) {
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
		int code = datas.get(0).getInt("code");
		List<Integer> dataInts = new ArrayList<Integer>();
		for (int i = 0; i < datas.size(); i++) {
			int nextCode = datas.get(i).getInt("code");
			if (code != nextCode) {
				int[] array = new int[dataInts.size()];
				for (int j = 0; j < dataInts.size(); j++) {
					array[j] = dataInts.get(j);
				}
				list.add(new REXPInteger(array));
				dataInts.clear();
				code = nextCode;
			}
			dataInts.add(datas.get(i).getInt("count") == null ? 0 : datas.get(i).getInt("count"));

		}
		int[] array = new int[dataInts.size()];
		for (int j = 0; j < dataInts.size(); j++) {
			array[j] = dataInts.get(j);
		}
		list.add(new REXPInteger(array));
		return list;
	}
}
