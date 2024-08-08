package JMRVideoExtension.iu;



public class InternalWindow extends javax.swing.JInternalFrame {

    public InternalWindow() {
        initComponents();  
    }
    
    /**
     * Internal video window that is associated (parent) of this
     */
    InternalWindowVideo intWinVid_Asociate;

    

    public Canvas getCanvas() {
        return canvas;
    }

    
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
    

    public InternalWindowVideo getIntWinVid_Asociate() {
        return intWinVid_Asociate;
    }


    public void setIntWinVid_Asociate(InternalWindowVideo intWinVid_Asociate) {
        this.intWinVid_Asociate = intWinVid_Asociate;
    }

    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        canvas = new JMRVideoExtension.iu.Canvas();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        javax.swing.GroupLayout canvasLayout = new javax.swing.GroupLayout(canvas);
        canvas.setLayout(canvasLayout);
        canvasLayout.setHorizontalGroup(
            canvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 388, Short.MAX_VALUE)
        );
        canvasLayout.setVerticalGroup(
            canvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(canvas);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JMRVideoExtension.iu.Canvas canvas;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}