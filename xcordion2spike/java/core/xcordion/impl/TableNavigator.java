package xcordion.impl;

import xcordion.api.TestElement;

import java.util.ArrayList;


public class TableNavigator<T extends TestElement<T>>  {

    private int originalHeight = 0;
	private int originalWidth;

    private ArrayList<RowNavigatorImpl<T>> rows = new ArrayList<RowNavigatorImpl<T>>();
    private ArrayList<RowNavigatorImpl<T>> contentRows = new ArrayList<RowNavigatorImpl<T>>();
    private ArrayList<ArrayList<T>> columnHeaders = new ArrayList<ArrayList<T>>();

    private int parseRow = -1, parseColumn = 0;

    private int navigateRow = -1;

    private T newRowParent;

    public TableNavigator(T table) {
        parseTableStructure(table, null);
        calculateDimensions();

        newRowParent = table.getFirstChildNamed("tbody");
        if (newRowParent == null) {
            newRowParent = table;
        }
    }

    private void calculateDimensions() {
        originalHeight = rows.size();
        for (RowNavigatorImpl<T> row : rows) {
            if (originalWidth < row.getWidth()) {
                originalWidth = row.getWidth();
            }
        }
    }

    private void parseTableStructure(T e, T notableParent) {
        for (T child : e.getChildren()) {
            String name = child.getLocalName().toLowerCase();
            if (name.equals("tr")) {
                parseTableRow(child, notableParent);
            } else if (name.equals("td") || name.equals("th") || name.equals("table")) {
                // gak, a nested/malformed table!  go no further, here be dragons...
                continue;
            } else if (name.equals("thead") || name.equals("tbody") || name.equals("tfoot")) {
                // Complex Table Model elements
                parseTableStructure(child, child);
            } else {
                // probably a span/div or something - blithely ignore
                parseTableStructure(child, notableParent);
            }
        }
    }

    private void parseTableRow(T row, T notableParent) {
        parseRow++;
        parseColumn = 0;
        skipRowspanCellsFromAbove();

	    for (T cell : row.getChildren()) {
	    	String cellName = cell.getLocalName().toLowerCase();
            if (cellName.equals("td") || cellName.equals("th")) {
                int rowspan = cell.getIntAttribute(null, "rowspan");
                int colspan = cell.getIntAttribute(null, "colspan");

                for (int y = parseRow; y < parseRow + rowspan; y++) {
                    while (y >= rows.size()) {
                        rows.add(new RowNavigatorImpl<T>(this));
                    }
                    RowNavigatorImpl<T> rowNavigator = rows.get(y);
                    if (y == parseRow) {
                        rowNavigator.setRowElement(row);
                    }
                    for (int x = parseColumn; x < parseColumn + colspan; x++) {
                        rowNavigator.put(x, cell);
                    }
                }
                parseColumn += colspan;
                skipRowspanCellsFromAbove();
            }
        }

        RowNavigatorImpl<T> parseRowNavigator = rows.get(parseRow);
        if (notableParent != null && notableParent.getLocalName().toLowerCase().equals("thead")) {
            // header row by default
            processHeaderRow(parseRowNavigator);
        } else if (notableParent != null && notableParent.getLocalName().toLowerCase().equals("tbody")) {
            // content row by default
            processContentRow(parseRowNavigator);
        } else if (notableParent == null || !notableParent.getLocalName().toLowerCase().equals("tfoot")) {
            for (int i = 0; i < parseRowNavigator.getWidth(); i++) {
                T cell = parseRowNavigator.get(i);
                if (cell.getLocalName().toLowerCase().equals("td")) {
                    processContentRow(parseRowNavigator);
                    return;
                }
            }
            for (int i = 0; i < parseRowNavigator.getWidth(); i++) {
                T cell = parseRowNavigator.get(i);
                if (cell.getLocalName().toLowerCase().equals("th")) {
                    processHeaderRow(parseRowNavigator);
                    return;
                }
            }
        }
    }

    private void processHeaderRow(RowNavigatorImpl<T> rowNavigator) {
        for (int i = 0; i < rowNavigator.getWidth(); i++) {
            T cell = rowNavigator.get(i);
            if (cell.getLocalName().toLowerCase().equals("th")) {
                while (columnHeaders.size() <= i) {
                    columnHeaders.add(new ArrayList<T>());
                }
                columnHeaders.get(i).add(cell);
            }
        }
    }

    private void processContentRow(RowNavigatorImpl<T> rowNavigator) {
        contentRows.add(rowNavigator);
    }

    private void skipRowspanCellsFromAbove() {
        if (parseRow < rows.size()) {
            RowNavigatorImpl<T> rowList = rows.get(parseRow);
            while (rowList != null && parseColumn < rowList.getWidth() && rowList.get(parseColumn) != null) {
                parseColumn++;
            }
        }
    }

    public int getOriginalWidth() {
		return originalWidth;
	}

	public int getOriginalHeight() {
		return originalHeight;
	}

	public boolean hasMoreContentRows() {
        return (contentRows.size() > navigateRow + 1);
	}

	public RowNavigatorImpl<T> nextContentRow() {
        navigateRow++;
        while (contentRows.size() <= navigateRow) {
            T newRowElement = newRowParent.addChild("tr");
            RowNavigatorImpl<T> rowStruct = new RowNavigatorImpl<T>(this, newRowElement);
            rowStruct.get(originalWidth-1);
            contentRows.add(rowStruct);
        }
        return contentRows.get(navigateRow);
	}

	public Iterable<T> getColumnHeaders(int column) {
        if (columnHeaders.size() > column) {
            return columnHeaders.get(column);
        }
        return new ArrayList<T>();
	}
}

