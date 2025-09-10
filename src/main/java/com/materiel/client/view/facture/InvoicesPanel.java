package com.materiel.client.view.facture;

import com.materiel.client.model.Invoice;
import com.materiel.client.service.InvoiceService;
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
 * Liste des factures.
 */
public class InvoicesPanel extends JPanel {
    private final InvoiceService service;
    private final JTable table;
    private final InvoiceTableModel model;

    public InvoicesPanel(){
        super(new BorderLayout());
        this.service = ServiceFactory.getInvoiceService();
        this.model = new InvoiceTableModel();
        this.table = new JTable(model);
        this.table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    int viewRow = table.getSelectedRow();
                    if(viewRow>=0){
                        int modelRow = table.convertRowIndexToModel(viewRow);
                        Invoice inv = model.get(modelRow);
                        InvoiceEditDialog dlg = new InvoiceEditDialog((Frame)SwingUtilities.getWindowAncestor(InvoicesPanel.this), inv);
                        dlg.setVisible(true);
                        if(dlg.isConfirmed()){ service.update(inv); refreshData(); }
                    }
                }
            }
        });

        refreshData();
    }

    public void refreshData(){ model.setInvoices(service.list()); }

    private static class InvoiceTableModel extends AbstractTableModel {
        private final String[] cols = {"Numéro","Date","Client","HT","TVA","TTC","Statut"};
        private final List<Invoice> data = new ArrayList<>();
        public void setInvoices(List<Invoice> invoices){ data.clear(); data.addAll(invoices); fireTableDataChanged(); }
        public Invoice get(int r){ return data.get(r); }
        @Override public int getRowCount(){ return data.size(); }
        @Override public int getColumnCount(){ return cols.length; }
        @Override public String getColumnName(int c){ return cols[c]; }
        @Override public Object getValueAt(int r,int c){
            Invoice i = data.get(r);
            switch(c){
                case 0: return i.getNumber();
                case 1: return i.getDate();
                case 2: return i.getCustomerName();
                case 3: return i.getTotalHT();
                case 4: return i.getTotalTVA();
                case 5: return i.getTotalTTC();
                case 6: return i.getStatus();
            }
            return null;
        }
        @Override public Class<?> getColumnClass(int c){ return c==1? java.time.LocalDate.class : c>=3 && c<=5? BigDecimal.class : String.class; }
    }
}
