package esteganografia_simple;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class paint extends JPanel {
    private BufferedImage foto;
    //tamanho do recipiente
    private int largura=0;
    private int altura=0;
    private BufferedImage Imagem_em_memoria;
//
    private JFileChooser fileChooser = new JFileChooser();
    private FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivo de Imagem","jpg","png","bmp");
    private File Diretorio = fileChooser.getCurrentDirectory();
    private String PathFile = "";

    public paint(){
        try {
                //A imagem padrão é carregada - tela branca
            foto = ImageIO.read(getClass().getResource("DEVRY.jpg"));
            largura = foto.getWidth();
            altura = foto.getHeight();
            this.setPreferredSize(new Dimension(largura, altura));
            this.setSize(new Dimension(largura, altura));
            this.setVisible(true);
            this.repaint();
        } catch (IOException ex) {            
        }
    }

    public Dimension getTamanho(){
        return new Dimension(largura,altura);
    }

    //o método paint é substituído
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //cria-se uma imagem na memória
        Imagem_em_memoria = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = Imagem_em_memoria.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //desenhe as imagens no recipiente
        g2d.drawImage(foto,0,0,this);
        //toda a imagem é desenhada
        g2.drawImage(Imagem_em_memoria, 0, 0, this);
    }

    //
    public void setFoto(BufferedImage f){
        this.foto = f;
        largura =f.getWidth();
        altura = f.getHeight();
        this.setSize(new Dimension(largura, altura));
        this.repaint();
    }

    public BufferedImage getFoto(){
        return this.foto;
    }

    //mostra uma janela de diálogo para abrir um arquivo de imagem
    public boolean Abrir_Imagem(){
       boolean ok=false;
       fileChooser = new JFileChooser();
       fileChooser.setFileFilter(filter);
       //fileChooser.setCurrentDirectory(new java.io.File("e:/"));
       fileChooser.setCurrentDirectory( Diretorio );
       int result = fileChooser.showOpenDialog(null);
       if ( result == JFileChooser.APPROVE_OPTION ){
            try {                
                foto = ImageIO.read( fileChooser.getSelectedFile() );
                PathFile = fileChooser.getSelectedFile().getPath();
                largura =foto.getWidth();
                altura = foto.getHeight();
                this.setSize(new Dimension(largura, altura));
                this.repaint();
                this.Diretorio = fileChooser.getCurrentDirectory();
                ok=true;
            } catch (IOException ex) {

            }
        }
       return ok;
    }

     //Método que salva a imagem
    public void guardar_imagem( BufferedImage foto ){        
        try {
            String tmp_file = this.PathFile.substring(0, this.PathFile.length()-4) + "_copia.bmp";
            //está salvo           
            ImageIO.write(foto, "bmp", new File( tmp_file ));
            //System.out.println( tmp_file );
            JOptionPane.showMessageDialog(null, "Imagem salva em:\n"
                    + "Arquivo: " + tmp_file);
	} catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro: não foi possível guardar a imagem...");
	}
   }
      // teste
}
