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
import java.awt.event.*;
import java.awt.image.*;

// =============================================================================
// =============================================================================
// inspired by Toh Lik Khoong's ColorPicker (http://navsurf.com) , but better ;-)
class ColorPicker extends Canvas implements MouseListener, MouseMotionListener{
  int WIDTH;      // Picker width (applet size depend)
  int HEIGHT;     // Picker height (default 50 px)
  int GREY=20;      // Grey picker width
  int GREYWHITE=8;  // Grey picker's white part height
  int OLD=10;       // Previously used colors width
  int CURRENT=35;   // Current color preview width
  int ADDONS=GREY+OLD+CURRENT;  // Non-rainbow part width

  // Initial values of old colors
  Color old[]={Color.red,Color.yellow,Color.green, Color.blue, Color.pink};

  Color current=Color.green;  // Warning: default color is set in main.java
  Color preview=Color.green;  // Warning: default color is set in main.java

  private int[] colormap;
  private Image col = null;
  private Point cur_point = new Point();

  // ===========================================================================
  // Constructor
  ColorPicker(int xx, int yy){
    WIDTH = xx;
    HEIGHT = yy;

    colormap = new int[(WIDTH-ADDONS)*HEIGHT];
    for (int x = 0; x < WIDTH-ADDONS; x++){
      for (int y = 1; y <= HEIGHT; y++){
        float H=((float) x)*((float)1/(float)(WIDTH-ADDONS));
        float S=1;
        if (y>(float)1*(float)HEIGHT/(float)2){
          S = (1-((float)y/(float)HEIGHT))*(float)2;
        }
        float B=1;
        if (y<(float)1*(float)HEIGHT/(float)2){
          B =((float)y/(float)HEIGHT)*(float)2;
        }
        colormap[x + (HEIGHT - y)*(WIDTH-ADDONS)] = Color.HSBtoRGB(H,S,B);
      }
    }

    col = createImage(new MemoryImageSource(WIDTH-ADDONS, HEIGHT, colormap, 0, WIDTH-ADDONS));
  }

  // ===========================================================================
  // Returns color of selected point
  public Color getColorAt(int x, int y){
      if (x<0) x=0;
      if (y<0) y=0;
      if (x>WIDTH-1) x=WIDTH-1;
      if (y>HEIGHT-1) y=HEIGHT-1;

      // rainbow
      if (x<WIDTH-ADDONS)
        return( new Color(colormap[x + y*(WIDTH-ADDONS)]));

      // grey
      if ((x>WIDTH-ADDONS)&&(x<WIDTH-ADDONS+GREY)){
        if (y<GREYWHITE)
          return(Color.white);
        int c=(int)((HEIGHT-y)*(255.0/(HEIGHT-GREYWHITE)));
        return (new Color (c,c,c));
      }

      // previous color
      if (x>WIDTH-CURRENT-OLD && x<WIDTH-CURRENT){
        return old[(y*5)/HEIGHT];
      }
    return current;
  }


  // ===========================================================================
  // (Re)paint whole color picker
  public void paint(Graphics g){

    // Rainbow
    g.drawImage(col, 0, 0, this);

    // Greyscale
    g.setColor(Color.white);
    g.fillRect(WIDTH-ADDONS,0,GREY+1,GREYWHITE+1);
    for (int i=0; i<HEIGHT-GREYWHITE ; i++){
      int c=(int)(i*(255.0/(HEIGHT-GREYWHITE)));
      g.setColor(new Color(c,c,c));
      g.drawLine(WIDTH-ADDONS,HEIGHT-i,WIDTH-ADDONS+GREY,HEIGHT-i);
    }

    // Old colors
    for (int a=0; a<5; a++){
      g.setColor(old[a]);
      g.fillRect(WIDTH-ADDONS+GREY+1,0+a*(HEIGHT/5),OLD,(a+1)*(HEIGHT/5));
    }

    // Current color
    g.setColor(current);
    g.fillRect(WIDTH-CURRENT,0,CURRENT,HEIGHT);

    // Preview color
    g.setColor(preview);
    g.fillRect(WIDTH-CURRENT+5,5,CURRENT-10,10);

    // Separator lines
    g.setColor(Color.lightGray);
    g.drawLine(0,0,WIDTH,0);
    g.drawLine(WIDTH-ADDONS,0,WIDTH-ADDONS,HEIGHT );
    g.drawLine(WIDTH-ADDONS+GREY,0,WIDTH-ADDONS+GREY,HEIGHT );
    g.drawLine(WIDTH-CURRENT,0,WIDTH-CURRENT,HEIGHT );
    g.drawLine(WIDTH-1,0,WIDTH-1,HEIGHT );
  }

  // ===========================================================================
  // Change current color
  void setNewColor(int i){
    current=preview=new Color(i);
    repaint(WIDTH-CURRENT,0,CURRENT,HEIGHT);
  }

  // ===========================================================================
  // Change current color
  void changecolor(MouseEvent e){
    setNewColor(getColorAt(e.getX(),e.getY()).getRGB());
  }

  // ===========================================================================
  // Insert new color to old color set...
  void oldcolors(MouseEvent e){
      if (e.getX() < WIDTH-OLD-CURRENT){ // ... if it's not selected from oldies
        old[0]=old[1];
        old[1]=old[2];
        old[2]=old[3];
        old[3]=old[4];
        old[4]=current;
        repaint(WIDTH-CURRENT-OLD,0,OLD,HEIGHT);
      }
  }

  // ===========================================================================
  // Repaint small color preview
  void preview_repaint(){
    repaint(WIDTH-CURRENT+5,5,CURRENT-10,10);
  }

  // ===========================================================================
  // Clear small color preview on mouse exit
  public void mouseExited(MouseEvent e){
      preview=current;
      preview_repaint();
    }

  // ===========================================================================
  // Draw small color preview
  public void mouseMoved(MouseEvent e){
    if (preview!=getColorAt(e.getX(),e.getY())){
      preview=getColorAt(e.getX(),e.getY());
      preview_repaint();
    }
  }

  // ===========================================================================
  // Other mouse handle routines
  public void mouseEntered(MouseEvent e){}
  public void mousePressed(MouseEvent e){
      changecolor (e);
  }
  public void mouseReleased(MouseEvent e){
      oldcolors (e);
  }
  public void mouseClicked(MouseEvent e){}
  public void mouseDragged(MouseEvent e){
      changecolor (e);
  }

}