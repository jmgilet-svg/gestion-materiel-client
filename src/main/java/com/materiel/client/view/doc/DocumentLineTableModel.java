package com.materiel.client.view.doc;
import com.materiel.client.model.DocumentLine;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;
import static java.math.RoundingMode.HALF_UP;

public class DocumentLineTableModel extends AbstractTableModel {
  private final String[] cols = {"Désignation","Qté","Unité","PU HT","Remise %","TVA %","Ligne HT","TVA €","Ligne TTC"};
  private final Class<?>[] types = {String.class, BigDecimal.class, String.class, BigDecimal.class, BigDecimal.class, BigDecimal.class, BigDecimal.class, BigDecimal.class, BigDecimal.class};
  private final List<DocumentLine> data;
  public DocumentLineTableModel(List<DocumentLine> lines){ this.data = lines; }

  @Override public int getRowCount(){ return data.size(); }
  @Override public int getColumnCount(){ return cols.length; }
  @Override public String getColumnName(int c){ return cols[c]; }
  @Override public Class<?> getColumnClass(int c){ return types[c]; }
  @Override public boolean isCellEditable(int r, int c){ return c <= 5; } // 0..5 éditables, totaux en lecture seule

  @Override public Object getValueAt(int r, int c){
    DocumentLine l = data.get(r);
    switch(c){
      case 0: return l.getDesignation();
      case 1: return l.getQuantite();
      case 2: return l.getUnite();
      case 3: return l.getPrixUnitaireHT();
      case 4: return l.getRemisePct();
      case 5: return l.getTvaPct();
      case 6: { // HT ligne
        var rem = nz(l.getRemisePct()).divide(new BigDecimal("100"), 6, HALF_UP);
        var base = nz(l.getQuantite()).multiply(nz(l.getPrixUnitaireHT())).multiply(BigDecimal.ONE.subtract(rem));
        return base.setScale(2, HALF_UP);
      }
      case 7: { // TVA €
        var ht = (BigDecimal) getValueAt(r,6);
        var tvaPct = nz(l.getTvaPct()).divide(new BigDecimal("100"), 6, HALF_UP);
        return ht.multiply(tvaPct).setScale(2, HALF_UP);
      }
      case 8: { // TTC
        var ht = (BigDecimal) getValueAt(r,6);
        var tva = (BigDecimal) getValueAt(r,7);
        return ht.add(tva).setScale(2, HALF_UP);
      }
    }
    return null;
  }

  @Override public void setValueAt(Object aValue, int r, int c){
    DocumentLine l = data.get(r);
    switch(c){
      case 0: l.setDesignation((String)aValue); break;
      case 1: l.setQuantite(toBD(aValue)); break;
      case 2: l.setUnite((String)aValue); break;
      case 3: l.setPrixUnitaireHT(toBD(aValue)); break;
      case 4: l.setRemisePct(toBD(aValue)); break;
      case 5: l.setTvaPct(toBD(aValue)); break;
    }
    fireTableRowsUpdated(r,r);
  }

  public void addEmptyLine(){
    DocumentLine l = new DocumentLine();
    l.setDesignation("Nouvelle ligne");
    l.setUnite("u");
    l.setQuantite(new BigDecimal("1"));
    l.setPrixUnitaireHT(new BigDecimal("0.00"));
    l.setRemisePct(new BigDecimal("0"));
    l.setTvaPct(new BigDecimal("20.0"));
    data.add(l);
    int r = data.size()-1; fireTableRowsInserted(r, r);
  }
  public void removeAt(int r){ if(r>=0 && r<data.size()){ data.remove(r); fireTableRowsDeleted(r,r);} }
  public void moveUp(int r){ if(r>0){ Collections.swap(data, r, r-1); fireTableRowsUpdated(r-1, r);} }
  public void moveDown(int r){ if(r>=0 && r<data.size()-1){ Collections.swap(data, r, r+1); fireTableRowsUpdated(r, r+1);} }

  private static BigDecimal toBD(Object v){
    if (v==null) return BigDecimal.ZERO;
    if (v instanceof BigDecimal) return (BigDecimal) v;
    return new BigDecimal(v.toString().replace(",", "."));
  }
  private static BigDecimal nz(BigDecimal b){ return b==null? BigDecimal.ZERO : b; }
}
