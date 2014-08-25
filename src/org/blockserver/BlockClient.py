'''
Created on Aug 25, 2014

@author: jython234
'''
from org.blockserver import MinecraftPEClient

def main():
    client = MinecraftPEClient.Client("blockclient");
    client.start();


if __name__=="main":
    main();