    /* DHTServer - ESP8266 Webserver with a DHT sensor as an input
     
       Based on ESP8266Webserver, DHTexample, and BlinkWithoutDelay (thank you)
     
       Version 1.0  5/3/2014  Version 1.0   Mike Barela for Adafruit Industries
    */
    #include <ESP8266WiFi.h>
    #include <WiFiClient.h>
    #include <ESP8266WebServer.h>
    #include <DHT.h>
    #define DHTTYPE DHT21
    #define DHTPIN  4
     
    const char* password  = "nikolaimilos";
    const char *ssid = "Milos i Nikola";
     
    ESP8266WebServer server(1001);
     
    // Initialize DHT sensor 
    // NOTE: For working with a faster than ATmega328p 16 MHz Arduino chip, like an ESP8266,
    // you need to increase the threshold for cycle counts considered a 1 or 0.
    // You can do this by passing a 3rd parameter for this threshold.  It's a bit
    // of fiddling to find the right value, but in general the faster the CPU the
    // higher the value.  The default for a 16mhz AVR is a value of 6.  For an
    // Arduino Due that runs at 84mhz a value of 30 works.
    // This is for the ESP8266 processor on ESP-01 
    DHT dht(DHTPIN, DHTTYPE, 11); // 11 works fine for ESP8266
     
    float humidity, temp_f;  // Values read from sensor
    String webString="";     // String to display
    // Generally, you should use "unsigned long" for variables that hold time
    unsigned long previousMillis = 0;        // will store last temp was read
    const long interval = 2000;              // interval at which to read sensor
    int sensorPin = A0; // light sensor, photo resistor
    String status = "";
    int sensorValue = 0;
     
    void handle_root() {
      server.send(200, "text/plain", "Hello from the weather esp8266, read from /temp or /humidity");
      delay(100);
    }
     
    void setup(void)
    {
      // You can open the Arduino IDE Serial Monitor window to see what the code is doing
      Serial.begin(115200);  // Serial connection from ESP-01 via 3.3v console cable
      dht.begin();           // initialize temperature sensor
     
      // Connect to WiFi network
      WiFi.begin(ssid, password);
      Serial.print("\n\r \n\rWorking to connect");
     
      // Wait for connection
      while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
      }
      Serial.println("");
      Serial.println("DHT Weather Reading Server");
      Serial.print("Connected to ");
      Serial.println(ssid);
      Serial.print("IP address: ");
      Serial.println(WiFi.localIP());
       
      server.on("/", handle_root);
  //---------------------------------------------------------------------------------------------------------------------------------------------------------    
           
      server.on("/temperatura=stanje", [](){  // if you add this subdirectory to your webserver call, you get text below :)
        gettemperature();       // read sensor
        webString="Temperature: ";
        webString+=temp_f;
        webString+=" *C";
       
        // Arduino has a hard time with float to string
        server.send(200, "text/plain", webString);            // send to someones browser when asked
      });
  //---------------------------------------------------------------------------------------------------------------------------------------------------------   
      server.on("/rel_vlaznost=stanje", [](){  // if you add this subdirectory to your webserver call, you get text below :)
        gettemperature();           // read sensor
        webString="Relativna vlažnost vazduha: "+String((int)humidity)+"%";
        server.send(200, "text/plain", webString);               // send to someones browser when asked
      });
  //---------------------------------------------------------------------------------------------------------------------------------------------------------    
      server.on("/osvetljenje=stanje", [](){  // if you add this subdirectory to your webserver call, you get text below :)
        gettemperature();           // read sensor
        webString="Relativno osvetljenje iznosi: "+status + "[/]";
        server.send(200, "text/plain", webString);               // send to someones browser when asked
      });
  //---------------------------------------------------------------------------------------------------------------------------------------------------------    
      server.on("/svo=stanje", [](){  // if you add this subdirectory to your webserver call, you get text below :)
        gettemperature();           // read sensor
        /*
        webString="Temperature: ";
        webString+=temp_f;
        webString+=" *C,";
        webString+="Relativna vlažnost vazduha: "+String((int)humidity)+"%,";
        webString+="Relativno osvetljenje iznosi: "+status + "[/]";
        */
        webString="";
        webString+=temp_f;
        webString+=" ";
        webString+=String((int)humidity);
        webString+=" ";
        webString+=status;
        
        server.send(200, "text/plain", webString);               // send to someones browser when asked
      });
   
      
      server.begin();
      Serial.println("HTTP server started");
    }
     
    void loop(void)
    {
      server.handleClient();
    } 
     
    void gettemperature() {
      // Wait at least 2 seconds seconds between measurements.
      // if the difference between the current time and last time you read
      // the sensor is bigger than the interval you set, read the sensor
      // Works better than delay for things happening elsewhere also
      unsigned long currentMillis = millis();
     
      if(currentMillis - previousMillis >= interval) {
        // save the last time you read the sensor 
        previousMillis = currentMillis;   
     
        // Reading temperature for humidity takes about 250 milliseconds!
        // Sensor readings may also be up to 2 seconds 'old' (it's a very slow sensor)
        humidity = dht.readHumidity();          // Read humidity (percent)
        temp_f = dht.readTemperature();     // Read temperature as Fahrenheit

        
        // Check if any reads failed and exit early (to try again).
        if (isnan(humidity) || isnan(temp_f)) {
          Serial.println("Failed to read from DHT sensor!");
          return;
        }
        //temp_f -=3;
      }
      sensorValue = analogRead ( sensorPin );
  
  if(sensorValue <= 100){
    status = "1/10";
  }
  if(sensorValue > 100 && sensorValue <= 200){
    status = "2/10";
  }
  if(sensorValue > 200 && sensorValue <= 300){
    status = "3/10";
  }
  if(sensorValue > 300 && sensorValue <= 400){
    status = "4/10";
  }
  if(sensorValue > 400 && sensorValue <= 500){
    status = "5/10";
  }
  if(sensorValue > 500 && sensorValue <= 600){
    status = "6/10";
  }
  if(sensorValue > 600 && sensorValue <= 700){
    status = "7/10";
  }
  if(sensorValue > 700 && sensorValue <= 800){
    status = "8/10";
  }
  if(sensorValue > 800 && sensorValue <= 900){
    status = "9/10";
  }
  if(sensorValue > 900){
    status = "10/10";
  }
    }
