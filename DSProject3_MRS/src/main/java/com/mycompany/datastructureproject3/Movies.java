/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.datastructureproject3;

import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Mahmud Bera Karagöz
 */
public class Movies extends javax.swing.JFrame {
    
    
    private String[][] main_users;
    private String[][] movies;
    private String[] moviesCodes;
    String targetUserFilename = "target_user.csv";
    String mainUserFileName = "main_data.csv";
    String moviesFileName = "movies.csv";

    MaxHeap mainhHeap = new MaxHeap(600);
    MaxHeap movieRate = new MaxHeap(5402);
        
    public static String[][] readCsv(String csvFile) { 
        String[][] matrix = null;
        try ( BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            int rowCount = 0;
            int columnCount = 0;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (matrix == null) {
                    rowCount = 1;
                    columnCount = values.length;
                    matrix = new String[rowCount][columnCount];
                } else {
                    rowCount++;
                    String[][] temp = new String[rowCount][columnCount];
                    java.lang.System.arraycopy(matrix, 0, temp, 0, rowCount - 1);
                    matrix = temp;
                }
                matrix[rowCount - 1] = values;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }
    
    public static String[] readCSVHeader(String csvFile) { 
        List<String> firstColumn = new ArrayList<>();

        try ( BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length > 0) {
                    firstColumn.add(columns[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return firstColumn.toArray(new String[0]);
    }
    
    public static void compare(String[] target, String[][] main, MaxHeap heap) {
        for (int i = 1; i < main.length; i++) {
            String sim = Double.toString(cosineSimilarity(target, main[i]));
            heap.insert(main[i][0], cosineSimilarity(target, main[i]));
        }
    }

    public static double cosineSimilarity(String[] vector1, String[] vector2) {

        double[] vectorA = convertToDoubleArray(vector1);
        double[] vectorB = convertToDoubleArray(vector2);

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static double[] convertToDoubleArray(String[] stringArray) {
        double[] doubleArray = new double[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            doubleArray[i] = Double.parseDouble(stringArray[i]);
        }
        return doubleArray;
    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }    
    
    private void loadMovie() {
        try ( BufferedReader br = new BufferedReader(new FileReader(moviesFileName))) {
            String header = br.readLine();
            String line;
            String[] allMovieNames = new String[5402]; // Bütün film isimlerini depolamak için bir dizi oluşturuldu
            int totalMovieCount = 0;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Bazi film isimleri tirnak isareti icerisinde yazilmis. bunlari tek bir string olarak almasi icin bir regex kullanildi.
                String movieName = parts[1];

                allMovieNames[totalMovieCount] = movieName; // Her bir film ismi diziye ekleniyor
                totalMovieCount++;

                if (totalMovieCount >= 5402) {
                    break; // İstenilen toplam film sayısına ulaşıldıysa döngüden çıkılır
                }
            }            
            
            // Her bir combo box'a rastgele seçilen filmler eklenir
            printMovie(jComboBox1, allMovieNames);
            printMovie(jComboBox2, allMovieNames);
            printMovie(jComboBox3, allMovieNames);
            printMovie(jComboBox4, allMovieNames);
            printMovie(jComboBox5, allMovieNames);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void printMovie(JComboBox cb, String[] movies){
        int a;
        Random random = new Random();
        for (int i = 0; i < 15; i++) {            
                a = random.nextInt(movies.length) ; // 0 ile film sayisi arasinda herhangi bir tam sayi dondurur.
                cb.addItem(movies[a]); // bu tam sayi indexinde bulundan filmi ekleme yapar.
        }        
    }
    
    private int getMovieIndex(String movieName, String fileName) {
        try ( BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                String movie = parts[1];

                if (movie.equals(movieName)) {
                    return Integer.parseInt(parts[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    private String[] calculateUserVector() { // girilen puanlar ile yeni bir puan vektoru olusturulur
       
        String[] userVector = new String[main_users[5].length];// Kullanıcı vektörü için bir dizi oluşturuldu
        Arrays.fill(userVector, "0");
        JComboBox<String>[] comboBoxes = new JComboBox[]{jComboBox1, jComboBox2, jComboBox3, jComboBox4, jComboBox5};
        JTextField[] textFields = new JTextField[]{jTextField1, jTextField2, jTextField3, jTextField4, jTextField5};

        for (int i = 0; i < comboBoxes.length; i++) {
            JComboBox<String> comboBox = comboBoxes[i];
            JTextField textField = textFields[i];

            String selectedMovie = (String) comboBox.getSelectedItem();
            String rating = textField.getText().trim();

            if (!rating.isEmpty() && rating.matches("[0-5]")) { // puanlama 0-5 arasinda yapilip yapilmadigi kontrol edilir.
                int movieIndex = getMovieIndex(selectedMovie, moviesFileName); // filmin kodunun puanlamada hangi indexte oldugu bulunur
                if (movieIndex != -1) {
                    userVector[movieIndex] = rating; // bulundan indexe gore puan vektore eklenir
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a valid score (between 0 and 5).", "Incorrect entry!", JOptionPane.ERROR_MESSAGE);
            }
        }
        return userVector;
    }
    
    /* ************************************************************** */
    
    
    /**
     * Creates new form Movies
     */
    public Movies() {
        initComponents();
        setLocationRelativeTo(null);
        loadMovie();
        main_users = readCsv(mainUserFileName);
        movies = readCsv(moviesFileName);
        moviesCodes = readCSVHeader(moviesFileName);
        // program basladiğinda dosyalardan okumalar yapilir
    }

    private void showPopUp(MouseEvent e) {
        popup.show(this, e.getX(), e.getY());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        popup = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        jMenuItem1.setText("Back to system");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuItem1ActionPerformed(evt);
            }
        });
        popup.add(jMenuItem1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(110, 1, 38));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Movies", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 1, 14))); // NOI18N
        jPanel1.setEnabled(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(796, 304));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                jPanel1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                jPanel1MouseReleased(evt);
            }
        });

        jComboBox1.setMinimumSize(new java.awt.Dimension(72, 33));

        jComboBox2.setPreferredSize(new java.awt.Dimension(72, 33));

        jComboBox3.setMinimumSize(new java.awt.Dimension(72, 33));

        jComboBox4.setMinimumSize(new java.awt.Dimension(72, 33));
        jComboBox4.setPreferredSize(new java.awt.Dimension(72, 33));

        jComboBox5.setMinimumSize(new java.awt.Dimension(72, 33));
        jComboBox5.setPreferredSize(new java.awt.Dimension(72, 33));

        jTextField2.setPreferredSize(new java.awt.Dimension(72, 33));

        jTextField3.setPreferredSize(new java.awt.Dimension(72, 33));

        jTextField4.setPreferredSize(new java.awt.Dimension(72, 33));

        jTextField5.setPreferredSize(new java.awt.Dimension(72, 33));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel1.setText("X :");

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel2.setText("K :");

        jButton1.setBackground(new java.awt.Color(153, 153, 153));
        jButton1.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(51, 51, 51));
        jButton1.setText("Get Recommendation");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox5, 0, 160, Short.MAX_VALUE)
                            .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(86, 86, 86)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField1)
                                .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTextField6, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(94, 94, 94)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.dispose();
        System sys = new System();
        sys.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        if (evt.isPopupTrigger())
            showPopUp(evt);
    }//GEN-LAST:event_jPanel1MousePressed

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        if (evt.isPopupTrigger())
            showPopUp(evt);
    }//GEN-LAST:event_jPanel1MouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (isNumeric(jTextField1.getText()) && isNumeric(jTextField2.getText()) && isNumeric(jTextField3.getText()) && isNumeric(jTextField4.getText()) && isNumeric(jTextField5.getText()) && Integer.parseInt(jTextField7.getText()) < 601 && Integer.parseInt(jTextField6.getText())< 5403) {
            jTextArea1.setText("");
            mainhHeap.clear();
            movieRate.clear();
            compare(calculateUserVector(), main_users, mainhHeap); // girilen puanlar ile olusturulan vektor karsilastirmaya sokulur ve islemler gerceklesir.
            int count = 1;
            for (int i = 0; i < Integer.parseInt(jTextField7.getText()); i++) {
                Node node = mainhHeap.removeMax();
                for (int j = 0; j < main_users.length; j++) {
                    if (node.getKey().equals(main_users[j][0])) {
                        for (int k = 1; k < main_users[j].length; k++) {
                            for (int l = 1; l < moviesCodes.length; l++) {
                                if (Integer.parseInt(moviesCodes[l]) == k) {
                                    movieRate.insert(moviesCodes[l], Double.parseDouble(main_users[j][k]));
                                    break;
                                }
                            }
                        }
                        for (int k = 0; k < Integer.parseInt(jTextField6.getText()); k++) {
                            Node node2 = movieRate.removeMax();
                            for (int l = 1; l < movies.length; l++) {
                                if (node2.getKey().equals(movies[l][0])) {
                                jTextArea1.append(count + "-) " + " Name: " + movies[l][1] + ", Category: " + movies[l][2] + "\n");
                                count++;
                                }                                
                            }
                        }
                        movieRate.clear();
                    }
                }
                
            }
        }else {
            JOptionPane.showMessageDialog(rootPane, "Check the values you entered!", "Warning!", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Movies.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Movies.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Movies.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Movies.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Movies().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JPopupMenu popup;
    // End of variables declaration//GEN-END:variables
}
