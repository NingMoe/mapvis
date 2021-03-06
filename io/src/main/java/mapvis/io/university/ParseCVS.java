package mapvis.io.university;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.awt.print.Book;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseCVS {
    public static List<Major> readCSVFile(String csvFileName) throws IOException {
        ICsvBeanReader beanReader = null;
        CellProcessor[] processors = new CellProcessor[] {
                new NotNull(),  // Course/Programme: Doctoral, Master, etc.
                new NotNull(),  // Academic Unit: FST, etc.
                new Optional(), // Department
                new NotNull(),  // Major/Specialization
                null,
                null,
                null,
                null,
                null,
                null,
                new ParseInt()  // total
        };
        final String[] header = new String[] {
                "Program",
                "Faculty",
                "Department",
                "Major",
                null,
                null,
                null,
                null,
                null,
                null,
                "Total"};

        List<Major> majors=new ArrayList<>();

        try {
            beanReader = new CsvBeanReader(new FileReader(csvFileName),
                    CsvPreference.EXCEL_PREFERENCE);
            beanReader.getHeader(true); // ignore the header

            Major major = null;
            while ((major = beanReader.read(Major.class, header, processors)) != null) {
                majors.add(major);
//                System.out.printf("%-40s %-30s %-60s %-10s %d",
//                        major.getFaculty().replaceFirst("Faculty of ",""),
//                        major.getDepartment()==null?"":major.getDepartment().replaceFirst("Department of ",""),
//                        major.getProgram(), major.getMajor(),
//                        major.getTotal());
//                System.out.println();
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Could not find the CSV file: " + ex);
        } catch (IOException ex) {
            System.err.println("Error reading the CSV file: " + ex);
        } finally {
            if (beanReader != null) {
                try {
                    beanReader.close();
                } catch (IOException ex) {
                    System.err.println("Error closing the reader: " + ex);
                }
            }
        }
        return majors;
    }

    static public void main(String[] args) throws IOException {
        readCSVFile("data/Student numbers.csv");
    }

}
