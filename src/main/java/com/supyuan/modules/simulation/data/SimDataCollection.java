package com.supyuan.modules.simulation.data;

import com.supyuan.component.base.BaseProjectModel;
import com.supyuan.jfinal.component.annotation.ModelBind;

@ModelBind(table = "sim_data_collection")
public class SimDataCollection extends BaseProjectModel<SimDataCollection> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final SimDataCollection dao = new SimDataCollection();

}
