AODV Routing Protocol Implementation + Simple Serial Interface Communicator + Tester


AODV Implementation
- RREQs, RREPs and UserData Packets
- Listener to receive Payloads
- Parsing, Sending and Generating Messages
- Decode / Encode with Base64
- Serial Communication
- Route Management
- CLI for User interactivity

Serial Communicator Features:
- SerialManager that handles the serial communication via jSerialComm
- Simple CLI Implementation to connect and send data to a serial port
- Configurable Port with Baudrate, Databits, Stopbits, Parity and Flowcontrol
- Show data string, predefined AT Commands or a few other commands like auto config
- Check Connection Status
- Custom Line Ending (CR, LF, CRLF)

Tester Features:
- Receives and parses data from a serial port
- Answers to predefined AT Commands
  - AT
  - AT+RX
  - AT+RSSI?
  - AT+ADDR?
  - AT+VER?
  - AT+SEND=XX
  - Sending a message
- Parses commands like AT+SEND=XX and reads the following XX bytes
- Sends a predefined response to the received data
- Error Handling


Tested with PuTTY and COM0COM 

How to use:
- Create a virtual serial port pair with COM0COM
- Start the Application main class and connect to the first virtual serial port of the pair
- Start the ApplicationTester main class and connect to the other virtual serial port
- You can send data via the CLI and the tester will parse any incoming data
  - To send a message, start with AT+RX to activate the receiver mode
  - Then use AT+SEND=XX where XX is the length of the message you want to send
  - Then send the message. The Tester will parse the message and send a predefined response
