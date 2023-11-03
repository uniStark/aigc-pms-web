import com.model.Entity;
import com.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BeamStark
 * @date 2023-11-03-15:47
 */
public class TEST {

    public static void main(String[] args) {

        List<String[]> csvs =
                getCsv2("~/java-project/aigc-pms-web/src/data/in.csv");


        System.out.println(csvs.size());
        System.out.println(removeDuplicates(csvs).size());
        for (String[] removeDuplicate : removeDuplicates(csvs)) {
            System.out.println(removeDuplicate[0] + removeDuplicate[1]);
        }
    }
    public static List<String[]> removeDuplicates(List<String[]> lines) {
        Set<String> uniqueLines = new HashSet<>();
        List<String[]> distinctLines = new ArrayList<>();

        for (String[] line : lines) {
            if (uniqueLines.add(line[1])) {
                distinctLines.add(line);
            }
        }

        return distinctLines;
    }
    private static void excel(){
        List<List<Object>> data = new ArrayList<>(Collections.singletonList(
                Arrays.asList("index", "request", "response")
        ));
        for (int i = 0; i < 10; i++) {
            data.add(Arrays.asList(i, "ttt", "bbb"));
        }
        System.out.println(data);
        writeDataToExcel("~/java-project/aigc-pms-web/src/data/out/text.xlsx", data);
//        createExcelFile2("~/java-project/aigc-pms-web/src/data/out/text.xlsx");

    }
    private static void void1(){
        Entity entity = new Entity();
        entity.setCsvFile("~/java-project/aigc-pms-web/src/data/in2.csv");
        //去重
        List<String[]> csvs =
                getCsv2("~/java-project/aigc-pms-web/src/data/in.csv").stream().distinct().collect(Collectors.toList());


        int totalSize = csvs.size(); // 列表的总大小
        int batchSize = (int) Math.ceil(totalSize / 8); // 每个等份的大小，向上取整

        List<List<String[]>> resultList = new ArrayList<>();
        for (int i = 1; i < totalSize; i += batchSize) {
            int endIndex = Math.min(i + batchSize, totalSize);
            List<String[]> sublist = csvs.subList(i, endIndex);
            resultList.add(sublist);
        }

        for (int i = 0; i < csvs.size(); i++) {
            String index = csvs.get(i)[0];
            String re = csvs.get(i)[1];
            System.out.println(index + "   " + re);
        }
    }

    public static List<String[]> getCsv2(String filePath) {
        List<String[]> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                lines.add(new String[]{String.valueOf(lineNumber), line});
                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println("读取 CSV 文件时出错：" + e.getMessage());
        }

        return lines;
    }


    public static void createExcelFile2(String filePath) {
        Workbook workbook = new XSSFWorkbook();
        workbook.createSheet("Sheet1");
        // 创建表头
//        List<String> list = Arrays.asList("request", "response");

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            List<List<Object>> data = Collections.singletonList(
                    Arrays.asList("index", "request", "response")
            );
            writeDataToExcel(filePath, data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void writeDataToExcel(String filePath, List<List<Object>> data) {
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(filePath));
             FileOutputStream fileOut = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.getSheetAt(0);

            int rowCount = sheet.getLastRowNum();
            for (List<Object> rowData : data) {
                Row row = sheet.createRow(++rowCount);
                int columnCount = 0;
                for (Object field : rowData) {
                    Cell cell = row.createCell(columnCount++);
                    if (field instanceof String) {
                        cell.setCellValue((String) field);
                    } else if (field instanceof Integer) {
                        cell.setCellValue((Integer) field);
                    } else if (field instanceof Double) {
                        cell.setCellValue((Double) field);
                    }
                }
            }

            workbook.write(fileOut);
//            System.out.println("Data has been written to Excel file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
