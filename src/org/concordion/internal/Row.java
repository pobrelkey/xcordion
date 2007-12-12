package org.concordion.internal;

import org.concordion.api.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Row {
    private final Element rowElement;
    private Element[] cells;

    Row(Element rowElement) {
        this(rowElement, getCellsFromElement(rowElement));
    }

    Row(Element rowElement, Element[] cells) {
        this.cells = cells;
        assert rowElement.isNamed("tr");
        this.rowElement = rowElement;
    }

    private static Element[] getCellsFromElement(Element rowElement) {
        List<Element> cells = new ArrayList<Element>();
        for (Element childElement : rowElement.getChildElements()){
            if (childElement.isNamed("td") || childElement.isNamed("th")) {
                cells.add(childElement);
            }
        }
        return cells.toArray(new Element[0]);
    }
    
    public boolean isHeaderRow() {
        for (Element cell : cells) {
            if (cell.isNamed("td")) {
                return false;
            }
        }
        return cells.length > 0;
    }

    public Element getElement() {
        return rowElement;
    }
    
    public Element[] getCells() {
        return cells;
    }

    public Element addCell() {
        Element cell = new Element("td");
        rowElement.appendChild(cell);

        ArrayList<Element> newCells = new ArrayList<Element>(Arrays.asList(cells));
        newCells.add(cell);
        cells = newCells.toArray(new Element[newCells.size()]);

        return cell;
    }

}
