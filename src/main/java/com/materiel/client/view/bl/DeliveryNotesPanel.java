package com.materiel.client.view.bl;

import com.materiel.client.model.DeliveryNote;
import com.materiel.client.service.DeliveryNoteService;
import com.materiel.client.service.ServiceFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Liste des bons de livraison.
 */
public class DeliveryNotesPanel extends JPanel {
    private final DeliveryNoteService service;
    private final JTable table;
    private final DeliveryNoteTableModel model;

    public DeliveryNotesPanel(){
        super(new BorderLayout());
        this.service = ServiceFactory.getDeliveryNoteService();
        this.model = new DeliveryNoteTableModel();
        this.table = new JTable(model);
        this.table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    int viewRow = table.getSelectedRow();
                    if(viewRow>=0){
                        int modelRow = table.convertRowIndexToModel(viewRow);
                        DeliveryNote note = model.get(modelRow);
                        DeliveryNoteEditDialog dlg = new DeliveryNoteEditDialog((Frame)SwingUtilities.getWindowAncestor(DeliveryNotesPanel.this), note);
                        dlg.setVisible(true);
                        if(dlg.isConfirmed()){ service.update(note); refreshData(); }
                    }
                }
            }
        });

        refreshData();
    }

    public void refreshData(){ model.setNotes(service.list()); }

    private static class DeliveryNoteTableModel extends AbstractTableModel {
        private final String[] cols = {"Numéro","Date","Client","HT","TVA","TTC","Statut"};
        private final List<DeliveryNote> data = new ArrayList<>();
        public void setNotes(List<DeliveryNote> notes){ data.clear(); data.addAll(notes); fireTableDataChanged(); }
        public DeliveryNote get(int r){ return data.get(r); }
        @Override public int getRowCount(){ return data.size(); }
        @Override public int getColumnCount(){ return cols.length; }
        @Override public String getColumnName(int c){ return cols[c]; }
        @Override public Object getValueAt(int r,int c){
            DeliveryNote n = data.get(r);
            switch(c){
                case 0: return n.getNumber();
                case 1: return n.getDate();
                case 2: return n.getCustomerName();
                case 3: return n.getTotalHT();
                case 4: return n.getTotalTVA();
                case 5: return n.getTotalTTC();
                case 6: return n.getStatus();
            }
            return null;
        }
        @Override public Class<?> getColumnClass(int c){ return c==1? java.time.LocalDate.class : c>=3 && c<=5? BigDecimal.class : String.class; }
    }
}
