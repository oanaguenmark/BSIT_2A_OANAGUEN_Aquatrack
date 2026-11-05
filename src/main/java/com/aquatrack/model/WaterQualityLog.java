package com.aquatrack.model;

/**
 * A daily water quality check. The Pass/Fail limits below are
 * common defaults for purified (RO) water - change them to match
 * your local standard / DOH requirements.
 */
public class WaterQualityLog {

    public static final int MAX_TDS = 50;           // ppm
    public static final double MIN_PH = 6.5;
    public static final double MAX_PH = 8.5;

    private String logId;
    private String date;
    private int tds;
    private double ph;
    private String filterStatus;    // Good or Needs Replacement
    private String checkedBy;
    private String result;          // Pass or Fail

    public WaterQualityLog(
            String logId,
            String date,
            int tds,
            double ph,
            String filterStatus,
            String checkedBy) {

        this.logId = logId;
        this.date = date;
        this.tds = tds;
        this.ph = ph;
        this.filterStatus = filterStatus;
        this.checkedBy = checkedBy;

        boolean ok = tds <= MAX_TDS && ph >= MIN_PH && ph <= MAX_PH;
        this.result = ok ? "Pass" : "Fail";
    }

    public String getLogId() {
        return logId;
    }

    public String getDate() {
        return date;
    }

    public int getTds() {
        return tds;
    }

    public double getPh() {
        return ph;
    }

    public String getFilterStatus() {
        return filterStatus;
    }

    public String getCheckedBy() {
        return checkedBy;
    }

    public String getResult() {
        return result;
    }

    public void displayInfo() {

        System.out.println("Log ID        : " + logId);
        System.out.println("Date          : " + date);
        System.out.println("TDS           : " + tds + " ppm");
        System.out.println("pH            : " + ph);
        System.out.println("Filter Status : " + filterStatus);
        System.out.println("Checked By    : " + checkedBy);
        System.out.println("Result        : " + result);
    }
}
