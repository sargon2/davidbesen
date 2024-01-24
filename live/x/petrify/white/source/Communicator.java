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
import java.awt.*;
import java.awt.event.*;

// =============================================================================
// =============================================================================
public class Communicator extends Thread {

  private volatile Board table=null;  // link to blackboard
  volatile Menu menu=null;            // link to menu
  volatile Main parent=null;  // link to parent

  private volatile String host=null;  // name of host
  private volatile int port;          // port number
  private volatile Socket s=null;     // communication socket
  volatile ObjectOutputStream oos=null;   // oos
  private volatile ObjectInputStream ois=null;    // ois

  volatile boolean communicatorclosing=false;   // true if closing comm module
  Sender sender=null;                           // subthread


  // =============================================================================
  // Constructor
  public Communicator(Board b, URL u, Menu m, int p, Main par) {
    parent=par;
    table=b;
    menu=m;
    if (u.getHost().equals(""))
      host=null;
    else
      host=u.getHost();
    port=p;
  }


  // =============================================================================
  // Connect to remote host
  synchronized void connect(){
    oos=null;
    ois=null;
    if (parent.fullViewMode)
      menu.setNetworkStatus(0);
    while ((oos==null || ois==null) && !communicatorclosing){
      table.setCursorAndSentence(true, Lang.get("Connecting to server"));
      try{
        s=null;
        s=new Socket(host,port);
      }catch(IOException e){
        System.out.println(Lang.get("**Connect to server failed, pausing 5 seconds"));
        table.setCursorAndSentence(true, Lang.get("Connect to server failed, pausing 5 seconds"));
        try{Thread.sleep(5000);}catch(InterruptedException ee){System.out.println("**What?? I'm sleeping! "+ee);};
      }
      try{
        oos=new ObjectOutputStream(s.getOutputStream());
        ois=new ObjectInputStream(s.getInputStream());
      }catch(NullPointerException e){}
      catch(IOException e){
        System.out.println(Lang.get("**Socket streams get failed, maximum client number reached. Waiting 15 seconds ")+e);
        table.setCursorAndSentence(true, Lang.get("Maximum client number reached. Waiting 15 seconds"));
        try{Thread.sleep(15000);}catch(InterruptedException ee){System.out.println("**What?? I'm sleeping! "+ee);};
      }
    }
    if (parent.fullViewMode)
      menu.setNetworkStatus(1);

    // Got connection, rut sender thread
    sender=new Sender(this);
    Thread th=new Thread(sender);
    th.start();

    return;
  }

  // =============================================================================
  // Add object to outgoing queue
  void send(Object o){
    sender.addToQueue(o);
    return;
  }

  // =============================================================================
  // Receiveing subthread
  public void run(){
    if (host==null){
      System.out.println(Lang.get("**Applet loaded from local file, not remote server"));
      System.out.println(Lang.get("**Network communication disabled"));
      table.network_disabled=true;
      return;
    }

    System.out.println(Lang.get("**Trying to connect"));
    connect();
    if (!communicatorclosing)
      System.out.println(Lang.get("**Connection estabilished, data receiving started"));

    while (!communicatorclosing){
      try{
        if (parent.fullViewMode)
          menu.setNetworkStatus(1);
        Object a=ois.readObject();

        System.out.println(Lang.get("Received object size: ")+getSize(a));

        if ( (((Shape)a).getID())==parent.ID){
          if ( a instanceof Raport){
            class Confirm extends Dialog {
              Confirm(String text, Frame owner){
                super(owner, Lang.get("Raport"),true);
                addWindowListener(new WindowAdapter(){        // handle window close
                  public void windowClosing(WindowEvent e){
                    dispose();
                  }
                });
                Label l=new Label(text);
                add("Center",l);
                java.awt.Button OK=new java.awt.Button("OK");
                OK.addActionListener(new ActionListener(){
                  public void actionPerformed(ActionEvent e){
                    dispose();
                  };
                });
                add("South",OK);
                setResizable(true);
                pack();
              }
            }

            Container parent2 = parent.getParent();
            while(!(parent2 instanceof Frame))
              parent2 = parent2.getParent();

            System.out.println(Lang.get("Raport received") +a);
            Confirm c=new Confirm(((Raport)a).text,(Frame)parent2);
            c.pack();
            c.show();

          }
         continue;
        }

        if (a instanceof Archive){
          table.setCursorAndSentence(false, null); // First transfer OK

          // process information about image saving possibility
          if (((Archive)a).savingenabled==true) ShowFrame.cansave=true;
          if (((Archive)a).emailingenabled==true) ShowFrame.canemail=true;

          // If server image size is inadequate, disconnect and disable network communication
          if (((Archive)a).DIM_X!=table.DIM_X || ((Archive)a).DIM_Y!=table.DIM_Y){
            communicatorclosing=true;
            table.setCursorAndSentence(false, Lang.get("Wrong server image size, see doc for details"));
            System.out.println(Lang.get("Server image size invalid"));
            System.out.println(Lang.get("Local image size: ")+table.DIM_X+"x"+table.DIM_Y);
            System.out.println(Lang.get("Remote image size: ")+((Archive)a).DIM_X+"x"+((Archive)a).DIM_Y);
            continue;
          }
        }

        if (a instanceof Pinger)
          if (parent.fullViewMode)
            menu.setPeople(((Pinger)a).people);  // People counter

        table.draw(a,parent.matrix);

      }catch (IOException e){
        System.out.println(Lang.get("IO Exception in second thread: ")+e);
        if (!communicatorclosing) connect();
      }
      catch (ClassNotFoundException e){System.out.println(Lang.get("Received something unreadable: ")+e);}
    }
    System.out.println(Lang.get("Stopping communication module"));
    if (parent.fullViewMode)
      menu.setNetworkStatus(0);
    table.network_disabled=true;
    try{
      if (ois!=null) ois.close();
      if (oos!=null) oos.close();
      if (s!=null) s.close();
    }catch(SocketException e){System.out.println(Lang.get("**Socket error while stopping communicator ")+e);}
    catch(IOException e){System.out.println(Lang.get("**IO error while while stopping communicator ")+e);}
  }


  // =============================================================================
  // Help method, for debugging only; author: dimitri@dima.dhs.org, http://dima.dhs.org/
  // returns approximation of Object size
  public static int getSize(Object object) {
      int size = -1;
      try {
          ByteArrayOutputStream bo = new ByteArrayOutputStream();
          ObjectOutputStream oo = new ObjectOutputStream(bo);
          oo.writeObject(object);
          oo.close();
          size = bo.size();
      } catch(Exception e) {
          System.out.println("oops:" + e);
      }
      return size;
  }

  // =============================================================================
  // Developer help method, converts AWT.Color to integer
  public static int color2int(Color c){
    return c.getRGB();
  }
}


// =============================================================================
// =============================================================================
class Sender implements Runnable{
  Communicator com=null;  // link to communicator
  volatile Vector v;      // array of items to transmit

  // =============================================================================
  // Constructor
  Sender(Communicator c){
    com=c;
    v=new Vector(5);
  }

  // =============================================================================
  // Sending subthread
  public void run(){
    while (true){
      if (com.parent.fullViewMode)
        com.menu.setNetworkStatus(1);
      while(v.isEmpty()){
        try{Thread.sleep(100);}catch(InterruptedException e){System.out.println("**Hey, I'm sleeping! "+e);}
      }

      if (com.parent.fullViewMode)
        com.menu.setNetworkStatus(2);
      try{
        com.oos.writeObject(v.firstElement());
        com.oos.flush();
        v.removeElementAt(0);
      }catch(IOException e){
        System.out.println(Lang.get("Connection lost, some elements too :")+e);
        v.removeAllElements();
        com.connect();
      }
    }
  };

  // =============================================================================
  // Add to outgoing queue
  void addToQueue(Object o){
    v.addElement(o);
  }
}