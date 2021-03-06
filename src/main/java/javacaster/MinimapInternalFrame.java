/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacaster;

/**
 *
 * @author kouta
 */
public class MinimapInternalFrame extends javax.swing.JInternalFrame {

    /**
     * Creates new form MinimapInternalFrame
     */
    public MinimapInternalFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        minimapViewer1 = new javacaster.MinimapViewer();

        javax.swing.GroupLayout minimapViewer1Layout = new javax.swing.GroupLayout(minimapViewer1);
        minimapViewer1.setLayout(minimapViewer1Layout);
        minimapViewer1Layout.setHorizontalGroup(
            minimapViewer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 466, Short.MAX_VALUE)
        );
        minimapViewer1Layout.setVerticalGroup(
            minimapViewer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 444, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(minimapViewer1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(minimapViewer1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void setRaycaster(Caster raycaster) {
        minimapViewer1.setRaycaster(raycaster);
    }

    @Override
    public void repaint() {
        if (minimapViewer1 != null) {
            minimapViewer1.repaint();
        }
        super.repaint(); //To change body of generated methods, choose Tools | Templates.
    }

    public void setMinimap(Minimap minimap) {
        minimapViewer1.setMinimap(minimap);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javacaster.MinimapViewer minimapViewer1;
    // End of variables declaration//GEN-END:variables

    Minimap getMinimap() {
        return minimapViewer1.getMinimap();
    }
}
