#include <WiFi.h>
#include <WiFiClientSecure.h> 
#include <TinyGPS++.h>
#include <OneWire.h>
#include <NewPing.h>
#include <HardwareSerial.h>  

#define TRIGGER_PIN  22  
#define ECHO_PIN     23  
#define MAX_DISTANCE 200                                   // sensor max rated at 400-500cm.


const char* ssid = "";                                   //"Om Kalthom 2";       //"BFCI";
const char* password = "";                              //"01010204060";     //"1234QWER@";

TinyGPSPlus gps;                                       // The TinyGPS++ object.
HardwareSerial SerialGPS ( 1 );
int distance = 0;
float lat= 0, lng = 0;
float celsius = 0 , fahrenheit = 0;
int ResponseCode = 0;
unsigned long lastTime = 0;                                               
unsigned long timerDelay = 5000;                    // Set timer to 5 seconds (5000)
float sensorLocData [2];
float sensorTempData[2]; 
int _time [3];
int _date [3];
                // https://healthcareeee.000webhostapp.com/new.php?
                //Human_temp=50&latitude=30.9987655&longitude=30.965355353&time=12%3A45&date=2020-11-8

const char* serverName = "https://healthcareeee.000webhostapp.com/new.php?";
 
char *host = "healthcareeee.000webhostapp.com"; 
char *uri = "/new.php?";

char* root_ca = 
\
"-----BEGIN CERTIFICATE-----\n"\
"MIIEsTCCA5mgAwIBAgIQCKWiRs1LXIyD1wK0u6tTSTANBgkqhkiG9w0BAQsFADBh\n" \
"MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\n" \
"d3cuZGlnaWNlcnQuY29tMSAwHgYDVQQDExdEaWdpQ2VydCBHbG9iYWwgUm9vdCBD\n" \
"QTAeFw0xNzExMDYxMjIzMzNaFw0yNzExMDYxMjIzMzNaMF4xCzAJBgNVBAYTAlVT\n" \
"MRUwEwYDVQQKEwxEaWdpQ2VydCBJbmMxGTAXBgNVBAsTEHd3dy5kaWdpY2VydC5j\n" \
"b20xHTAbBgNVBAMTFFJhcGlkU1NMIFJTQSBDQSAyMDE4MIIBIjANBgkqhkiG9w0B\n" \
"AQEFAAOCAQ8AMIIBCgKCAQEA5S2oihEo9nnpezoziDtx4WWLLCll/e0t1EYemE5n\n" \
"+MgP5viaHLy+VpHP+ndX5D18INIuuAV8wFq26KF5U0WNIZiQp6mLtIWjUeWDPA28\n" \
"OeyhTlj9TLk2beytbtFU6ypbpWUltmvY5V8ngspC7nFRNCjpfnDED2kRyJzO8yoK\n" \
"MFz4J4JE8N7NA1uJwUEFMUvHLs0scLoPZkKcewIRm1RV2AxmFQxJkdf7YN9Pckki\n" \
"f2Xgm3b48BZn0zf0qXsSeGu84ua9gwzjzI7tbTBjayTpT+/XpWuBVv6fvarI6bik\n" \
"KB859OSGQuw73XXgeuFwEPHTIRoUtkzu3/EQ+LtwznkkdQIDAQABo4IBZjCCAWIw\n" \
"HQYDVR0OBBYEFFPKF1n8a8ADIS8aruSqqByCVtp1MB8GA1UdIwQYMBaAFAPeUDVW\n" \
"0Uy7ZvCj4hsbw5eyPdFVMA4GA1UdDwEB/wQEAwIBhjAdBgNVHSUEFjAUBggrBgEF\n" \
"BQcDAQYIKwYBBQUHAwIwEgYDVR0TAQH/BAgwBgEB/wIBADA0BggrBgEFBQcBAQQo\n" \
"MCYwJAYIKwYBBQUHMAGGGGh0dHA6Ly9vY3NwLmRpZ2ljZXJ0LmNvbTBCBgNVHR8E\n" \
"OzA5MDegNaAzhjFodHRwOi8vY3JsMy5kaWdpY2VydC5jb20vRGlnaUNlcnRHbG9i\n" \
"YWxSb290Q0EuY3JsMGMGA1UdIARcMFowNwYJYIZIAYb9bAECMCowKAYIKwYBBQUH\n" \
"AgEWHGh0dHBzOi8vd3d3LmRpZ2ljZXJ0LmNvbS9DUFMwCwYJYIZIAYb9bAEBMAgG\n" \
"BmeBDAECATAIBgZngQwBAgIwDQYJKoZIhvcNAQELBQADggEBAH4jx/LKNW5ZklFc\n" \
"YWs8Ejbm0nyzKeZC2KOVYR7P8gevKyslWm4Xo4BSzKr235FsJ4aFt6yAiv1eY0tZ\n" \
"/ZN18bOGSGStoEc/JE4ocIzr8P5Mg11kRYHbmgYnr1Rxeki5mSeb39DGxTpJD4kG\n" \
"hs5lXNoo4conUiiJwKaqH7vh2baryd8pMISag83JUqyVGc2tWPpO0329/CWq2kry\n" \
"qv66OSMjwulUz0dXf4OHQasR7CNfIr+4KScc6ABlQ5RDF86PGeE6kdwSQkFiB/cQ\n" \
"ysNyq0jEDQTkfa2pjmuWtMCNbBnhFXBYejfubIhaUbEv2FOQB3dCav+FPg5eEveX\n" \
"TVyMnGo=\n" \
"-----END CERTIFICATE-----\n";

OneWire  ds(2); 
void initNet();
int sendData(String requestData, char* host, int port);                                               
void getLoc();
void getTempC_F();
int getDistance();
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); 
                                            
void setup() {
  Serial.begin(115200); 
  SerialGPS. begin ( 9600 , SERIAL_8N1, 16 , 17 );
  initNet();
}

void loop() {
  //  Checking wireless network connection
    if(WiFi.status() != WL_CONNECTED) {
         initNet();
      }
    // Reading Sensors Data  
    getTempC_F();
    Serial.print(getDistance());     
    Serial.println("cm");
    getLoc();
    
    sensorLocData [0]= lng;
    sensorLocData [1]= lat;
    sensorTempData[0]= celsius;
    sensorTempData[1]= fahrenheit;
    _time[0]= gps.time.hour();
    _time[1]= gps.time.minute();
    _time[2]= gps.time.second();
    _date[0]= gps.date.day();
    _date[1]= gps.date.month();
    _date[2]= gps.date.year();
    
    
//  Human_temp=40&latitude=30.784451&longitude=30.997881&time=1%3A30&date=2020-8-5
    String reqData = "Human_temp="+ String(celsius,4)+"&latitude="+
                     String(lat,6)+"&longitude="+String(lng,6)+"&time=12%3A45&date=2020-11-8"
                     "&time="+String( _time[0],2)+":"+String( _time[1],2)+
                             ":"+String( _time[2],2)+"&date="+String( _date[1],2)+"/"+
                             String(_date[0],2)+"/"+String(_date[2],4);
                             
//                    
    
    if (getDistance()> 10 || getDistance()== 0 || celsius > 32 && gps.time.isValid()){
        Serial.println ("Trying to send an Alert Msg !!");
        ResponseCode = sendData(reqData , host, 443);
        if (ResponseCode == 200){
        Serial.print("Alert Sent Successfully !!  ");              
        Serial.println(ResponseCode );
      }
        else if (ResponseCode != 200){
        Serial.print("Failed to send Alert !! ");                 
        Serial.println(ResponseCode);
        }
 }
    
    
}

void initNet(){
  WiFi.begin(ssid, password);
  Serial.println("Connecting !!!");
  Serial.println("");
  Serial.print("Connected to : ");
  Serial.println(WiFi.SSID());
  Serial.print ("with IP Address: ");
  Serial.println(WiFi.localIP());
  Serial.print("ESP Board MAC Address:  ");
  Serial.println(WiFi.macAddress());
  Serial.print(F("Status: ")); 
  Serial.println(WiFi.status());
  Serial.println("Timer set to 5 seconds (timerDelay variable), it will take 5 seconds before publishing the first reading.");  
}

int sendData(String requestData, char *host ,int port){
  int httpResponseCode= 0;                                     
  String line ="";
  WiFiClientSecure client;                                                                
  if(WiFi.status()== WL_CONNECTED){
    
     Serial.print("Connecting to ");
     Serial.println(host);
     
     client.setCACert(root_ca);
     while (!client.connect(host,port)) { 
        Serial.println("Connection failed."); 
        Serial.println("Waiting 5 seconds before retrying..."); 
        delay(5000);  
      }  

     String body = requestData;
//     String getRequest = "GET " + String(uri) + " HTTP/1.1\r\n" +
//     "Host: " + host + "\r\n" + body; 
     client.print(String("GET ") + uri + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +requestData);
     //Serial.println(getRequest); 
     //client.print(getRequest);
     client.println("Connection: close");
     client.println();
           
      while (client.connected()) {
        line = client.readStringUntil('\n');
        if (line == "\r") {
          Serial.println("headers received");
          break;
        }
      }
      if (line != "" || line != "\n"){
          httpResponseCode = line.toInt(); 
        }
      client.stop();  
      Serial.println(); 
      Serial.println("***DONE***"); 
      Serial.println(); 
      delay(6000); 
      
      }  
          
 return httpResponseCode; 
}

void getLoc(){  
 while(SerialGPS. available ()> 0 ) {
    gps. encode (SerialGPS. read ());
  }
    lat = gps.location.lat();
    lng = gps.location.lng();
    if (gps.location.isValid())
  {
    Serial.print("Latitude: ");
    Serial.println(gps.location.lat(), 6);
    Serial.print("Longitude: ");
    Serial.println(gps.location.lng(), 6);
    Serial.print("Altitude: ");
    Serial.println(gps.altitude.meters());
  }
  else
  {
    Serial.println("Location: Not Available");
  }
  
  Serial.print("Date: ");
  if (gps.date.isValid())
  {
    Serial.print(gps.date.month());
    Serial.print("/");
    Serial.print(gps.date.day());
    Serial.print("/");
    Serial.println(gps.date.year());
  }
  else
  {
    Serial.println("Not Available");
  }

  Serial.print("Time: ");
  if (gps.time.isValid())
  {
    if (gps.time.hour() < 10) Serial.print(F("0"));
    Serial.print(gps.time.hour());
    Serial.print(":");
    if (gps.time.minute() < 10) Serial.print(F("0"));
    Serial.print(gps.time.minute());
    Serial.print(":");
    if (gps.time.second() < 10) Serial.print(F("0"));
    Serial.print(gps.time.second());
    Serial.print(".");
    if (gps.time.centisecond() < 10) Serial.print(F("0"));
    Serial.println(gps.time.centisecond());
  }
  else
  {
    Serial.println("Not Available");
  }

  Serial.println();
  Serial.println();
  delay(1000);
}

void getTempC_F(){
  
    byte i;
    byte present = 0;
    byte type_s;
    byte data[12];
    byte addr[8];
 
    if ( !ds.search(addr)){
        ds.reset_search();
        delay(250);
        return;
    }
 
    if (OneWire::crc8(addr, 7) != addr[7]){
        Serial.println("CRC is not valid!");
        return;
    }
                                      // the first ROM byte indicates which chip
    switch (addr[0]){
       case 0x10:
           type_s = 1;
           break;
       case 0x28:
           type_s = 0;
           break;
       case 0x22:
           type_s = 0;
           break;
       default:
           Serial.println("Device is not a DS18x20 family device.");
           return;
       }
       
    ds.reset();
    ds.select(addr);
    ds.write(0x44, 1);                            // start conversion, with parasite power on at the end
    delay(1000);
    present = ds.reset();
    ds.select(addr);
    ds.write(0xBE);                               // Read Scratchpad
 
    for ( i = 0; i < 9; i++){
        data[i] = ds.read();
    }
                                                  // Convert the data to actual temperature
   int16_t raw = (data[1] << 8) | data[0];
  if (type_s) {
    raw = raw << 3;                              // 9 bit resolution default
    if (data[7] == 0x10){
      raw = (raw & 0xFFF0) + 12 - data[6];
    }
  }
  else{
    byte cfg = (data[4] & 0x60);
    if (cfg == 0x00) raw = raw & ~7;                      // 9 bit resolution, 93.75 ms
    else if (cfg == 0x20) raw = raw & ~3;                 // 10 bit res, 187.5 ms
    else if (cfg == 0x40) raw = raw & ~1;                 // 11 bit res, 375 ms
   }
   
    celsius = (float)raw / 16.0;
    fahrenheit = celsius * 1.8 + 32.0;
    Serial.print("Temperature = ");
    Serial.print(celsius);
    Serial.print(" Celsius, ");
    Serial.print(fahrenheit);
    Serial.println(" Fahrenheit");
   
  }

int getDistance(){
  
  return sonar.ping_cm();
}
