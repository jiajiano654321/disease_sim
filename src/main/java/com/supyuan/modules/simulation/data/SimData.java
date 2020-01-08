package com.supyuan.modules.simulation.data;

import com.supyuan.component.base.BaseProjectModel;
import com.supyuan.jfinal.component.annotation.ModelBind;

@ModelBind(table = "sim_data")
public class SimData extends BaseProjectModel<SimData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final SimData dao = new SimData();

}
