package org.example;

public class ReadDemo {
    public static void main(String[] args) {
        String filePath1 = "/fdda_system.json";

        String[] nameList1 = SystemReader.readSystemNames(filePath1);

        // Display system names
        for (String name : nameList1) {
            System.out.println("System Name from SystemReader: " + name);
        }

        String filePath2 = "/fdda1_report.json";

        String[] nameList2 = ReportReader.readReportNames(filePath2);

        // Display report names
        for (String name : nameList2) {
            System.out.println("Report Name from ReportReader: " + name);
        }

    }
}
