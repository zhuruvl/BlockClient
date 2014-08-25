'''
Created on Aug 25, 2014

@author: jython234
'''
from threading import Thread
from binascii import unhexlify
from org.blockserver import RakNet
from org.blockserver import BlockUtil
import logging
import socket

class Client(Thread):
    '''
    Represents a MCPE Client
    '''
    playerName = "Steve";
    logger = None;
    status = "SEARCH";
    startTime = None;
    
    socket = None;

    def run(self):
        self.setName("Main-Client")
        self.initLogger();
        self.initSocket();
        
        self.logger.info("Now begining client loop.");
        self.startTime = BlockUtil.getSystemTimeMillis();
        while True:
            #Main client loop
            if self.status == "SEARCH":
                #Client Searching
                pk = RakNet.OpenConnections(BlockUtil.getSystemTimeMillis() - self.startTime);
                data = pk.encode();
                
                print(data);
                
                
        
    def initLogger(self):
        self.logger = logging.getLogger("BlockClient");
        self.logger.setLevel(logging.DEBUG);
        
        ch = logging.StreamHandler();
        ch.setLevel(logging.DEBUG);
        
        formatter = logging.Formatter(fmt="[%(asctime)s] [%(levelname)s/%(threadName)s]: %(message)s", datefmt="%H:%M:%S");
        
        ch.setFormatter(formatter)
        self.logger.addHandler(ch);
        
        self.logger.debug("Logger started.");
        
        
        
    def initSocket(self):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM);
        self.logger.debug("Socket Started...");