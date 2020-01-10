package com.supyuan.modules.simulation.alg;

import java.io.File;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.supyuan.component.base.BaseProjectController;
import com.supyuan.jfinal.component.annotation.ControllerBind;
import com.supyuan.jfinal.component.db.SQLUtils;
import com.supyuan.modules.simulation.data.AlgTimeVO;
import com.supyuan.modules.simulation.data.SimDataCollection;
import com.supyuan.modules.simulation.sim.SimResult;

@ControllerBind(controllerKey = "/sim/alg")
public class AlgorithmController extends BaseProjectController {
	private String PATH = "/pages/simulation/sim/alg_";
	
	private String SQL_YEAR = "select DISTINCT YEAR(d.date) as year from sim_data d";


	private String FIND_COLLECTIONS = "select c.* from sim_data_collection c";
	
	public void index() {
		list();
	}

	public void list() {
		Algorithm model = getModelByAttr(Algorithm.class);
		SQLUtils sql = new SQLUtils(" from sim_alg a where 1=1 ");
		if (model.getAttrValues().length != 0) {
			sql.setAlias("t");
			// 查询条件
			sql.whereLike("name", model.getStr("name"));
		}

		Page<Algorithm> page = Algorithm.dao.paginate(getPaginator(), "select a.* ", //
				sql.toString().toString());

		// 下拉框
		setAttr("page", page);
		setAttr("attr", model);
		render(PATH + "list.html");
	}

	public void time() {
		setAttr("id", getParaToInt(0));
		List<Record> years = Db.find(SQL_YEAR);
		List<SimDataCollection> collections = SimDataCollection.dao.find(FIND_COLLECTIONS);
		setAttr("collections",collections);
		setAttr("years",years);
		render(PATH + "time.html");
	}
	
	public void space() {
		setAttr("id", getParaToInt(0));
		List<Record> years = Db.find(SQL_YEAR);
		List<SimDataCollection> collections = SimDataCollection.dao.find(FIND_COLLECTIONS);
		setAttr("collections",collections);
		setAttr("years",years);
		render(PATH + "space.html");
	}

	public void forwardSim() {
		Integer id = getParaToInt("id");
		Integer n = getParaToInt("model.n");
		double det = Double.parseDouble(getPara("model.det"));
		double gam = Double.parseDouble(getPara("model.gam"));
		double a = Double.parseDouble(getPara("model.a"));
		double b = Double.parseDouble(getPara("model.b"));
		double fau = Double.parseDouble(getPara("model.fau"));
		double b1 = Double.parseDouble(getPara("model.b1"));
		double fau1 = Double.parseDouble(getPara("model.fau1"));
		AlgTimeVO algTimeVO = new AlgTimeVO(id, n, det, gam, a, b, fau, b1, fau1);
		setAttr("vo", algTimeVO);
		render(PATH + "time.html");
	}
	/**
	 * 保存新的算法，表单提交，有文件上传，前台请设置multipart
	 */
	public void save() {
		String msg = "保存成功";
		Integer pid = getParaToInt();
		UploadFile uploadFile = this.getFile();
		File file = null;
		if(uploadFile != null) {
			file = uploadFile.getFile();
		}
		Algorithm model = getModel(Algorithm.class);
		if(pid != 1) {
			model.update();
		}else {
			model.set("creator", getSessionUser().getUserID());
			model.save();
		}
		setSessionUser(getSessionUser());
		renderMessage(msg);
	}
}
