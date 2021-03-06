package xcordion.impl;

import xcordion.api.ItemAndExpression;
import xcordion.api.Pragma;
import xcordion.api.RowNavigator;
import xcordion.api.TestElement;

import java.util.ArrayList;
import java.util.List;

public class RowNavigatorImpl<T extends TestElement<T>> implements RowNavigator<T> {
    private TableNavigator<T> tableNavigator;
    private T rowElement;
    private ArrayList<T> cells;
    private List<ItemAndExpression<Pragma>> pragmas = new ArrayList<ItemAndExpression<Pragma>>();

    public RowNavigatorImpl(TableNavigator<T> tableNavigator) {
        this.tableNavigator = tableNavigator;
        cells = new ArrayList<T>();
    }

    public RowNavigatorImpl(TableNavigator<T> tableNavigator, T newRowElement) {
        this(tableNavigator);
        rowElement = newRowElement;
    }

    public T getRowElement() {
        return rowElement;
    }

    public void setRowElement(T rowElement) {
        this.rowElement = rowElement;
    }

    public void put(int index, T cell) {
        while (cells.size() <= index) {
            cells.add(null);
        }
        cells.set(index, cell);
    }

    public int getWidth() {
        return cells.size();
    }

    public T get(int index) {
        while (cells.size() <= index) {
            cells.add(rowElement.addChild("td"));
        }
        return cells.get(index);
    }

    public T getSidecarCell() {
        return get(tableNavigator.getOriginalWidth());
    }

    public List<ItemAndExpression<Pragma>> getPragmas() {
        return pragmas;
    }

    public void setPragmas(List<ItemAndExpression<Pragma>> pragmas) {
        this.pragmas = pragmas;
    }
}
