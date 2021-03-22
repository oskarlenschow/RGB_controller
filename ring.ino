#include <Adafruit_NeoPixel.h>
#include <EEPROM.h>

#define S0 9
#define S1 6


Adafruit_NeoPixel strip0 = Adafruit_NeoPixel(16, S0, NEO_GRB + NEO_KHZ800);
Adafruit_NeoPixel strip1 = Adafruit_NeoPixel(50, S1, NEO_GRB + NEO_KHZ800);

//MODES

#define SINGLE    0
#define FADE      1
#define FLASH     2
#define TWIST     3
#define LOAD      4
#define GPU_TEMP  5
#define GPU_USAGE 6
#define HEALTH    7
#define HUE       8
#define GRADIENT  9

// PREFIXES

#define MODE       0
#define MYSPEED    1
#define INDEX      2
#define AMMOUNT    3
#define COLOR      4
#define COLORS     5
#define PERCENTAGE 6
#define HUEVALUES  7
#define TEMP       8
#define USAGE      9


int colors[8][3];
int hueColors[60][3];
int speed, ammount, hueAmmount, mode, index, readInt, temp;
float healthPercentage = 0, loadPercentage = 0;

void setup() {
  //strip0.begin();
  //strip0.show(); // Initialize all pixels to 'off'*/
  strip1.begin();
  strip1.show(); // Initialize all pixels to 'off'*/
  Serial.begin(115200);     // opens serial port, sets data rate to 9600 bps
  delay(1);
  Serial.setTimeout(1);
  //load();
  if (mode == GRADIENT) //Ugly solution, just to avoid calculating gradients again and again with no differences. Needs solution, just like displaying the same color again and again without change.
        gradient();
  setStripColor(50, 50, 50);

}

void loop() {
  while (Serial.available()) {
    readInt = Serial.read();
    delayMicroseconds(500);
    if (readInt == COLORS) {
      ammount = Serial.read();
      delay(1);
      for (int i = 0; i < ammount; i++) {
        colors[i][0] = Serial.read();
        delay(1);
        colors[i][1] = Serial.read();
        delay(1);
        colors[i][2] = Serial.read();
        delay(1);
      }
    }
    else if (readInt == MYSPEED)
      speed = (100 / Serial.read()) * 30;
    else if (readInt == MODE) 
      mode = Serial.read();
    else if (readInt == COLOR) {
      colors[index][0] = Serial.read();
      delay(1);
      colors[index][1] = Serial.read();
      delay(1);
      colors[index][2] = Serial.read();
      delay(1);
    }
    else if (readInt == INDEX)
      index = Serial.read();
    else if (readInt == AMMOUNT)
      ammount = Serial.read();
    else if (readInt == PERCENTAGE)
      healthPercentage = (float) Serial.read() / 100;
    else if (readInt == USAGE)
      loadPercentage = (float) Serial.read() / 100;
    else if (readInt == HUEVALUES) {
      hueAmmount = Serial.read();
      delayMicroseconds(500);
      for (int i = 0; i < hueAmmount; i++) {
        hueColors[i][0] = Serial.read();
        delayMicroseconds(250);
        hueColors[i][1] = Serial.read();
        delayMicroseconds(250);
        hueColors[i][2] = Serial.read();
        delayMicroseconds(250);
      }
    }
    if (mode == GRADIENT)
        gradient();
    delayMicroseconds(500);
  }
  if (mode == SINGLE)
    setStripColor(colors[index][0], colors[index][1], colors[index][2]);
  else if (mode == FLASH)
    flash();
  else if (mode == HEALTH)
    health();
  else if (mode == FADE)
    fade();
  else if (mode == GPU_USAGE)
    usage();
  else if (mode == LOAD)
    loading();
  else if (mode == HUE)
    hue();
  store();
}

void setDiodeColor(int pixel, int red, int green, int blue) {
  //pixel = (pixel + 7) % 50;
  pixel = pixel % 50;
  //strip0.setPixelColor(pixel, red, green, blue);
  strip1.setPixelColor(pixel, red, green, blue);
}
void setStripColor(int red, int green, int blue) {
  for (int i = 0; i < 50; i++) {
    setDiodeColor(i, red, green, blue);
  }
  //strip0.show();
  strip1.show();
}
void flash() {

  for (int i = 0; i < ammount; i++) {
    setStripColor(colors[i][0], colors[i][1], colors[i][2]);

    unsigned long startTime = millis();
    while (true) {
      if (millis() > startTime + speed) {
        break;
      }
      if (Serial.available()) break;
    }
  }

}
void fade() {

  for (int i = 0; i < ammount; i++) {
    double fromRed = colors[i][0];
    double fromGreen = colors[i][1];
    double fromBlue = colors[i][2];

    double toRed = colors[(i + 1) % ammount][0];
    double toGreen = colors[(i + 1) % ammount][1];
    double toBlue = colors[(i + 1) % ammount][2];

    double rDelta = (fromRed - toRed) / speed;
    double gDelta = (fromGreen - toGreen) / speed;
    double bDelta = (fromBlue - toBlue) / speed;

    for (int j = 1; j < speed; j++) {
      if (Serial.available()) return;

      int rValue = (int) (fromRed - (j * rDelta));
      int gValue = (int) (fromGreen - (j * gDelta));
      int bValue = (int) (fromBlue - (j * bDelta));

      setStripColor(rValue, gValue, bValue);

      delay(10);
    }
  }
}
void gradient() {
  int steps = 50 / (ammount - 1);
  int index = 0;

  for (int i = 0; i < (ammount - 1); i++) {
    double fromRed = colors[i][0];
    double fromGreen = colors[i][1];
    double fromBlue = colors[i][2];

    double toRed = colors[(i + 1) % ammount][0];
    double toGreen = colors[(i + 1) % ammount][1];
    double toBlue = colors[(i + 1) % ammount][2];

    double rDelta = (fromRed - toRed) / steps;
    double gDelta = (fromGreen - toGreen) / steps;
    double bDelta = (fromBlue - toBlue) / steps;

    for (int j = 0; j < steps; j++) {

      int rValue = (int) (fromRed - (j * rDelta));
      int gValue = (int) (fromGreen - (j * gDelta));
      int bValue = (int) (fromBlue - (j * bDelta));

      setDiodeColor(j + (index * steps), rValue, gValue, bValue);

      strip0.show();
      strip1.show();

    }
    index++;
  }

}
void loading() {
  for (int i = 0; i < 50; i++) {
    setDiodeColor(i, 0, 0, 0);
    setDiodeColor((i + 1) % 50, colors[index][0] * 0.05, colors[index][1] * 0.05, colors[index][2] * 0.05);
    setDiodeColor((i + 2) % 50, colors[index][0] * 0.4, colors[index][1] * 0.4, colors[index][2] * 0.4);
    setDiodeColor((i + 3) % 50, colors[index][0], colors[index][1], colors[index][2]);

    unsigned long startTime = millis();
    while (true) {
      if (millis() > startTime + speed) {
        break;
      }
      if (Serial.available()) break;
    }
    strip0.show();
    strip1.show();
  }
}
void health() {
  int health = healthPercentage * 50;
  int i = 0;
  for (i; i < health; i++) {
    setDiodeColor(i, colors[0][0], colors[0][1], colors[0][2]);
  }
  for (i; i < 50; i++) {
    setDiodeColor(i, colors[1][0], colors[1][1], colors[1][2]);
  }

  strip0.show();
  strip1.show();
}

void usage() {
  int load = loadPercentage * 50;
  int i = 0;
  for (i; i < load; i++) {
    setDiodeColor(i, colors[1][0], colors[1][1], colors[1][2]);
  }
  for (i; i < 50; i++) {
    setDiodeColor(i, colors[0][0], colors[0][1], colors[0][2]);
  }

  strip0.show();
  strip1.show();
}
void hue() {
  for (int i = 0; i < hueAmmount; i++) {
    setDiodeColor(i, hueColors[i][0], hueColors[i][1], hueColors[i][2]);
    //setDiodeColor(i, 255, 0, 0);
  }
  //strip0.show();
  strip1.show();
}

void store() {
  int storeMode;
  if (mode == HUE || mode == GPU_TEMP || mode == GPU_USAGE || mode == HEALTH)
    storeMode = 0;
  else
    storeMode = mode;
  EEPROM.write(0, storeMode);
  EEPROM.write(1, index);
  EEPROM.write(2, ammount);
  EEPROM.write(3, speed);
  int index = 4;
  for (int i = 0; i < 8; i++) {
    for (int j = 0; j < 3; j++) {
      EEPROM.write(index, colors[i][j]);
      index++;
    }
  }
}
void load() {
  mode = EEPROM.read(0);
  index = EEPROM.read(1);
  ammount = EEPROM.read(2);
  speed = EEPROM.read(3);
  int index = 4;
  for (int i = 0; i < 8; i++) {
    for (int j = 0; j < 3; j++) {
      colors[i][j] = EEPROM.read(index);
      index++;
    }
  }
}


