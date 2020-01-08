package com.supyuan.modules.simulation.data;

import java.io.File;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.supyuan.component.base.BaseProjectController;
import com.supyuan.jfinal.component.annotation.ControllerBind;
import com.supyuan.jfinal.component.db.SQLUtils;
import com.supyuan.system.menu.MenuSvc;
import com.supyuan.system.menu.SysMenu;
import com.supyuan.util.StrUtils;

@ControllerBind(controllerKey = "/data/import")
public class DataImportController extends BaseProjectController {

	private static final String path = "/pages/simulation/data/data_";
	private DataImportService svc = Enhancer.enhance(DataImportService.class);

	public void index() {
		list();
	}

	public void list() {
		SimDataCollection model = getModelByAttr(SimDataCollection.class);

		SQLUtils sql = new SQLUtils(" from sim_data_collection t where 1=1 ");
		if (model.getAttrValues().length != 0) {
			sql.setAlias("t");
			// 查询条件
			sql.whereLike("name", model.getStr("name"));
		}

		// 排序
		String orderBy = getBaseForm().getOrderBy();
		if (StrUtils.isEmpty(orderBy)) {
			sql.append(" order by t.create_time desc");
		} else {
			sql.append(" order by t.").append(orderBy);
		}

		Page<SimDataCollection> page = SimDataCollection.dao.paginate(getPaginator(), "select t.* ", //
				sql.toString().toString());

		// 下拉框
		setAttr("page", page);
		setAttr("attr", model);
		render(path + "list.html");
	}

	public void view() {
		SimDataCollection model = SimDataCollection.dao.findById(getParaToInt());
		Page<SimData> page = SimData.dao.paginate(getPaginator(), "select d.* ","from sim_data d where d.collection_id = ?",getParaToInt());
		setAttr("model", model);
		setAttr("page",page);
		render(path + "view.html");
	}

	public void edit() {

	}

	public void add() {
		render(path + "add.html");
	}

	public void save() {
		String msg = "保存成功";
		Integer pid = getParaToInt();
		UploadFile uploadFile = this.getFile();
		File file = null;
		if(uploadFile != null) {
			file = uploadFile.getFile();
		}

		SimDataCollection model = getModel(SimDataCollection.class);

		// 日志添加
		Integer userid = getSessionUser().getUserID();
		msg = svc.addDataCollection(pid, model, userid, file);

		setSessionUser(getSessionUser());
		renderMessage(msg);
	}

	public void delete() {

	}
}
