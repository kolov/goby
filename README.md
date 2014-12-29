Goby
====

MPEG Streaming in Java for the Raspberry Pi

Why
====

I bought a Raspberry Pi with a camera and I expected it would be easy to stream an image of my reef tank from 
the camera using Java. I found no Java way to do this, only one full application with UI, http server and everything: [mjpg-streamer](http://sourceforge.net/projects/mjpg-streamer/). Still, as I found no way to embed the stream in my own app I started this project. See it for yourself: now and then, the Pi's camera will be pointed to the aquarium and *Goby* will be running and accessible at <http://192.168.0.14:8080/goby/>. Everything runs at the Pi, so I don't expect to many concurrent request could be served.

How it works
====

The Raspberry Pi camera can be controlled with raspstill. This command:

    raspistill --nopreview -w 640 -h 480 -q 55 -o /var/vid/pic.jpg -tl 100 -t 9999999 -th 0:0:0 &
makes the camera store a fresh snapshot each 100ms in the same file /var/vid/pic.jpg. On my pi, it is more like 500ms, I am not sure exactly why. The hard part was to get notified on update. Linux' inotify 
see [notipy](https://github.com/kolov/notipy). The image is served as multipart/x-mixed-replace and a new version streamed each time the file is rewritten. The application need an asynchronous Servlet 3.1 container, has been tested with Jetty 9.

Is it good?
====
The speed whith which *raspistill* is taking snapshots is disappointing, I hope it is possible to build something nicer with *raspivid*. As it is now, it is good enoight for a surveillance application but nothing more. 

Why Goby?
====
That's him: A yellow goby is inhabitant #3 of the tank: ![goby](http://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Gobidon_okinawae1.jpg/500px-Gobidon_okinawae1.jpg)
