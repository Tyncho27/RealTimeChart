// Arduino code is available to download - link below the video

/* Vibration sensor connected to Arduino pins as follows:
 Arduino            Vibration Sensor
   D9                    DOut
   GND                   GND
   +5V                   VCC     
   
   D13                Indication LED   
*/

int ledPin = 13;
int EP =5;

void setup(){
  pinMode(ledPin, OUTPUT);
  pinMode(EP, INPUT); //set EP input for measurment
  Serial.begin(9600); //init serial 9600
  Serial.println("----------------------Vibration demo------------------------");
}
void loop(){
  long measurement =TP_init();
  delay(50);
  Serial.print("measurment = ");
  Serial.println(measurement);
  
}

long TP_init(){
  delay(10);
  long measurement=pulseIn (EP, HIGH);  //wait for the pin to get HIGH and returns measurement
  return measurement;
}
