package org.example;

import java.util.List;

public class ReportList {
    private List<ReportInfo> reports;

    public List<ReportInfo> getReports() {
        return reports;
    }

    public static class ReportInfo {
        private String name;

        public String getName() {
            return name;
        }
    }
}
