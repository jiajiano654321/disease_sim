package com.supyuan.modules.simulation.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataImportFactory {

	public static DataImportFactory factory = null;
	public static final int EXCEL_TYPE = 1;
	public static final int CSV_TYPE = 2;
	
	
	public static DataImportFactory getInstance() {
		if(factory == null) {
			factory = new DataImportFactory();
		}
		return factory;
	}
	
	public List<SimDataVo> transformData(File importFile,int importType){
		switch(importType) {
		case DataImportFactory.EXCEL_TYPE:
			return transFromExcel(importFile);
		default: return null;
		}
	}
	private List<SimDataVo> transFromExcel(File importFile){
		List<SimDataVo> vos = new ArrayList<SimDataVo>();
		XSSFSheet sheet = null;
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(importFile);
			sheet = wb.getSheetAt(0);
			for(int i = 1;i <= sheet.getLastRowNum(); i++) {
				XSSFRow row = sheet.getRow(i);
				int code = Integer.parseInt(row.getCell(1).getRawValue());
				int popuNum = Integer.parseInt(row.getCell(4).getRawValue());
				for(int j = 5;j <row.getLastCellNum();j++) {
					SimDataVo vo = new SimDataVo();
					vo.setId(code);
					vo.setLongitude(CoordinateCache.getLongitude(code));
					vo.setLatitude(CoordinateCache.getLatitude(code));
					vo.setPopuNum(popuNum);
					vo.setCount(Integer.parseInt(row.getCell(j).getRawValue()));
					vo.setDate(sheet.getRow(0).getCell(j).getDateCellValue());
					vos.add(vo);
				}
			}
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vos;
	}
	
	public static void main(String[] args) {
		File importFile = new File("d://1.xlsx");
		List<SimDataVo> vos = DataImportFactory.getInstance().transformData(importFile, 1);
		for(SimDataVo vo : vos) {
			System.out.println(vo);
		}
	}
}
