package Esteganografia_Simples;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Esteganografia {
    /* Firma= permite reconhecer se uma foto tem ou não uma mensagem oculta */
    private String firma="SI";
    /* Ele armazenará o tamanho da mensagem mais o tamanho da assinatura mais seu próprio tamanho */
    private int Comprimento=0;
    //A imagem
    private BufferedImage foto=null;
    private int r,g,b;
    private Color color;
    //Irá armazenar a mensagem decomposta em uma matriz binária
    private String mensagem_binario;
    private String mensagem_original;
    private int contador = 0;

    public Esteganografia(){}

    /* Dada a mensagem (String) que está oculta, vincula-se à assinatura
       como o comprimento total da mensagem */
    private void SetMensagem(String mensagem){
        String bi="";
        //o tamanho total do mensagem
        Comprimento = mensagem.length() + firma.length() * 2;
        //Transforma o valor INTEIRO do comprimento em um valor BINÁRIO
        for( int i = 15; i>=0; i--){
           bi = bi + ( ( ( Comprimento & ( 1<<i ) ) > 0 ) ? "1" : "0" ) ;
        }
        //Concatenar toda a mensagem
        mensagem_binario = getMensagemToBinary(firma) + bi + getMensagemToBinary(mensagem);
    }

    /* O nome diz tudo */
    public void OcultarMensagem(BufferedImage f,String mensagem){
        int tmp_count=0;
        //Pedir um procedimento privado
        SetMensagem(mensagem);
        //Cria uma imagem com a qual trabalhar
        foto = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB);
        //Percorre o pixel inteiro por imagem de pixel adicionando 1 e 0 aos bits LSB
        for(int linha=0;linha<foto.getHeight();linha++){
          for(int coluna=0;coluna<foto.getWidth();coluna++){
                //A cor do pixel é obtida nas coordenadas (i, j)
                color = new Color( f.getRGB(coluna, linha) );
                //Enquanto há uma mensagem, o LSB
                if(tmp_count<=this.mensagem_binario.length()){
                    //São convertidos para o equivalente binário
                    String red = toBinary( (byte) color.getRed() );
                    String verde = toBinary( (byte) color.getGreen() );
                    String azul = toBinary( (byte) color.getBlue() );
                    //O último bit é substituído
                    red = SubstituirLSB(red);
                    verde = SubstituirLSB(verde);
                    azul = SubstituirLSB(azul);
                    //Mudanças de binário para número inteiro
                    r = Integer.parseInt(red ,2);
                    g = Integer.parseInt(verde ,2);
                    b = Integer.parseInt(azul ,2);
                }else{
                   r = color.getRed();
                   g = color.getGreen();
                   b = color.getBlue();
                }
                //É colocado na nova imagem com os valores em preto e branco
                foto.setRGB(coluna, linha, new Color(r,g,b).getRGB());
                tmp_count+=3;
          }
        }
    }

   //Lê os primeiros 6 pixels para formar os bits necessários para "SI" retornar TRUE / FALSE
   private boolean lerfirma(BufferedImage f){
       boolean ok=false;
       String t = "";
        for(int j=0;j<6;j++){
            color = new Color(f.getRGB(j, 0));
            String red = toBinary( (byte) color.getRed() );
            String verde = toBinary( (byte) color.getGreen() );
            String azul = toBinary( (byte) color.getBlue() );
            red = getLSB(red);
            verde = getLSB(verde);
            azul = getLSB(azul);
            t = t + red + verde + azul;
        }          
        if( toChar(t.substring(0, 8)).equals("S") &&  toChar(t.substring(8, 16)).equals("I") ){
            ok=true;
        }
       return ok;
   }

   /* Extrair a parte que corresponde ao tamanho total da mensagem */
   private void LerComprimentoDaMensagem(BufferedImage f){
        String t = "";
        for(int j=5;j<12;j++){
            color = new Color(f.getRGB(j,0));
            String red = toBinary( (byte) color.getRed() );
            String verde = toBinary( (byte) color.getGreen() );
            String azul = toBinary( (byte) color.getBlue() );
            red = getLSB(red);
            verde = getLSB(verde);
            azul = getLSB(azul);
            t = t + red + verde + azul;
        }
        this.Comprimento = toCharInt(t.substring(1, 17));        
      }

      /* Extrai os bits da imagem e forma a mensagem oculta novamente */
      public String getMensagemToImage(BufferedImage f){
        //Procure a imagem para ter a assinatura
        mensagem_original="Não existe mensagem oculta";
        if( lerfirma(f) ){//se a assinatura existir contínua
            //Chamar função privada
            LerComprimentoDaMensagem(f);
            //
            String[] s = new String[this.Comprimento];
            String tmp="";
            //Executa todo o pixel x pixel da imagem
            for(int linha=0;linha<f.getHeight();linha++){
                for(int coluna=0;coluna<f.getWidth();coluna++){
                    //A cor do pixel é obtida nas coordenadas (i, j)
                    color = new Color(f.getRGB(coluna, linha));
                    //São convertidos para o equivalente binário
                    String red = toBinary( (byte) color.getRed() );
                    String verde = toBinary( (byte) color.getGreen() );
                    String azul = toBinary( (byte) color.getBlue() );
                    //Você obtém os bits LSB
                    red = getLSB(red);
                    verde = getLSB(verde);
                    azul = getLSB(azul);
                    //Quando você terminar de ler toda a mensagem, você sai
                    if(tmp.length()<=(this.Comprimento*8)){
                        tmp = tmp + red + verde + azul;
                    }else{
                        break;
                    }
                }
            }
            //A String obtida de 1 e 0, separando-a em uma matriz de bytes
            int count_tmp =0;
            for(int a=0; a<(this.Comprimento*8);a = a + 8){
                s[count_tmp]=tmp.substring(a, a + 8);                
                count_tmp++;
            }
            //Solicita um procedimento privado para reconstruir a mensagem
            mensagem_original = getMensagemToString(s);
        }//Fim
        return mensagem_original;
    }

    public BufferedImage getFoto(){
        return this.foto;
    }

   private String toBinary(byte caracter){
        byte byteDeCaracter = (byte)caracter;
        String binario="";
        for( int i = 7; i>=0; i--){
           binario = binario + ( ( ( byteDeCaracter & ( 1<<i ) ) > 0 ) ? "1" : "0" ) ;
        }
        return binario;
    }

    /* Converte um binário para char */
    private String toChar(String binario){
        int i = Integer.parseInt(binario ,2);
        String aChar = new Character((char)i).toString();
        return aChar;        
    }

    private int toCharInt(String binario){
        int i = Integer.parseInt(binario ,2);        
        return i;
    }

    /*  Dado uma mensagem em um STRING decompõe isso em um String com o seu
        equivalente binário */
    private String getMensagemToBinary(String mensagem){
        String mb = "";
        char[] mensagem_tmp = mensagem.toCharArray();
        for(int i=0; i<mensagem_tmp.length;i++){
            mb = mb + toBinary( (byte) mensagem_tmp[i]);
        }
        return mb;
        
    }

    /* Reconstrói a mensagem da matriz binária para uma String */
    private String getMensagemToString(String[] mensagem){
        String mo ="";
        //Lê o elemento da quinta posição, os quatro primeiros são a assinatura 
        //e o comprimento da mensagem
        for(int i=4; i<mensagem.length;i++){
            mo = mo + toChar(mensagem[i]) ;
        }
        return mo;
    }

    /* Substitui o bit menos significativo por um bit da mensagem */
    private String  SubstituirLSB(String colorRGB){
        if(contador < mensagem_binario.length()){
            colorRGB = colorRGB.substring(0,7) + mensagem_binario.substring(contador, contador+1);
            contador++;    
        }
        return colorRGB;
    }

    private String getLSB(String binario){
        return binario.substring(7, 8);
    }

}