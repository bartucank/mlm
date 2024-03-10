package com.metuncc.mlm.utils.excel;

import com.metuncc.mlm.dto.BookCategoryEnumDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
public class ExcelWriter {
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private List<ShelfDTO> shelfDTOList;
    private List<BookCategoryEnumDTO> categoryEnumDTOList;
    public byte[] create() throws IOException{
        XSSFWorkbook workbook = prepare();
        workbook.write(byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
    private XSSFWorkbook prepare(){
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books");
        Sheet shelfSheet = workbook.createSheet("Shelfs");
        Sheet categorySheet = workbook.createSheet("Categories");
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

        //Preparing Shelf Sheet;
        int rowCount = 0;
        int columnNo = 0;
        Row headerForShelf = shelfSheet.createRow(rowCount++);
        createCell(headerForShelf,columnNo++,"ID",headerStyle);
        createCell(headerForShelf,columnNo++,"Floor",headerStyle);
        for (int i = 0; i < 3; i++) {
            shelfSheet.setColumnWidth(i,10000);
        }
        for (ShelfDTO shelfDTO : shelfDTOList) {
            columnNo = 0;
            Row row = shelfSheet.createRow(rowCount++);
            createCell(row,columnNo++,shelfDTO.getId().toString(),defaultStyle);
            createCell(row,columnNo++,shelfDTO.getFloor(),defaultStyle);
        }

        //Preparing Category Sheet;
        rowCount = 0;
        columnNo = 0;
        Row headerForCategory = categorySheet.createRow(rowCount++);
        createCell(headerForCategory,columnNo++,"KEY",headerStyle);
        createCell(headerForCategory,columnNo++,"Description",headerStyle);
        for (int i = 0; i < 2; i++) {
            categorySheet.setColumnWidth(i,10000);
        }
        for (BookCategoryEnumDTO bookCategoryEnumDTO : categoryEnumDTOList) {
            columnNo =0;
            Row row = categorySheet.createRow(rowCount++);
            createCell(row,columnNo++,bookCategoryEnumDTO.getEnumValue().toString(),defaultStyle);
            createCell(row,columnNo++,bookCategoryEnumDTO.getStr(),defaultStyle);
        }

        rowCount =0;
        columnNo =0;
        Row header = sheet.createRow(rowCount++);
        createCell(header,columnNo++,"ISBN Number",headerStyle);
        createCell(header,columnNo++,"Book Name",headerStyle);
        createCell(header,columnNo++,"Book Description",headerStyle);
        createCell(header,columnNo++,"Book Publisher",headerStyle);
        createCell(header,columnNo++,"Book Author",headerStyle);
        createCell(header,columnNo++,"Shelf",headerStyle,"You can choose shelf from dropdown list. Also, you can enter shelf id.",sheet,workbook);
        createCell(header,columnNo++,"Category",headerStyle,"You can choose category from dropdown list. Also, you can enter category key.",sheet,workbook);


        for (int i = 0; i < 7; i++) {
            sheet.setColumnWidth(i,8000);
        }



        //Preparing Category Dropdown List;
        List<String> categoryArrayList = new ArrayList<>();
        categoryArrayList.addAll(categoryEnumDTOList.stream().map(BookCategoryEnumDTO::getEnumValue).collect(Collectors.toList()));
        CellRangeAddressList addressListForCategory = new CellRangeAddressList(1,50000,6,6 );
        DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dataValidationConstraintForCategory = dataValidationHelper.createExplicitListConstraint(categoryArrayList.stream().toArray(String[]::new));
        DataValidation dataValidation = dataValidationHelper.createValidation(dataValidationConstraintForCategory,addressListForCategory);
        dataValidation.setShowErrorBox(true);
        dataValidation.createErrorBox("Error","Please select a valid category.");
        sheet.addValidationData(dataValidation);

        //Preparing Shelf Dropdown List;

        List<Long> shelfArrayList = new ArrayList<>();
        shelfArrayList = shelfDTOList.stream().map(ShelfDTO::getId).collect(Collectors.toList());
        List<String> shelfStrList = new ArrayList<>();
        for (Long l : shelfArrayList) {
            shelfStrList.add(l.toString());
        }
        CellRangeAddressList addressListForShelf = new CellRangeAddressList(1,50000,5,5 );
        DataValidationHelper dataValidationHelperForShelf = sheet.getDataValidationHelper();
        DataValidationConstraint dataValidationConstraintForShelf = dataValidationHelperForShelf.createExplicitListConstraint(shelfStrList.stream().toArray(String[]::new));
        DataValidation dataValidationForShelf = dataValidationHelperForShelf.createValidation(dataValidationConstraintForShelf,addressListForShelf);
        dataValidationForShelf.setShowErrorBox(true);
        dataValidationForShelf.createErrorBox("Error","Please select a valid shelf. Please note that you have to select or type shelf id.");
        sheet.addValidationData(dataValidationForShelf);



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
