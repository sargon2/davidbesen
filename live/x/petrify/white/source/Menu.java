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
import java.net.*;
import java.io.*;
import java.applet.*;
import java.applet.*;

// =============================================================================
// =============================================================================
// Menu class
public class Menu extends Canvas implements MouseListener, MouseMotionListener{

  private Board boardlink;              // Link to blackboard
  private Applet appletlink;            // Link to applet
  private Communicator commlink;        // Link to communicator module
  private AppletContext appletcontext;  // Link to applet's context
  private Main parentMain;                  // Link to Main class

  Object mode=new Freehand();             // Initial draw mode
  private short thick=3;                  // Initial thick
  private short people=0;                 // Initial people counter

  private Button mouseover=null;        // Mouse at nothing at start

  // Declaration of buttons
  private Button btn_freehand, btn_line, btn_box, btn_fillbox, btn_circle, btn_fillcircle, btn_texttool;
  private Button thick_1,thick_2,thick_3,thick_4,thick_5,thick_6;
  private Button th_select1,th_select2,th_select3;
  private Button th_copy,th_show,th_delete;
  private Button reset, copyright, network;

  private Button thumbnail_coord;   // pseudobutton, location of small image
  private int counter_x,counter_y;  // counter coords

  private Image normal=null;      // Normal menu image
  private Image bold=null;        // Bold (mouseover) menu image
  private Image light=null;       // Light (selected) menu image
  private Image menubg=null;       // Light (selected) menu image
  volatile MediaTracker tracker;  // Main image load tracker

  String str_light;
  String str_bold;
  String str_normal;
  String str_menubg=null;

  int counterColor=Color.lightGray.getRGB();  // default counter color
  int emptyThColor=Color.lightGray.getRGB();  // default empty thumbnail color
  volatile Image[] small=new Image[3];        // Three clipboards
  int ID;
  volatile private int thumbnail=0;      // Initial thumbnail
  volatile private int network_status=0; // Initial network status OFF

  URL url_light;  // global and accessible, because if image loading failes,
  URL url_bold;   // here are the wrong URL's and drawboard can display them
  URL url_normal; // with an error message
  URL url_menubg;

  int bgimagetranscolor; // bgimage transparent color
  int bgimagetransoffsetx; // bgimage transparent x offset
  int bgimagetransoffsety; // bgimage transparent y offset


  // =============================================================================
  // Constructor
  public Menu(URL url, Main m, String skindef, int id, URL u, String str_menubg, int bgitc,int offsetx, int offsety) {
    parentMain=m;
    ID=id;
    bgimagetranscolor=bgitc;
    bgimagetransoffsetx=offsetx;
    bgimagetransoffsety=offsety;
    boolean datafileOK=false;

    if (skindef!=null){
      System.out.println(Lang.get("Trying to load custom menu datafile ")+skindef);
      datafileOK=parseSkindef(url, skindef);
      if (datafileOK==false) System.out.println(Lang.get("Loading datafile failed"));
    }

    if (datafileOK==false){
      System.out.println(Lang.get("Loading default menu"));
      btn_freehand=new Button(0,0,50,50);  // draw mode
      btn_line=new Button(50,0,100,40);
      btn_box=new Button(0,50,50,95);
      btn_fillbox=new Button(50,40,100,85);
      btn_circle=new Button(0,95,50,145);
      btn_fillcircle=new Button(50,85,100,123);
      btn_texttool=new Button(50,123,100,155);

      thick_1=new Button(0,160,65,174); // thick select
      thick_2=new Button(0,174,65,184);
      thick_3=new Button(0,184,65,195);
      thick_4=new Button(0,195,65,208);
      thick_5=new Button(0,208,65,220);
      thick_6=new Button(0,220,65,235);

      th_select1=new Button(0,250,15,250+20);     // clipboard select
      th_select2=new Button(0,250+20,15,250+40);
      th_select3=new Button(0,250+40,15,250+60);

      th_copy=new Button(75,250,100,250+20);      // clipboard operations
      th_show=new Button(75,250+20,100,250+40);
      th_delete=new Button(75,250+40,100,250+60);

      reset=new Button(67,209,96,236);      // screen reset
      copyright=new Button(60,310,93,343);  // copyright notice
      network=new Button(0,310,45,350);  // never pressed, just setting location

      thumbnail_coord=new Button(17,255,77,305);

      counter_x=81;
      counter_y=160;

      str_light="menu_light.gif"; // image files
      str_bold="menu_bold.gif";
      str_normal="menu_normal.gif";
    }

    // load images...
    if (normal==null || bold==null || light==null || menubg==null){
      String prefix=url.getProtocol()+"://";
      try{
        if (u.getHost().equals("")){
          url_light=new URL(prefix+url.getHost()+url.getFile()+"skin/"+str_light);
          url_bold=new URL(prefix+url.getHost()+url.getFile()+"skin/"+str_bold);
          url_normal=new URL(prefix+url.getHost()+url.getFile()+"skin/"+str_normal);
          if (str_menubg!=null)
            url_menubg=new URL(prefix+url.getHost()+url.getFile()+str_menubg);
        }else{
          url_light=new URL(prefix+url.getHost()+":"+url.getPort()+url.getFile()+"skin/"+str_light);
          url_bold=new URL(prefix+url.getHost()+":"+url.getPort()+url.getFile()+"skin/"+str_bold);
          url_normal=new URL(prefix+url.getHost()+":"+url.getPort()+url.getFile()+"skin/"+str_normal);
          if (str_menubg!=null)
            url_menubg=new URL(prefix+url.getHost()+":"+url.getPort()+url.getFile()+str_menubg);
        }
        normal=Toolkit.getDefaultToolkit().getImage(url_normal);
        bold=Toolkit.getDefaultToolkit().getImage(url_bold);
        light=Toolkit.getDefaultToolkit().getImage(url_light);
        if (str_menubg!=null){
          menubg=Toolkit.getDefaultToolkit().getImage(url_menubg);
        }

        repaint();
      }catch (java.net.MalformedURLException e){
        System.out.println(Lang.get("Bad URL - menu image file invalid!"));
      };

      // ... and wait for it
      tracker = new MediaTracker(this);
      tracker.addImage(normal, 0);
      tracker.addImage(bold, 1);
      tracker.addImage(light, 2);
      if (str_menubg!=null)
        tracker.addImage(menubg, 3);
    }
  };


  // =============================================================================
  public void setBgColor(int c){
    this.setBackground(new Color(c));
  }

  // =============================================================================
  public void setCounterColor(int c){
    counterColor=c;
  }

  // =============================================================================
  public void setEmptyThColor(int c){
    emptyThColor=c;
  }

  // =============================================================================
  public int modifyTransparencyCalculateOffset(int i, int imgwidth, int imgheight,
    int ptheight, int ptwidth, int bgimagetransoffsety, int bgimagetransoffsetx)
  {
    return (
    ( ((i/imgwidth)+ptheight-bgimagetransoffsety)  %ptheight)*ptwidth  // y
    )+(
    (i+ptwidth-bgimagetransoffsetx)%imgwidth%ptwidth
    );  //x
  };

  // =============================================================================
  public int[] modifyTransparencyImage(int[] pattern,int ptwidth, int ptheight, Image img){
    int[] pixels=new int[img.getWidth(null)*Main.DIM_Y];
    int imgwidth=img.getWidth(null);
    int imgheight=img.getHeight(null);

    PixelGrabber imggrab = new PixelGrabber(img, 0, 0, img.getWidth(null),
      img.getHeight(null), pixels, 0, img.getWidth(null));

    try {
      imggrab.grabPixels();
    }
    catch (Exception ee) {
      System.err.println(Lang.get("interrupted waiting for pixels!"));
      return null;
    }

    // menu transparenting
    int limit=img.getWidth(null)*img.getHeight(null);
    for (int i = 0; i < limit; i++) {
      if (((pixels[i]&0xff)==(bgimagetranscolor&0xff))
        && ((pixels[i]>>8&0xff)==(bgimagetranscolor>>8&0xff))
        && ((pixels[i]>>16&0xff)==(bgimagetranscolor>>16&0xff))
      )
      {
      pixels[i]=pattern[
        modifyTransparencyCalculateOffset(i, imgwidth, imgheight,
        ptheight, ptwidth, bgimagetransoffsety, bgimagetransoffsetx)
        ];
      }
    }

    // extend transparent section to whole right side of drawboard,
    // overpainting any free space under menu andy
    for (int i = limit; i < pixels.length; i++) {
      pixels[i]=pattern[
        modifyTransparencyCalculateOffset(i, imgwidth, imgheight,
        ptheight, ptwidth, bgimagetransoffsety, bgimagetransoffsetx)
        ];
    }

    return pixels;
  }

  // =============================================================================
  // If transparency option is choosen, apply it
  public void modifyTransparency(){
    int[] bg=new int[menubg.getWidth(null)*menubg.getHeight(null)];
    int bgwidth=menubg.getWidth(null);
    int bgheight=menubg.getHeight(null);

    PixelGrabber bggrab = new PixelGrabber(menubg, 0, 0, menubg.getWidth(null),
      menubg.getHeight(null), bg, 0, menubg.getWidth(null));
    try {
      bggrab.grabPixels();
    }
    catch (Exception ee) {
      System.err.println(Lang.get("interrupted waiting for pixels!"));
      return;
    }

    normal=createImage(new MemoryImageSource(normal.getWidth(null),Main.DIM_Y, ColorModel.getRGBdefault(), modifyTransparencyImage(bg,bgwidth,bgheight,normal), 0, normal.getWidth(null)));
    light=createImage(new MemoryImageSource(light.getWidth(null),Main.DIM_Y, ColorModel.getRGBdefault(), modifyTransparencyImage(bg,bgwidth,bgheight,light), 0, light.getWidth(null)));
    bold=createImage(new MemoryImageSource(bold.getWidth(null),Main.DIM_Y, ColorModel.getRGBdefault(), modifyTransparencyImage(bg,bgwidth,bgheight,bold), 0, bold.getWidth(null)));
    System.gc();
  }

  // =============================================================================
  // repaint routine
  public void paint(Graphics g){
    if (normal!=null){
      g.drawImage(normal,0,0,this);
    };

    // network status indicator
    switch(network_status){
      case 0: {g.drawImage(light,network.x1,network.y1,network.x2,network.y2,
        network.x1,network.y1,network.x2,network.y2,this);break;}
      case 1: {g.drawImage(normal,network.x1,network.y1,network.x2,network.y2,
        network.x1,network.y1,network.x2,network.y2,this);break;}
      case 2: {g.drawImage(bold,network.x1,network.y1,network.x2,network.y2,
        network.x1,network.y1,network.x2,network.y2,this);break;}
    }

    Button but=null;
    if (mode instanceof Freehand) but=btn_freehand;
    if (mode instanceof Line) but=btn_line;
    if (mode instanceof Box) but=btn_box;
    if (mode instanceof FillBox) but=btn_fillbox;
    if (mode instanceof Circle) but=btn_circle;
    if (mode instanceof FillCircle) but=btn_fillcircle;
    if (mode instanceof LetterBox) but=btn_texttool;

    g.drawImage(light,but.x1,but.y1,but.x2,but.y2,but.x1,but.y1,but.x2,but.y2,this);

    // highlight button other than current
    if (!but.equals(mouseover) && mouseover!=null){
      g.drawImage(bold,mouseover.x1,mouseover.y1,mouseover.x2,mouseover.y2,
        mouseover.x1,mouseover.y1,mouseover.x2,mouseover.y2,this);
    }

    // bold current thickness
    if (thick==1) but=thick_1;
    if (thick==2) but=thick_2;
    if (thick==3) but=thick_3;
    if (thick==4) but=thick_4;
    if (thick==5) but=thick_5;
    if (thick==6) but=thick_6;
    g.drawImage(light,but.x1,but.y1,but.x2,but.y2,but.x1,but.y1,but.x2,but.y2,this);

    // bold current clipboard number
    int y1=0;int y2=0;
    if (thumbnail==0)
      g.drawImage(light,
        th_select1.x1,th_select1.y1,th_select1.x2,th_select1.y2,
        th_select1.x1,th_select1.y1,th_select1.x2,th_select1.y2,
      this);
    if (thumbnail==1)
      g.drawImage(light,
        th_select2.x1,th_select2.y1,th_select2.x2,th_select2.y2,
        th_select2.x1,th_select2.y1,th_select2.x2,th_select2.y2,
      this);
    if (thumbnail==2)
      g.drawImage(light,
        th_select3.x1,th_select3.y1,th_select3.x2,th_select3.y2,
        th_select3.x1,th_select3.y1,th_select3.x2,th_select3.y2,
      this);

    // draw selected clipboard, if exist
    if (small[thumbnail]!=null){
      g.drawImage(small[thumbnail],
        thumbnail_coord.x1,thumbnail_coord.y1,thumbnail_coord.x2-thumbnail_coord.x1,thumbnail_coord.y2-thumbnail_coord.y1,
      this);
    }else{
      g.setColor(new Color(emptyThColor));
      g.fillRect(thumbnail_coord.x1,thumbnail_coord.y1,
        thumbnail_coord.x2-thumbnail_coord.x1,thumbnail_coord.y2-thumbnail_coord.y1);
    }

    // counter
    if (network_status!=0){
      FontMetrics fm = g.getFontMetrics();
      String str=String.valueOf(people);
      g.setColor(new Color(counterColor));
      g.drawString(str,counter_x-fm.stringWidth(str)/2,fm.getHeight()+counter_y);
    }
  }
  public void update(Graphics g){
    paint(g);
  }

  // =============================================================================
  // make a link to blackboard
  public void setBoard(Board bb){
    boardlink=bb;
  }

  // =============================================================================
  // make link to applet
  public void setApplet(Applet aa){
    appletlink=aa;
  }

  // =============================================================================
  // make link to communicator
  public void setCommunicator(Communicator c){
    commlink=c;
  }

  // =============================================================================
  // make link to applet context
  public void setAppletContext(AppletContext aa){
    appletcontext=aa;
  }

  // =============================================================================
  // set new value of people counter
  public void setPeople(short i){
    people=i;
    System.out.println(Lang.get("People ")+i);
    repaint();
  }

  // =============================================================================
  // set new value of people counter
  public short getThick(){
    return thick;
  }

  // =============================================================================
  // mouse exited
  public void mouseExited(MouseEvent e){
    mouseover=null;
    repaint();
  };

  // =============================================================================
  // Network status indicator
  public void setNetworkStatus(int i){
    network_status=i;
    repaint();
    // 0 - OFF
    // 1 - ON
    // 2 - TRANSFER
  };

  synchronized public void saveThumbnail(int nr){
    small[nr]=createImage(boardlink.DIM_X,boardlink.DIM_Y);
    Graphics gg=small[nr].getGraphics();
    parentMain.bgImage.flush();
    gg.drawImage(parentMain.bgImage,0,0,boardlink.DIM_X,boardlink.DIM_Y,parentMain);
  }

  // =============================================================================
  // mouse moved
  public void mouseMoved(MouseEvent e){
    int x=e.getX();
    int y=e.getY();
    Button oldmouseover=mouseover;

    mouseover=null;
    if (btn_line.hit(x,y)) mouseover=btn_line;
    if (btn_freehand.hit(x,y)) mouseover=btn_freehand;
    if (btn_box.hit(x,y)) mouseover=btn_box;
    if (btn_fillbox.hit(x,y)) mouseover=btn_fillbox;
    if (btn_circle.hit(x,y)) mouseover=btn_circle;
    if (btn_fillcircle.hit(x,y)) mouseover=btn_fillcircle;
    if (btn_texttool.hit(x,y)) mouseover=btn_texttool;
    if (thick_1.hit(x,y))mouseover=thick_1;
    if (thick_2.hit(x,y))mouseover=thick_2;
    if (thick_3.hit(x,y))mouseover=thick_3;
    if (thick_4.hit(x,y))mouseover=thick_4;
    if (thick_5.hit(x,y))mouseover=thick_5;
    if (thick_6.hit(x,y))mouseover=thick_6;
    if (th_select1.hit(x,y))mouseover=th_select1;
    if (th_select2.hit(x,y))mouseover=th_select2;
    if (th_select3.hit(x,y))mouseover=th_select3;
    if (th_copy.hit(x,y))mouseover=th_copy;
    if (th_show.hit(x,y) && small[thumbnail]!=null)mouseover=th_show;
    if (th_delete.hit(x,y) && small[thumbnail]!=null)mouseover=th_delete;
    if (reset.hit(x,y))mouseover=reset;
    if (copyright.hit(x,y))mouseover=copyright;

    if (mouseover!=null && !mouseover.equals(oldmouseover) ){ // new button focus
      if (oldmouseover==null)oldmouseover=mouseover;
      repaint(
        Math.min(mouseover.x1,oldmouseover.x1),
        Math.min(mouseover.y1,oldmouseover.y1),
        Math.abs(Math.max(mouseover.x2,oldmouseover.x2)-Math.min(mouseover.x1,oldmouseover.x1)),
        Math.abs(Math.max(mouseover.y2,oldmouseover.y2)-Math.min(mouseover.y1,oldmouseover.y1))
      );
    }
    else if(oldmouseover!=null && mouseover==null){ // focus lost
      repaint(oldmouseover.x1,oldmouseover.y1,oldmouseover.x2,oldmouseover.y2);
    }
  };

  public void mouseEntered(MouseEvent e){};
  public void mousePressed(MouseEvent e){};
  public void mouseReleased(MouseEvent e){};
  public void mouseDragged(MouseEvent e){};

  // =============================================================================
  // mouse clicked
  public void mouseClicked(MouseEvent e){
    if (e.getModifiers()==e.BUTTON1_MASK){
      int x=e.getX();
      int y=e.getY();

      if (btn_line.hit(x,y)) mode=new Line();
      if (btn_freehand.hit(x,y)) mode=new Freehand();
      if (btn_box.hit(x,y)) mode=new Box();
      if (btn_fillbox.hit(x,y)) mode=new FillBox();
      if (btn_circle.hit(x,y)) mode=new Circle();
      if (btn_fillcircle.hit(x,y)) mode=new FillCircle();
      if (btn_texttool.hit(x,y)) mode=new LetterBox();
      if (thick_1.hit(x,y)) thick=1;
      if (thick_2.hit(x,y)) thick=2;
      if (thick_3.hit(x,y)) thick=3;
      if (thick_4.hit(x,y)) thick=4;
      if (thick_5.hit(x,y)) thick=5;
      if (thick_6.hit(x,y)) thick=6;
      if (th_select1.hit(x,y)) thumbnail=0;
      if (th_select2.hit(x,y)) thumbnail=1;
      if (th_select3.hit(x,y)) thumbnail=2;

      if (th_copy.hit(x,y)){
        saveThumbnail(thumbnail);
      }

      if (th_delete.hit(x,y)){
        small[thumbnail]=null;
      }

      if (th_show.hit(x,y)){
        int X=boardlink.DIM_X;
        int Y=boardlink.DIM_Y;
        Container parent = this.getParent();
        while(!(parent instanceof Frame))
          parent = parent.getParent();
        if (small[thumbnail]!=null){
          ShowFrame sf=new ShowFrame(
            (Frame)parent, true,
            X,Y,small[thumbnail], Lang.get("Saved image number ")+(thumbnail+1), commlink
            ,"1","1");
          sf.setID(ID);
          sf.show();
        }
      }

      if (reset.hit(x,y)){
        boardlink.reset_board();
      }

      if (copyright.hit(x,y)){
        Container parent = this.getParent();
        while(!(parent instanceof Frame))
          parent = parent.getParent();
        Copyright copy=new Copyright((Frame)parent, false,appletlink.getDocumentBase(), appletcontext,parentMain);
      }

      repaint();
    };
  }

  private boolean parseSkindef(URL url, String skindef){
    URL wsk;
    try{
      wsk = new URL(url.getProtocol()+"://"+url.getHost()+":"+url.getPort()+url.getFile()+"skin/"+skindef);
    }catch(MalformedURLException e){
      System.out.println(Lang.get("Wrong datafile URL ")+e);
      return (false);
    }
    System.out.println(Lang.get("Data file: ")+wsk);

    URLConnection pol;
    try{
      pol = wsk.openConnection();
      BufferedReader d=new BufferedReader(new InputStreamReader(pol.getInputStream()));
      String linia ;
      while ((linia=d.readLine())!=null){
        linia=linia.trim();
        if (linia.startsWith("#")) continue;
        if (linia.length()==0) continue;

        if (linia.startsWith("menu_normal")) str_normal=substring(linia,1);
        else if (linia.startsWith("menu_light")) str_light=substring(linia,1);
        else if (linia.startsWith("menu_bold")) str_bold=substring(linia,1);
        else if (linia.startsWith("counter")){
          counter_x=Integer.valueOf(substring(linia,1)).intValue();
          counter_y=Integer.valueOf(substring(linia,2)).intValue();
        }else{
          String v=substring(linia,0);
          int x1=Integer.valueOf(substring(linia,1)).intValue();
          int y1=Integer.valueOf(substring(linia,2)).intValue();
          int x2=Integer.valueOf(substring(linia,3)).intValue();
          int y2=Integer.valueOf(substring(linia,4)).intValue();

          if (v.equals("freehand")) btn_freehand=new Button(x1,y1,x2,y2);
          if (v.equals("line")) btn_line=new Button(x1,y1,x2,y2);
          if (v.equals("box")) btn_box=new Button(x1,y1,x2,y2);
          if (v.equals("fillbox")) btn_fillbox=new Button(x1,y1,x2,y2);
          if (v.equals("circle")) btn_circle=new Button(x1,y1,x2,y2);
          if (v.equals("fillcircle")) btn_fillcircle=new Button(x1,y1,x2,y2);
          if (v.equals("texttool")) btn_texttool=new Button(x1,y1,x2,y2);
          if (v.equals("thick1")) thick_1=new Button(x1,y1,x2,y2);
          if (v.equals("thick2")) thick_2=new Button(x1,y1,x2,y2);
          if (v.equals("thick3")) thick_3=new Button(x1,y1,x2,y2);
          if (v.equals("thick4")) thick_4=new Button(x1,y1,x2,y2);
          if (v.equals("thick5")) thick_5=new Button(x1,y1,x2,y2);
          if (v.equals("thick6")) thick_6=new Button(x1,y1,x2,y2);
          if (v.equals("select1")) th_select1=new Button(x1,y1,x2,y2);
          if (v.equals("select2")) th_select2=new Button(x1,y1,x2,y2);
          if (v.equals("select3")) th_select3=new Button(x1,y1,x2,y2);
          if (v.equals("copy")) th_copy=new Button(x1,y1,x2,y2);
          if (v.equals("show")) th_show=new Button(x1,y1,x2,y2);
          if (v.equals("delete")) th_delete=new Button(x1,y1,x2,y2);
          if (v.equals("reset")) reset=new Button(x1,y1,x2,y2);
          if (v.equals("copyright")) copyright=new Button(x1,y1,x2,y2);
          if (v.equals("network")) network=new Button(x1,y1,x2,y2);
          if (v.equals("thumbnail")) thumbnail_coord=new Button(x1,y1,x2,y2);

        }
      }
      if (
        btn_freehand==null ||  btn_line==null ||  btn_box==null ||  btn_fillbox==null ||  btn_circle==null
        || btn_fillcircle==null || btn_texttool==null || thick_1==null || thick_2==null || thick_3==null
        || thick_4==null || thick_5==null || thick_6==null || th_select1==null || th_select2==null
        || th_select3==null || th_copy==null || th_show==null || th_delete==null || reset==null
        || copyright==null || network==null || thumbnail_coord==null
      ){
        System.out.println(Lang.get("Some elements were not defined, reverting to default values"));
        System.out.println(Lang.get("Check drawboard and skindef versions"));
        return false;
      }
    }catch(IOException e){
      System.out.println(Lang.get("Skin datafile IO Exception ")+e);
      System.out.println(Lang.get("Reverting to default values"));
      return false;
    }

    return true;
  }

  static String substring(String str, int number){
    str=str.trim();
    str=removeTwoSpaces(str);
    int start=0;
    for (int i=0; i<number; i++){
      start=str.indexOf(" ",start+1);
      if (start<0) return null;
    }

    int end=0;
    end=str.indexOf(" ",start+1);
    if (end<=0) end=str.length();

    if (str.charAt(start)==' ') start++;
    // System.out.println(number+" "+start+" "+end+" |"+str.substring(start,end)+"|");
    return str.substring(start,end);
  }

  public static String removeTwoSpaces (String str){
    if (str == null) return(null);
    char[] tempArray = new char[str.length()];
    int j = 0;
    if (!(str.charAt(0) == ' ' && str.charAt(1)==' ')){
      tempArray[j] = str.charAt(0);
      j++;
    }
    for (int i = 1; i < str.length(); i++){
      if (!(str.charAt(i) == ' ' && str.charAt(i-1)==' ')){
        tempArray[j] = str.charAt(i);
        j++;
      }
    }
    return(new String(tempArray, 0, j));
  }

}

// =============================================================================
// =============================================================================
// Button class
class Button{
  int x1,y1,x2,y2;

  // =============================================================================
  // constructor
  Button(int xx1, int yy1, int xx2, int yy2){
    x1=xx1;x2=xx2;
    y1=yy1;y2=yy2;
  }

  // =============================================================================
  // is (x,y) point inside button?
  public boolean hit(int x, int y){
    if (x1<=x && x<=x2 && y1<=y && y<=y2) return true;
    return false;
  }
}

