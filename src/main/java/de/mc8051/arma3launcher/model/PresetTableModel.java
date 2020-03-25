package de.mc8051.arma3launcher.model;

import de.mc8051.arma3launcher.objects.Modset;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public class PresetTableModel extends AbstractListModel  {

    private ArrayList<Modset> data = new ArrayList<>();

    public void add(Modset m){
        data.add(m);
        fireContentsChanged(this, 0, data.size());
    }

    public void clear() {
        data.clear();
        fireContentsChanged(this, 0, data.size());
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Object getElementAt(int index) {
        return data.get(index);
    }
}
