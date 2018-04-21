

import java.awt.*;
import Luxand.*;
import Luxand.FSDK.*;
import Luxand.FSDKCam.*;

MiniBox manager = new MiniBox();
Robot robot  = null;
boolean auto_detect = false;
boolean detect_initial = false;

void setup() {
  size(200,630);
  fill(227,97,37);
  rect(20,20,width-40,height-40);
  manager.handle_setup();
  try {
      robot = new Robot();
    } catch (AWTException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
}

void mousePressed()
{
  if(auto_detect == false){
     auto_detect= true;
  }
  else{
      auto_detect = false;
      detect_initial = false; 
  }
}

void draw() {
    manager.handle_draw();
}
void testit()
{  
   robot.mouseWheel(1);
}


class MiniBox
{
  
  int jishu = 0;
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
  
  
    void handle_setup()
    {
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
      
    }
    
    void handle_draw()
    {
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
    }

void detectface(HImage for_detect)
{
      if(FSDK.FSDKE_OK==FSDK.DetectFacialFeatures(for_detect,feature_for_me))
      {
          compare = (feature_for_me.features[0].y+feature_for_me.features[1].y)/2;
          if(compare>current_y&&(compare-current_y)>1){
            current_y = compare;
            ball_place = current_y;
            testit();
          }
          else{
            if(compare<current_y&&(current_y-compare)>5){
            current_y = compare;
            ball_place = current_y;              
            }
          }
      }
}

}

