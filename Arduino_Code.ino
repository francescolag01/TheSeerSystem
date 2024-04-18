/*
 * created by Rui Santos, https://randomnerdtutorials.com
 * 
 * Complete Guide for Ultrasonic Sensor HC-SR04
 *
    Ultrasonic sensor Pins:
        VCC: +5VDC
        Trig : Trigger (INPUT) - Pin11
        Echo: Echo (OUTPUT) - Pin 12
        GND: GND
 */


int trigPin1 = 3;    // Trigger of first sensor
int echoPin1 = 2;    // Echo of first sensor
int trigPin2 = 5;
int echoPin2 = 4;
// long vs int could be reason for negative values?
// also seems to stop after short duration of being powered on
long duration1, inches1, duration2, inches2;

 
void setup() {
  //Serial Port begin

  Serial.begin (9600);

  //Define inputs and outputs

  pinMode(trigPin1, OUTPUT);
  pinMode(echoPin1, INPUT);
  pinMode(trigPin2, OUTPUT);
  pinMode(echoPin2, INPUT);
}
 
void loop() {
  // The sensor is triggered by a HIGH pulse of 10 or more microseconds.
  // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:

  digitalWrite(trigPin1, LOW);
  delayMicroseconds(5);
  digitalWrite(trigPin1, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin1, LOW);
  pinMode(echoPin1, INPUT);
  duration1 = pulseIn(echoPin1, HIGH);
  inches1 = (duration1/2) / 74; 

  digitalWrite(trigPin2, LOW);
  delayMicroseconds(5);
  digitalWrite(trigPin2, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin2, LOW);
  pinMode(echoPin2, INPUT);
  duration2 = pulseIn(echoPin2, HIGH);
  inches2 = (duration2/2) / 74;
 
  // Read the signal from the sensor: a HIGH pulse whose
  // duration is the time (in microseconds) from the sending
  // of the ping to the reception of its echo off of an object.

  if ((inches1 < 0) || (inches2 < 0)) {
    Serial.print("--------");
  }
  else {

    if (inches1 < 56) {
      Serial.print("o");
      if (inches1 < 10) {
        Serial.print("00");
      }
      else {
        Serial.print("0");
      }
      Serial.print(inches1);
      }

    else {
      Serial.print("f");
      if (inches1 < 100) {
        Serial.print("0");
      }
      Serial.print(inches1);
    }
    

    if (inches2 < 56) {
      Serial.print("o");
      if (inches2 < 10) {
        Serial.print("00");
      }
      else {
        Serial.print("0");
      }
      Serial.print(inches2);
    }

    else {
      Serial.print("f");
      if (inches2 < 100) {
        Serial.print("0");
      }
      Serial.print(inches2);
    }
  }
  
  
  delay(250);

  Serial.println();

}