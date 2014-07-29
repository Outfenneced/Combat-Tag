package com.trc202.Containers;

import java.io.FileWriter;
import java.io.IOException;

public class HallOfShame {

    private static final String startRow = "<tr> \r\n ";
    private static final String endRow = "</tr>";
    private static final String startCol = "<td>";
    private static final String endCol = "</td>";

    public void generateHTMLPage(String file, String table) {
        String output = "";
        output = output + "<html> \r\n <body> \r\n";
        output = output + "<h1>Hall Of PvP Shame</h1> \r\n";
        output = output + table;
        output = output + "</table> \r\n </body> \r\n </html> ";
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(output);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateTable(String[][] twoDArray) {
        String output = "";
        output = output + "<table border=\"1\"> \r\n";
        for (String[] stringArray : twoDArray) {
            output = output + startRow;
            for (String string : stringArray) {
                output = output + startCol + string + endCol;
            }
            output = output + endRow;
        }
        output = output + "</table>";
        return output;
    }
    /**
     * <table border="1">
     * <tr>
     * <td>row 1, cell 1</td>
     * <td>row 1, cell 2</td>
     * </tr>
     * <tr>
     * <td>row 2, cell 1</td>
     * <td>row 2, cell 2</td>
     * </tr>
     * </table>
     */
}
