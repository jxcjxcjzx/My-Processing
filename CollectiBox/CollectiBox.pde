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




ArrayList<String> pasecontent = new ArrayList<String>();
boolean firstpass = false;
boolean secondpass = false;
paster pase = new paster();
int jishu = 0;
String TheOpenFileWay = "gbk";

void setup()
{
  size(700, 600);
  pasecontent.add("I am the first one");
}

void draw()
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

void mousePressed()
{
  jishu=mouseY/20;
  if(pasecontent.size()>=(jishu)&&jishu>0){
      pase.handle(pasecontent.get(jishu-1)); 
      pasecontent.remove(jishu-1); 
  }
}

void keyPressed()
{
  if (keyCode==17) {
    firstpass = true;
  }
  if (keyCode==86) {
    secondpass = true;
  }
  if(key=='o'){
    new FileOpen();
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
          e.printStackTrace();
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
              e.printStackTrace();
            }
       }
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
    String readincontent = "";
    if(filename!=null&&filename.contains(".txt")){
      
          try{
          URL url = new URL("file:///"+fileopenloc+filename);         
          InputStreamReader isr = new InputStreamReader(url.openStream(),TheOpenFileWay);
          BufferedReader br = new BufferedReader(isr); 
          while((readincontent = br.readLine())!=null){
               pasecontent.add(readincontent);   
          }
        }    
           catch (IOException e){
             e.printStackTrace();    
           }  
    }
    else{
    }
    
  } 

}

