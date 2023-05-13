# <img src="jpsxdec/src/jpsxdec/gui/icon48.png" align="top"> jPSXdec

**jPSXdec is a modern, cross-platform PlayStation 1 audio/video converter**  
<sub>Also supports extracting files and TIM images.</sub>

----------------------------------------------------------------------------------

### Command Line

Help:

````
java -jar jpsxdec.jar -h
````

Build Index File:

````
java -jar jpsxdec.jar -f <input file> -x <output index file>
````

Sector Dump (display information on all sectors, including counts):

````
java -jar jpsxdec.jar -x <index file> -sectordump <output file>
````

Export all Files:

````
java -jar jpsxdec.jar -x <index file> -all file
````

`file` can also be replaced with `video`, `audio`, or `image`.

Visualize Sectors (PDF can get very laggy):

````
java -jar jpsxdec.jar -visualize <PDF output file>
````
