/*
Drawboard - Java applet used to make graphical teleconferences
Copyright (C) 2001  Tomek "TomasH" Zielinski, tomash@fidonet.org.pl

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package drawboard;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

// =============================================================================
// =============================================================================
public class Board implements MouseListener, MouseMotionListener, KeyListener, FocusListener{
  short DIM_X;          // canvas width
  short DIM_Y;          // canvas heigth

  private Object element;   // current draw element (line, circle, etc.)
  private String sentence;  // information sequence

  private Main parent=null;                 // link to Main object
  private Communicator comm=null;           // Link to communicator
  private ColorPicker colorpicker=null;     // link to ColorPicker
  private Menu me;                          // link to menu

  private boolean outbound;                 // true, if cursor is out canvas
  boolean network_disabled=false;           // true if applet opened from local file
  static boolean offline=true;             // true if not connected at the moment
  private short mousekey=0;                 // which mouse key pressed

  // ===========================================================================
  // Constructor
  public Board(short XX, short YY, Main p) {
    parent=p;
    DIM_X=XX;
    DIM_Y=YY;
    int color=Color.blue.getRGB();
  }

  // ===========================================================================
  // Repaint the Board
  public void paint(Graphics g){

    // double buffering
    System.arraycopy(parent.matrix,0,parent.matrix2,0,parent.matrix.length);
    if ((!outbound)&&(element!=null)){
      draw(element,parent.matrix2);
    }

    parent.bgImage.flush();
    g.drawImage(parent.bgImage,0,0,null);

    if (network_disabled && sentence==null)sentence=Lang.get("WARNING: Network communication disabled, see doc for details");
    if (sentence!=null){
      g.setColor(Color.black);
      g.drawString(sentence, 10,DIM_Y-20);
      g.setColor(Color.white);
      g.drawString(sentence, 11,DIM_Y-19);
    }
  }

  // ===========================================================================
  // Draw element
  void draw(Object element, int[] mat){
    ((Shape)element).draw(mat, DIM_X);
  }


  // ===========================================================================
  // Makes links between Board and ColorPicker/Menu
  void setLinks(ColorPicker c, Menu m, Communicator com){
    comm=com;
    colorpicker=c;
    me=m;
  }

  // ===========================================================================
  // Returns current draw mode
  Object current_mode(){
    Object ret=null;
    try{
      ret=me.mode.getClass().newInstance();
    }catch(Exception e){System.out.println(Lang.get("Reinitialize of element failed: ")+e);}
    return ret;
  }

  // ===========================================================================
  // Returns current thickness
  short current_thick(){
    return me.getThick();
  }

  // ===========================================================================
  // Returns current color
  int current_color(){
    return colorpicker.current.getRGB();
  }

  // =============================================================================
  // Set the WAIT cursor on and off
  void setCursorAndSentence(boolean b, String s){
    sentence=s;
    offline=b;
    if (b){
      parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }else{
      parent.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }
  }


  // ===========================================================================
  // Resets board
  void reset_board(){
    int color=current_color();
    for (int i=0; i<parent.matrix.length; i++)parent.matrix[i]=color;
    send(new Reset(current_color()));
  }

  public void focusLost(FocusEvent e){}
  public void focusGained(FocusEvent e){
    parent.requestFocus();
  }

  public void keyReleased(KeyEvent e){}
  public void keyPressed(KeyEvent e){}
  public void keyTyped(KeyEvent e){
    if (!outbound){
      if (current_mode() instanceof LetterBox){

        if (e.getKeyChar()!=e.VK_BACK_SPACE){                 // new char

          // send letter through network...
          Letter l=new Letter(
              (short)(((LetterBox)element).x2-2),
              (short)(((LetterBox)element).y2-5),
              ((LetterBox)element).RGB,
              (short)e.getKeyChar()
            );
          send(l);

          // ... draw it and move carret forward
          ((LetterBox)element).addletter(e.getKeyChar(),parent.matrix[(((LetterBox)element).y2)*(parent.DIM_X-parent.RIGHT_MARGIN)+((LetterBox)element).x2]);
          draw(l,parent.matrix);
          l=null;
          ((LetterBox)element).setx2y2(
            (short)(((LetterBox)element).x2+2+Alphabet.getWidth(e.getKeyChar())),
            ((LetterBox)element).y2);

        }else{                                                // delete previous char
          int back=((LetterBox)element).removeletter();
          if (back>0){
            FillBox fb=new FillBox(   // draw box with previous color
                (short)(((LetterBox)element).x2-back-2),
                (short)(((LetterBox)element).y2-5),
                ((LetterBox)element).removelettercolor(),(short)1
              );
            fb.setx2y2(
                (short)(((LetterBox)element).x2-2),
                (short)(((LetterBox)element).y2+6)
            );
            draw(fb,parent.matrix);
            send(fb);
            fb=null;

            // and move carret backward
            ((LetterBox)element).setx2y2(
              (short)(((LetterBox)element).x2-back),
              ((LetterBox)element).y2);
          }

        }
      }
    }
  }

  public void mouseExited(MouseEvent e){
    outbound=true;
  }
  public void mouseEntered(MouseEvent e){
    outbound=false;

    // LetterBox is initialized on enter, not click
    if (current_mode() instanceof LetterBox){
      element=current_mode();
      ((Shape)element).init((short)e.getX(),(short)e.getY(),current_color(), current_thick(),(short)e.getX(),(short)e.getY());
      parent.requestFocus();
    }else{
      element=null;
    }
  }

  public void mouseClicked(MouseEvent e){}
  public void mouseMoved(MouseEvent e){
    // texttool watches mousemove, not only mousedrag
    if (element instanceof LetterBox){
      ((Shape)element).init((short)e.getX(),(short)e.getY(),current_color(), current_thick(),(short)e.getX(),(short)e.getY());
    }
  }


  // ===========================================================================
  // Handle mouse press
  public void mousePressed(MouseEvent e){
    boolean stateChanged=false;
    if (mousekey==0){
      if (e.getModifiers()==e.BUTTON1_MASK){
        mousekey=1;
        stateChanged=true;
      }
      else
        mousekey=2;
    }

    if (mousekey==1 && stateChanged){
      element=current_mode();
      ((Shape)element).init((short)e.getX(),(short)e.getY(),current_color(), current_thick(),(short)e.getX(),(short)e.getY());
    }

    if (mousekey==1 && element instanceof LetterBox){
      ((Shape)element).setx2y2((short)e.getX(),(short)e.getY());
    }

    if (mousekey==2){
      colorpicker.setNewColor(parent.matrix[e.getY()*DIM_X+e.getX()]);
    }

  }

  // ===========================================================================
  // Handle mouse drag
  public void mouseDragged(MouseEvent e){

    if (mousekey==1){
      // This code cannot be placed in mouseExited and mouseEntered because of IE JVM bug
      if (e.getX()<0 || e.getX()>DIM_X || e.getY()<0 || e.getY()>DIM_Y){
        outbound=true;
      }else{
        outbound=false;
      }

      if (element instanceof Freehand) {  // Freehand is special mode because of limited element capacity...
        if (((Freehand)element).nextxy((short)e.getX(),(short)e.getY())){
          send(((Freehand)element));
          draw(element,parent.matrix);
          element=new Freehand((short)e.getX(),(short)e.getY(),current_color(), current_thick());
          ((Freehand)element).init ((short)e.getX(),(short)e.getY(),current_color(), current_thick(),(short)e.getX(),(short)e.getY());
        }
      }
      else{   // ... all other types are simple
        ((Shape)element).setx2y2((short)e.getX(),(short)e.getY());
      }
    }

    if (mousekey==2)
      if (e.getX()>=0 && e.getX()<DIM_X && e.getY()>=0 && e.getY()<DIM_Y)
        colorpicker.setNewColor(parent.matrix[e.getY()*DIM_X+e.getX()]);

  }


  // ===========================================================================
  // Send current element
  public void send(Object o){
    if (!network_disabled && !offline){
      ((Shape)o).setID(parent.ID);
      comm.send(o);
    }
  }

  // ===========================================================================
  // Handle mouse relase
  public void mouseReleased(MouseEvent e){
    if (mousekey==1){
      if (element instanceof LetterBox){

      }else if (!outbound){
        draw(element,parent.matrix);
        send(element);
        element=null;
      }
    }
    mousekey=0;
  }

}

