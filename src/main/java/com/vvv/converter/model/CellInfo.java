package com.vvv.converter.model;

import org.apache.poi.ss.usermodel.CellType;

public class CellInfo {
	private CellType cellType;
	@SuppressWarnings("rawtypes")
	private Class dataType;

	public CellInfo(CellType cellType, Class dataType) {
		this.cellType = cellType;
		this.dataType = dataType;
	}

	public CellType getCellType() {
		return cellType;
	}

	@SuppressWarnings("rawtypes")
	public Class getObjectType() {
		return dataType;
	}
}
