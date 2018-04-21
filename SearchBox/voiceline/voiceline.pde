import ddf.minim.*;
import peasy.*;
import krister.Ess.FFT;

krister.Ess.AudioInput myinput;
FFT myfft;
int bufferSize=512;
boolean drawit = false;
boolean fallen = false;


void setup() {

  background(255);
  frameRate(30);
  size(1200, 300);
  Ess.start(this);
  myinput=new krister.Ess.AudioInput(bufferSize);
  myfft=new FFT(bufferSize*2);
  //   spectrum
  myinput.start();

  myfft.damp(.3);
  myfft.equalizer(true);
  myfft.limits(.025,.20);
}


void draw()
{
  background(21, 90, 210);
  /*
  for (int i=0;i<3;i++)
    for (int j=0;j<3;j++) {
      fill(120, 120, 120);
      rect(20+j*100, 20+i*100, 60, 60);
      fill(255, 255, 255);
      text((i)*3+(j+1), 46+j*100, 55+i*100);
    }
*/
  if (drawit) {
    background(120, 120, 120);
    for (int j=0;j<512;j++) {
      fill(255, 255, 255);
      ellipse(20+j*2, height-30-(myfft.spectrum[j]/myfft.max)*height, 5, 5);
    }
  }

  if (fallen) {   // get one voice input 
    while (myinput.buffer[0]<0.01) {
    }


    myfft.getSpectrum(myinput);
    
    //  for(int i=0;i<myfft.spectrum.length;i++){

    // }
    //println(myfft.spectrum.length);
    drawit = true;
    fallen = false;
  }
}

/*
void handle(ArrayList<Float> a)
 {
 String b[] = new String[a.size()];
 for(int i=0;i<b.length;i++){
 b[i] = String.valueOf(a.get(i));
 }
 saveStrings(String.valueOf(xuanze),b);
 println("Saved it to file ; "+String.valueOf(xuanze));
 }
 
 
 int parse(ArrayList<Float> c)
 {
 // now the kernel fucntion springs
 int jilu = 1;
 float zonghe = 10000.0; 
 float back = 10000.0;
 int count = 0;
 for(count=1;count<10;count++){
 zonghe=0.0;
 String d[] = loadStrings(String.valueOf(count));
 float e[] = new float[d.length];
 for(int i=0;i<e.length;i++){
 e[i] = new Float(0.01).parseFloat(d[i]);
 } 
 for(int j=0;j<e.length;j++){
 if((c.get(j)*j-e[j]*j)>0.0)
 zonghe+=c.get(j)*j-e[j]*j;
 else
 zonghe-=c.get(j)*j-e[j]*j;
 }
 if(zonghe<back){
 back=zonghe;
 jilu = count;
 }
 }
 
 return jilu;
 }
 
 void adjust(int result,ArrayList<Float> f)
 {
 if(result==xuanze){    
 println("Right away");
 }
 else{
 // adjust to the wrong side
 String d[] = loadStrings(String.valueOf(result));
 float e[] = new float[d.length];
 for(int i=0;i<e.length;i++){
 e[i] = new Float(0.01).parseFloat(d[i]);
 } 
 for(int i=0;i<e.length;i++){
 e[i] -= exp((f.get(i)-e[i])*0.3);
 }
 String store[] = new String[e.length];
 for(int i=0;i<e.length;i++){
 store[i] = String.valueOf(e[i]);
 }
 saveStrings(String.valueOf(result),store);
 println("Saved it to file ; "+String.valueOf(result));
 
 //  adjust to the right side
 d = loadStrings(String.valueOf(xuanze));
 e = new float[d.length];
 for(int i=0;i<e.length;i++){
 e[i] = new Float(0.01).parseFloat(d[i]);
 } 
 for(int i=0;i<e.length;i++){
 e[i] += exp((f.get(i)-e[i])*30);
 }
 store = new String[e.length];
 for(int i=0;i<e.length;i++){
 store[i] = String.valueOf(e[i]);
 }
 saveStrings(String.valueOf(xuanze),store);
 println("Saved it to file ; "+String.valueOf(xuanze));      
 }
 }
 
 
 
 void clear()
 {
 for(int i=0;i<7;i++)
 jishu[i]=0;
 }
 
 */

void mousePressed()
{
  int heng = mouseX/100;
  int shu = mouseY/100;
  fallen = true;
  drawit = false;
}

