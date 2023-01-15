package com.gakk.noorlibrary.util;

import java.util.Vector;

public class BrainFuska {

  private static class Node<T>{
    private T value;
    private Node<T> previous;
    private Node<T> next;

    Node(T value){
      this.set(value);
      this.setPrevious(null);
      this.setNext(null);
    }

    void set(T value){
      this.value = value;
    }
    void setPrevious(Node<T> node){
      this.previous = node;
    }
    void setNext(Node<T> node){
      this.next = node;
    }

    T getValue(){
      return this.value;
    }
    Node<T> getNext(){
      return this.next;
    }
    Node<T> getPrevious(){
      return this.previous;
    }
  }

  private static final char COMMAND_INCREMENT_POINTER = '>';
  private static final char COMMAND_DECREMENT_POINTER = '<';
  private static final char COMMAND_INCREMENT_DATA    = '+';
  private static final char COMMAND_DECREMENT_DATA    = '-';
  private static final char COMMAND_WRITE_DATA        = '.';
  private static final char COMMAND_READ_DATA         = ',';
  private static final char COMMAND_JUMP_FORWARD      = '[';
  private static final char COMMAND_JUMP_BACK         = ']';

  private Node<Character> commands;
  private Node<Byte> tape;

  public BrainFuska(Vector<Character> source){
    Vector<Character> tokens = this.tokenize(source);
    this.commands = this.parse(tokens);
    this.tape = new Node<>((byte)0);
  }

  private Vector<Character> tokenize(Vector<Character> source){
    Vector<Character> tokens = new Vector<>();
    for(int i = 0; i < source.size(); i++){
      char s = source.get(i);
      if(s == COMMAND_INCREMENT_POINTER || s == COMMAND_DECREMENT_POINTER ||
         s == COMMAND_INCREMENT_DATA    || s == COMMAND_DECREMENT_DATA    ||
         s == COMMAND_WRITE_DATA        || s == COMMAND_READ_DATA         ||
         s == COMMAND_JUMP_FORWARD      || s == COMMAND_JUMP_BACK)
        tokens.add(s);
    }
    return tokens;
  }

  private Node<Character> parse(Vector<Character> tokens){
    Node<Character> head = null;
    Node<Character> tail = null;
    for(int i = 0; i < tokens.size(); i++){
      Character c = tokens.get(i);
      Node<Character> newbie = new Node<>(c);
      if(tail == null){
        head = newbie;
      }else{
        tail.setNext(newbie);
        newbie.setPrevious(tail);
      }
      tail = newbie;
    }
    return head;
  }

  public String evaluate() throws Exception{
    StringBuilder stringBuilder = new StringBuilder();

    //this.dump();
    Node<Character> command = this.commands;
    while(command != null){
      switch(command.getValue()){
        case COMMAND_INCREMENT_POINTER:
          Node<Byte> next = this.tape.getNext();
          if(next == null){
            next = new Node<>((byte)0);
            this.tape.setNext(next);
            next.setPrevious(this.tape);
          }
          this.tape = next;
          break;
        case COMMAND_DECREMENT_POINTER:
          Node<Byte> previous = this.tape.getPrevious();
          if(previous == null){
            previous = new Node<>((byte)0);
            this.tape.setPrevious(previous);
            previous.setNext(this.tape);
          }
          this.tape = previous;
          break;
        case COMMAND_INCREMENT_DATA:
          byte v1 = (byte)(this.tape.getValue() + 1);
          this.tape.set(v1);
          break;
        case COMMAND_DECREMENT_DATA:
          byte v2 = (byte)(this.tape.getValue() - 1);
          this.tape.set(v2);
          break;
        case COMMAND_WRITE_DATA:
          byte v3 = this.tape.getValue();
          stringBuilder.append((char)v3);
          break;
        case COMMAND_READ_DATA:
          byte v4 = (byte)System.in.read();
          this.tape.set(v4);
          break;
        case COMMAND_JUMP_FORWARD:
          if(this.tape.getValue() == 0){
            int nest = 0;
            while(command != null){
              command = command.getNext();
              if(command != null){
                if(command.getValue() == COMMAND_JUMP_FORWARD){
                  ++nest;
                }else if(command.getValue() == COMMAND_JUMP_BACK){
                  if(nest == 0)
                    break;
                  else
                    --nest;
                }
              }else{
                throw new Exception("Error: Couldn't find matching ']'");
              }
            }
          }
          break;
        case COMMAND_JUMP_BACK:
          if(this.tape.getValue() != 0){
            int nest = 0;
            while(command != null){
              command = command.getPrevious();
              if(command != null){
                if(command.getValue() == COMMAND_JUMP_BACK){
                  ++nest;
                }else if(command.getValue() == COMMAND_JUMP_FORWARD){
                  if(nest == 0)
                    break;
                  else
                    --nest;
                }
              }else{
                throw new Exception("Error: Couldn't find matching '['");
              }
            }
          }
          break;
        default:
          break;
      }
      command = command.getNext();
    }
   return stringBuilder.toString();
  }

  private void dump(){
    Node<Character> target = this.commands;
    while(target != null){
      Character value = target.getValue();
      System.out.println(String.valueOf(value));
      target = target.getNext();
    }
  }


}
