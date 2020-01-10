package com.supyuan.modules.simulation.alg;

import com.jfinal.plugin.activerecord.Page;
import com.supyuan.component.base.BaseProjectController;
import com.supyuan.jfinal.component.annotation.ControllerBind;
import com.supyuan.jfinal.component.db.SQLUtils;
import com.supyuan.modules.simulation.data.SimDataCollection;
import com.supyuan.modules.simulation.sim.SimResult;
import com.supyuan.util.StrUtils;

@ControllerBind(controllerKey = "/sim/record")
public class AlgRecordController  extends BaseProjectController	{
	
	private String PATH = "/pages/simulation/sim/record_";
	
	/**
	 * 主入口，对应地址/sim/record/{alg_id}
	 * alg_id为上一页面中选择算法的ID
	 */
	public void index() {
		list();
	}
	/**
	 * 获取结果记录接口，格式为/sim/record/list/{alg_id}
	 * alg_id为上一页面中选择算法的ID，查询功能也使用该接口,查询条件传递参考其他页面
	 */
	public void list() {
		Integer algId = getParaToInt(0);
		SimDataCollection model = getModelByAttr(SimDataCollection.class);

		SQLUtils sql = new SQLUtils(" from sim_result t where 1 = 1");
		sql.setAlias("t");
		sql.whereEquals("alg_id", algId);
		if (model.getAttrValues().length != 0) {
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
		render(PATH + "list.html");
	}
	
	/**
	 * 保存算法运算结果接口，格式为/sim/record/save，提交方式为表单提交，前端请使用表单提交方式，如想用json格式提交，再另行修改
	 * 对应的是运算结果可视化页面的保存按钮，传递格式为model格式，其中“alg_param”属性为Json字符串格式保存
	 */
	public void save() {
		String msg = "保存成功";
		SimResult model = getModel(SimResult.class);
		model.set("creator",getSessionUser().getUserID());
		model.save();
		renderMessage(msg);
	}	
}
