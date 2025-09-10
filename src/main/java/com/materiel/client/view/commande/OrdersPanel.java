package com.materiel.client.view.commande;

import com.materiel.client.model.Order;
import com.materiel.client.service.OrderService;
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
 * Liste des commandes avec édition des lignes.
 */
public class OrdersPanel extends JPanel {
    private final OrderService service;
    private final JTable table;
    private final OrderTableModel model;

    public OrdersPanel() {
        super(new BorderLayout());
        this.service = ServiceFactory.getOrderService();
        this.model = new OrderTableModel();
        this.table = new JTable(model);
        this.table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    int viewRow = table.getSelectedRow();
                    if(viewRow>=0){
                        int modelRow = table.convertRowIndexToModel(viewRow);
                        Order order = model.get(modelRow);
                        OrderEditDialog dlg = new OrderEditDialog((Frame)SwingUtilities.getWindowAncestor(OrdersPanel.this), order);
                        dlg.setVisible(true);
                        if(dlg.isConfirmed()){ service.update(order); refreshData(); }
                    }
                }
            }
        });

        refreshData();
    }

    public void refreshData(){
        model.setOrders(service.list());
    }

    private static class OrderTableModel extends AbstractTableModel {
        private final String[] cols = {"Numéro","Date","Client","HT","TVA","TTC","Statut"};
        private final List<Order> data = new ArrayList<>();
        public void setOrders(List<Order> orders){ data.clear(); data.addAll(orders); fireTableDataChanged(); }
        public Order get(int r){ return data.get(r); }
        @Override public int getRowCount(){ return data.size(); }
        @Override public int getColumnCount(){ return cols.length; }
        @Override public String getColumnName(int c){ return cols[c]; }
        @Override public Object getValueAt(int r,int c){
            Order o = data.get(r);
            switch(c){
                case 0: return o.getNumber();
                case 1: return o.getDate();
                case 2: return o.getCustomerName();
                case 3: return o.getTotalHT();
                case 4: return o.getTotalTVA();
                case 5: return o.getTotalTTC();
                case 6: return o.getStatus();
            }
            return null;
        }
        @Override public Class<?> getColumnClass(int c){ return c==1? java.time.LocalDate.class : c>=3 && c<=5? BigDecimal.class : String.class; }
    }
}
