Goby
====

MPEG Streaming in Java for the Raspberry Pi

Why
====

I bought a Raspberry Pi with a camera and I expected it would be easy the stream an image of my reef tank from 
the camera using Java. I coundn't find out of the box solution, so this is my take. See it for yourself: at certain moments, the Pi's camera will be pointed to the aquarium and *Goby* will be running and accessible at <http://192.168.0.14:8080/goby/>. Everything runs at the Pi, so I don't expect to many concurrent request could be served.

How it works
====

The Raspberry Pi camera can be controlled with raspstill. This command:

    raspistill --nopreview -w 640 -h 480 -q 55 -o /var/vid/pic.jpg -tl 100 -t 9999999 -th 0:0:0 &
makes the camera store a fresh snapshot each 100ms in the same file /var/vid/pic.jpg. On my pi, it is more like 500ms, I am not sure exactly why. The hard part was to get notified on update. Linux' inotify 
see [notipy](https://github.com/kolov/notipy). The image is served as multipart/x-mixed-replace and a new version streamed each time the file is rewritten. The application need an asynchronous Servlet 3.1 container, has been tested with Jetty 9.

Is it good?
====
I do not know. The speed whith which *raspistill* is taking snapshots is disappointing, and I haven't looked at *raspivid*. As it is now, it is good enoight for a surveillance application but nothing more. 


