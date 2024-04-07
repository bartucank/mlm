package com.metuncc.mlm.utils.excel;

import com.metuncc.mlm.dto.BookCategoryEnumDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import lombok.Builder;
import lombok.Data;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class CourseStudentExcelWriter {
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public byte[] create() throws IOException{
        XSSFWorkbook workbook = prepare();
        workbook.write(byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
    private XSSFWorkbook prepare(){
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Student Number");
        XSSFDataFormat textFormat = workbook.createDataFormat();

        //Header Font;
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.RED.getIndex());
        headerFont.setFontHeight(16);

        //Header CellStyle;
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setDataFormat(textFormat.getFormat("@"));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFont(headerFont);
        headerStyle.setLocked(true);


        //Font;
        XSSFFont defaultFont = workbook.createFont();
        defaultFont.setBold(false);
        defaultFont.setFontHeight(11);

        //CellStyle;
        CellStyle defaultStyle = workbook.createCellStyle();
        defaultStyle.setDataFormat(textFormat.getFormat("@"));
        defaultStyle.setFont(defaultFont);

        int rowCount = 0;
        int columnNo = 0;
        Row header = sheet.createRow(rowCount++);
        String comment = "Student automatically added/removed to the course if student already registered to MLM. If you are using this excel to add, system will send an invitation email to student if s/he not registered. Invalid student id numbers will be ignored.";
        createCell(header,columnNo++,"Student Number",headerStyle,comment,sheet,workbook);


        for (int i = 0; i < 7; i++) {
            sheet.setColumnWidth(i,8000);
        }

        return workbook;
    }
    public void createCell(Row row, int column, String content, CellStyle style){
        Cell cell = row.createCell(column);
        cell.setCellValue(content);
        cell.setCellStyle(style);
        return;
    }
    public void createCell(Row row, int column, String content, CellStyle style,String comment,Sheet sheet,XSSFWorkbook workbook){
        Cell cell = row.createCell(column);
        cell.setCellValue(content);
        cell.setCellStyle(style);
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex()+3);
        anchor.setRow1(row.getRowNum());
        anchor.setRow2(row.getRowNum()+3);
        Comment comment1 = drawing.createCellComment(anchor);
        comment1.setString(workbook.getCreationHelper().createRichTextString(comment));
        comment1.setVisible(false);
        return;
    }


}
