import java.awt.FileDialog; 
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.Timer;

boolean ReadMode = true;
boolean CollectMode = false;
int index = 0;
int currentindex = 0;
int adjust = -500*3;
String fileopenloc = "";
String filename = "";
boolean opened = false;
ArrayList<String> list = new ArrayList<String>();
ArrayList<String> bookcontent = new ArrayList<String>();
String TheOpenFileWay = "utf-8";
int pianyi = 0;
String processtxtpath = "F:/ceshi/test/process.txt";
String configpath = "E:/HappyOne/research_center/ReadBox/config";
String dangerpath = "E:/HappyOne/research_center/ReadBox/dangerconfig";
String fileaddr = "";
boolean refresh = false;
Collectbox collect = new Collectbox();
boolean continuedetect = true;
Sys_timer timerhere = new  Sys_timer();
ArrayList<String> waitlist = new ArrayList<String>();
ArrayList<String> dangerlist = new ArrayList<String>();
boolean waitforconfig = false;
int currentconfigindex = 0;


// one pic variable for special use
PImage newyear;


void setup()
{
  size(900,500);
  mouseWheelSetup();
  DangerInit();
  collect.handle_in_setup();
  Timer timer = new Timer();
  timer.schedule(new MyTask(),9,60*1000);
  smooth();
  noStroke();
  
   // special use in special days
  //  newyear = loadImage("safety.jpg");
    
}

void DangerInit()
{
    String a[]= loadStrings(dangerpath);
    for(int k=0;k<a.length;k++){
     dangerlist.add(a[k]);
    }  
}

void draw()
{
  if(ReadMode){
      if(opened){
         list.clear();
         currentindex = 0;
         File listfiles[] = new File(fileopenloc).listFiles();
         if(listfiles.length>0){
           for(int j=0;j<listfiles.length;j++){
             list.add(listfiles[j].getName());
           }
           openfile(fileopenloc+list.get(0));
         }
         opened = false;
      }
      if(bookcontent.size()>0&&refresh){
          background(167,219,95);
        for(int m=0;m<bookcontent.size();m++){
          if((20+m*20+adjust)<height&&(20+m*20+adjust)>0){  
            fill(0,0,0);
            text(bookcontent.get(m),60,20+m*20+adjust);
          }
        }
        refresh = false;
      }
  }
  if(CollectMode){
    collect.handle_in_draw();
  }
  if(!waitlist.isEmpty()&&mouseX>width-200){
    fill(234,234,92);
    for(int k=0;k<waitlist.size();k++){
      if(k<=9){
        rect(width-200,k*40,200,40);
      }
    }    
    for(int k=0;k<waitlist.size();k++){
      if(k<=9){
        fill(0,0,0);
        if(dangerlist.contains(waitlist.get(k))){
          fill(255,0,0);
        }
        text(waitlist.get(k),width-180,k*40+20);
      }
    }        
  }  
  
   // special show of images
  // image(newyear,width/2-newyear.width/2,0);
  
}


void mousePressed()
{
    collect.handle_in_mousePressed();
    if(mouseX>width-200){
      int index = mouseY/40;
      if(index<waitlist.size()){
        waitforconfig = true;
        if(waitforconfig){
            fill(80,216,50);
            rect(width-450,index*40,250,40);
            fill(0,0,0);
            text("Sure to trust "+waitlist.get(index)+"?  Y  or  N",width-440,index*40+20);
            currentconfigindex = index;
        }
      }
    }
}

void addconfig(String name)
{
  String a[]= loadStrings(configpath);
  ArrayList<String> b = new ArrayList<String>();
  for(int k=0;k<a.length;k++){
    b.add(a[k]);
  }
  b.add(name);
  String c[] = new String[b.size()];
  for(int i=0;i<c.length;i++){
    c[i] = b.get(i);
  }
  saveStrings(configpath,c);
}

void keyPressed()
{
  if((key=='y'||key=='Y')&&waitforconfig){
    addconfig(waitlist.get(currentconfigindex));
    waitlist.remove(currentconfigindex);
    waitforconfig = false;
  }
  if((key=='n'||key=='N')&&waitforconfig){
    waitlist.remove(currentconfigindex);    
    waitforconfig = false;    
  }
  if(key=='p'){
    if(index==1){
      ReadMode = true;
      CollectMode = false;
      index--;
    }
  }
  if(key=='b'){
    if(index==0){
      CollectMode = true;
      ReadMode = false;
      index++;
    }
  }
  if(ReadMode){
      if(key=='o'){
        new FileOpen();
        opened = true;
      }
      if(keyCode==37){
        adjust = -500*3;
        if(currentindex>0){
          currentindex--;
          openfile(fileopenloc+list.get(currentindex));
        }
      }
      if(keyCode==39){
        adjust = -500*3;
        if(currentindex<list.size()-1){
          currentindex++;
          openfile(fileopenloc+list.get(currentindex));
        }
      }
      if(key=='d'){
        if(new String(list.get(currentindex)).contains(".txt")){
          File deletefile = new File(fileopenloc+list.get(currentindex));
          deletefile.delete();
          list.remove(currentindex);
        }
      }
      if(key=='g'){
        TheOpenFileWay = "gbk";
      }
      if(key=='u'){
        TheOpenFileWay = "utf-8";
      }
  }
  if(CollectMode){
    collect.handle_in_keyPressed();
  }
}

void detect()
{
    continuedetect = false;
    String loadin[] = loadStrings("file:///"+processtxtpath);
    String configin[] = loadStrings("file:///"+configpath);
    ArrayList<String> config = new ArrayList<String>();
        for(int i=0;i<configin.length;i++){
          config.add(configin[i]);
        }
        if(loadin!=null){
          for(int i=0;i<loadin.length;i++){
            if(config.contains(loadin[i])){
            }
            else{
              waitlist.add(loadin[i]);
            }
          }
        }
        File delete = new File(processtxtpath);
        delete.delete();
    continuedetect = true;  
}


void openfile(String filename)
{
    bookcontent.clear();
    if(filename!=null&&filename.contains(".txt")){
          String readincontent = "";
          try{
              URL url = new URL("file:///"+filename);         
              InputStreamReader isr = new InputStreamReader(url.openStream(),TheOpenFileWay);
              BufferedReader br = new BufferedReader(isr); 
              while((readincontent = br.readLine())!=null){
                    if(readincontent.length()>65){
                      int jishu = readincontent.length()/65;
                      int w = 0;
                      for(w=0;w<jishu;w++)
                        bookcontent.add(readincontent.substring(0+65*w,65+65*w));       
                      bookcontent.add(readincontent.substring(65+65*(w-1),readincontent.length()));           
                    }
                    else{
                      bookcontent.add(readincontent);   
                    }
              }
              isr.close();
              br.close();
            }    
           catch (IOException e){
           }  
    }
    else{
    }
    
}


class FileOpen
{
  FileDialog fileopen;
  
    FileOpen()
  {
   fileopen = new FileDialog(new Frame(),"打开文件对话框",FileDialog.LOAD);   
   fileopen.addWindowListener(new WindowAdapter()
   {
     public void windowClosing(WindowEvent e)
     {
       fileopen.setVisible(false);
     }
   });
   open();
  }
    public void open()
  {
    fileopen.setVisible(true);
    fileopenloc = fileopen.getDirectory();
    filename = fileopen.getFile();
  } 

}


void delFile(String path) {
     File file=new File(path);
     if(file.exists() && file.isFile()){
       file.delete();
     }
}






  void mouseWheelSetup()
{
    addMouseWheelListener( // the rest of of this is acutally the argument list for the function call
            new java.awt.event.MouseWheelListener() 
            {
                 public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) 
                 {
                   refresh = true;
                   adjust -= 9*evt.getWheelRotation();
                 }
            }
   ) // this is the end of the argument list
   ;    // this single semicolon is the entire, complete function body
}   



 class MyTask extends java.util.TimerTask{
        public void run() {
        // TODO Auto-generated method stub
        if(continuedetect){     
          File cancell;     
          do{
            timerhere.action_performed();
            cancell  = new File(processtxtpath);
            fill(255,0,0);
            text("something is wrong with process get",10,10);
          }
          while(!cancell.exists());
          detect();
        }
    }
    }

class Sys_timer
{ 
  void action_performed()
  {
    Runtime rn = Runtime.getRuntime();
    Process p = null;
    try{
      p = rn.exec("E:/HappyOne/research_center/ReadBox/process.exe");
    }
    catch(Exception e){
    }
  }
}


import java.text.SimpleDateFormat;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.FileDialog; 
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStreamReader;
import java.net.URL;




class Collectbox{

ArrayList<String> pasecontent = new ArrayList<String>();
boolean firstpass = false;
boolean secondpass = false;
paster pase = new paster();
int jishu = 0;
String TheOpenFileWay = "utf-8";

void handle_in_setup()
{
  pasecontent.add("I am the first one");
}

void handle_in_draw()
{
  background(167,219,95);
  if (firstpass&&secondpass) {
    pasecontent.add(pase.handle());
    firstpass = false;
    secondpass = false;
  }
  if(pasecontent.size()>0){
        for(int i=0;i<pasecontent.size();i++){
          if(20+20*i<height){
            fill(0,0,0);
            text(pasecontent.get(i),10,30+20*i);
          }
        }
  }
  
}

void handle_in_mousePressed()
{
  jishu=mouseY/20;
  if(pasecontent.size()>=(jishu)&&jishu>0){
      pase.handle(pasecontent.get(jishu-1)); 
      pasecontent.remove(jishu-1); 
  }
}

void handle_in_keyPressed()
{
  if (keyCode==17) {
    firstpass = true;
  }
  if (keyCode==86) {
    secondpass = true;
  }
  if(key=='o'){
    new FileOpen();
    openfile(fileopenloc+filename);
  }
  if(key=='g'){
    TheOpenFileWay = "gbk";
  }
  if(key=='u'){
    TheOpenFileWay = "utf-8";
  }
  if(key=='c'){
    pasecontent.clear();
  }
}


void openfile(String filename)
{
    pasecontent.clear();
    if(filename!=null&&filename.contains(".txt")){
          String readincontent = "";
          try{
          URL url = new URL("file:///"+filename);         
          InputStreamReader isr = new InputStreamReader(url.openStream(),TheOpenFileWay);
          BufferedReader br = new BufferedReader(isr); 
          while((readincontent = br.readLine())!=null){
                if(readincontent.length()>65){
                  int jishu = readincontent.length()/65;
                  int w = 0;
                  for(w=0;w<jishu;w++)
                  pasecontent.add(readincontent.substring(0+65*w,65+65*w));       
                  pasecontent.add(readincontent.substring(65+65*(w-1),readincontent.length()));           
                }
                else{
                  pasecontent.add(readincontent);   
                }
          }
          isr.close();
          br.close();
        }    
           catch (IOException e){ 
           }  
    }
    else{
    }
}


}


class paster
{
  String forpaste = "";

  String handle()
  {
    String forreturn = "";
    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable clipTf = sysClip.getContents(null);
    if (clipTf!=null) {
      //检查文本类型
      if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
          this.forpaste = (String)clipTf.getTransferData(DataFlavor.stringFlavor);
        }
        catch (Exception e) 
        {            
        }
      } 
    }  
    forreturn = this.forpaste;
    return forreturn;
  }
  
  
  void handle(String content)
  {
    if(content!=null){
      Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection change =  new StringSelection(content);
      ClipboardOwner owner = null;
      Transferable clipTf = sysClip.getContents(null);
       if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {            
              sysClip.setContents(change,owner);  
            }
            catch (Exception e) 
            {            
            }
       }
    }
    
  }
    
}




