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
import java.awt.image.*;
import java.awt.event.*;

// ===========================================================================
// ===========================================================================
// Saved image window
public class ShowFrame extends Dialog {

  static boolean cansave=false;       // can server save the images?
  static boolean canemail=false;
  int ID;

  // ===========================================================================
  // ===========================================================================
  class ExtraCanvas extends Canvas{
    Image img;
    ExtraCanvas(Image i){
      img=i;
    }
    public void paint(Graphics g){
      g.drawImage(img,0,0,this);
    }
  }
  ExtraCanvas canvas; // Showed image


  // ===========================================================================
  // Constructor
  ShowFrame(Frame owner, boolean modal, int x, int y, Image i, String tytul, final Communicator commlink,
    final String from, final String smtp) {
    super(owner, tytul, modal);

    addWindowListener(new WindowAdapter(){        // handle window close
      public void windowClosing(WindowEvent e){
        dispose();
      }
    });

    canvas=new ExtraCanvas(i);
    canvas.setSize(x,y);
    add("Center",canvas);

    Panel bottom;

    if (canemail&&cansave)
      bottom=new Panel(new GridLayout(3,1));
    else if (canemail||cansave)
      bottom=new Panel(new GridLayout(1,1));
    else bottom=null;

    Panel mail=new Panel(new FlowLayout());
    java.awt.Label label=new java.awt.Label("Please type target e-mail");
    final java.awt.TextField input=new java.awt.TextField("",20);
    java.awt.Button send=new java.awt.Button("Send!");

    if (Board.offline==true){
      label.setEnabled(false);
      input.setEnabled(false);
      send.setEnabled(false);
      send.setLabel("disconnected");
    }else{
      label.setEnabled(true);
      input.setEnabled(true);
      send.setEnabled(true);
      send.setLabel("Send!");
    }

    send.addActionListener(new ActionListener(){

      // handle image saving:
      public void actionPerformed(ActionEvent e){

        // grab pixels to array
        int[] pixels=new int[canvas.img.getWidth(null)*canvas.img.getHeight(null)];
        PixelGrabber pg = new PixelGrabber(canvas.img, 0, 0, canvas.img.getWidth(null),
          canvas.img.getHeight(null), pixels, 0, canvas.img.getWidth(null));
        try {
          pg.grabPixels();
        }
        catch (Exception ee) {
          System.err.println("interrupted waiting for pixels!");
          return;
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
          System.err.println("image fetch aborted or errored");
          return;
        }

        // and send it compressed to server
        Archive arc=new Archive(pixels,(short)canvas.img.getWidth(null),(short)canvas.img.getHeight(null));
        arc.setID(ID);
        arc.emailSet(from,smtp);
        arc.filename=input.getText();
        if (Board.offline!=true){
          commlink.send(arc);
          dispose();
        }

      }
    });

    mail.add(label);
    mail.add(input);
    mail.add(send);
    if (canemail)
      bottom.add(mail);

    java.awt.Button button=new java.awt.Button();

    if (Board.offline!=true){
      button.setLabel("Save image on server");
      button.setEnabled(true);
    }else{
      button.setLabel("cannot save when connection is lost");
      button.setEnabled(false);
    }

    button.addActionListener(new ActionListener(){

      // handle image saving:
      public void actionPerformed(ActionEvent e){

        // grab pixels to array
        int[] pixels=new int[canvas.img.getWidth(null)*canvas.img.getHeight(null)];
        PixelGrabber pg = new PixelGrabber(canvas.img, 0, 0, canvas.img.getWidth(null),
          canvas.img.getHeight(null), pixels, 0, canvas.img.getWidth(null));
        try {
          pg.grabPixels();
        }
        catch (Exception ee) {
          System.err.println("interrupted waiting for pixels!");
          return;
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
          System.err.println("image fetch aborted or errored");
          return;
        }

        // and send it compressed to server
        Archive arc=new Archive(pixels,(short)canvas.img.getWidth(null),(short)canvas.img.getHeight(null));
        arc.setID(ID);
        if (Board.offline!=true){
          commlink.send(arc);
          dispose();
        }

      }
    });

    Panel or=new Panel(new FlowLayout());
    or.add(new Label("or"));
    if (canemail&&cansave)
      bottom.add(or);

    Panel save=new Panel(new FlowLayout());
    save.add(button);
    if (cansave)
      bottom.add(save);


    if (bottom!=null) add("South",bottom);

    this.setResizable(false);
    this.pack();
//    this.show();
  }

  void setID(int id){
    ID=id;
  }

}
