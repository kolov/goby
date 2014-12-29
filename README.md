Goby
====

MPEG Streaming in Java for the Raspberry Pi

Why
====

I bought a Raspberry Pi with a camera and I expected it would be easy the stream an image of my reef tank from 
the camera using Java. I coundn't find out of the box solution, so this is my take. 

Demo
==== 
At certain moments, the Raspberry Pi will be pointed to the aquarium and *Goby* will be running.

How it works
====

The Raspberry Pi camera can be controlled with raspstill. This

    raspistill --nopreview -w 640 -h 480 -q 55 -o /var/vid/pic.jpg -tl 100 -t 9999999 -th 0:0:0 &
stores a camera snapshot each 100ms in the same file /var/vid/pic.jpg. The hard part was to get notified on update: 
see [notipy](https://github.com/kolov/notipy).
