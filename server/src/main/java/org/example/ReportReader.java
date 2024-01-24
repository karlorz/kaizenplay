package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ReportReader {
    public static String[] readReportNames(String jsonPath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = ReportReader.class.getResourceAsStream(jsonPath);
            if (inputStream != null) {
                ReportList reportList = objectMapper.readValue(inputStream, ReportList.class);

                List<ReportList.ReportInfo> reports = reportList.getReports();

                return reports.stream()
                        .map(ReportList.ReportInfo::getName)
                        .toArray(String[]::new);
            } else {
                System.out.println("File not found: " + jsonPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[0]; // Return empty array if there's an issue
    }
}
