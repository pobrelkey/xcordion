package org.concordion.internal;

import org.concordion.api.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableSupport {
    private final CommandCall tableCommandCall;
    private int row = -1;
    private int column = 0;

    private ArrayList<ArrayList<Element>> cells = new ArrayList<ArrayList<Element>>();
    private ArrayList<Row> rows = new ArrayList<Row>();
    private ArrayList<CommandCall> commandsByColumn = new ArrayList<CommandCall>();
    private HashMap<Element, CommandCall> commandsByElement;

    public TableSupport(CommandCall tableCommandCall) {
        assert tableCommandCall.getElement().isNamed("table");
        this.tableCommandCall = tableCommandCall;
        parseTableStructure(tableCommandCall.getElement());
        populateCommandCallByColumnList();
    }

    private void parseTableStructure(Element e) {
        for (Element child : e.getChildElements()) {
            if (child.isNamed("tr")) {
                parseTableRow(child);
            } else if (child.isNamed("td") || child.isNamed("th") || child.isNamed("table")) {
                // gak, a nested/malformed table!
                continue;
            } else {
                // probably a Complex Table Model element (THEAD, TBODY etc.)
                parseTableStructure(child);
            }
        }
    }

    static private int getIntAttribute(Element e, String name) {
        String stringValue = e.getAttributeValue(name);
        int result = 1;
        if (stringValue != null && stringValue.length() > 0) {
            try {
                result = Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private void parseTableRow(Element tr) {
        row++;
        column = 0;
        skipRowspanCellsFromAbove();

        for (Element cell : tr.getChildElements()) {
            if (cell.isNamed("td") || cell.isNamed("th")) {
                int rowspan = getIntAttribute(cell, "rowspan"), colspan = getIntAttribute(cell, "colspan");

                for (int y = row; y < row + rowspan; y++) {
                    while (y >= cells.size()) {
                        cells.add(new ArrayList<Element>());
                    }
                    ArrayList<Element> rowList = cells.get(y);
                    for (int x = column; x < column + colspan; x++) {
                        put(rowList, x, cell);
                    }
                }
                column += colspan;
                skipRowspanCellsFromAbove();
            }
        }

        ArrayList<Element> rowCells = cells.get(row);
        Row rowObject = new Row(tr, rowCells.toArray(new Element[rowCells.size()]));
        rows.add(rowObject);
    }

    private void skipRowspanCellsFromAbove() {
        if (row < cells.size()) {
            ArrayList<Element> rowList = cells.get(row);
            while (rowList != null && column < rowList.size() && rowList.get(column) != null) {
                column++;
            }
        }
    }

    private void populateCommandCallByColumnList() {
        commandsByElement = new HashMap<Element, CommandCall>();
        CommandCallList children = tableCommandCall.getChildren();
        for (int i = 0; i < children.size(); i++) {
            CommandCall childCall = children.get(i);
            commandsByElement.put(childCall.getElement(), childCall);
        }

        for (Row row : rows) {
            for (int i = 0; i < row.getCells().length; i++) {
                Element cell = row.getCells()[i];
                if (cell != null && cell.isNamed("th") && commandsByElement.containsKey(cell)) {
                    put(commandsByColumn, i, commandsByElement.get(cell));
                }
            }
        }
    }

    static private <T> void put(List<T> list, int index, T item) {
        while (list.size() <= index) {
            list.add(null);
        }
        list.set(index, item);
    }

    public Row[] getDetailRows() {
        ArrayList<Row> result = new ArrayList<Row>();
        for (Row row : rows) {
            for (int i = 0; i < row.getCells().length; i++) {
                Element cell = row.getCells()[i];
                if (cell != null && cell.isNamed("td")) {
                    result.add(row);
                    break;
                }
            }
        }
        return result.toArray(new Row[result.size()]);
    }

    public List<CommandCall> getCommandCallsFor(Row detailRow) {
        ArrayList<CommandCall> calls = new ArrayList<CommandCall>();
        Element[] cells = detailRow.getCells();
        for (int columnIndex = 0; columnIndex < cells.length && columnIndex < commandsByColumn.size(); columnIndex++) {
            CommandCall cellCall = commandsByColumn.get(columnIndex);
            if (cellCall != null) {
                calls.add(new CommandCall(cellCall.getCommand(), cells[columnIndex], cellCall.getExpression(), cellCall.getResource()));
            } else if (commandsByElement.containsKey(cells[columnIndex])) {
                calls.add(commandsByElement.get(cells[columnIndex]));
            }
        }
        return calls;
    }

    public Row addDetailRow() {
        Element rowElement = new Element("tr");
        Element tableElement = tableCommandCall.getElement();

        Element tbody = tableElement.getFirstChildElement("tbody");
        if (tbody != null) {
            tbody.appendChild(rowElement);
        } else {
            tableElement.appendChild(rowElement);
        }

        for (int i = 0; i < commandsByColumn.size(); i++) {
            rowElement.appendChild(new Element("td"));
        }
        return new Row(rowElement);
    }

}
