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
import java.net.*;
import java.util.*;

public class Server implements Runnable{
  volatile ServerSocket serverSocket = null;      // ServerSocket
  volatile int PORT = 7904;                       // default port number
  volatile int PORTINFOPEOPLE = 0;                // port for checking users online
  volatile int PORTPNGPRINT = 0;                  // port for printing currnet image
  volatile int PORTBGIMAGELOAD = 0;               // port for specyfing new bgimage
  final short RIGHT_MARGIN=Main.RIGHT_MARGIN;     // Menu width
  final short BOTTOM_MARGIN=Main.BOTTOM_MARGIN;   // Colorpicker height
  volatile SingleClientHandle[] sch=null;         // table of client sockets
  int MAX=15;                                     // deault maximum
  volatile static int CURRENT=0;                  // temporary people counter
  int DELAY=30;                                   // delay between pings
  short DIM_X=600, DIM_Y=450;                     // image size
  int color=0xFFA6A8F8;                           // default background color
  volatile int matrix[];                          // "offline" image pixel array
  volatile int emptymatrix[];                     // empty image pixel array (also with loaded picture)

  static boolean cansave=true;                    // can server save pictures?
  String savepath=System.getProperty("user.dir")+File.separatorChar+"screenshot";
                                                  // default path for saving images
  static boolean canemail=false;                    // can server send image emails?
  String from="nobody@localhost";                 // "From" field in e-mail image
  String smtp="localhost";
  String lang="en";
  static boolean runQuiet=false;                  // run without any output
  String bgimagefile=null;

  Lang l;

  // ===========================================================================
  // "Starter"
  public static void main(String[] args) throws IOException {
    new Server(args);
  }

  // returns date and time for event logging...
  public static String hour(){
    return hour(':');
  }
  // ... and filename
  public static String hour(char separator){
  Calendar now = Calendar.getInstance();
  return
    (((now.get(Calendar.YEAR)<10)?"0":"")+now.get(Calendar.YEAR))
    +separator+
    (((now.get(Calendar.MONTH)<9)?"0":"")+(now.get(Calendar.MONTH)+1))
    +separator+
    (((now.get(Calendar.DAY_OF_MONTH)<10)?"0":"")+now.get(Calendar.DAY_OF_MONTH))
    +separator+
    (((now.get(Calendar.HOUR_OF_DAY)<10)?"0":"")+now.get(Calendar.HOUR_OF_DAY))
    +separator+
    (((now.get(Calendar.MINUTE)<10)?"0":"")+now.get(Calendar.MINUTE))
    +separator+
    (((now.get(Calendar.SECOND)<10)?"0":"")+now.get(Calendar.SECOND));
  }

  // ===========================================================================
  // Constructor (with options reading)
  Server(String[] args){

    for (int a=0; a<args.length; a++)
      if (args[a].equalsIgnoreCase("-quiet")) runQuiet=true;
    if (!runQuiet){
      System.out.println("");
      System.out.println("Drawboard, Copyright (C) 2001 Tomek \"TomasH\" Zielinski, tomash@fidonet.org.pl");
      System.out.println();
      System.out.println("Drawboard comes with ABSOLUTELY NO WARRANTY. This is free software, and you");
      System.out.println("are welcome to redistribute it under the terms of the GNU Lesser General");
      System.out.println("Public License. This software uses PngEncoder class by J. David Eisenberg.");
      System.out.println("Use -help switch to get help");
      System.out.println();
    }
    for (int a=0; a<args.length; a++)
      if (args[a].equalsIgnoreCase("-h") || args[a].equalsIgnoreCase("--help") || args[a].equalsIgnoreCase("-help")){
        System.out.println("Command line switches:");
        System.out.println(" -x SIZEX       - applet horizontal size [pixels]     (default: 600)");
        System.out.println(" -y SIZEY       - applet vertical size [pixels]       (default: 450)");
        System.out.println(" -p PORT        - server port number                  (default: 7904)");
        System.out.println(" -i PORT        - infopeople service socket number    (default: none)");
        System.out.println(" -r PORT        - imageprinter service socket number  (default: none)");
        System.out.println(" -l PORT        - imagebgload service socket number   (default: none)");
        System.out.println(" -m MAXCLIENTS  - maximum client number               (default: 15)");
        System.out.println(" -d DELAY       - delay between client ping [seconds] (default: 30)");
        System.out.println(" -c RRGGBB      - initial background color [hex]      (default: 0000FF)");
        System.out.println(" -s [on|off]    - allow image saving                  (default: on)");
        System.out.println(" -savepath DIR  - [full] image saving directory path  (default: ./screenshot )");
        System.out.println(" -e [on|off]    - allow image e-mail sending          (default: off)");
        System.out.println(" -from EMAIL    - e-mail \"From:\" field            (default: nobody@localhost)");
        System.out.println(" -smtp HOST     - e-mail SMTP host                    (default: localhost)");
        System.out.println(" -lang XX       - switch outgoing e-mail language     (default: en)");
        System.out.println(" -b FILE/URL    - background image                    (default: none)");
        System.out.println(" -quiet         - runs quiet (no text output)");
        System.out.println(" -help          - shows this help");
        System.out.println("");
        System.exit(0);
      }

    for (int a=0; a<args.length; a++){
      try{
          if (args[a].equalsIgnoreCase("-p")) PORT=java.lang.Integer.parseInt(args[a+1]);
          if (args[a].equalsIgnoreCase("-i")) PORTINFOPEOPLE=java.lang.Integer.parseInt(args[a+1]);
          if (args[a].equalsIgnoreCase("-r")) PORTPNGPRINT=java.lang.Integer.parseInt(args[a+1]);
          if (args[a].equalsIgnoreCase("-l")) PORTBGIMAGELOAD=java.lang.Integer.parseInt(args[a+1]);
          if (args[a].equalsIgnoreCase("-m")) MAX=java.lang.Integer.parseInt(args[a+1]);
          if (args[a].equalsIgnoreCase("-d")) DELAY=java.lang.Integer.parseInt(args[a+1]);
          if (args[a].equalsIgnoreCase("-x")) DIM_X=(short)java.lang.Integer.parseInt(args[a+1]);
          if (args[a].equalsIgnoreCase("-y")) DIM_Y=(short)java.lang.Integer.parseInt(args[a+1]);
          if (args[a].equalsIgnoreCase("-c")) color=
            255<<24+
            (java.lang.Integer.parseInt(args[a+1].substring(0,2),16))<<16+
            (java.lang.Integer.parseInt(args[a+1].substring(2,4),16))<<8+
            (java.lang.Integer.parseInt(args[a+1].substring(4,6),16));
          if (args[a].equalsIgnoreCase("-s"))
            if (args[a+1].equalsIgnoreCase("off"))
              cansave=false;
          if (args[a].equalsIgnoreCase("-e"))
            if (args[a+1].equalsIgnoreCase("on"))
              canemail=true;
          if (args[a].equalsIgnoreCase("-savepath"))
            savepath=args[a+1];
          if (args[a].equalsIgnoreCase("-from")) from=args[a+1];
          if (args[a].equalsIgnoreCase("-smtp")) smtp=args[a+1];
          if (args[a].equalsIgnoreCase("-lang")) lang=args[a+1];

          if (args[a].equalsIgnoreCase("-b")){
            bgimagefile=args[a+1];
          }

      }catch(NumberFormatException e){
        System.err.println("Parameter '"+args[a]+" "+args[a+1]+"' is invalid, exiting.");
        System.exit(-1);
      }
      catch(ArrayIndexOutOfBoundsException e){
        System.err.println("Something is missing after "+args[a]+" switch, run with --help option to get help.");
        System.exit(-1);
      }

    }
    CURRENT=0;
    l=new Lang(lang);

    if (!runQuiet){
      System.out.println();
      System.out.println("Runtime options:");
      System.out.println("  applet dimensions: "+DIM_X+"x"+DIM_Y);
      System.out.println("  server port: "+PORT);
      if (PORTINFOPEOPLE!=0)
        System.out.println("  infopeople service socket port: "+PORTINFOPEOPLE);
      else
        System.out.println("  infopeople service disabled");
      if (PORTPNGPRINT!=0)
        System.out.println("  imageprinter service socket port: "+PORTPNGPRINT);
      else
        System.out.println("  imageprinter service disabled");

      if (PORTBGIMAGELOAD!=0)
        System.out.println("  imagebgload service socket port: "+PORTBGIMAGELOAD);
      else
        System.out.println("  imageprinter service disabled");

      System.out.println("  maximum client number: "+MAX);
      System.out.println("  delay between ping: "+DELAY);
      System.out.println("  initial background color:"+
        " R"+((color>>16)&0xFF)+
        " G"+((color>>8)&0xFF)+
        " B"+((color)&0xFF)
      );
      if (cansave)
        System.out.println("  images will be saved at "+savepath+File.separatorChar);
      else
        System.out.println("  image saving is disabled");
      if (canemail){
        System.out.println("  e-mails will be send as "+from+" through "+smtp);
        System.out.println("  outgoing e-mail language: "+lang);
      }else{
        System.out.println("  image e-mailing is disabled");
      }
      if (bgimagefile!=null)
        System.out.println("  background image file: "+bgimagefile);
      else
        System.out.println("  background image disabled");

    }
    try{
      DIM_X-=RIGHT_MARGIN;
      DIM_Y-=BOTTOM_MARGIN;
      matrix=new int[DIM_X*DIM_Y];
      loadBgImage(bgimagefile);
      eraseBgImage(color);

      sch = new SingleClientHandle[MAX];
      serverSocket = new ServerSocket(PORT);
      Thread thread=new Thread(this);
      thread.start();
      while(true) addClient(serverSocket.accept());
    }catch(IOException e){if(!runQuiet)System.out.println(e);}
  }

  // ===========================================================================
  // Try to load background image or just clear the board
  void eraseBgImage(int c){
    if (emptymatrix==null){
      for (int i=0; i<matrix.length; i++)
        matrix[i]=c;
    }else{
      for (int i = 0; i < emptymatrix.length; i++) {
        matrix[i]=emptymatrix[i];
      }
    }
  }

  // ===========================================================================
  // Try to load background image or just clear the board
  String loadBgImage(String source){
    if (source==null){
      emptymatrix=null;
      return "ERR: no location specified";
    }

    source=source.trim();

    // loading from file
    if (!source.toLowerCase().startsWith("http://")){
      FileInputStream f=null;
      int newmatrix[]=null;

      try{
        f = new FileInputStream(source);

        if (f!=null){
          newmatrix=PngDecoder.decode(f).getData();
          if (newmatrix.length!=matrix.length){
            if (!runQuiet)
              System.out.println("Background image has wrong size: "+source);
            emptymatrix=null;
            return "ERR: wrong image size";
          }else{
            emptymatrix=new int[matrix.length];
            for (int i = 0; i < newmatrix.length; i++) {
              emptymatrix[i]=newmatrix[i];
            }
          }
        }
      }
      catch (NullPointerException e){
        if (!runQuiet){
          System.out.println("Error occured while loading image: "+source);
        }
        emptymatrix=null;
        return "ERR: error while loading image";
      }
      catch(FileNotFoundException e){
        if (!runQuiet){
          System.out.println("Unfortunatelly, we cannot find background image: "+source);
          emptymatrix=null;
          return "ERR: no image at specified location";
        }
      }
      catch (IOException e){
        if (!runQuiet){
          System.out.println("Invalid image: "+source);
        }
        emptymatrix=null;
        return "ERR: wrong image data";
      }
      return "OK: image loaded from file";

    // loading from network
    }else{
      URL wsk;
      URLConnection pol;
      InputStream w;

      try{
        wsk = new URL(source);
        pol = wsk.openConnection();
        w = pol.getInputStream();
        int newmatrix[]=null;

        newmatrix=PngDecoder.decode(w).getData();
        if (newmatrix.length!=matrix.length){
          if (!runQuiet)
            System.out.println("Background image has wrong size: "+source);
          emptymatrix=null;
          return "ERR: wrong image size";
        }else{
          emptymatrix=new int[matrix.length];
          for (int i = 0; i < newmatrix.length; i++) {
            emptymatrix[i]=newmatrix[i];
          }
        }
      }catch(MalformedURLException e){
        if (!runQuiet){
          System.out.println("MalformedURLException: "+source);
        }
        emptymatrix=null;
        return "ERR: MalformedURLException";
      }
      catch(IOException e){
        if (!runQuiet){
          System.out.println("IOException: "+source);
        }
        emptymatrix=null;
        return "ERR: IOException";
      }
      return "OK: image loaded from network";
    }

  }

  // ===========================================================================
  // Check current client number
  synchronized void setCurrent(){
    CURRENT=0;
    for (int c=0; c<MAX; c++)
      if (sch[c]!=null) CURRENT++;
//    if(!runQuiet)System.out.println(CURRENT+" users counted");
  }

  // ===========================================================================
  // Cyclic ping sending and connection checking
  synchronized void checkConnections(){
    setCurrent();
    for (int c=0; c<MAX; c++){
      if ((sch[c]!=null) && sch[c].finish==true){
        if (!runQuiet) System.out.println("** Number "+c+" hangs, disconnecting!");
        try{
          CURRENT--;
          sch[c].s.close();
          sch[c]=null;
        }catch(SocketException e){if (!runQuiet)System.out.println("**Socket error while closing socket "+e);}
        catch(IOException e){if (!runQuiet)System.out.println("**IO error while closing socket "+e);}
      } else if(sch[c]!=null){
        Pinger a=new Pinger((short)CURRENT);
//        if (!runQuiet)System.out.println("** Pinging "+c+" with value "+CURRENT);
        sch[c].addObject(a);
      }
    }
  }

  // ===========================================================================
  // Subthread calling checkConnections
  public void run(){
    if (PORTINFOPEOPLE!=0){
      InfoPeople i=new InfoPeople(this);
      Thread t=new Thread(i);
      t.start();
    }
    if (PORTPNGPRINT!=0){
      ImagePrinter i=new ImagePrinter(this);
      Thread t=new Thread(i);
      t.start();
    }
    if (PORTBGIMAGELOAD!=0){
      BGImageLoader i=new BGImageLoader(this);
      Thread t=new Thread(i);
      t.start();
    }
    while (true){
      try{Thread.sleep(DELAY*1000);}catch(InterruptedException e){if (!runQuiet)System.out.println("**Hey, I'm sleeping! "+e);}
      checkConnections();
    }
  }

  // ===========================================================================
  // Add (or refuse) new client
  void addClient(Socket socket){
    int c=0;
    try{while (
      sch[c]!=null) c++;}
    catch(ArrayIndexOutOfBoundsException e){
      try{
        socket.close();}
      catch(IOException ee){if (!runQuiet)System.out.println("Something wrong: "+ee);}
      if (!runQuiet)System.out.println("Refuse connection");
      return;
    }
    sch[c]=new SingleClientHandle(c,socket,this,runQuiet);
    if(!runQuiet) System.out.println(Server.hour()+"["+c+"] Connected from "+socket.getInetAddress().getHostAddress());

    setCurrent();
    sendToAll(new Pinger((short)CURRENT));
  }

  // ===========================================================================
  // Send object to all connected clients
  synchronized void sendToAll(Object o){
    if (o instanceof Reset){
      eraseBgImage(((Reset)o).RGB);
      if (emptymatrix!=null)
        o=new Archive(matrix,DIM_X, DIM_Y,cansave,canemail);
    }else{

      // if archive image received, transform data to report
      if (o instanceof Archive){
        int id=((Archive)o).getID();
        if (((Archive)o).save(savepath+File.separatorChar)){
          o=new Raport("Your image was successfully saved",id);
          if(!runQuiet) System.out.println(Server.hour()+" received image saved to a file");
        }else{
          o=new Raport("Your image was NOT saved",id);
          if(!runQuiet) System.out.println(Server.hour()+" received image NOT SAVED to a file!");
        }
      }else{
        ((Shape)o).draw(matrix,(short)DIM_X);
      }
    }

    // if (!runQuiet) System.out.println("Distributing to all clients object "+o);
    for (int c=0; c<MAX;c++){
      if (sch[c]!=null) sch[c].addObject(o);
    }
  }
}


// ===========================================================================
// ===========================================================================
class SingleClientHandle{
  volatile Vector v=null;     // Objects to send
  volatile int id;            // ID of this client
  volatile Socket s=null;     // socket
  volatile ObjectOutputStream oos=null;
  volatile ObjectInputStream ois=null;

  Server serverlink=null;
  boolean finish=false;
  boolean runQuiet;

  // ===========================================================================
  // Constructor
  SingleClientHandle(int i, Socket socket, Server server,   boolean r){
    v=new Vector();
    runQuiet=r;
    id=i;
    s=socket;
    serverlink=server;
//    System.out.println(id+" Initializing handle");
    try{
      oos=new ObjectOutputStream(s.getOutputStream());
      ois=new ObjectInputStream(s.getInputStream());
    }catch(IOException e){
      if (!runQuiet) System.out.println(Server.hour()+"["+id+"] Cannot get OO and OI streams, exiting. "+e);
      return;
    }
    SingleSender sender=new SingleSender(oos,id, this,runQuiet);
    sender.start();
    SingleListener listener=new SingleListener(ois,id, this,runQuiet);
    listener.start();
  }

  // ===========================================================================
  // Add to send queue
  synchronized void addObject(Object o){
//    if(!runQuiet) System.out.println(id+" Adding to queue: "+o);
    v.addElement(o);
  }
}

// ===========================================================================
// ===========================================================================
class SingleSender extends Thread{
  int id;
  boolean runQuiet;
  ObjectOutputStream oos=null;
  SingleClientHandle father=null;

  // ===========================================================================
  // Constructor
  SingleSender(ObjectOutputStream ooooss, int i, SingleClientHandle shc, boolean r){
    id=i;
    runQuiet=r;
    oos=ooooss;
    father=shc;
  }

  // ===========================================================================
  // Sending responsible subthread
  public void run(){
    try{
      if (!runQuiet) System.out.println(Server.hour()+"["+id+"] Sender: Sending archive packet");
      oos.writeObject(new Pinger((short)Server.CURRENT));
      oos.writeObject(new Archive(father.serverlink.matrix,father.serverlink.DIM_X, father.serverlink.DIM_Y,Server.cansave,Server.canemail));
      oos.flush();
    }catch(IOException e){
      if (!runQuiet) System.out.println(Server.hour()+"["+id+"] Sender: Archive data sending error! "+e);
      father.finish=true;
    }

    while (!father.finish){
      while(father.v.isEmpty() && !father.finish){
        try{Thread.sleep(500);}catch(InterruptedException e){if (!runQuiet) System.out.println(Server.hour()+"["+id+"] Sender: Hey, I'm sleeping! "+e);}
      }
      if (!father.v.isEmpty()){
        try{
          Object a=father.v.firstElement();
//          if (!runQuiet) System.out.println(id+" Sender: sending "+a);
          father.v.removeElementAt(0);
          oos.writeObject(a);
          oos.flush();
        }catch(IOException e){
          if (!runQuiet) System.out.println(Server.hour()+"["+id+"] Sender: Could not send anything "+e);
          father.finish=true;
        }
      }
    }
    try{
      oos.close();
    }catch(IOException e){
      if (!runQuiet) System.out.println("IOException while closing OOS, ignoring "+e );
    }
    if (!runQuiet) System.out.println(Server.hour()+"["+id+"] Sender: Subthread ending");
  }
}


// ===========================================================================
// ===========================================================================
class SingleListener extends Thread{
  int id;
  boolean runQuiet;
  ObjectInputStream ois=null;
  SingleClientHandle father=null;

  // ===========================================================================
  // Constructor
  SingleListener(ObjectInputStream ooiiss, int i, SingleClientHandle shc,  boolean r){
    ois=ooiiss;
    runQuiet=r;
    id=i;
    father=shc;
  }


  // ===========================================================================
  // Listen responsible thread
  public void run(){

    while (!father.finish){
      try{
        Object o=ois.readObject();
        if (!runQuiet) System.out.println(Server.hour()+"["+id+"] Received object "+o.getClass().getName()+" (size: "+Communicator.getSize(o)+" bytes)");

        // if archive image to email received, transform data to report

        if (o instanceof Archive){
          int id=((Archive)o).getID();
          if (((Archive)o).email){
            ((Archive)o).emailSet(father.serverlink.from,father.serverlink.smtp);
            String status=((Archive)o).send();
            if (status.startsWith(Archive.SUCCESS) && (!runQuiet))
              System.out.println(Server.hour()+" image was e-mailed to: "+((Archive)o).filename);
            else
              System.out.println(Server.hour()+" image was NOT e-mailed to: "+((Archive)o).filename+", user report: "+status);
            o=new Raport(status,id);
          }
        }

        father.serverlink.sendToAll(o);
      }catch(IOException e){if (!runQuiet) System.out.println(Server.hour()+
        "["+id+"] Listener: While reading got: "+e);
        father.finish=true;
      }
      catch(ClassNotFoundException e){
        if (!runQuiet) System.out.println(Server.hour()+"["+id+"] Listener: Strange class, trying to ignore: "+e);
      }
    }
    try{
      ois.close();
    }catch(IOException e){
      if (!runQuiet) System.out.println("IOException while closing OIS, ignoring "+e );
    }
    if (!runQuiet) System.out.println(Server.hour()+"["+id+"] Listener: ending");
  }
}



// ===========================================================================
// ===========================================================================
class InfoPeople implements Runnable{
  ServerSocket serverSocket;
  Socket socket;
  Server server;

  InfoPeople(Server s){
    server=s;
  }

  public void run(){
    try{
      serverSocket = new ServerSocket(server.PORTINFOPEOPLE);
    }catch(IOException e){
      if (!server.runQuiet) System.out.println("IOException while handling PortInforPeople, disabling feature: "+e );
      return;
    }
    while (true){
      try{
        socket=serverSocket.accept();
        DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
        dos.writeChars(String.valueOf(server.CURRENT));
        dos.close();
        socket.close();
      }catch(IOException e){
        if (!server.runQuiet) System.out.println("IOException while serving PortInforPeople, ignoring error: "+e );
      }
    }

  }
}




// ===========================================================================
// ===========================================================================
class ImagePrinter implements Runnable{
  ServerSocket serverSocket;
  Socket socket;
  Server server;

  ImagePrinter(Server s){
    server=s;
  }

  public void run(){
    try{
      serverSocket = new ServerSocket(server.PORTPNGPRINT);
    }catch(IOException e){
      if (!server.runQuiet) System.out.println("IOException while handling ImagePrinter, disabling feature: "+e );
      return;
    }

    while (true){
      try{
        socket=serverSocket.accept();
        DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PngEncoder encoder=new PngEncoder(server.matrix,(int)server.DIM_X,false,PngEncoder.FILTER_NONE,9);
        byte[] plik=encoder.pngEncode(true);
        String crlf = "\r\n";
        in.readLine();
        dos.writeBytes("HTTP/1.0 200 OK" + crlf);
        dos.writeBytes("Content-type: image/png" + crlf);
        dos.writeBytes("Connection: close" + crlf);
        dos.writeBytes("Content-length: " + plik.length + crlf);
        dos.writeBytes(crlf);
        dos.write(plik);
        plik=null;
        dos.flush();
        dos.close();
        in.close();
        socket.close();
      }catch(IOException e){
        if (!server.runQuiet) System.out.println("IOException while serving ImagePrinter, ignoring error: "+e );
      }
    }
  }
}

// ===========================================================================
// ===========================================================================
class BGImageLoader implements Runnable{
  ServerSocket serverSocket;
  Socket socket;
  Server server;

  BGImageLoader(Server s){
    server=s;
  }

  public void run(){
    try{
      serverSocket = new ServerSocket(server.PORTBGIMAGELOAD);
    }catch(IOException e){
      if (!server.runQuiet) System.out.println("IOException while handling ImageBgLoad, disabling feature: "+e );
      return;
    }

    while (true){
      try{
        socket=serverSocket.accept();
        DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String input=in.readLine();

        String crlf = "\r\n";
        dos.writeBytes(server.loadBgImage(input));
        dos.writeBytes(crlf);
        dos.flush();
        dos.close();
        in.close();
        socket.close();
      }catch(IOException e){
        if (!server.runQuiet) System.out.println("IOException while serving ImageBgLoad, ignoring error: "+e );
      }
    }
  }
}