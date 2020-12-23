package com.vvv.converter;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vvv.converter.model.CellInfo;

import nl.fountain.xelem.excel.Cell;
import nl.fountain.xelem.excel.Row;
import nl.fountain.xelem.excel.Table;
import nl.fountain.xelem.excel.Workbook;
import nl.fountain.xelem.excel.Worksheet;
import nl.fountain.xelem.excel.ss.SSCell;
import nl.fountain.xelem.lex.ExcelReader;

public class Toolkit {

	public static XSSFWorkbook transferWorkbook(File file) {

		// boolean printHelp = true;
		XSSFWorkbook outWorkbook = new XSSFWorkbook();
		try {
			//ArgsParser argsParser = new ArgsParser(args);

			ExcelReader er = new ExcelReader();
			InputSource source = new InputSource(new FileInputStream(file));
			//Workbook workbook = er.getWorkbook(file.toURI().getPath());
			Workbook workbook = er.getWorkbook(source);


			for (Worksheet sheet : workbook.getWorksheets()) {
				XSSFSheet outSheet = outWorkbook.createSheet(sheet.getName());
				Table data = sheet.getTable();
				Integer rowIndex = 0;
				for (Row row : data.getRows()) {
					XSSFRow outRow = outSheet.createRow(rowIndex++);
					for (Cell cell : row.getCells()) {
						if (getCellTypeMap().containsKey(cell.getXLDataType())) {
							CellInfo cellInfo = getCellTypeMap().get(cell.getXLDataType());
							XSSFCell outCell = outRow.createCell(cell.getIndex() - 1, cellInfo.getCellType());
							// outCell.setCellValue
							if (cell.getFormula() == null) {
								if (cell.getXLDataType().equals(SSCell.DATATYPE_BOOLEAN)) {
									outCell.setCellValue(cell.booleanValue());
								} else if (cell.getXLDataType().equals(SSCell.DATATYPE_NUMBER)) {
									outCell.setCellValue(cell.doubleValue());
								} else if (cell.getXLDataType().equals(SSCell.DATATYPE_DATE_TIME)) {
									outCell.setCellStyle(getDateCellTyle(outWorkbook));
									outCell.setCellValue((Date) cell.getData());
								} else {
									outCell.setCellValue(cell.getData$());
								}
							} else {
								// TODO I couldn't find a way to convert R1C1 style formula to A1 style
								System.out.println("Reference file has formula. Only value has been written. Formula : "
										+ cell.getFormula());
								outCell.setCellValue(cell.getData$());
							}
						} else {
							XSSFCell outCell = outRow.createCell(cell.getIndex() - 1);
							outCell.setCellValue(cell.getData$());
						}

					}

				}
			}

//			OutputStream outStream = new BufferedOutputStream(
//					new FileOutputStream(argsParser.getDestinationFile().getFile()));
//			try {
//				outWorkbook.write(outStream);
//				System.out.println("Finished : " + argsParser.getDestinationFile().getFile().getPath());
//			} finally {
//				outStream.close();
//				outWorkbook.close();
//			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
//			if (printHelp) {
//				printHelp();
//			}
		}
		return outWorkbook;
	}

	private static CellStyle getDateCellTyle(XSSFWorkbook outWorkbook) {
		CreationHelper createHelper = outWorkbook.getCreationHelper();
		CellStyle cellDateStyle = outWorkbook.createCellStyle();
		cellDateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
		return cellDateStyle;
	}

	private static Map<String, CellInfo> getCellTypeMap() {
		Map<String, CellInfo> typeMap = new HashMap<>();
		typeMap.put(SSCell.DATATYPE_BOOLEAN, new CellInfo(CellType.BOOLEAN, Boolean.class));
		typeMap.put(SSCell.DATATYPE_NUMBER, new CellInfo(CellType.NUMERIC, Double.class));
		typeMap.put(SSCell.DATATYPE_DATE_TIME, new CellInfo(CellType.NUMERIC, Date.class));
		typeMap.put(SSCell.DATATYPE_STRING, new CellInfo(CellType.STRING, String.class));
		return typeMap;
	}

	private static void printHelp() {
		System.out.println("How To Use This Converter : ");
		System.out.println("  java -cp StylesheetML-Converter.jar com.vvv.converter.Main <oldFile> <newFile>");
		System.out.println("Warning : If destination file exists, It will be overwritten");
	}
}
