
import java.awt.FileDialog; 
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import Luxand.*;
import Luxand.FSDK.*;
import Luxand.FSDKCam.*;
import java.io.InputStreamReader;
import java.net.URL;


ArrayList<String> bookcontent;
ArrayList<String> booknamelist;
ArrayList<String> bookposlist;
boolean opened = false;
int booklistindex = 0;
String TheOpenFileWay = "utf-8";
HotBox_handle Book = new HotBox_handle();

void setup()
{
    size(300,600);
    Book.handle_in_setup();
}

void draw()
{
    Book.handle_in_draw();
}




class HotBox_handle
{
int jishu = 0;
boolean auto_detect = false;
boolean detect_initial = false;

TCameras CameraList = new TCameras();
int CameraCount[] = new int[19];
TCameras CameraNameList = new TCameras();
TCameras CameraDevicePathList = new TCameras();
HCamera CameraHandle = new HCamera();
String camera_name = "";
HImage Image = new HImage();
String HardwareID[] = {"vbwhjbhjwsbjhb"};
String LicenseInfo ="MWMz5NI2Ok++deLjspWTIiwgapVBHACp24tZgoP7oqgj37703k7SU07Tj2/CvBMIQXyWnuWNqTKaQTsTAkILs/vP6bCAboxHxgv9j+JMn0OT8ZzZGl5VBRhNxCFh1T5hgYsZPoHrX7ArUQiSPspFtPcG9YNBYVPJZjFXso11nug=";
FSDK_Features.ByReference feature_for_me = new FSDK_Features.ByReference();
int current_y = 0;
int compare = 0;
int ball_place = 0;   

PImage a;
PImage bookmark;
PImage ascroll;
PImage bookmarkscroll;
PImage []load = new PImage[136];
PImage currentbackground;
int loc;
int lastmouse = 0;
int currentpic = 1;
String picname;
boolean change = false;
int movepixel = 0;
boolean showmark = false;
int loadfilenum = 0;
boolean enterpicmode = true;
boolean entertextmode = false;
int viewadjust = 0;
int []pos = new int[6];
int colorchoice = 0;

boolean whetheradd = false;

// the standard configuration
int sys_scroll = 0;


void handle_in_setup()
{
    if(!this.whetheradd){
      
       for(int i=1;i<136;i++)
       {
        picname = "y"+i+".jpg";
        load[i] = loadImage(picname); 
       }
       a = loadImage("note.jpg");
       bookmark = loadImage("smallicon.jpg");
       currentbackground = load[currentpic];
       ascroll = loadImage("notescroll.jpg");
       bookmarkscroll = loadImage("smalliconscroll.jpg");
       booknamelist = new ArrayList<String>();
       bookposlist = new ArrayList<String>();
       // initial work for this reader
       booknamelist.add("Welcome");
       bookposlist.add("Welcome");
       booknamelist.add("Introduction");
       bookposlist.add("Introduction");
       for(int weizhi = 0;weizhi<6;weizhi++){
       pos[weizhi] = 150*weizhi-300;
       }
       mouseWheelSetup2();
       
      if(FSDK.FSDKE_OK==FSDK.GetHardware_ID(HardwareID)){

      }
      if(FSDK.FSDKE_OK==FSDK.ActivateLibrary(LicenseInfo))
      {

      }
      if(FSDK.FSDKE_OK==FSDK.Initialize())
      {
      
      }     
      if(FSDK.FSDKE_OK==FSDKCam.InitializeCapturing()){
      }
    
      if(FSDK.FSDKE_OK == FSDKCam.SetCameraNaming(false)){
      }
    
      if(FSDK.FSDKE_OK ==FSDKCam.GetCameraList(CameraList, CameraCount)){
      }    
      if(FSDK.FSDKE_OK == FSDKCam.GetCameraListEx(CameraNameList,CameraDevicePathList,CameraCount))
      {
        camera_name = CameraNameList.cameras[0];
      }
      
       whetheradd = true;
    }
           // initial drawing 

       image(ascroll,0+sys_scroll,height/8);   
       image(bookmarkscroll,0+sys_scroll,450);
       image(bookmarkscroll,150+sys_scroll,450);
   
}

void handle_in_draw()
{
    background(currentbackground);  

  // nackground changing ad altering
  if(change){
   currentbackground = load[currentpic];
   change = false;  
  }
    
   if(opened){
   // 进行文字信息的显示  
   enterpicmode = false;
   entertextmode = true;
   opened = false;
   showmark = true;
   }     
   // picmode  area
   if(enterpicmode){
    showimage(a,currentbackground,0+sys_scroll,height/8);   
    for(int mark = 0;mark<6;mark++){
         showimage(bookmark,currentbackground,pos[mark]+movepixel+sys_scroll,450);
       }
    fill(0,0,0);   
    if(booknamelist.get(booklistindex).length()<6){
    text(booknamelist.get(booklistindex),40+sys_scroll,480);
    }else{
    text(booknamelist.get(booklistindex).substring(0,5)+"...",40+sys_scroll,480);
    }
    if(booknamelist.get(booklistindex).length()<6){
    text(booknamelist.get(booklistindex+1),190+sys_scroll,480);
    }else{
    text(booknamelist.get(booklistindex+1).substring(0,5)+"...",190+sys_scroll,480);
    }  
   }
   // textmode  area
   if(entertextmode){
       if(!auto_detect){
           if(FSDK.FSDKE_OK==FSDKCam.CloseVideoCamera(CameraHandle)){
           }    
         //video.stop();
       }
       if(auto_detect){
         if(!detect_initial){
         if(FSDK.FSDKE_OK == FSDKCam.OpenVideoCamera(camera_name,CameraHandle)){
         }
           //video.start();              
           detect_initial = true;
         }
         jishu++;
         if(jishu==40){
              jishu=0;
              if(FSDK.FSDKE_OK == FSDKCam.GrabFrame(CameraHandle,Image)){     
                  detectface(Image);  
              }    
         }         
       }
       
       showtext(bookcontent,colorchoice);
   }
   

}

void detectface(HImage for_detect)
{
      if(FSDK.FSDKE_OK==FSDK.DetectFacialFeatures(for_detect,feature_for_me))
      {
          compare = (feature_for_me.features[0].y+feature_for_me.features[1].y)/2;
          if(compare>current_y&&(compare-current_y)>1){
            current_y = compare;
            ball_place = current_y;
            viewadjust-=340;
          }
          else{
            if(compare<current_y&&(current_y-compare)>5){
            current_y = compare;
            ball_place = current_y;              
            }
          }
      }
}

// 特效显示图标
void showimage(PImage a,PImage backimage,int anchorx,int anchory)
{
      int loc;
      image(a,anchorx,anchory);
      for(int i=0;i<a.width;i++)
          for(int j=0;j<a.height;j++)
            {
              loc = i+j*a.width;
              if((int)red(a.pixels[loc])+(int)red(a.pixels[loc])
              +(int)red(a.pixels[loc])==255*3){
                if((i+anchorx)>0&&(i+anchorx)<backimage.width&&(j+anchory)>0&&(j+anchory)<backimage.height){
                set(i+anchorx,j+anchory,color(backimage.pixels[i+anchorx+(j+anchory)*backimage.width]));
                }
              }
            }
}

void showtext(ArrayList<String> a,int colorcode)
{
   // show the text
   String xianshi = a.toString();
   
       int linecount = 0;
       int i = 0;
       int widthadjust = 22;
       int leftadjust = 15;
       
     //  fill(162,145,80);
     //  rect(0,0,width,height);
       
       linecount = xianshi.length()/widthadjust+1;
       for(i=0;i<linecount-1;i++){
           if(colorcode == 0){
           fill(0,0,0);
           }
           if(colorcode == 1){
           fill(255,255,255);
           }
           if(colorcode == 2){
           fill(129,115,211);
           }
           if(colorcode == 3){
           fill(120,120,120);
           }
           if(colorcode == 4){
           fill(215,240,63);
           }           
           text(xianshi.substring(widthadjust*i,widthadjust*(i+1)),leftadjust,30*(i+1)-2+viewadjust);
       }
       if(colorcode == 0){
       fill(0,0,0);
       }
       if(colorcode == 1){
       fill(255,255,255);
       }
       if(colorcode == 2){
       fill(129,115,211);
       }
       if(colorcode == 3){
       fill(120,120,120);
       }
       if(colorcode == 4){
       fill(215,240,63);
       }  
       text(xianshi.substring(widthadjust*i,xianshi.length()),leftadjust,30*(i+1)-2+viewadjust);
   
}

  // function when icon clicked
   void openicon(String filepos)
   {
        String readincontent = "";
        bookcontent = new ArrayList<String>();
           try{
              URL url = new URL("file:///"+filepos);         
              InputStreamReader isr = new InputStreamReader(url.openStream(),"gbk");
              BufferedReader br = new BufferedReader(isr); 
              while((readincontent = br.readLine())!=null){
                    bookcontent.add(readincontent);   
              }
            }    
           catch (IOException e){    
           }             
   }



  void mouseWheelSetup2()
{
    addMouseWheelListener( // the rest of of this is acutally the argument list for the function call
            new java.awt.event.MouseWheelListener() 
            {
                 public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) 
                 {
                    viewadjust -= 9*evt.getWheelRotation();
                 }
            }
   ) // this is the end of the argument list
   ;    // this single semicolon is the entire, complete function body
}   



}


void mousePressed()
{
  if(mouseX>68&&mouseY>113&&mouseX<224&&mouseY<271&&Book.enterpicmode){
    // 打开文件,纸上为打开文件热区
     new FileOpen();
  }
    // the icon open file fuunction
  // the first loadin
  if(mouseX>47&&mouseY>500&&mouseX<101&&mouseY<539){
    if(booklistindex==0){
    bookcontent = new ArrayList<String>();
    bookcontent.add("你好，这是由卷心菜编写的-悦读者-9号，请放心使用，已通过"
    +"Talkerjz4 system soft center 进入批准，非商业用途，如果能够对阁下的工作"
    +"或是生活有一些帮助，请告诉我们，卷心菜祝您天天好心情！");
    opened = true;
    }else{
     if(booklistindex==1){
     bookcontent = new ArrayList<String>();
    bookcontent.add("您好！此软件专用于阅读.txt文档，点击最大的笔记本图标"
    +"就可以选阁下电脑中的任意一个txt文档并进入阅读页面。"
    +"当然，按下Backspace（退格）键就可以回到起始页面"
    +"在下面我们提供了下拉条，按住鼠标拖动就可以华东书目栏"
    +"当看到您喜欢的图书的时候，只需点击相应小图标的正中就可以打开文件"
    +"在阅读时，请安静.....哈哈，这个看场合啦。通过键盘上的方向键可以"
    +"任意变换字体颜色及背景，最后，卷心菜祝您使用愉快！");
    opened = true;
     }
     else{ 
     Book.openicon(bookposlist.get(booklistindex));
     opened = true;
     }  
  }
  }
  // the second loadin
  if(mouseX>201&&mouseY>500&&mouseX<251&&mouseY<539){
    if(booklistindex==0){
    bookcontent = new ArrayList<String>();
    bookcontent.add("您好！此软件专用于阅读.txt文档，点击最大的笔记本图标"
    +"就可以选阁下电脑中的任意一个txt文档并进入阅读页面。"
    +"当然，按下Backspace（退格）键就可以回到起始页面"
    +"在下面我们提供了下拉条，按住鼠标拖动就可以华东书目栏"
    +"当看到您喜欢的图书的时候，只需点击相应小图标的正中就可以打开文件"
    +"在阅读时，请安静.....哈哈，这个看场合啦。通过键盘上的方向键可以"
    +"任意变换字体颜色及背景，最后，卷心菜祝您使用愉快！");
    opened = true;
    }else{
    Book.openicon(bookposlist.get(booklistindex+1));
    Book.viewadjust = 0;
    opened = true;
    }
  }
  
  Book.lastmouse = mouseX;
}


void keyPressed()
{
  if(keyCode==39){
    Book.currentpic++;
    if(Book.currentpic==136){
      Book.currentpic=1;
    }
  }
  
  if(key=='a'){
    Book.auto_detect = true;
    
  }
  if(key=='n'){
    Book.auto_detect = false;
    Book.detect_initial = false;    
  }
  
  if(key=='u'){
    TheOpenFileWay = "utf-8";
  }
  
  if(key=='g'){
    TheOpenFileWay = "gbk";
  }
  
  // choose the color
  if(keyCode==37){
    Book.currentpic--;
    if(Book.currentpic==0){
      Book.currentpic = 135;
    }
  }
  
  if(keyCode==38){
    Book.colorchoice++;
    if(Book.colorchoice==5){
      Book.colorchoice = 0;
    }
  }
  
  if(keyCode==40){
     Book.colorchoice--;
    if(Book.colorchoice==-1){
      Book.colorchoice = 4;
    }
  }
  // altering between the picmode and the textmode
  if(key=='\b'){
    if(Book.enterpicmode){
    Book.enterpicmode = false;
    Book.entertextmode = true;
    }
    else{
    Book.enterpicmode = true;
    Book.entertextmode = false;
    }
  }
  Book.change = true;
}

void mouseDragged()
{
  if(mouseY>363&&mouseY<438){
      if(booklistindex<booknamelist.size()-2&&(mouseX-Book.lastmouse)>-151&&(mouseX-Book.lastmouse)<0){
      Book.movepixel = mouseX-Book.lastmouse;
      }
      if(booklistindex>0&&(mouseX-Book.lastmouse)<151&&(mouseX-Book.lastmouse)>0){
      Book.movepixel = mouseX-Book.lastmouse;
      }
      else{
      }
  }
  else{
  }

}

void mouseReleased()
{
  if(Book.movepixel>90&&booklistindex>0){
    Book.movepixel = 150;
    booklistindex--;
  }
  else{   
      if(Book.movepixel<-90&&booklistindex<booknamelist.size()-2){
        Book.movepixel = -150;
        booklistindex++;
      }
      else{
      Book.movepixel = 0;
      }
  }
}



class FileOpen
{
  FileDialog fileopen;
  String fileopenloc;
  String filename;
  
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
    bookcontent = new ArrayList<String>();
    String readincontent = "";
    if(filename!=null&&filename.contains(".txt")){
      
          try{
          URL url = new URL("file:///"+fileopenloc+filename);         
          InputStreamReader isr = new InputStreamReader(url.openStream(),TheOpenFileWay);
          BufferedReader br = new BufferedReader(isr); 
          while((readincontent = br.readLine())!=null){
                bookcontent.add(readincontent);   
          }
          opened = true;  
          booknamelist.add(filename);
          bookposlist.add(fileopenloc+filename);
          booklistindex = booknamelist.size()-2; 
        }    
           catch (IOException e){
      	   e.printStackTrace();    
           }  
    }
    else{
    }
    
  } 

}

