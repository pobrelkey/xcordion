package org.concordion.internal;

import org.concordion.api.Element;
import org.concordion.api.Resource;

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
    private DocumentParser documentParser;

    public TableSupport(CommandCall tableCommandCall, DocumentParser documentParser) {
        assert tableCommandCall.getElement().isNamed("table");
        this.documentParser = documentParser;
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

    public List<CommandCall> getCommandCallsFor(Row detailRow, Resource rowResource) {
        ArrayList<CommandCall> calls = new ArrayList<CommandCall>();
        Element[] cells = detailRow.getCells();
        for (int columnIndex = 0; columnIndex < cells.length || columnIndex < commandsByColumn.size(); columnIndex++) {
            CommandCall cellCall = (columnIndex < commandsByColumn.size()) ? commandsByColumn.get(columnIndex) : null;
            Element cell;
            if (columnIndex < cells.length) {
                cell = cells[columnIndex];
            } else {
                cell = detailRow.addCell();
            }

            CommandCall resultCall = null;
            if (commandsByElement.containsKey(cell)) {
                resultCall = commandsByElement.get(cell);
            } else if (cellCall != null) {
                resultCall = new CommandCall(cellCall.getCommand(), cell, cellCall.getExpression(), cellCall.getResource());
            }

            if (resultCall != null) {
                for (Element child : cell.getChildElements()) {
                    documentParser.generateCommandCallTree(child, resultCall, resultCall.getResource());
                }
                calls.add(resultCall);
            } else {
                CommandCall dummy = new CommandCall(null, cell, "", rowResource);
                documentParser.generateCommandCallTree(cell, dummy, rowResource);
                calls.addAll(dummy.getChildren());                
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
