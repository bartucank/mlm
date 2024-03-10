package com.metuncc.mlm.utils.excel;

import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class ExcelReader {
    public List<ExcelBookRow> parseExcel(MultipartFile file) throws IOException {
        List<ExcelBookRow> rows = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();
        if(Objects.nonNull(sheet)){
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                int columnNo = 0;
                ExcelBookRow excelBookRow = new ExcelBookRow(nullOrNonNull(dataFormatter.formatCellValue(row.getCell(columnNo++))),
                        nullOrNonNull(dataFormatter.formatCellValue(row.getCell(columnNo++))),
                        nullOrNonNull(dataFormatter.formatCellValue(row.getCell(columnNo++))),
                        nullOrNonNull(dataFormatter.formatCellValue(row.getCell(columnNo++))),
                        nullOrNonNull(dataFormatter.formatCellValue(row.getCell(columnNo++))),
                        nullOrNonNull(dataFormatter.formatCellValue(row.getCell(columnNo++))),
                        nullOrNonNull(dataFormatter.formatCellValue(row.getCell(columnNo++))));
                rows.add(excelBookRow);
            }
        }
        return rows;
    }

    public String nullOrNonNull(String a){
        if(Objects.isNull(a)){
            return null;
        }
        if(a.equals("")){
            return null;
        }
        return a;
    }

}
