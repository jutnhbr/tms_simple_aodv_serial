Simple Serial Interface Communicator + Tester

Current Features:
- SerialManager that handles the serial communication via jSerialComm
- Simple CLI Implementation to connect and send data to a serial port
- Configurable Port with Baudrate, Databits, Stopbits, Parity and Flowcontrol
- Show data string, predefined AT Commands or a few other commands like auto config
- Check Connection Status
- Custom Line Ending (CR, LF, CRLF)

Tester Features:
- Receives and parses data from a serial port
- Answers to predefined AT Commands
- Error Handling
- Parses commands like AT+SEND=XX and reads the following XX bytes
- Sends a predefined response to the received data

Tested with PuTTY and COM0COM 

How to use:
- Create a virtual serial port pair with COM0COM
- Start the Application main class and connect to the first virtual serial port of the pair
- Start the ApplicationTester main class and connect to the other virtual serial port
- You can send data via the CLI and the tester will parse any incoming data