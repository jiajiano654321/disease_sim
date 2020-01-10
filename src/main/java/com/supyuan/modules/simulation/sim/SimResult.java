package com.supyuan.modules.simulation.sim;

import com.supyuan.component.base.BaseProjectModel;
import com.supyuan.jfinal.component.annotation.ModelBind;

@ModelBind(table = "sim_result")
public class SimResult extends BaseProjectModel<SimResult> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final SimResult dao = new SimResult();
	
}
