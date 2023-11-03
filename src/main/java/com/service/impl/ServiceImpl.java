package com.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Entity;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author BeamStark
 * @date 2023-10-30-01:40
 */
@Slf4j
@Service
public class ServiceImpl implements com.service.Service {

    private static int count = 0;
    private static final int index = 1;
    private static boolean flag;

    @Override
    public void stopdo() {
        log.info("结束运行，等待线程执行完毕...");
        flag = false;
    }

    @Override
    public Entity todo(Entity entity) {
        count = 0;
        flag = true;
        Double threadCount = Double.valueOf(entity.getThreadCount());
        String outFilePath = entity.getOutFilePath();

        // 格式化时间为指定格式
        String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH点mm分"));
        String filePath = outFilePath + "out-" + formattedTime + ".xlsx";

        entity.setOutFilePath(filePath);

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        //生成excel文件
        createExcelFile(filePath);

        //去重
        List<String[]> csvs =
                ServiceImpl.getCsv2(entity);

        int totalSize = csvs.size();
        int batchSize = (int) Math.ceil(totalSize / threadCount);

        List<List<String[]>> resultList = new ArrayList<>();
        for (int i = 0; i < totalSize; i += batchSize) {
            int endIndex = Math.min(i + batchSize, totalSize);
            List<String[]> sublist = csvs.subList(i, endIndex);
            resultList.add(sublist);
        }

        List<Runnable> tasks = new ArrayList<>();
        for (List<String[]> list : resultList) {
            tasks.add(() -> doMain(list, entity));
        }

        execute(tasks);
        // 记录结束时间
        long endTime = System.currentTimeMillis();
        // 计算运行时间
        long executionTime = endTime - startTime;
        log.info("共生成 {} 条数据，执行时间 {} 秒", count - 1, executionTime / 1000);
        return entity;
    }

    public static void doMain(List<String[]> csvs, Entity entity) {

        int size = csvs.size() - 1;
        log.info("共{}个线程，每个线程{}条数据，从第{}开始跑..", entity.getThreadCount(), size, index);
        for (int i = index; i < csvs.size(); i++) {
            if (flag) {
                String request = csvs.get(i)[1];
                Integer csvIndex = Integer.valueOf(csvs.get(i)[0]);
                String response = "null";
                try {
                    response = doPost(request, entity);
                } catch (Exception e) {
                    log.warn("第" + count + "条数据请求超时，设置为null");
                }
                System.out.println("第 " + count + " 个[" + csvIndex + "] 请求参数：" + request + "\n " +
                        "返回参数：" + response);
//                System.out.println(count + " 返回参数：" + response);
                List<List<Object>> data = Collections.singletonList(Arrays.asList(csvIndex, request,
                        response));
                writeDataToExcel(entity.getOutFilePath(), data);
            }
        }
    }

    //返回请求结果的answer
    private static String doPost(String body, Entity entity) {

        // 创建RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        //设置连接超时和读取超时时间（单位：毫秒）
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(entity.getTimeout());
        requestFactory.setReadTimeout(entity.getTimeout());
        restTemplate.setRequestFactory(requestFactory);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + entity.getApiKey());

        // 设置请求体
        String requestBody = entity.getContext().replace("$query", body);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // 发送POST请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(entity.getUrl(),
                HttpMethod.POST,
                requestEntity, String.class);

        // 处理响应
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            //转json
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.get("answer").toString();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            log.info("Response: " + responseBody);
        } else {
            log.error("Request failed with status code: " + responseEntity.getStatusCodeValue());
        }
        return "null";
    }

    public static List<String> getCsv(Entity entity){
        List<String> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(entity.getCsvFile()))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                list.addAll(Arrays.asList(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String[]> getCsv2(Entity entity) {
        List<String[]> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(entity.getCsvFile()))) {
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
        return removeDuplicates(lines);
    }

    //去重去空
    public static List<String[]> removeDuplicates(List<String[]> lines) {
        Set<String> uniqueLines = new HashSet<>();
        List<String[]> distinctLines = new ArrayList<>();

        for (String[] line : lines) {
            if (uniqueLines.add(line[1]) && !line[1].isEmpty()) {
                distinctLines.add(line);
            }
        }

        return distinctLines;
    }

    public void createExcelFile(String filePath) {
        Workbook workbook = new XSSFWorkbook();
        workbook.createSheet("Sheet1");

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            log.info("Excel file has been created successfully.");
            List<List<Object>> data = Collections.singletonList(
                    Arrays.asList("index", "request", "response")
            );
            synchronized (this) {
                writeDataToExcel(filePath, data);
            }
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


    //存在多线程同时写入的问题，需要加线程锁
    public synchronized static void writeDataToExcel(String filePath, List<List<Object>> data) {
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
                    }
                    else if (field instanceof Integer) {
                        cell.setCellValue((Integer) field);
                    }
                    else if (field instanceof Double) {
                        cell.setCellValue((Double) field);
                    }
                }
            }

            workbook.write(fileOut);
            count++;
//            System.out.println("Data has been written to Excel file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void execute(List<Runnable> tasks) {

        if (tasks == null || tasks.isEmpty()) {
            throw new IllegalArgumentException("任务列表不能为空");
        }

        int threadCount = tasks.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (Runnable task : tasks) {
            executorService.submit(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("等待线程执行异常", e);
        } finally {
            executorService.shutdown();
        }
    }
}
