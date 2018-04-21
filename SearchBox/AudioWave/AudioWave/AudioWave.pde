/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/16907*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
import ddf.minim.*;
import peasy.*;
import krister.Ess.*;
PeasyCam cam;

Minim minim;
krister.Ess.AudioInput audioInput;

String name_train[] = {"hello.txt"};
int count_line[] = {10};

ArrayList<Float> sample = new ArrayList<Float>();

int one = 0;
int two = 0;
int three = 0;
int four = 0;
int five = 0;
int six = 0;
int seven = 0;


float lineWidth = 2;
float rotateaa  = 0;


void setup() {

  background(255);
  frameRate(30);
  size(400, 400, P3D);
  minim = new Minim(this);
  minim.debugOn();
  Ess.start(this);
  audioInput = minim.getLineIn(minim.MONO);
}

void keyPressed()
{
      FFT myfft;
    int bufferSize=512;

myinput=new AudioInput(bufferSize);
myfft=new FFT(bufferSize*2);
myinput.start();

myfft.damp(.3);
myfft.equalizer(true);
myfft.limits(.025,.20);
myfft.getSpectrum(audioInput);
}

void draw() {


  background(0);
  strokeWeight(2);
  stroke(128, 128, 128, 128);



  for (int i = 0; i <=width; i++) {
    if (audioInput.left.get(i)>0.07)
      seven++;
    if (audioInput.left.get(i)>0.06&&audioInput.left.get(i)<0.07)
      six++;
    if (audioInput.left.get(i)>0.05&&audioInput.left.get(i)<0.06)
      five++;
    if (audioInput.left.get(i)>0.04&&audioInput.left.get(i)<0.05)
      four++;
    if (audioInput.left.get(i)>0.03&&audioInput.left.get(i)<0.04)
      three++;
    if (audioInput.left.get(i)>0.02&&audioInput.left.get(i)<0.03)
      two++;
    if (audioInput.left.get(i)>0.01&&audioInput.left.get(i)<0.02)
      one++;
  }
  if ((float)one/width!=0) {
     println((float)one/width+","+(float)two/width+","+(float)three/width+","+(float)four/width+","+(float)five/width+","+(float)six/width+","+(float)seven/width);
    sample.add((float)one/width);
    sample.add((float)two/width);
    sample.add((float)three/width);
    sample.add((float)four/width);
    sample.add((float)five/width);
    sample.add((float)six/width);
    sample.add((float)seven/width);
  }
  clear();
  if (sample.size()>count_line[0]*7-1) {
    //String recognize = detectvoice("1.txt",7,0.05);
    //println(recognize);

    
    sample.clear();
  }
}

void clear()
{
  one = two = three=four=five=six=seven=0;
}

String detectvoice(String trainname,int linecount,float thresh)
{
  float total = 0.0;
  float total2 = 0.0;
  String forreturn = "";
  String hello[] = loadStrings(trainname);
  Float hellofloat[] = new Float[linecount*7];
  for (int i=0;i<linecount;i++) {
    String fenge[] = hello[i].split(",");
    for (int j=0;j<7;j++) {
      // println(Float.parseFloat(fenge[j]));
      hellofloat[j+i*7] = Float.parseFloat(fenge[j]);
      total+=hellofloat[j+i*7];
    }
  }
  for (int k=0;k<linecount*7;k++) {  
    total2+=sample.get(k);
  }
  float adjust = (total2-total)/(linecount*7);
  float zonghe = 0.0;

  for (int k=0;k<linecount*7;k++) {
    if (hellofloat[k]>0.03) {
      hellofloat[k] -= adjust*2;
    }
    if((hellofloat[k].floatValue()*k-k*sample.get(k))>0.0){
      zonghe += (hellofloat[k].floatValue()*k-k*sample.get(k));
    }
    if((hellofloat[k].floatValue()*k-k*sample.get(k))<0.0){     
       zonghe -= (hellofloat[k].floatValue()*k-k*sample.get(k));
    }
    if (zonghe<thresh){
      forreturn = String.valueOf(zonghe);
    }
    if(zonghe>thresh&&sample.get(0)>0.0){
       // training... 
       for (int kk=0;kk<linecount*7;kk++) {
         hellofloat[kk] += (sample.get(kk))*0.3;
         hello[kk] = String.valueOf(hellofloat[kk]);
       }
       print("training...");
       saveStrings(trainname,hello);
    }
  }
  return forreturn;
}




