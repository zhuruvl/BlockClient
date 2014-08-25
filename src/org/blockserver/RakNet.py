'''
Created on Aug 25, 2014

@author: jython234
'''
from org.blockserver import BlockUtil

class OpenConnections:
    long_starttime = None;
    bytes_magic = bytearray.fromhex("000ffff00fefefefefdfdfdfd12345678")
    
    def __init(self, startTime):
        self.long_starttime = BlockUtil.long_to_bytes(startTime);
        
    def encode(self):
        buffer = bytearray();
        buffer.append(0x1C);
        
        buffer.append(self.long_starttime);
        buffer.append(self.bytes_magic);
        
        return buffer;
        
        