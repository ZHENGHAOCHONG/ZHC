package com.example.simplenewsystem;
import java.net.Socket;

public class SocketStatus {

  private Socket s = null;

  public Socket getSocket() {
    return this.s;
  }


  public void setSocket(Socket st) {
    this.s = st ;
  }
}
