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

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

// =============================================================================
// =============================================================================
public class Copyright extends Dialog implements MouseListener, MouseMotionListener{

  final int WIDTH=400;    // about box dimensions
  final int HEIGHT=300;

  Main parent;          // link to main object
  Image image=null;     // "about" image
  AppletContext aplctx=null;  // browser window where applet resides

  boolean sizeSet=false;    // size of window incerased by insets
  MediaTracker mt;

  // =============================================================================
  public Copyright(Frame owner, boolean modal, URL u, AppletContext apl, Main p) {
    super(owner, Lang.get("Copyright & about information..."), modal);
    aplctx=apl;
    parent=p;
    addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        dispose();
      }
    });

    image=getImageFromJAR("/about.gif");
    if (image!=null){
      mt=new MediaTracker(this);
      mt.addImage(image,0);
      try{
        mt.waitForAll();
      }catch (InterruptedException e){};
    }
    addMouseListener(this);
    addMouseMotionListener(this);

    this.setLayout(null);

    setSize(WIDTH+this.getInsets().right+this.getInsets().left,
      HEIGHT+this.getInsets().bottom+this.getInsets().top);
    setResizable(false);

    setVisible(true);
  }

  // =============================================================================
  public void update(Graphics g){
    paint(g);
  }

  // =============================================================================
  public void paint(Graphics g){
    if(image!=null && !mt.checkID(0))
      return;

    if (!sizeSet){
      sizeSet=true;
      setSize(WIDTH+this.getInsets().right+this.getInsets().left,HEIGHT+this.getInsets().bottom+this.getInsets().top);
    }

    if (image!=null){
      g.drawImage(image,this.getInsets().left,this.getInsets().top,null);
    }else{
      g.translate(this.getInsets().left/2,this.getInsets().top/2);
      g.drawString("Whooops, there is no about.gif file so we have to use plain text...",17, 3+this.getInsets().top);
      g.drawString("author: Tomasz Zielinski",85, 98+this.getInsets().top);
      g.drawString("e-mail: tomash@fidonet.org.pl",85, 123+this.getInsets().top);
      g.drawString("manual: drawboard.sf.net",85, 147+this.getInsets().top);
      g.drawString("homepage: sf.net/projects/drawboard",85, 173+this.getInsets().top);
      g.drawString("Drawboard comes with ABSOLUTELY NO WARRANTY.",43, 220+this.getInsets().top);
      g.drawString("This is free software, and you are welcome to redistribute",36, 235+this.getInsets().top);
      g.drawString("it under the terms of the GNU General Public License.",48, 250+this.getInsets().top);
      g.drawString("This software uses PngEncoder library created by J. David Eisenberg",9, 274+this.getInsets().top);
      g.setFont(new java.awt.Font("Monospaced", 1, 25));
      g.drawString("Drawboard v 1.4",84, 43+this.getInsets().top);
    }


  }

  public void mouseDragged(MouseEvent e){}
  public void mouseMoved(MouseEvent e){  // mouse change to hand over links
    int X=e.getX()-this.getInsets().left;
    int Y=e.getY()-this.getInsets().top;
    if (
      (X>65 && X<355 && Y>100 && Y<155)||
      (X>60 && X<300 && Y>155 && Y<180)||
      (X>30 && X<375 && Y>180 && Y<205)
    )
      this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    else
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mousePressed(MouseEvent e){}
  public void mouseReleased(MouseEvent e){}

  // =============================================================================
  public void mouseClicked(MouseEvent e){
    int X=e.getX()-this.getInsets().left;
    int Y=e.getY()-this.getInsets().top;
    try{
      if (X>65 && X<355 && Y>100 && Y<155){
        aplctx.showDocument(new URL("mailto:tomash@fidonet.org.pl"),"_blank");
      }
      if (X>60 && X<300 && Y>155 && Y<180){
        aplctx.showDocument(new URL("http://drawboard.sf.net/"),"_blank");
      }
      if (X>30 && X<375 && Y>180 && Y<205){
        aplctx.showDocument(new URL("http://sf.net/projects/drawboard/"),"_blank");
      }
    }catch(MalformedURLException ee){System.out.println("Link error!?");}
  }

  // =============================================================================
  public String filterFile(String file){
    int index = file.lastIndexOf("/");
    if ((index > -1) && (file.length() > index + 1))
      return (file.substring(0,index + 1));
    else return file;
  }

  // =============================================================================
  protected Image getImageFromJAR(String fileName){
      if( fileName == null )
        return null;

      Image i = null;
      byte[] thanksToNetscape = null;
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      InputStream in = parent.getClass().getResourceAsStream(fileName);

      try{
         int length = in.available();
         thanksToNetscape = new byte[length];
         in.read( thanksToNetscape );
         i = toolkit.createImage( thanksToNetscape );
      }
      catch(NullPointerException exc){
         System.out.println( exc +" getting resource " +fileName );
         return null;
      }
      catch(IOException exc){
         System.out.println( exc +" getting resource " +fileName );
         return null;
      }
      return i;
  }

}
