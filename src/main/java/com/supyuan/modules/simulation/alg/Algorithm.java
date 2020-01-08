package com.supyuan.modules.simulation.alg;

import com.supyuan.component.base.BaseProjectModel;
import com.supyuan.jfinal.component.annotation.ModelBind;
import com.supyuan.modules.simulation.data.SimData;

@ModelBind(table = "sim_alg")
public class Algorithm extends BaseProjectModel<Algorithm>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final Algorithm dao = new Algorithm();

}
