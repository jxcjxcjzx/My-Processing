/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/14110*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
import krister.Ess.*;
import processing.opengl.*;

float a;
FFT myfft;
AudioInput myinput;
int bufferSize=512;

void setup() {
size(800,600);
frameRate(24);
background(255);
smooth();
fill(0);
strokeWeight(5);


Ess.start(this);
myinput=new AudioInput(bufferSize);
myfft=new FFT(bufferSize*2);
//   spectrum
myinput.start();

myfft.damp(.3);
myfft.equalizer(true);
myfft.limits(.025,.20);
}

void draw() {
background(255);







}


public void audioInputData(AudioInput theInput) {
myfft.getSpectrum(myinput);
}
