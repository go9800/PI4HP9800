package HPinterface;


import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;


public class RaspiInterface
{
	int i;
	long t;
	int diVal, doVal, siVal, soVal, coVal;
	boolean ceo;

  // define wiringPI pin numbers // BCM GPIO #
  final int[] DIO = {
  		21, // GPIO 5
  		22, // GPIO 6
  		23, // GPIO 13 
  		24, // GPIO 19
  		25, // GPIO 26
  		26, // GPIO 12
  		27, // GPIO 16
  		28 // GPIO 20
  };
  
  final int[] CO = {
  		8, // GPIO 2
  		9, // GPIO 3
  		7, // GPIO 4
  		15 // GPIO 14
  };

  final int[] SO = {
  		0, // GPIO 17
  		2, // GPIO 27
  		3, // GPIO 22
  		16 // GPIO 15
  };

  final int[] SI = {
  		12, // GPIO 10
  		13, // GPIO 9
  		14, // GPIO 11
  		1 // GPIO 18
  };

  final int NCEO = 4; // GPIO 23
  final int NCFI = 5; // GPIO 24
  final int NSSI = 6; // GPIO 25
  final int NSIH = 10; // GPIO 8
  final int NSTP = 11; // GPIO 7

  final int IODIR = 29; // GPIO 21
  
  public String intToHexString(int value, int digits)
  {
    String hex_value = Integer.toHexString(value);
    return("0000000000000000".substring(16 - digits).substring(hex_value.length()) + hex_value);
  }


  public RaspiInterface()
  {
    // setup wiring pi
    if (Gpio.wiringPiSetup() == -1) {
      System.out.println("GPIO SETUP FAILED");
      return;
    }

    // export and configure all required GPIO pins
    for(i = 0; i < 8; i++ ) {
      GpioUtil.export(DIO[i], GpioUtil.DIRECTION_IN);
      Gpio.pinMode(DIO[i], Gpio.INPUT);
      Gpio.pullUpDnControl(DIO[i], Gpio.PUD_OFF); // no pull-down resistor
    }

    for(i = 0; i < 4; i++ ) {
    	GpioUtil.export(CO[i], GpioUtil.DIRECTION_IN);
      Gpio.pinMode(CO[i], Gpio.INPUT);
      Gpio.pullUpDnControl(CO[i], Gpio.PUD_OFF); // no pull-down resistor

      GpioUtil.export(SO[i], GpioUtil.DIRECTION_IN);
      Gpio.pinMode(SO[i], Gpio.INPUT);
      Gpio.pullUpDnControl(SO[i], Gpio.PUD_OFF); // no pull-down resistor

    	GpioUtil.export(SI[i], GpioUtil.DIRECTION_OUT);
      Gpio.pinMode(SI[i], Gpio.OUTPUT);
      Gpio.pullUpDnControl(SI[i], Gpio.PUD_OFF); // no pull-down resistor
    }
    
    GpioUtil.export(NCEO, GpioUtil.DIRECTION_IN);
    Gpio.pinMode(NCEO, Gpio.INPUT);
    Gpio.pullUpDnControl(NCEO, Gpio.PUD_OFF); // no pull-down resistor

    GpioUtil.export(NCFI, GpioUtil.DIRECTION_HIGH); // default NCFI high = false
    Gpio.pinMode(NCFI, Gpio.OUTPUT);
    Gpio.pullUpDnControl(NCFI, Gpio.PUD_OFF); // no pull-down resistor

    GpioUtil.export(NSSI, GpioUtil.DIRECTION_HIGH); // default NSSI high = false
    Gpio.pinMode(NSSI, Gpio.OUTPUT);
    Gpio.pullUpDnControl(NSSI, Gpio.PUD_OFF); // no pull-down resistor

    GpioUtil.export(NSIH, GpioUtil.DIRECTION_IN);
    Gpio.pinMode(NSIH, Gpio.INPUT);
    Gpio.pullUpDnControl(NSIH, Gpio.PUD_OFF); // no pull-down resistor

    GpioUtil.export(NSTP, GpioUtil.DIRECTION_IN);
    Gpio.pinMode(NSTP, Gpio.INPUT);
    Gpio.pullUpDnControl(NSTP, Gpio.PUD_OFF); // no pull-down resistor

    GpioUtil.export(IODIR, GpioUtil.DIRECTION_HIGH); // default DIO input
    Gpio.pinMode(NSSI, Gpio.OUTPUT);
    Gpio.pullUpDnControl(NSSI, Gpio.PUD_OFF); // no pull-down resistor

    while(true) { 
    	// get STP state
      if(Gpio.digitalRead(NSTP) == 0) {  // STP true?
      	System.out.println("STOP");
      	break;
      }
      
      // get CEO state
      if(Gpio.digitalRead(NCEO) == 0) {  // CEO true?
        t = System.nanoTime();

      	coVal = soVal = doVal = 0;
      	
      	for(i = 3; i >= 0; i--) {
        	coVal = coVal << 1 | Gpio.digitalRead(CO[i]);
        	soVal = soVal << 1 | Gpio.digitalRead(SO[i]);
      	}
      	
      	for(i = 7; i >= 0; i--) {
        	doVal = doVal << 1 | Gpio.digitalRead(DIO[i]);
      	}
      	
      	System.out.print(Integer.toHexString(coVal) + " "+ Integer.toHexString(soVal) + " " + Integer.toHexString(doVal) + " ");
      	
      	Gpio.digitalWrite(NCFI, 0);  // set CFI
        while(Gpio.digitalRead(NCEO) == 0);  // wait until CEO false 
      	Gpio.digitalWrite(NCFI, 1);  // clear CFI
      	
        t = System.nanoTime() - t;
        System.out.println(t);
      }
    }
  }

  public static void main(String[] args)
  {
    new RaspiInterface();
  }
}

