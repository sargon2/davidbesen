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
import java.awt.image.*;
import java.util.*;
import java.io.*;

public class Main extends Applet implements Runnable{
  static short RIGHT_MARGIN=100;    // Menu width
  static short BOTTOM_MARGIN=50;    // Colorpicker height
  static short DIM_X, DIM_Y;             // Window dimensions
  private int PORT=7904;                  // Port number
  int ID=0;

  private volatile MemoryImageSource bgSource;  // Offline image generator
  volatile Image bgImage;                       // Offline image
  volatile int matrix[];                        // Offline image array
  volatile int matrix2[];                       // Temporary offline image array
  Thread repainter;                             // Image repaint thread
  boolean listenersInitialized=false;           // Initializes event listeners when images loaded

  Board board;            // Blackboard instance
  ColorPicker picker;     // Colorpicker instance
  Menu menu;              // Menu instance
  Communicator comm=null; // Communication module

  Lang l;

  int initBgColor=0xFFA6A8F8;             // initial background color (before server connection)
  int initPenColor=0xFF10139E;            // initial pen color
  int counterColor=Color.gray.getRGB();   // menu people counter color
  int menuBgColor=Color.black.getRGB();   // menu background color
  int menuEmptyThColor=Color.darkGray.getRGB(); // menu empty clipboard color
  int menuBgImageTransparentColor=0xffff00ff;   // menuimage transparent color

  String temp_menubgimage=null;
  int temp_menubgimageoffsetx=0;
  int temp_menubgimageoffsety=0;

  Alphabet a=new Alphabet();  // fake initialization, IE requires one instance of
                              // any class to use its static methods

  boolean fullViewMode=true;  // opposite to onlyview - the "view-only" client

// =============================================================================
// =============================================================================
  public void init(){

    // Generate unique ID (well, not uniqe, but count the probability :)))
    ID=(int)(Math.random()*1000000000)*1000000+(
      (int)(System.currentTimeMillis()&0xfffff));

    //Set Language
    if (getParameter("lang")!=null)
      l=new Lang(getParameter("lang"));
    else
      l=new Lang("en");

    //Read runtime parameters
    readParameters();

    // Determine applet size
    try{
      DIM_X = (short)(Integer.parseInt( getParameter( "width" )));
      DIM_Y = (short)(Integer.parseInt( getParameter( "height")));
      System.out.println(Lang.get("Applet window dimensions: width = ") + DIM_X + Lang.get(", height = ") + DIM_Y);
    }catch(Exception e){DIM_X=600; DIM_Y=450;} // usefult only when developing with JBuilder
    this.setSize(DIM_X,DIM_Y);


    if (!fullViewMode){
      RIGHT_MARGIN=0;
      BOTTOM_MARGIN=0;
    }

    // Initialize arrays
    matrix=new int[(DIM_X-RIGHT_MARGIN)*(DIM_Y-BOTTOM_MARGIN)];
    matrix2=new int[(DIM_X-RIGHT_MARGIN)*(DIM_Y-BOTTOM_MARGIN)];
    for (int i=0; i<matrix.length; i++){
      matrix[i]=initBgColor;
      matrix2[i]=initBgColor;
    }

    // Initialize image generator and start repainter
    bgSource=new MemoryImageSource((DIM_X-RIGHT_MARGIN),(DIM_Y-BOTTOM_MARGIN), ColorModel.getRGBdefault(), matrix2, 0, (DIM_X-RIGHT_MARGIN));
    bgImage = createImage(bgSource);
    repainter=new Thread(this);
    repainter.setPriority(Thread.MIN_PRIORITY);
    repainter.start();

    this.setLayout(null);

    // Menu instance
    if (fullViewMode){
      menu=new Menu(getCodeBase(), this,getParameter("skindef"), ID, getCodeBase(),
        temp_menubgimage,menuBgImageTransparentColor,temp_menubgimageoffsetx,temp_menubgimageoffsety);
      menu.setSize(RIGHT_MARGIN,DIM_Y);
      menu.setCursor(new Cursor(Cursor.HAND_CURSOR));
      menu.setBounds(DIM_X-RIGHT_MARGIN,0,RIGHT_MARGIN,DIM_Y);
      add(menu);
      menu.setVisible(false);
    }

    /// picker
    if (fullViewMode){
      picker=new ColorPicker(DIM_X-RIGHT_MARGIN,BOTTOM_MARGIN);
      picker.setSize(DIM_X-RIGHT_MARGIN,BOTTOM_MARGIN);
      picker.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
      picker.setBounds(0,DIM_Y-50,DIM_X-100,50);
      add(picker);
      picker.setVisible(false);
    }

    // board
    board=new Board((short)(DIM_X-RIGHT_MARGIN),(short)(DIM_Y-BOTTOM_MARGIN), this);

    // Communicator
    comm=new Communicator(board, getCodeBase(), menu, PORT, this);

    // Make links between objects
    board.setLinks(picker, menu, comm);

    if (fullViewMode){
      menu.setBoard(board);
      menu.setApplet(this);
      menu.setAppletContext(this.getAppletContext());
      menu.setCommunicator(comm);
    }
    comm.start();

    // Set extra data
    if (fullViewMode){
      picker.setNewColor(initPenColor);  // initial pen color
      menu.setBgColor(menuBgColor);
      menu.setCounterColor(counterColor);
      menu.setEmptyThColor(menuEmptyThColor);
    }

    setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

    if (fullViewMode){
      try{
        menu.tracker.waitForAll(100);
      }catch(InterruptedException e){}
    }
  }


  // =============================================================================
  // Close all connections and exit
  public void destroy(){
    comm.communicatorclosing=true;
  }

  // =============================================================================
  // Subthread, repaints image
  public void run(){
    while(true){
      repaint();
      try{
        Thread.sleep(50);
      }
      catch (InterruptedException e){
        System.out.println(Lang.get("repaint thread interrupted"));
      }
    }
  }

  // =============================================================================
  // Blackboard area repaint
  public void paint(Graphics g){
   if (!listenersInitialized){
      g.drawString("Drawboard",10,25);
      g.drawString("(C) by Tomasz 'TomasH' Zielinski",10,40);
      g.drawString("E-mail: tomash@fidonet.org.pl",10,55);
      g.drawString("WWW: http://drawboard.sf.net",10,70);
      g.drawString("Loading graphics...",10,90);
      if (menu.tracker.isErrorAny()){
        String errmes="";
        if (menu.tracker.isErrorID(0))
          errmes+=menu.url_normal+"; ";
        if (menu.tracker.isErrorID(1))
          errmes+=menu.url_bold+"; ";
        if (menu.tracker.isErrorID(2))
          errmes+=menu.url_light+"; ";
        if (menu.tracker.isErrorID(3))
          errmes+=menu.url_menubg;
        g.drawString(Lang.get("LOADING GRAPHICS ERROR: ")+errmes,10,130);
        comm.communicatorclosing=true;
      }
    }else{
      g.setColor(new Color(menuBgColor));
      g.fillRect(0,0,DIM_X,DIM_Y);
    }
  }



  // =============================================================================
  // Graphics handle
  public void update(Graphics g){
    if (!fullViewMode)
      listenersInitialized=true;

    if (fullViewMode && (!menu.tracker.checkAll(true) || menu.tracker.isErrorAny())){
        paint(g);
    }else{
      if (!listenersInitialized){
        setListeners();
        if (temp_menubgimage!=null)
          menu.modifyTransparency();
      }
      board.paint(g);
    }
  }

  // =============================================================================
  // After menu images loaded
  public void setListeners(){
    listenersInitialized=true;
    if (fullViewMode){
      picker.setVisible(true);
      picker.addMouseListener(picker);
      picker.addMouseMotionListener(picker);
      menu.setVisible(true);
      menu.addMouseListener(menu);
      menu.addMouseMotionListener(menu);
      addMouseListener(board);
      addMouseMotionListener(board);

      enableEvents(AWTEvent.KEY_EVENT_MASK);
      addKeyListener(board);
      addFocusListener(board);
      requestFocus();
    }

  }

  // =============================================================================
  // Read HTML-embedded parameters
  void readParameters(){
    if (getParameter("onlyview")!=null){
      fullViewMode=(getParameter("onlyview").equalsIgnoreCase("true"))?false:true;
      System.err.println("Read-only mode enabled!");
    }

    try{
      if (getParameter("port")!=null){
        PORT=Integer.parseInt(getParameter("port"));
        System.out.println("Non-default port number defined: "+PORT);
      }
    }catch(NumberFormatException e){System.out.println(Lang.get("Wrong port number, reverting to default value: ")+PORT);}

    try{
      if (getParameter("bgcolor")!=null)
        initBgColor=(255<<24)|
          ((Integer.parseInt(getParameter("bgcolor").substring(0,2),16))<<16)|
          ((Integer.parseInt(getParameter("bgcolor").substring(2,4),16))<<8)|
          ((Integer.parseInt(getParameter("bgcolor").substring(4,6),16)));
    }catch(NumberFormatException e){System.out.println(Lang.get("Wrong bgcolor definition, reverting to default value:")+
        " R"+((initBgColor>>16)&0xFF)+" G"+((initBgColor>>8)&0xFF)+" B"+((initBgColor)&0xFF));}

    try{
      if (getParameter("pencolor")!=null)
        initPenColor=(255<<24)|
          (Integer.parseInt(getParameter("pencolor").substring(0,2),16))<<16|
          (Integer.parseInt(getParameter("pencolor").substring(2,4),16))<<8|
          (Integer.parseInt(getParameter("pencolor").substring(4,6),16));
    }catch(NumberFormatException e){System.out.println(Lang.get("Wrong pen color definition, reverting to default value:")+
        " R"+((initPenColor>>16)&0xFF)+" G"+((initPenColor>>8)&0xFF)+" B"+((initPenColor)&0xFF));}

    try{
      if (getParameter("countercolor")!=null)
        counterColor=(255<<24)|
          (Integer.parseInt(getParameter("countercolor").substring(0,2),16))<<16|
          (Integer.parseInt(getParameter("countercolor").substring(2,4),16))<<8|
          (Integer.parseInt(getParameter("countercolor").substring(4,6),16));
    }catch(NumberFormatException e){System.out.println(Lang.get("Wrong countercolor definition, reverting to default value:")+
        " R"+((counterColor>>16)&0xFF)+" G"+((counterColor>>8)&0xFF)+" B"+((counterColor)&0xFF));}

    try{
      if (getParameter("menubgcolor")!=null)
        menuBgColor=(255<<24)|
          (Integer.parseInt(getParameter("menubgcolor").substring(0,2),16))<<16|
          (Integer.parseInt(getParameter("menubgcolor").substring(2,4),16))<<8|
          (Integer.parseInt(getParameter("menubgcolor").substring(4,6),16));
    }catch(NumberFormatException e){System.out.println(Lang.get("Wrong menubgcolor definition, reverting to default value:")+
        " R"+((menuBgColor>>16)&0xFF)+" G"+((menuBgColor>>8)&0xFF)+" B"+((menuBgColor)&0xFF));}

    try{
      if (getParameter("emptythumbnailcolor")!=null)
        menuEmptyThColor=(255<<24)|
          (Integer.parseInt(getParameter("emptythumbnailcolor").substring(0,2),16))<<16|
          (Integer.parseInt(getParameter("emptythumbnailcolor").substring(2,4),16))<<8|
          (Integer.parseInt(getParameter("emptythumbnailcolor").substring(4,6),16));
    }catch(NumberFormatException e){System.out.println(Lang.get("Wrong emptythumbnailcolor definition, reverting to default value:")+
        " R"+((menuBgColor>>16)&0xFF)+" G"+((menuBgColor>>8)&0xFF)+" B"+((menuBgColor)&0xFF));}

    if (getParameter("menubgimage")!=null){
        temp_menubgimage=getParameter("menubgimage");
    }

    try{
      if (getParameter("menubgimagetransparentcolor")!=null)
        menuBgImageTransparentColor=(255<<24)|
          (Integer.parseInt(getParameter("menubgimagetransparentcolor").substring(0,2),16))<<16|
          (Integer.parseInt(getParameter("menubgimagetransparentcolor").substring(2,4),16))<<8|
          (Integer.parseInt(getParameter("menubgimagetransparentcolor").substring(4,6),16));
    }catch(NumberFormatException e){System.out.println(Lang.get("Wrong menubgimagetransparentcolor definition, reverting to default value:")+
        " R"+((menuBgImageTransparentColor>>16)&0xFF)+" G"+((menuBgImageTransparentColor>>8)&0xFF)+" B"+((menuBgImageTransparentColor)&0xFF));}

    try{
      if (getParameter("menubgimageoffsetx")!=null)
        temp_menubgimageoffsetx=Integer.parseInt(getParameter("menubgimageoffsetx"));
    }catch(NumberFormatException e){System.out.println(Lang.get("Wrong menubgimageoffsetx definition, reverting to default value:")
    +temp_menubgimageoffsetx);}

    try{
      if (getParameter("menubgimageoffsety")!=null)
        temp_menubgimageoffsety=Integer.parseInt(getParameter("menubgimageoffsety"));
    }catch(NumberFormatException e){System.out.println(Lang.get("Wrong menubgimageoffsety definition, reverting to default value:")
    +temp_menubgimageoffsety);}


  }

  int getID(){
    return ID;
  }


}

class Lang{
  static myHashtable langEN=new myHashtable ();
  static myHashtable langPL=new myHashtable ();
  static myHashtable langDE=new myHashtable ();
  static myHashtable lang = langEN;

  static String get(String s){
    return lang.get(s);
  }

  Lang(String l){
    langEN.put("WARNING: Network communication disabled, see doc for details","WARNING: Network communication disabled, see doc for details");
    langEN.put("Your image was successfully sent to ","Your image was successfully sent to ");
    langEN.put("Wrong archive data received ","Wrong archive data received ");
    langEN.put(" error [1] while saving file "," error [1] while saving file ");
    langEN.put(" error [2] while saving file "," error [2] while saving file ");
    langEN.put(" error [3] while saving file "," error [3] while saving file ");
    langEN.put(" error [4] while saving file "," error [4] while saving file ");
    langEN.put("Subject: Your requested Drawboard image","Subject: Your requested Drawboard image");
    langEN.put("Image is included in this e-mail as multipart base64 image/png","Image is included in this e-mail as multipart base64 image/png");
    langEN.put("This is graphics file sent to you from Drawboard applet","This is graphics file sent to you from Drawboard applet");
    langEN.put("Your image was NOT sent to ","Your image was NOT sent to ");
    langEN.put("Reinitialize of element failed: ","Reinitialize of element failed: ");
    langEN.put("Connecting to server","Connecting to server");
    langEN.put("**Connect to server failed, pausing 5 seconds","**Connect to server failed, pausing 5 seconds");
    langEN.put("Connect to server failed, pausing 5 seconds","Connect to server failed, pausing 5 seconds");
    langEN.put("**Socket streams get failed, maximum client number reached. Waiting 15 seconds ","**Socket streams get failed, maximum client number reached. Waiting 15 seconds ");
    langEN.put("Maximum client number reached. Waiting 15 seconds","Maximum client number reached. Waiting 15 seconds");
    langEN.put("**Applet loaded from local file, not remote server","**Applet loaded from local file, not remote server");
    langEN.put("**Network communication disabled","**Network communication disabled");
    langEN.put("**Trying to connect","**Trying to connect");
    langEN.put("**Connection estabilished, data receiving started","**Connection estabilished, data receiving started");
    langEN.put("Received object size: ","Received object size: ");
    langEN.put("Raport","Raport");
    langEN.put("Raport received","Raport received");
    langEN.put("Wrong server image size, see doc for details","Wrong server image size, see doc for details");
    langEN.put("Server image size invalid","Server image size invalid");
    langEN.put("Local image size: ","Local image size: ");
    langEN.put("Remote image size: ","Remote image size: ");
    langEN.put("IO Exception in second thread: ","IO Exception in second thread: ");
    langEN.put("Received something unreadable: ","Received something unreadable: ");
    langEN.put("Stopping communication module","Stopping communication module");
    langEN.put("**Socket error while stopping communicator ","**Socket error while stopping communicator ");
    langEN.put("**IO error while while stopping communicator ","**IO error while while stopping communicator ");
    langEN.put("Connection lost, some elements too :","Connection lost, some elements too :");
    langEN.put("Copyright & about information...","Copyright & about information...");
    langEN.put("Applet window dimensions: width = ","Applet window dimensions: width = ");
    langEN.put(", height = ",", height = ");
    langEN.put("repaint thread interrupted","repaint thread interrupted");
    langEN.put("LOADING GRAPHICS ERROR: ","LOADING GRAPHICS ERROR: ");
    langEN.put("Read-only mode enabled!","Read-only mode enabled!");
    langEN.put("Non-default port number defined: ","Non-default port number defined: ");
    langEN.put("Wrong port number, reverting to default value: ","Wrong port number, reverting to default value: ");
    langEN.put("Wrong bgcolor definition, reverting to default value:","Wrong bgcolor definition, reverting to default value:");
    langEN.put("Wrong pen color definition, reverting to default value:","Wrong pen color definition, reverting to default value:");
    langEN.put("Wrong countercolor definition, reverting to default value:","Wrong countercolor definition, reverting to default value:");
    langEN.put("Wrong menubgcolor definition, reverting to default value:","Wrong menubgcolor definition, reverting to default value:");
    langEN.put("Wrong emptythumbnailcolor definition, reverting to default value:","Wrong emptythumbnailcolor definition, reverting to default value:");
    langEN.put("Wrong menubgimagetransparentcolor definition, reverting to default value:","Wrong menubgimagetransparentcolor definition, reverting to default value:");
    langEN.put("Wrong menubgimageoffsetx definition, reverting to default value:","Wrong menubgimageoffsetx definition, reverting to default value:");
    langEN.put("Wrong menubgimageoffsety definition, reverting to default value:","Wrong menubgimageoffsety definition, reverting to default value:");
    langEN.put("Trying to load custom menu datafile ","Trying to load custom menu datafile ");
    langEN.put("Loading datafile failed","Loading datafile failed");
    langEN.put("Loading default menu","Loading default menu");
    langEN.put("Bad URL - menu image file invalid!","Bad URL - menu image file invalid!");
    langEN.put("interrupted waiting for pixels!","interrupted waiting for pixels!");
    langEN.put("People ","People ");
    langEN.put("Saved image number ","Saved image number ");
    langEN.put("Wrong datafile URL ","Wrong datafile URL ");
    langEN.put("Data file: ","Data file: ");
    langEN.put("Some elements were not defined, reverting to default values","Some elements were not defined, reverting to default values");
    langEN.put("Check drawboard and skindef versions","Check drawboard and skindef versions");
    langEN.put("Skin datafile IO Exception ","Skin datafile IO Exception ");
    langEN.put("Reverting to default values","Reverting to default values");
    langEN.put("Please type target email","Please type target email:");

    langPL.put("WARNING: Network communication disabled, see doc for details","UWAGA: Komunikacja sieciowa wylaczona, siegnij do dokumentacji");
    langPL.put("Your image was successfully sent to ","Obraz wyslano do ");
    langPL.put("Wrong archive data received ","Otrzymano bledne dane archiwalne ");
    langPL.put(" error [1] while saving file "," blad [1] zapisu do pliku ");
    langPL.put(" error [2] while saving file "," blad [2] zapisu do pliku ");
    langPL.put(" error [3] while saving file "," blad [3] zapisu do pliku ");
    langPL.put(" error [4] while saving file "," blad [4] zapisu do pliku ");
    langPL.put("Subject: Your requested Drawboard image","Subject: Obraz narysowany w Drawboard");
    langPL.put("Image is included in this e-mail as multipart base64 image/png","Obrazek dolaczono do maila jako zalacznik typu  multipart base64 image/png");
    langPL.put("This is graphics file sent to you from Drawboard applet","To jest obrazek wyslany do Ciebie przez aplet Drawboard");
    langPL.put("Your image was NOT sent to ","Obraz NIE zostal wyslany do ");
    langPL.put("Reinitialize of element failed: ","Reinicjalizacja elementu nie powiodla sie: ");
    langPL.put("Connecting to server","Trwa laczenie z serwerem");
    langPL.put("**Connect to server failed, pausing 5 seconds","**Polaczenie nieudane, pieciosekundowa pauza");
    langPL.put("Connect to server failed, pausing 5 seconds","Polaczenie nieudane, pieciosekundowa pauza");
    langPL.put("**Socket streams get failed, maximum client number reached. Waiting 15 seconds ","**Maksymalna liczba klientow przekroczona. Pauza pietnastosekundowa ");
    langPL.put("Maximum client number reached. Waiting 15 seconds","Maksymalna liczba klientow przekroczona. 15 sekund pauzy.");
    langPL.put("**Applet loaded from local file, not remote server","**Aplet zaladowany z pliku lokalnego, nie poprzez siec");
    langPL.put("**Network communication disabled","**Komunikacja sieciowa wylaczona");
    langPL.put("**Trying to connect","**Proba polaczenia");
    langPL.put("**Connection estabilished, data receiving started","**Polaczenie ustanowione, rozpoczeto odbior danych");
    langPL.put("Received object size: ","Rozmiar otrzymanego obiektu: ");
    langPL.put("Raport","Raport");
    langPL.put("Raport received","Otrzymano raport");
    langPL.put("Wrong server image size, see doc for details","Nieprawidlowy rozmiar obrazu na serwerze, przeczytaj dokumentacje");
    langPL.put("Server image size invalid","Nieprawidlowy rozmiar obrazu");
    langPL.put("Local image size: ","Rozmiar lokalnego obrazu: ");
    langPL.put("Remote image size: ","Rozmiar zdalnego obrazu: ");
    langPL.put("IO Exception in second thread: ","IO Exception w drugim watku: ");
    langPL.put("Received something unreadable: ","Otrzymano cos nieczytelnego: ");
    langPL.put("Stopping communication module","Zatrzymywanie modulu komunikacyjnego");
    langPL.put("**Socket error while stopping communicator ","**Socket error podczas zatrzymywania modulu komunikacyjnego ");
    langPL.put("**IO error while while stopping communicator ","**IO error podczas zatrzymywania modulu komunikacyjnego ");
    langPL.put("Connection lost, some elements too :","Polaczenie utracone, czesc danych rowniez :");
    langPL.put("Copyright & about information...","Copyright & about information...");
    langPL.put("Applet window dimensions: width = ","Rozmiary okna apletu: szerokosc = ");
    langPL.put(", height = ",", wysokosc = ");
    langPL.put("repaint thread interrupted","watek odrysowania przerwany");
    langPL.put("LOADING GRAPHICS ERROR: ","BLAD LADOWANIA GRAFIKI: ");
    langPL.put("Read-only mode enabled!","Tryb read-only aktywny!");
    langPL.put("Non-default port number defined: ","Zdefiniowano niestandardowy numer portu: ");
    langPL.put("Wrong port number, reverting to default value: ","Bledny numer portu, powrot do wartosci domyslnej: ");
    langPL.put("Wrong bgcolor definition, reverting to default value:","Bledna definicja bgcolor, powrot do wartosci domyslnej:");
    langPL.put("Wrong pen color definition, reverting to default value:","Bledna definicja pen color, powrot do wartosci domyslnej:");
    langPL.put("Wrong countercolor definition, reverting to default value:","Bledna definicja countercolor, powrot do wartosci domyslnej:");
    langPL.put("Wrong menubgcolor definition, reverting to default value:","Bledna definicja menubgcolor, powrot do wartosci domyslnej:");
    langPL.put("Wrong emptythumbnailcolor definition, reverting to default value:","Bledna definicja emptythumbnailcolor, powrot do wartosci domyslnej:");
    langPL.put("Wrong menubgimagetransparentcolor definition, reverting to default value:","Bledna definicja transparentcolor, powrot do wartosci domyslnej:");
    langPL.put("Wrong menubgimageoffsetx definition, reverting to default value:","Bledna definicja menubgimageoffsetx, powrot do wartosci domyslnej:");
    langPL.put("Wrong menubgimageoffsety definition, reverting to default value:","Bledna definicja menubgimageoffsety, powrot do wartosci domyslnej:");
    langPL.put("Trying to load custom menu datafile ","Ladowanie pliku definicji menu ");
    langPL.put("Loading datafile failed","Ladowanie pliku definicji nieudane");
    langPL.put("Loading default menu","Ladowanie domyslnego menu");
    langPL.put("Bad URL - menu image file invalid!","Zly URL - nie zaladowano pliku definicji menu!");
    langPL.put("interrupted waiting for pixels!","przerwano pobieranie pikseli!");
    langPL.put("People ","Ludzie ");
    langPL.put("Saved image number ","Zapamietany obraz numer ");
    langPL.put("Wrong datafile URL ","Zly URL pliku z danymi ");
    langPL.put("Data file: ","Plik z danymi: ");
    langPL.put("Some elements were not defined, reverting to default values","Nie zdefiniowano niektorych elementow, powrot do wartosci domyslnych");
    langPL.put("Check drawboard and skindef versions","Sprawdz wersje programu i pliku z danymi");
    langPL.put("Skin datafile IO Exception ","Skin datafile IO Exception ");
    langPL.put("Reverting to default values","Powrot do wartosci domyslnych");
    langPL.put("Please type target email","Podaj e-mail adresata:");

    langDE.put("WARNING: Network communication disabled, see doc for details","ACHTUNG: Netzwerkverbindung deaktiviert, siehe Dokumentation fuer Details");
    langDE.put("Your image was successfully sent to ","Ihr Bild wurde erfolgreich versandt an ");
    langDE.put("Wrong archive data received ","Ungueltige Archivdaten empfangen ");
    langDE.put(" error [1] while saving file "," Fehler [1] beim Speichern der Datei ");
    langDE.put(" error [2] while saving file "," Fehler [2] beim Speichern der Datei ");
    langDE.put(" error [3] while saving file "," Fehler [3] beim Speichern der Datei ");
    langDE.put(" error [4] while saving file "," Fehler [4] beim Speichern der Datei ");
    langDE.put("Subject: Your requested Drawboard image","Subject: Ihr gewuenschtes Drawboard Bild");
    langDE.put("Image is included in this e-mail as multipart base64 image/png","Das Bild ist in diese eMail als multipart base64 image/png eingebunden");
    langDE.put("This is graphics file sent to you from Drawboard applet","Dieses Bild wurde Ihnen vom Drawboard Applet zugesandt");
    langDE.put("Your image was NOT sent to ","Das Bild konnte NICHT gesendet werden an ");
    langDE.put("Reinitialize of element failed: ","Reinitialisierung des Elements scheiterte: ");
    langDE.put("Connecting to server","Verbinde mit Server");
    langDE.put("**Connect to server failed, pausing 5 seconds","**Verbindung zum Server scheiterte, warte 5 Sekunden");
    langDE.put("Connect to server failed, pausing 5 seconds","Verbindung zum Server scheiterte, warte 5 Sekunden");
    langDE.put("**Socket streams get failed, maximum client number reached. Waiting 15 seconds ","**Socketverbindungen lieferten Fehler, maximal zulaessige Clientanzahl erreicht. Warte 15 Sekunden ");
    langDE.put("Maximum client number reached. Waiting 15 seconds","Maximal zulaessige Clientanzahl erreicht. Warte 15 Sekunden");
    langDE.put("**Applet loaded from local file, not remote server","**Das Applet wird lokal geladen, nicht vom Server");
    langDE.put("**Network communication disabled","**Netzwerkverbindung deaktiviert");
    langDE.put("**Trying to connect","**Versuche zu verbinden");
    langDE.put("**Connection estabilished, data receiving started","**Verbindung aufgebaut, Datenempfang gestartet");
    langDE.put("Received object size: ","Empfangene Objektgroesse: ");
    langDE.put("Raport","Rapport");
    langDE.put("Raport received","Rapport empfangen");
    langDE.put("Wrong server image size, see doc for details","Falsche Bildgroesse auf Server, siehe Dokumentation fuer Details");
    langDE.put("Server image size invalid","Server Bildgroesse ungueltig");
    langDE.put("Local image size: ","Bildgroesse lokal: ");
    langDE.put("Remote image size: ","Bildgroesse entfernt: ");
    langDE.put("IO Exception in second thread: ","IO Aussnahme im zweiten Thread: ");
    langDE.put("Received something unreadable: ","Unlesbare Information empfangen: ");
    langDE.put("Stopping communication module","Stoppe Kommunikationsmodul");
    langDE.put("**Socket error while stopping communicator ","**Socketfehler beim Beenden des Kommunikator ");
    langDE.put("**IO error while while stopping communicator ","**IO Fehler beim Beenden des Kommunikator ");
    langDE.put("Connection lost, some elements too :","Verbindung sowie einige Elemente verloren: ");
    langDE.put("Copyright & about information...","Copyright und weitere Informationen...");
    langDE.put("Applet window dimensions: width = ","Applet Fenstermasse: Breite = ");
    langDE.put(", height = ",", Hoehe = ");
    langDE.put("repaint thread interrupted","Repaint Thread unterbrochen");
    langDE.put("LOADING GRAPHICS ERROR: ","FEHLER BEIM LADEN DER GRAFIK: ");
    langDE.put("Read-only mode enabled!","Nur-lesen Modus aktiviert!");
    langDE.put("Non-default port number defined: ","Nicht-standarmaessige Portnummer definiert: ");
    langDE.put("Wrong port number, reverting to default value: ","Falsche Portnummer, schalte auf Standardwert: ");
    langDE.put("Wrong bgcolor definition, reverting to default value:","Falsche bgcolor definiert, schalte auf Standardwert:");
    langDE.put("Wrong pen color definition, reverting to default value:","Falsche pen color definiert, schalte auf Standardwert:");
    langDE.put("Wrong countercolor definition, reverting to default value:","Falsche countercolor definiert, schalte auf Standardwert:");
    langDE.put("Wrong menubgcolor definition, reverting to default value:","Falsche menubgcolor definiert, schalte auf Standardwert:");
    langDE.put("Wrong emptythumbnailcolor definition, reverting to default value:","Falsche emptythumbnailcolor definiert, schalte auf Standardwert:");
    langDE.put("Wrong menubgimagetransparentcolor definition, reverting to default value:","Falsche menubgimagetransparentcolor definiert, schalte auf Standardwert:");
    langDE.put("Wrong menubgimageoffsetx definition, reverting to default value:","Falscher menubgimageoffsetx definiert, schalte auf Standardwert:");
    langDE.put("Wrong menubgimageoffsety definition, reverting to default value:","Falscher menubgimageoffsety definiert, schalte auf Standardwert:");
    langDE.put("Trying to load custom menu datafile ","Versuche eigene Menudatendatei zu laden ");
    langDE.put("Loading datafile failed","Laden der Datendatei gescheitert");
    langDE.put("Loading default menu","Lade Standardmenu");
    langDE.put("Bad URL - menu image file invalid!","Ungueltiger URL - Menue-Bilddatei ungueltig!");
    langDE.put("interrupted waiting for pixels!","Unterbrochen beim Warten auf Pixel!");
    langDE.put("People ","Teilnehmer ");
    langDE.put("Saved image number ","Bild gespeichert, Nummer ");
    langDE.put("Wrong datafile URL ","Falscher Datendatei URL ");
    langDE.put("Data file: ","Datendatei: ");
    langDE.put("Some elements were not defined, reverting to default values","Einige Elemente sind nicht definiert, schalte auf Standardwerte");
    langDE.put("Check drawboard and skindef versions","Ueberpruefen Sie drawboard und skindef Versionen");
    langDE.put("Skin datafile IO Exception ","Skin-Datendatei IO Aussnahme ");
    langDE.put("Reverting to default values","Schalte auf Standardwerte");
    langDE.put("Please type target email","Bitte Ziel-eMail eingeben:");

    if (l.equalsIgnoreCase("pl"))
      lang=langPL;
    else if (l.equalsIgnoreCase("de"))
      lang=langDE;
    else
      lang=langEN;
  }
}

class myHashtable extends Hashtable{
  String get(String s){
    if (super.get(s)!=null)
      return (String)super.get(s);
    else
      return ("MISSING TRANSLATION: "+s);
  }
}


/*
    langEN.put("WARNING: Network communication disabled, see doc for details",);
    langEN.put("Your image was successfully sent to ",);
    langEN.put("Wrong archive data received ",);
    langEN.put(" error [1] while saving file ",);
    langEN.put(" error [2] while saving file ",);
    langEN.put(" error [3] while saving file ",);
    langEN.put(" error [4] while saving file ",);
    langEN.put("Subject: Your requested Drawboard image",);
    langEN.put("Image is included in this e-mail as multipart base64 image/png",);
    langEN.put("This is graphics file sent to you from Drawboard applet",);
    langEN.put("Your image was NOT sent to ",);
    langEN.put("Reinitialize of element failed: ",);
    langEN.put("Connecting to server",);
    langEN.put("**Connect to server failed, pausing 5 seconds",);
    langEN.put("Connect to server failed, pausing 5 seconds",);
    langEN.put("**Socket streams get failed, maximum client number reached. Waiting 15 seconds ",);
    langEN.put("Maximum client number reached. Waiting 15 seconds",);
    langEN.put("**Applet loaded from local file, not remote server",);
    langEN.put("**Network communication disabled",);
    langEN.put("**Trying to connect",);
    langEN.put("**Connection estabilished, data receiving started",);
    langEN.put("Received object size: ",);
    langEN.put("Raport",);
    langEN.put("Raport received",);
    langEN.put("Wrong server image size, see doc for details",);
    langEN.put("Server image size invalid",);
    langEN.put("Local image size: ",);
    langEN.put("Remote image size: ",);
    langEN.put("IO Exception in second thread: ",);
    langEN.put("Received something unreadable: ",);
    langEN.put("Stopping communication module",);
    langEN.put("**Socket error while stopping communicator ",);
    langEN.put("**IO error while while stopping communicator ",);
    langEN.put("Connection lost, some elements too :",);
    langEN.put("Copyright & about information...",);
    langEN.put("Applet window dimensions: width = ",);
    langEN.put(", height = ",);
    langEN.put("repaint thread interrupted",);
    langEN.put("LOADING GRAPHICS ERROR: ",);
    langEN.put("Read-only mode enabled!",);
    langEN.put("Non-default port number defined: ",);
    langEN.put("Wrong port number, reverting to default value: ",);
    langEN.put("Wrong bgcolor definition, reverting to default value:",);
    langEN.put("Wrong pen color definition, reverting to default value:",);
    langEN.put("Wrong countercolor definition, reverting to default value:",);
    langEN.put("Wrong menubgcolor definition, reverting to default value:",);
    langEN.put("Wrong emptythumbnailcolor definition, reverting to default value:",);
    langEN.put("Wrong menubgimagetransparentcolor definition, reverting to default value:",);
    langEN.put("Wrong menubgimageoffsetx definition, reverting to default value:",);
    langEN.put("Wrong menubgimageoffsety definition, reverting to default value:",);
    langEN.put("Trying to load custom menu datafile ",);
    langEN.put("Loading datafile failed",);
    langEN.put("Loading default menu",);
    langEN.put("Bad URL - menu image file invalid!",);
    langEN.put("interrupted waiting for pixels!",);
    langEN.put("People ",);
    langEN.put("Saved image number ",);
    langEN.put("Wrong datafile URL ",);
    langEN.put("Data file: ",);
    langEN.put("Some elements were not defined, reverting to default values",);
    langEN.put("Check drawboard and skindef versions",);
    langEN.put("Skin datafile IO Exception ",);
    langEN.put("Reverting to default values",);
*/
