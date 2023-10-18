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

import java.io.*;

import java.net.Socket;
import java.util.zip.Deflater;import java.util.zip.Inflater;import java.util.zip.DataFormatException;
import java.lang.Math;import java.io.Serializable;

// =============================================================================
// ===========================================================================
// Current image, maintained and distributed to new clients by server class
public class Archive implements Shape, Serializable{
  byte parcel[];    // bitmap (zipped after creation, unzipped after transsmision
  boolean savingenabled;  // can client request image-save?  (info for applet)
  boolean emailingenabled;  // can client send image-email?  (info for applet)
  short DIM_X;      // server image x dim
  short DIM_Y;      // server image y dim
  public int ID=0;  // sender's ID  (0 = server message)

  public String filename; // info for server, filename of screenshot
                          // also e-mail address of receipment

  static String SUCCESS="Your image was successfully sent to ";

  boolean email=false;     // true=e-mail, false=save
  public String from;
  public String smtp;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // init e-mailing
  void emailSet(String f, String s){
    from=f;
    smtp=s;
    email=true;
  }

  // Create current image package (server -> applet)
  Archive (int[] what, short x, short y, boolean canSave, boolean canEmail){
    this(what,x,y);
    savingenabled=canSave;
    emailingenabled=canEmail;
  }

  // Create current image package
  Archive (int[] what, short x, short y){
    DIM_X=x;
    DIM_Y=y;
    int offset=what.length;
    parcel=new byte[offset*3];
    for (int i=0; i<what.length; i++){
      parcel[i]=(byte)((what[i]>>16)&0xff);           // R
      parcel[i+offset]=(byte)((what[i]>>8)&0xff);     // G
      parcel[i+offset+offset]=(byte)(what[i]&0xff);   // B
    }

    // pack data
    Deflater def=new Deflater();
    def.setInput(parcel,0,parcel.length);  // Deflater.setInput(byte[]) has bug!
    def.finish();
    int bytesDone = def.deflate(parcel);

    // and prepare to transfer
    byte temp[]=new byte[bytesDone];
    for (int i=0; i<bytesDone; i++)
      temp[i]=parcel[i];
    parcel=new byte[bytesDone];
    for (int i=0; i<bytesDone; i++)
      parcel[i]=temp[i];
    temp=null;
    def.end();
    System.gc();
  }

  // Unpack zipped array
  void unpack(){
    byte temp[]=new byte[DIM_X*DIM_Y*3];

    // unpack data...
    Inflater inf=new Inflater();
    inf.setInput(parcel);
    inf.finished();
    try{
      inf.inflate(temp);
    }catch(DataFormatException e){
      if (Server.runQuiet==false)
        System.out.println(Lang.get("Wrong archive data received ")+e);
      return;
    };
    inf.end();
    parcel=new byte[temp.length];
    System.arraycopy(temp,0,parcel,0,temp.length);
  }

  // Draw current image on client's board, when it receives parcel
  public void draw(int[] matrix, short width){

    unpack();

    // copy unpacked data to the buffre
    int offset=matrix.length;
    for (int i=0; i<offset; i++)
      matrix[i]=
        parcel[i+offset+offset]&0xff |
        (((parcel[i+offset]&0xff)<<8)) |
        (((parcel[i]&0xff)<<16)) |
        (((0xff)<<24));
  };

  // Save received image to file in desired directory
  boolean save(String path){
    boolean success=true;
    //System.out.println(path+Server.hour('_'));
    unpack();
    int unzipped[] = new int[DIM_X*DIM_Y];
    int offset=DIM_X*DIM_Y;
    for (int i=0; i<offset; i++)
      unzipped[i]=
        parcel[i+offset+offset]&0xff |
        (((parcel[i+offset]&0xff)<<8)) |
        (((parcel[i]&0xff)<<16)) |
        (((0xff)<<24));

    PngEncoder encoder=new PngEncoder(unzipped,(int)DIM_X,false,PngEncoder.FILTER_NONE,9);
    byte[] plik=encoder.pngEncode(true);

    // if image exists, add trailing underscore
    filename=path+Server.hour('_');
    while (new File(filename+".png").exists())
      filename+="_";

    // save image to file
    File f = new File(filename+".png");
    try{
      RandomAccessFile p = new RandomAccessFile(f,"rw");
      try{
        for (int i=0; i<plik.length;i++)
          p.writeByte(plik[i]);
      }catch(IOException ee){
        success=false;
        if (Server.runQuiet==false)
          System.out.println(Lang.get(" error [1] while saving file ")+filename+".png: "+ee);
      }finally{
        p.close();
      }
    }catch(FileNotFoundException e){
      success=false;
      if (Server.runQuiet==false)
        System.out.println(Lang.get(" error [2] while saving file ")+filename+".png: "+e);
    }catch(SecurityException e){
      success=false;
      if (Server.runQuiet==false)
        System.out.println(Lang.get(" error [3] while saving file ")+filename+".png: "+e);
    }catch(IOException e){
      success=false;
      if (Server.runQuiet==false)
        System.out.println(Lang.get(" error [4] while saving file ")+filename+".png: "+e);
    }finally{
      unzipped=null;
      encoder=null;
      plik=null;
      System.gc();
    }
    return success;
  };

  // send image via e-mail
  String send(){
    String to=filename;
    Socket s;
    BufferedReader is;
    PrintStream os;
    boolean success=true;
    String answer="";

    unpack();
    int unzipped[] = new int[DIM_X*DIM_Y];
    int offset=DIM_X*DIM_Y;
    for (int i=0; i<offset; i++)
      unzipped[i]=
        parcel[i+offset+offset]&0xff |
        (((parcel[i+offset]&0xff)<<8)) |
        (((parcel[i]&0xff)<<16)) |
        (((0xff)<<24));
    PngEncoder encoder=new PngEncoder(unzipped,(int)DIM_X,false,PngEncoder.FILTER_NONE,9);
    byte[] plik=encoder.pngEncode(true);

    char[] alphabet ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    char[] base64 = new char[((plik.length + 2) / 3) * 4];

    // do Base64 encoding
    for (int i=0, index=0; i<plik.length; i+=3, index+=4) {
        boolean quad = false;
        boolean trip = false;

        int val = (0xFF & (int) plik[i]);
        val <<= 8;
        if ((i+1) < plik.length) {
            val |= (0xFF & (int) plik[i+1]);
            trip = true;
        }
        val <<= 8;
        if ((i+2) < plik.length) {
            val |= (0xFF & (int) plik[i+2]);
            quad = true;
        }
        base64[index+3] = alphabet[(quad? (val & 0x3F): 64)];
        val >>= 6;
        base64[index+2] = alphabet[(trip? (val & 0x3F): 64)];
        val >>= 6;
        base64[index+1] = alphabet[val & 0x3F];
        val >>= 6;
        base64[index+0] = alphabet[val & 0x3F];
    };
    plik=null;

    try{
      s=new Socket(smtp,25);
      is = new BufferedReader(new InputStreamReader(s.getInputStream()));
      os = new PrintStream(s.getOutputStream());
      answer=is.readLine();
      if(!answer.startsWith("220")){
        success=false;
        throw (new Exception());
      }
      os.println("helo tomek.pl");
      answer=is.readLine();
      if(!answer.startsWith("250")){
        success=false;
        throw (new Exception());
      }
      os.println("mail from: <"+from+">");
      answer=is.readLine();
      if(!answer.startsWith("250")){
        success=false;
        throw (new Exception());
      }
      os.println("rcpt to: <"+to+">");
      answer=is.readLine();
      if(!answer.startsWith("250")){
        success=false;
        throw (new Exception());
      }
      os.println("data");
      answer=is.readLine();
      if(!answer.startsWith("354")){
        success=false;
        throw (new Exception());
      }
      os.println("From: <"+from+">");
      os.println("To: <"+to+">");
      os.println(Lang.get("Subject: Your requested Drawboard image"));
      os.println("Mime-Version: 1.0");
      os.println("Content-Type: multipart/mixed; boundary=\"NextPart_0\"");
      os.println("Content-Transfer-Encoding: 7bit");
      os.println();
      os.println(Lang.get("Image is included in this e-mail as multipart base64 image/png"));
      os.println();
      os.println("--NextPart_0");
      os.println("Content-Type: text/plain; charset=ISO-8859-1");
      os.println("Content-Transfer-Encoding: 7bit");
      os.println();
      os.println(Lang.get("This is graphics file sent to you from Drawboard applet"));
      os.println();
      os.println("--NextPart_0");
      os.println("Content-Type: image/png; name=\"graphics.png\"");
      os.println("Content-Transfer-Encoding: base64");
      os.println("Content-Description: Your image");
      os.println();
      for(int i=0; i<base64.length; i++)
        os.print(base64[i]);
      os.println();
      os.println("--NextPart_0--");
      os.println(".");
      answer=is.readLine();
      if(!answer.startsWith("250")){
        success=false;
        throw (new Exception());
      }
      os.println("quit");
      answer=is.readLine();
      if(!answer.startsWith("221")){
        success=false;
        throw (new Exception());
      }

      if (os!=null)os.close();
      if (is!=null)is.close();
      if (s!=null)s.close();
    }catch(Exception e){
      return (Lang.get("Your image was NOT sent to ")+to+Lang.get(", error report: ")+e+" "+answer);
    }
    return (SUCCESS+to);
  }

  // Not used in this class
  public void setx2y2(short x2, short y2){};
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){}
}


// =============================================================================
// ===========================================================================
// A single "drawing" interface
interface Shape {
  public void setID(int i);
  public int getID();
  public void draw(int[] matrix, short width);  // draw themselves
  public void setx2y2(short x2, short y2);      // modify current object
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2);
}

// =============================================================================
// ===========================================================================
// Pinger pseudoshape, contains only people counter information
class Pinger implements Shape, Serializable{
  short people;
  Pinger(short i){people=i;}
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){}
  public void setx2y2(short xx2, short yy2){}
  public void draw(int[] matrix, short width){}
  public int ID=0;

  // Get unique ID
  public int getID(){return ID;}

  // Set unique ID
  public void setID(int i){ID=i;}
}

// =============================================================================
// ===========================================================================
// Reset screen pseudoshape
class Reset implements Shape, Serializable{
  int RGB;
  public int ID=0;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  public Reset(int cc){
    RGB=cc;
  }

  // =============================================================================
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){}

  // =============================================================================
  public void setx2y2(short xx2, short yy2){}

  // =============================================================================
  public void draw(int[] matrix, short width){
    for (int i=0; i<matrix.length; i++)
      matrix[i]=RGB;
  }
}


// ===========================================================================
// =============================================================================
// Single line shape
class Line implements Shape, Serializable{
  short x1, y1, x2, y2;
  int RGB;
  short thick;
  public int ID=0;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  Line(){};

  // =============================================================================
  Line(short X1, short Y1, int c, short t){
    x1=X1;
    y1=Y1;
    RGB=c;
    thick=t;
  }

  // =============================================================================
  // Light one pixel
  static void pixel(int[] matrix, short width, int x, int y, int color){
    if (x<width && x>=0 && y>=0 && y<matrix.length/width)
      matrix[y*width+x]=color;
  }

  // =============================================================================
  // Light screen "dot", diameter-thick circle
  static final void dot(int[] matrix, short width, short x, short y, short thick, int color){
    int height=matrix.length/width;
    switch (thick){

      case 1:
        pixel(matrix,width,x,y,color);              //   *  - shape
        break;

      case 2:
        pixel(matrix,width,x,y,color);              //   X*  - shape
        pixel(matrix,width,x+1,y,color);
        pixel(matrix,width,x,y+1,color);
        pixel(matrix,width,x+1,y+1,color);
        break;

      case 3:
        pixel(matrix,width,x,y,color);
        pixel(matrix,width,x+1,y,color);      //    *
        pixel(matrix,width,x,y+1,color);      //   *X*
        pixel(matrix,width,x-1,y,color);      //    *
        pixel(matrix,width,x,y-1,color);
        break;

      case 4:
        pixel(matrix,width,x,y,color);
        pixel(matrix,width,x,y-1,color);    //  **
        pixel(matrix,width,x+1,y-1,color);  // *X**
        pixel(matrix,width,x-1,y,color);    // ****
        pixel(matrix,width,x+1,y,color);    //  **
        pixel(matrix,width,x+2,y,color);
        pixel(matrix,width,x-1,y+1,color);
        pixel(matrix,width,x,y+1,color);
        pixel(matrix,width,x+1,y+1,color);
        pixel(matrix,width,x+2,y+1,color);
        pixel(matrix,width,x,y+2,color);
        pixel(matrix,width,x+1,y+2,color);
        break;

      case 5:
        pixel(matrix,width,x,y,color);
        pixel(matrix,width,x,y-2,color);
        pixel(matrix,width,x-1,y-1,color);    //    *
        pixel(matrix,width,x,y-1,color);      //   ***
        pixel(matrix,width,x+1,y-1,color);    //  **X**
        pixel(matrix,width,x-2,y,color);      //   ***
        pixel(matrix,width,x-1,y,color);      //    *
        pixel(matrix,width,x,y,color);
        pixel(matrix,width,x+1,y,color);
        pixel(matrix,width,x+2,y,color);
        pixel(matrix,width,x-1,y+1,color);
        pixel(matrix,width,x,y+1,color);
        pixel(matrix,width,x+1,y+1,color);
        pixel(matrix,width,x,y+2,color);
        break;

      case 6:
        pixel(matrix,width,x,y,color);
        pixel(matrix,width,x,y-2,color);
        pixel(matrix,width,x+1,y-2,color);
        pixel(matrix,width,x-1,y-1,color);    //    **
        pixel(matrix,width,x,y-1,color);      //   ****
        pixel(matrix,width,x+1,y-1,color);    //  **X***
        pixel(matrix,width,x+2,y-1,color);    //  ******
        pixel(matrix,width,x-2,y,color);      //   ****
        pixel(matrix,width,x-1,y,color);      //    **
        pixel(matrix,width,x+1,y,color);
        pixel(matrix,width,x+2,y,color);
        pixel(matrix,width,x+3,y,color);
        pixel(matrix,width,x-2,y+1,color);
        pixel(matrix,width,x-1,y+1,color);
        pixel(matrix,width,x,y+1,color);
        pixel(matrix,width,x+1,y+1,color);
        pixel(matrix,width,x+2,y+1,color);
        pixel(matrix,width,x+3,y+1,color);
        pixel(matrix,width,x-1,y+2,color);
        pixel(matrix,width,x,y+2,color);
        pixel(matrix,width,x+1,y+2,color);
        pixel(matrix,width,x+2,y+2,color);
        pixel(matrix,width,x,y+3,color);
        pixel(matrix,width,x+1,y+3,color);
    }
  }

  // =============================================================================
  // Used also by another shapes
  public final static void drawthickline(int[] matrix, short width,int RGB,
        short x0, short y0, short x1, short y1, short thick){

    int color=RGB;

    short x = x0, y = y0;
    int dx = x1-x0, dy = y1-y0;   // direction of line

    int sx = (dx > 0 ? 1 : (dx < 0 ? -1 : 0));    // increment or decrement depending on direction of line
    int sy = (dy > 0 ? 1 : (dy < 0 ? -1 : 0));

    if ( dx < 0 ) dx = -dx;    // decision parameters for voxel selection
    if ( dy < 0 ) dy = -dy;
    int ax = 2*dx, ay = 2*dy;
    int decx, decy;

    int max = dx, var = 0;    // determine largest direction component, single-step related variable
    if ( dy > max ) { var = 1; }

    switch ( var ){    // traverse Bresenham line
    case 0:  // single-step in x-direction
      for (decy=ay-dx; /**/; x += sx, decy += ay){
        dot(matrix,width,x,y,thick, color);
          if ( x == x1 ) break;            // take Bresenham step
          if ( decy >= 0 ) { decy -= ax; y += sy; }
        }
        break;
    case 1:  // single-step in y-direction
        for (decx=ax-dy; /**/; y += sy, decx += ax){
        dot(matrix,width,x,y,thick, color);
            if ( y == y1 ) break;            // take Bresenham step
            if ( decx >= 0 ) { decx -= ay; x += sx; }
        }
        break;
    }
  }

  // =============================================================================
  public void draw(int[] matrix, short width){
    drawthickline( matrix,width,RGB,x1,y1,x2, y2, thick);
  }

  // =============================================================================
  public void setx2y2(short xx2, short yy2){
    x2=xx2;y2=yy2;
  };

  // =============================================================================
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){
    x1=xx1;
    y1=yy1;
    RGB=cc;
    thick=tt;
    x2=xx2;y2=yy2;
  };

}

// =============================================================================
// ===========================================================================
// Circle (ellipse) shape
class Circle implements Shape, Serializable{
  short x1, y1, x2, y2;
  int RGB;
  short thick;
  public int ID=0;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  Circle(){};

  // =============================================================================
  Circle(short X, short Y, int c, short t){
    x1=X;
    y1=Y;
    RGB=c;
    thick=t;
  }

  // =============================================================================
  public static void elipsePoints(int[] matrix, final short width, final short xc, final short yc, final short x, final short y, final int RGB, final short thick){
    Line.dot(matrix,width,(short)(xc+x),(short)(yc+y),thick,RGB);
    Line.dot(matrix,width,(short)(xc+x),(short)(yc-y),thick,RGB);
    Line.dot(matrix,width,(short)(xc-x),(short)(yc+y),thick,RGB);
    Line.dot(matrix,width,(short)(xc-x),(short)(yc-y),thick,RGB);
   }


  public static void ellipseDraw(int[] matrix, final short width, final short xc, final short yc, final short a, final short b, final int RGB, final short thick){
    int a2 = a*a;
    int b2 = b*b;

    int x, y, dec;
    for (x = 0, y = b, dec = 2*b2+a2*(1-2*b); b2*x <= a2*y; x++){
      elipsePoints( matrix, width,xc,yc,(short)x,(short)y,RGB,thick);
      if ( dec >= 0 )
          dec += 4*a2*(1-(y--));
      dec += b2*(4*x+6);
    }

    for (x = a, y = 0, dec = 2*a2+b2*(1-2*a); a2*y <= b2*x; y++){
      elipsePoints( matrix, width,xc,yc,(short)x,(short)y,RGB,thick);
      if ( dec >= 0 )
          dec += 4*b2*(1-(x--));
      dec += a2*(4*y+6);
    }

  }

  // =============================================================================
  public void draw(int[] matrix, short width){
    short xx=(short)Math.abs(x1-x2);
    short yy=(short)Math.abs(y1-y2);
    if (xx==00 || yy==00) return;
    short a=0, b=0;
    if (xx<yy){
      a=(short)Math.min(xx,yy);
      b=(short)Math.max(xx,yy);
    }else{
      b=(short)Math.min(xx,yy);
      a=(short)Math.max(xx,yy);
    }
    ellipseDraw(matrix,width,x1,y1,a,b,RGB, thick);
  }


  // =============================================================================
  public void setx2y2(short xx2, short yy2){
    x2=xx2;y2=yy2;
  };

  // =============================================================================
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){
    x1=xx1;
    y1=yy1;
    RGB=cc;
    thick=tt;
    x2=xx2;y2=yy2;
  };

}


// =============================================================================
// ===========================================================================
// Filled circle (ellipse) shape
class FillCircle extends Circle implements Shape, Serializable{
  public int ID=0;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  FillCircle(){};

  // =============================================================================
  FillCircle(short X, short Y, int c, short t){
    super(X, Y, c, t);
  }

  public static void fillElipsePoints(int[] matrix, int xc, int yc, int x, int y, int RGB, int width){
    int ysize=matrix.length/width;
    for (int i=Math.max(0,xc-x); i<Math.min(width,xc+x); i++){
      if (yc-y>=0)  matrix[(yc-y)*width+i]=RGB;
    }
    for (int i=Math.max(0,xc-x); i<Math.min(width,xc+x); i++){
      if (yc+y<ysize) matrix[(yc+y)*width+i]=RGB;
    }
  }

  public static void drawFillEllipse(int[] matrix, int x1, int y1, int x2, int y2, int RGB, int width){
    int xx=Math.abs(x1-x2);
    int yy=Math.abs(y1-y2);
    if (xx==00 || yy==00) return;
    int a=0, b=0;
    if (xx<yy){
      a=Math.min(xx,yy);
      b=Math.max(xx,yy);
    }else{
      b=Math.min(xx,yy);
      a=Math.max(xx,yy);
    }

    int a2 = a*a;
    int b2 = b*b;

    int x, y, dec;
    for (x = 0, y = b, dec = 2*b2+a2*(1-2*b); b2*x <= a2*y; x++){
      fillElipsePoints( matrix,x1,y1,x,y,RGB,width);
      if ( dec >= 0 )
          dec += 4*a2*(1-(y--));
      dec += b2*(4*x+6);
    }
    for (x = a, y = 0, dec = 2*a2+b2*(1-2*a); a2*y <= b2*x; y++){
      fillElipsePoints( matrix,x1,y1,x,y,RGB,width);
        if ( dec >= 0 )
            dec += 4*b2*(1-(x--));
        dec += a2*(4*y+6);
    }
  }

  // =============================================================================
  public void draw(int[] matrix, short width){
    drawFillEllipse( matrix,x1,y1,x2,y2,RGB,width);
  }
}

// =============================================================================
// ===========================================================================
// Raport message
class Raport implements Shape, Serializable{
  public int ID=0;
  public String text;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  Raport(String t, int id){
    ID=id;
    text=t;
  };

  public void draw(int[] matrix, short width){}
  public void setx2y2(short xx2, short yy2){};
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){};
}



// =============================================================================
// ===========================================================================
// Box (rectangle) shape
class Box implements Shape, Serializable{
  short x1, y1, x2, y2;
  int RGB;
  short thick;
  public int ID=0;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  Box(){};

  // =============================================================================
  Box(short X, short Y, int c, short t){
    x1=X;
    y1=Y;
    RGB=c;
    thick=t;
  }

  // =============================================================================
  public void draw(int[] matrix, short width){
    Line.drawthickline(matrix,width,RGB,x1,y1,x2,y1, thick);
    Line.drawthickline(matrix,width,RGB,x2,y1,x2,y2, thick);
    Line.drawthickline(matrix,width,RGB,x2,y2,x1,y2, thick);
    Line.drawthickline(matrix,width,RGB,x1,y2,x1,y1, thick);
  }

  // =============================================================================
  public void setx2y2(short xx2, short yy2){
    x2=xx2;y2=yy2;
  };

  // =============================================================================
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){
    x1=xx1;
    y1=yy1;
    RGB=cc;
    thick=tt;
    x2=xx2;y2=yy2;
  };
}


// =============================================================================
// ===========================================================================
// Filled box (rectangle) shape
class FillBox extends Box implements Shape, Serializable{
  public int ID=0;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  FillBox(){};

  // =============================================================================
  FillBox(short X, short Y, int c, short t){
    super(X, Y, c, t);
  }

  // =============================================================================
  public void draw(int[] matrix, short width){
    for (int y=Math.min(super.y1, super.y2); y<Math.max(super.y1, super.y2);y++){
      int offset=y*width;
      for (int x=Math.min(super.x1,super.x2); x<Math.max(super.x1,super.x2);x++){
        matrix[offset+x]=RGB;
      }
    }
  }
}


// ===========================================================================
// =============================================================================
// Freehand shape
class Freehand implements Shape, Serializable{
  final short SIZE=255;
  short x[]=new short[SIZE];
  short y[]=new short[SIZE];
  short counter=2;
  int RGB;
  short thick;
  public int ID=0;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  Freehand(){};

  // =============================================================================
  Freehand(short X, short Y, int c, short t){
    x[0]=X;
    y[0]=Y;

    RGB=c;
    thick=t;
  }

  // =============================================================================
  public void draw(int[] matrix, short width){
    for (short a=0; a<counter-1; a++){
      Line.drawthickline(matrix,width,RGB,x[a],y[a],x[a+1],y[a+1], thick);
    }
  }

  // =============================================================================
  public void setx2y2(short xx2, short yy2){
  };

  // =============================================================================
  boolean nextxy(short xx, short yy){
    x[counter]=xx;
    y[counter]=yy;
    counter++;
    if (counter==SIZE) return true; else return false;
  };

  // =============================================================================
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){
    counter=2;
    x[0]=xx1;
    y[0]=yy1;

    RGB=cc;
    thick=tt;
    x[1]=xx2;
    y[1]=yy2;
  };

}



// =============================================================================
// ===========================================================================
// Letter (single character)
class Letter implements Shape, Serializable{
  short x1, y1, x2, y2;
  int RGB;
  short thick;
  public int ID=0;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  Letter(){};

  // =============================================================================
  Letter(short X, short Y, int c, short t){
    x1=X;
    y1=Y;
    RGB=c;
    thick=t;
  }

  // =============================================================================
  public void draw(int[] matrix, short width){
    char[][]letter=Alphabet.getLetter((char)thick);
    for (int y=letter.length-1; y>-1;y--){
      for (int x=0; x<letter[0].length;x++){
        if (letter[y][x]!=0)
          Line.pixel(matrix,width,x1+x,y1+y,RGB);
      }
    }
  }

  // =============================================================================
  public void setx2y2(short xx2, short yy2){
    x2=xx2;y2=yy2;
  };

  // =============================================================================
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){
    x1=xx1;
    y1=yy1;
    RGB=cc;
    thick=tt;
    x2=xx2;y2=yy2;
  };
}


// =============================================================================
// ===========================================================================
// LetterBox (texttool rectangle shape)
class LetterBox implements Shape, Serializable{
  short x1, y1, x2, y2;
  int RGB;
  short thick;
  public int ID=0;

  char[] prevchar;
  int[] prevcharcolor;
  int charcnt=0;
  int charcntcolor=0;

  // Set unique ID
  public void setID(int i){ID=i;}

  // Get unique ID
  public int getID(){return ID;}

  // =============================================================================
  LetterBox(){
      prevchar=new char[1024];
      prevcharcolor=new int[1024];
      charcnt=0;
      charcntcolor=0;
      x2=y2=-1;
  };

  // =============================================================================
  LetterBox(short X, short Y, int c, short t){
    x1=X;
    y1=Y;
    RGB=c;
    thick=t;
    x2=y2=-1;
  }

  public void addletter(char c, int color){
    if (charcnt<1024){
      prevchar[charcnt]=c;
      prevcharcolor[charcntcolor]=color;
      charcnt++;
      charcntcolor++;
    }
  }

  public int removeletter(){
    if (charcnt>0){
      return Alphabet.getWidth(prevchar[--charcnt])+2;
    }else
      return 0;
  }

  public int removelettercolor(){
    if (charcntcolor>0){
      return prevcharcolor[--charcntcolor];
    }else
      return 0;
  }

  // =============================================================================
  public void draw(int[] matrix, short width){
    int ox=0;
    int oy=3;
    Line.drawthickline(matrix,width,RGB,(short)(ox+x1-3),(short)(oy+y2-10),(short)(ox+x2+3),(short)(oy+y2-10), (short)1);
    Line.drawthickline(matrix,width,RGB,(short)(ox+x1-3),(short)(oy+y2+3),(short)(ox+x2+3),(short)(oy+y2+3), (short)1);
    Line.drawthickline(matrix,width,RGB,(short)(ox+x2+3),(short)(oy+y2-10),(short)(ox+x2+3),(short)(oy+y2+3), (short)1);
    Line.drawthickline(matrix,width,RGB,(short)(ox+x2-3),(short)(oy+y2+3),(short)(ox+x2-3),(short)(oy+y2-10), (short)1);
  }

  // =============================================================================
  public void setx2y2(short xx2, short yy2){
    x2=xx2;y2=yy2;
  };

  // =============================================================================
  public void init(short xx1,short yy1,int cc, short tt, short xx2, short yy2){
    charcnt=0;
    charcntcolor=0;
    x1=xx1;
    y1=yy1;
    RGB=cc;
    thick=tt;
    x2=xx1;y2=yy1;
  };
}

