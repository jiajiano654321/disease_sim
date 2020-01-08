package com.supyuan.modules.simulation.data;

import com.supyuan.component.base.BaseProjectModel;
import com.supyuan.jfinal.component.annotation.ModelBind;


@ModelBind(table = "sim_coordinate", key = "code")
public class Coordinate  extends BaseProjectModel<Coordinate> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Coordinate dao = new Coordinate();
}
