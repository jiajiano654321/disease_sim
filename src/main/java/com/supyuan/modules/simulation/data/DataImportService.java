package com.supyuan.modules.simulation.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.supyuan.jfinal.base.BaseService;

public class DataImportService extends BaseService  {
	
	@Before(Tx.class)
	public String addDataCollection(Integer pid,SimDataCollection model,int userid, File file) {
		if(file != null) {
			if(!file.getName().endsWith("xlsx")) {
				return "数据文件格式错误，请检查后重新上传!";
				
			}
		}
		if (pid != null && pid > 0) { // 更新
			model.update();

		} else { // 新增
			model.remove("id");
			model.put("creator", userid);
			model.save();
		}
		if(file != null) {
			List<SimDataVo> datas = DataImportFactory.getInstance().transformData(file, DataImportFactory.EXCEL_TYPE);
			List<SimData> sims = new ArrayList<SimData>();
			if(CollectionUtils.isNotEmpty(datas)) {
				for(SimDataVo vo : datas) {
					SimData data = new SimData();
					data.set("code", vo.getId());
					data.set("longitude", vo.getLongitude());
					data.set("latitude", vo.getLatitude());
					data.set("popu_num",vo.getPopuNum());
					data.set("count", vo.getCount());
					data.set("date", vo.getDate());
					data.set("collection_id", model.get("id"));
					sims.add(data);
					
				}
				Db.batchSave(sims, sims.size());
			}
		}
		return "保存成功！";
	}
}
